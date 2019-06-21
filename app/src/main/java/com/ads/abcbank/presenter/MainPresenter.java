package com.ads.abcbank.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ads.abcbank.utils.AsyncThread;
import com.ads.abcbank.utils.HTTPContants;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.IMainView;
import com.alibaba.fastjson.JSONObject;

public class MainPresenter {
    private Context context;
    private IMainView mainView;
    private AsyncThread asyncThread;

    public MainPresenter(Context context, IMainView mainView) {
        asyncThread = Utils.getAsyncThread();
        this.context = context;
        this.mainView = mainView;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.mProgressDialog != null) {
                Utils.mProgressDialog.dismiss();
            }
            switch (msg.what) {
                case 0:
                    Logger.e("CODE_INIT", "初始化请求返回数据====" + msg.obj);
                    if (msg.obj != null) {
                        mainView.init(msg.obj.toString());
                    }else{
                        mainView.init(null);
                    }
                    break;
                case 1:
                    Logger.e("register", "注册请求返回数据====" + msg.obj);
                    if (msg.obj != null) {
                        mainView.register(msg.obj.toString());
                    }else{
                        mainView.register(null);
                    }
                    break;
            }
        }
    };


    public void init(JSONObject jsonObject) {
        Logger.e("CODE_INIT","开始执行初始化请求");
        asyncThread.httpService(HTTPContants.CODE_INIT, jsonObject, handler, 0);
    }

    public void register(JSONObject jsonObject) {
        Logger.e(Utils.KEY_REGISTER_BEAN,"开始执行注册请求");
        asyncThread.httpService(HTTPContants.CODE_REGISTER, jsonObject, handler, 1);
    }

}