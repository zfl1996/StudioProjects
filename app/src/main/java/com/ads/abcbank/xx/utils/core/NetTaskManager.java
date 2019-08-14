package com.ads.abcbank.xx.utils.core;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import com.ads.abcbank.bean.CmdpollBean;
import com.ads.abcbank.bean.PresetBean;
import com.ads.abcbank.bean.RegisterBean;
import com.ads.abcbank.bean.RequestBean;
import com.ads.abcbank.service.DownloadService;
import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.FileUtil;
import com.ads.abcbank.utils.HTTPContants;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.xx.utils.Constants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class NetTaskManager {

    Timer timer;
    TimerTask timerTask;

    HandlerThread netThread;
    Handler netHandler, uiHandler;
    Context context;
    NetTaskListener netTaskListener;

    public NetTaskManager(Context context, NetTaskListener netTaskListener) {
        this.context = context;
        this.netTaskListener = netTaskListener;

        timer = new Timer();
        timerTask =  new TimerTask() {
            @Override
            public void run() {
                Utils.getExecutorService().submit(() -> reqAllData());
            }
        };


        netThread = new HandlerThread("netThread");
        netThread.start();

        netHandler = new Handler(netThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
//                    case Constants.NET_MANAGER_INIT:
//                        Utils.getExecutorService().submit(() -> reqAllData());
//
//                        break;

                    case Constants.NET_MANAGER_DATA_CMDPOLL:
                        parseCmdPoll(msg.obj);

                        break;

                    case Constants.NET_MANAGER_DATA_PLAYLIST:
                        parsePalyList(msg.obj);

                        break;

                    case Constants.NET_MANAGER_DATA_PRESET:
                        parsePresetData(msg.obj);

                        break;


                    default:
                        break;
                }
            }
        };
    }

    public void initNetManager() {
        Utils.getExecutorService().submit(() -> {
            reqAllData();
            timer.schedule(timerTask, 1*60*1000, 1*60*1000 );
        });
    }

    public void cancalTask() {
        if (null != timer)
            timer.cancel();

        if (null != netThread)
            netThread.quit();
    }

    private void parseCmdPoll(Object obj) {
        if (obj != null) {
            Utils.put(context, Utils.KEY_CMD_POLL, obj.toString());
        }
    }

    private void parsePresetData(Object obj) {
        if (obj != null && !TextUtils.isEmpty(obj.toString())) {
            try {
                PresetBean bean = JSON.parseObject(obj.toString(), PresetBean.class);
                if (!"0".equals(bean.resCode)) {
                    return;
                }
            } catch (Exception e) {
                return;
            }

            Utils.put(context, Utils.KEY_PRESET, obj.toString());
            if (null != netTaskListener)
                netTaskListener.onPresetArrived(JSONObject.parseObject(obj.toString()));
        }
    }

    private void parsePalyList(Object obj) {
        FileUtil.writeJsonToFile(obj.toString());

        if (Utils.getNewPlayList(context, obj.toString())) {
            if (null != netTaskListener)
                netTaskListener.onPlaylistArrived(JSONObject.parseObject(obj.toString()));
        }
    }

    private void reqAllData() {
        retrievePreset();
        Utils.getAsyncThread()
                .httpService(HTTPContants.CODE_CMDPOLL, JSONObject.parseObject(JSONObject.toJSONString(new CmdpollBean())),
                        netHandler, Constants.NET_MANAGER_DATA_CMDPOLL);
        Utils.getAsyncThread()
                .httpService(HTTPContants.CODE_PLAYLIST, JSONObject.parseObject(JSONObject.toJSONString(DownloadService.getPlaylistBean())),
                        netHandler, Constants.NET_MANAGER_DATA_PLAYLIST);
    }

    private void retrievePreset() {
        RequestBean requestBean = new RequestBean();
        String beanStr = Utils.get(ActivityManager.getInstance().getTopActivity(), Utils.KEY_REGISTER_BEAN, "").toString();
        if (!TextUtils.isEmpty(beanStr)) {
            RegisterBean bean = JSON.parseObject(beanStr, RegisterBean.class);
            requestBean.appId = bean.appId;
            requestBean.trCode = bean.trCode;
            requestBean.trVersion = bean.trVersion;
            requestBean.cityCode = bean.cityCode;
            requestBean.brchCode = bean.brchCode;
            requestBean.clientVersion = bean.clientVersion;
            requestBean.terminalId = bean.terminalId;
            requestBean.uniqueId = bean.uniqueId;
        }
        requestBean.timestamp = System.currentTimeMillis();
        requestBean.flowNum = 0;
        Utils.getAsyncThread().httpService(HTTPContants.CODE_PRESET, JSONObject.parseObject(JSONObject.toJSONString(requestBean)), netHandler, Constants.NET_MANAGER_DATA_PRESET);
    }

    Message buildMessage(int w, Object obj, boolean isMain) {
        Message msg = isMain ? uiHandler.obtainMessage() : netHandler.obtainMessage();
        msg.what = w;
        msg.obj = obj;

        return msg;
    }

    public interface NetTaskListener {
        void onPlaylistArrived(JSONObject jsonObject);
        void onPresetArrived(JSONObject jsonObject);
    }

}
