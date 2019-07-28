package com.ads.abcbank.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.ads.abcbank.bean.PresetBean;
import com.ads.abcbank.utils.AsyncThread;
import com.ads.abcbank.utils.HTTPContants;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.IView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class TempPresenter {
    private Context context;
    private IView tempView;
    private AsyncThread asyncThread;

    private long presetTime = 30 * 60 * 1000;

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
                        Utils.put(context, Utils.KEY_PLAY_LIST, msg.obj.toString());
                    }
                    break;
                case 5:
                    Logger.e("getPreset", "获取预设汇率信息返回数据====" + msg.obj);
                    if (msg.obj != null) {
                        Utils.put(context, Utils.KEY_PRESET, msg.obj);
                        try {
                            PresetBean bean = JSON.parseObject(msg.obj.toString(), PresetBean.class);
                            if (!"0".equals(bean.resCode)) {
                                return;
                            }
                        } catch (Exception e) {
                            return;
                        }
                        tempView.updatePresetDate(JSONObject.parseObject(msg.obj.toString()));
                    } else {
                        tempView.updatePresetDate(null);
                    }
                    break;
                default:
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

    public void getCmdPoll() {
        asyncThread.httpService(HTTPContants.CODE_CMDPOLL, new JSONObject(), handler, 3);
    }

    public void cmdResult(JSONObject jsonObject) {
        asyncThread.httpService(HTTPContants.CODE_CMDRESULT, jsonObject, handler, 4);
    }

    public void getPreset() {
    }
}
