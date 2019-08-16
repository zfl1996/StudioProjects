package com.ads.abcbank.xx.utils.core;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import com.ads.abcbank.bean.CmdpollBean;
import com.ads.abcbank.bean.DownloadBean;
import com.ads.abcbank.bean.PresetBean;
import com.ads.abcbank.bean.RegisterBean;
import com.ads.abcbank.bean.RequestBean;
import com.ads.abcbank.service.DownloadService;
import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.FileUtil;
import com.ads.abcbank.utils.HTTPContants;
import com.ads.abcbank.utils.HandlerUtil;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.xx.utils.BllDataExtractor;
import com.ads.abcbank.xx.utils.Constants;
import com.ads.abcbank.xx.utils.helper.ResHelper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class NetTaskManager {
    static final String TAG = "NetTaskManager";

    Timer timer;
    TimerTask timerTask;

    HandlerThread netThread;
    Handler netHandler;
    Context context;
    NetTaskListener netTaskListener;
    boolean isInited = false;

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
                    case Constants.NET_MANAGER_INIT:
                        if (!isInited) {
                            isInited = true;
                            timer.schedule(timerTask, 50, 2*60*1000 );
                        }

                        break;

                    case Constants.NET_MANAGER_DATA_CMDPOLL:
                        parseCmdPoll(msg.obj);

                        break;

                    case Constants.NET_MANAGER_DATA_PLAYLIST:
                        parsePalyList(msg.obj);

                        break;

                    case Constants.NET_MANAGER_DATA_PRESET:
                        parsePresetData(msg.obj);

                        break;

                    case Constants.NET_MANAGER_DATA_FINISHNOTIFY:
                        sendNotify((String[])msg.obj);

                        break;


                    default:
                        break;
                }
            }

        };
    }

    private void sendNotify(String[] notifyData) {
        DownloadBean downloadBean = new DownloadBean();

        downloadBean.id = notifyData[0];
        downloadBean.started = notifyData[1];
        downloadBean.secUsed = notifyData[2];
        downloadBean.status = "finish";

        Utils.getAsyncThread().httpService(HTTPContants.CODE_DOWNLOAD_FINISH,
                JSONObject.parseObject(JSONObject.toJSONString(downloadBean)),
                HandlerUtil.noCheckGet(), 1);
    }

    public void initNetManager() {
        ResHelper.sendMessage(netHandler, Constants.NET_MANAGER_INIT, null);
    }

    public void notifyownloadFinish(Object data) {
        ResHelper.sendMessage(netHandler, Constants.NET_MANAGER_DATA_FINISHNOTIFY, data);
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
        FileUtil.writeJsonToFile(obj.toString(), false);
        boolean needDownload = BllDataExtractor.needDownload(context, obj.toString());
        Logger.e(TAG, "netTaskListener.onPlaylistArrived-->needDownload:" + needDownload
            + ", willNotify:" + (null != netTaskListener) );

        if (needDownload) {
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

    public interface NetTaskListener {
        void onPlaylistArrived(JSONObject jsonObject);
        void onPresetArrived(JSONObject jsonObject);
    }

}
