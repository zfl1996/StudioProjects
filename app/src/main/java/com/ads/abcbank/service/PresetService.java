package com.ads.abcbank.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.HTTPContants;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.BaseActivity;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;

public class PresetService extends Service {
    public PresetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*每次调用startService启动该服务都会执行*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Logger.e("TAG", "启动获取汇率列表服务：" + new Date().toString());
        Utils.getAsyncThread().httpService(HTTPContants.CODE_PRESET, new JSONObject(), handler, 0);

        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Logger.e("getPreset", "获取预设汇率列表返回数据====" + msg.obj);
                    if (msg.obj != null) {
                        Utils.put(PresetService.this, Utils.KEY_PRESET, msg.obj);
                        Activity activity = ActivityManager.getInstance().getTopActivity();
                        if (activity instanceof BaseActivity) {
                            ((BaseActivity) activity).getiView().updatePresetDate(JSONObject.parseObject(msg.obj.toString()));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
}