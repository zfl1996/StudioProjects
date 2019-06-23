package com.ads.abcbank.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.ads.abcbank.bean.CmdpollResultBean;
import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.FileUtil;
import com.ads.abcbank.utils.HTTPContants;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.BaseActivity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;

public class CmdService extends Service {

    public CmdService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*每次调用startService启动该服务都会执行*/
    public int onStartCommand(Intent intent, int flags, int startId) {

        startService(new Intent(CmdService.this, TimeCmdService.class));
        String timeCmd = Utils.get(CmdService.this, Utils.KEY_TIME_CMD, "5").toString();
        String timePlaylist = Utils.get(CmdService.this, Utils.KEY_TIME_PLAYLIST, "20").toString();
        String timePreset = Utils.get(CmdService.this, Utils.KEY_TIME_PRESET, "30").toString();

        String timeCurrentCmd = Utils.get(CmdService.this, Utils.KEY_TIME_CURRENT_CMD, "1").toString();
        String timeCurrentPlaylist = Utils.get(CmdService.this, Utils.KEY_TIME_CURRENT_PLAYLIST, "1").toString();
        String timeCurrentPreset = Utils.get(CmdService.this, Utils.KEY_TIME_CURRENT_PRESET, "1").toString();
        if (timeCurrentCmd.compareTo(timeCmd) != 0) {
            Utils.put(CmdService.this, Utils.KEY_TIME_CURRENT_CMD, (Integer.parseInt(timeCurrentCmd) + 1) + "");
        } else {
            Utils.put(CmdService.this, Utils.KEY_TIME_CURRENT_CMD, "1");
            Logger.e("TAG", "启动获取cmdpoll轮询命令服务：" + new Date().toString());
            Utils.getAsyncThread().httpService(HTTPContants.CODE_CMDPOLL, new JSONObject(), handler, 0);
        }
        if (timeCurrentPlaylist.compareTo(timePlaylist) != 0) {
            Utils.put(CmdService.this, Utils.KEY_TIME_CURRENT_PLAYLIST, (Integer.parseInt(timeCurrentCmd) + 1) + "");
        } else {
            Utils.put(CmdService.this, Utils.KEY_TIME_CURRENT_PLAYLIST, "1");
            Logger.e("TAG", "启动获取播放列表：" + new Date().toString());
            Utils.getAsyncThread().httpService(HTTPContants.CODE_PLAYLIST, new JSONObject(), handler, 1);
        }
        if (timeCurrentPreset.compareTo(timePreset) != 0) {
            Utils.put(CmdService.this, Utils.KEY_TIME_CURRENT_PRESET, (Integer.parseInt(timeCurrentCmd) + 1) + "");
        } else {
            Utils.put(CmdService.this, Utils.KEY_TIME_CURRENT_PRESET, "1");
            Logger.e("TAG", "启动获取预设汇率列表服务：" + new Date().toString());
            Utils.getAsyncThread().httpService(HTTPContants.CODE_PRESET, new JSONObject(), handler, 2);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Logger.e("getCmdPoll", "获取cmdpoll轮询命令返回数据====" + msg.obj);
                    if (msg.obj != null) {
                        Utils.put(CmdService.this, Utils.KEY_CMD_POLL, msg.obj);
                        Activity activity = ActivityManager.getInstance().getTopActivity();
                        if (activity instanceof BaseActivity) {
                            ((BaseActivity) activity).getCmdResult(JSON.parseObject(msg.obj.toString(), CmdpollResultBean.class));
                        }
                    }
                    break;
                case 1:
                    Logger.e("getPlayList", "获取播放列表返回数据====" + msg.obj);
                    if (msg.obj != null) {
//                        Utils.put(CmdService.this, Utils.KEY_PLAY_LIST, msg.obj);
                        FileUtil.writeJsonToFile(msg.obj.toString());
                        Activity activity = ActivityManager.getInstance().getTopActivity();
                        if (activity instanceof BaseActivity) {
                            if (Utils.getNewPlayList(activity, msg.obj.toString())) {
                                ((BaseActivity) activity).getiView().updateMainDate(JSONObject.parseObject(msg.obj.toString()));
                            }
                        }
                    }
                    break;
                case 2:
                    Logger.e("getPreset", "获取预设汇率列表返回数据====" + msg.obj);
                    if (msg.obj != null) {
                        Utils.put(CmdService.this, Utils.KEY_PRESET, msg.obj);
                        Activity activity = ActivityManager.getInstance().getTopActivity();
                        if (activity instanceof BaseActivity) {
                            ((BaseActivity) activity).getiView().updatePresetDate(JSONObject.parseObject(msg.obj.toString()));
                        }
                    }
                    break;
            }
        }
    };
}