package com.ads.abcbank.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.ads.abcbank.utils.AsyncThread;
import com.ads.abcbank.utils.HTTPContants;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.IView;
import com.alibaba.fastjson.JSONObject;

public class TempPresenter {
    private Context context;
    private IView tempView;
    private AsyncThread asyncThread;

    private  long presetTime = 30 * 60 * 1000;

    public TempPresenter(Context context, IView tempView) {
        asyncThread = Utils.getAsyncThread();
        this.context = context;
        this.tempView = tempView;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.mProgressDialog != null) {
                Utils.mProgressDialog.dismiss();
            }
            switch (msg.what) {
                case 2:
                    Logger.e("getPlayList", "获取播放列表返回数据====" + msg.obj);
                    if (msg.obj != null) {
                        Utils.put(context, Utils.KEY_PLAY_LIST, msg.obj);
                    }
                    break;
                case 5:
                    Logger.e("getPreset", "获取预设汇率信息返回数据====" + msg.obj);
                    if (msg.obj != null) {
                        Utils.put(context, Utils.KEY_PRESET, msg.obj);
                        tempView.updatePresetDate(JSONObject.parseObject(msg.obj.toString()));
                    } else {
                        tempView.updatePresetDate(null);
                    }
                    break;
            }
        }
    };


    public void init() {

        asyncThread.httpService(HTTPContants.CODE_INIT, new JSONObject(), handler, 0);
    }

    public void register(JSONObject jsonObject) {
        asyncThread.httpService(HTTPContants.CODE_REGISTER, jsonObject, handler, 1);
    }

    public void getPlayList() {
        Logger.e(Utils.KEY_PLAY_LIST_ALL,"开始执行获取播放列表请求");
        asyncThread.httpService(HTTPContants.CODE_PLAYLIST, new JSONObject(), handler, 2);
    }

    public void getCmdPoll() {
        asyncThread.httpService(HTTPContants.CODE_CMDPOLL, new JSONObject(), handler, 3);
    }

    public void cmdResult(JSONObject jsonObject) {
        asyncThread.httpService(HTTPContants.CODE_CMDRESULT, jsonObject, handler, 4);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            presetTime = Integer.
                    parseInt(Utils.
                            get(context, Utils.KEY_TIME_PRESET, "30")
                            .toString()) * 1000;
            getPreset();
        }
    };

    public void getPreset() {
        Logger.e("getPreset","开始执行获取预设汇率信息请求");
        asyncThread.httpService(HTTPContants.CODE_PRESET, new JSONObject(), handler, 5);
        handler.postDelayed(runnable, presetTime);
    }
}
