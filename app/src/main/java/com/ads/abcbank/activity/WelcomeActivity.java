package com.ads.abcbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.InitResultBean;
import com.ads.abcbank.bean.RegisterBean;
import com.ads.abcbank.presenter.MainPresenter;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.view.IMainView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class WelcomeActivity extends BaseActivity implements IMainView {
    private MainPresenter mainPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        mainPresenter = new MainPresenter(this, this);
        Utils.put(this, Utils.KEY_TIME_CURRENT_CMD, "1");
        Utils.put(this, Utils.KEY_TIME_CURRENT_PLAYLIST, "1");
        Utils.put(this, Utils.KEY_TIME_CURRENT_PRESET, "1");

        if (TextUtils.isEmpty(Utils.get(this, Utils.KEY_TIME_CMD, "").toString())) {
            Utils.put(this, Utils.KEY_TIME_CMD, "5");
        }
        if (TextUtils.isEmpty(Utils.get(this, Utils.KEY_TIME_PLAYLIST, "").toString())) {
            Utils.put(this, Utils.KEY_TIME_PLAYLIST, "20");
        }
        if (TextUtils.isEmpty(Utils.get(this, Utils.KEY_TIME_PRESET, "").toString())) {
            Utils.put(this, Utils.KEY_TIME_PRESET, "30");
        }
        if (TextUtils.isEmpty(Utils.get(this, Utils.KEY_TIME_FILE, "").toString())) {
            Utils.put(this, Utils.KEY_TIME_FILE, "30");
        }

        String beanStr = Utils.get(WelcomeActivity.this, Utils.KEY_REGISTER_BEAN, "").toString();
        if (TextUtils.isEmpty(beanStr)) {
            handler.postDelayed(runnable, 3000);
        } else {
            mainPresenter.init(JSONObject.parseObject(beanStr));
        }
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String beanStr = Utils.get(WelcomeActivity.this, Utils.KEY_REGISTER_BEAN, "").toString();
            Intent intent = new Intent();
            if (TextUtils.isEmpty(beanStr)) {
                intent.setClass(WelcomeActivity.this, MainActivity.class);
            } else {
                RegisterBean bean = JSON.parseObject(beanStr, RegisterBean.class);
                switch (bean.data.frameSetNo) {
                    case "1":
                        intent.setClass(WelcomeActivity.this, Temp1Activity.class);
                        break;
                    case "2":
                        intent.setClass(WelcomeActivity.this, Temp2Activity.class);
                        break;
                    case "3":
                        intent.setClass(WelcomeActivity.this, Temp3Activity.class);
                        break;
                    case "4":
                        intent.setClass(WelcomeActivity.this, Temp4Activity.class);
                        break;
                    case "5":
                        intent.setClass(WelcomeActivity.this, Temp5Activity.class);
                        break;
                    case "6":
                        intent.setClass(WelcomeActivity.this, Temp6Activity.class);
                        break;
                }
            }
            startActivity(intent);
        }
    };

    @Override
    public void onBackPressed() {
    }

    @Override
    public void init(String jsonObject) {
        if (!TextUtils.isEmpty(jsonObject)) {
            InitResultBean initResultBean = JSON.parseObject(jsonObject, InitResultBean.class);
            if (initResultBean.resCode.equals("0")) {
                String beanStr = Utils.get(WelcomeActivity.this, Utils.KEY_REGISTER_BEAN, "").toString();
                Intent intent = new Intent();
                if (TextUtils.isEmpty(beanStr)) {
                    intent.setClass(WelcomeActivity.this, MainActivity.class);
                } else {
                    RegisterBean bean = JSON.parseObject(beanStr, RegisterBean.class);
                    switch (bean.data.frameSetNo) {
                        case "1":
                            intent.setClass(WelcomeActivity.this, Temp1Activity.class);
                            break;
                        case "2":
                            intent.setClass(WelcomeActivity.this, Temp2Activity.class);
                            break;
                        case "3":
                            intent.setClass(WelcomeActivity.this, Temp3Activity.class);
                            break;
                        case "4":
                            intent.setClass(WelcomeActivity.this, Temp4Activity.class);
                            break;
                        case "5":
                            intent.setClass(WelcomeActivity.this, Temp5Activity.class);
                            break;
                        case "6":
                            intent.setClass(WelcomeActivity.this, Temp6Activity.class);
                            break;
                    }
                }
                startActivity(intent);
            } else if (initResultBean.resCode.equals("-1")) {
                finish();
            } else if (initResultBean.resCode.equals("1")) {
                finish();
            }
        }else{
            String beanStr = Utils.get(WelcomeActivity.this, Utils.KEY_REGISTER_BEAN, "").toString();
            Intent intent = new Intent();
            if (TextUtils.isEmpty(beanStr)) {
                intent.setClass(WelcomeActivity.this, MainActivity.class);
            } else {
                RegisterBean bean = JSON.parseObject(beanStr, RegisterBean.class);
                switch (bean.data.frameSetNo) {
                    case "1":
                        intent.setClass(WelcomeActivity.this, Temp1Activity.class);
                        break;
                    case "2":
                        intent.setClass(WelcomeActivity.this, Temp2Activity.class);
                        break;
                    case "3":
                        intent.setClass(WelcomeActivity.this, Temp3Activity.class);
                        break;
                    case "4":
                        intent.setClass(WelcomeActivity.this, Temp4Activity.class);
                        break;
                    case "5":
                        intent.setClass(WelcomeActivity.this, Temp5Activity.class);
                        break;
                    case "6":
                        intent.setClass(WelcomeActivity.this, Temp6Activity.class);
                        break;
                }
            }
            startActivity(intent);
        }
    }

    @Override
    public void register(String jsonObject) {
    }
}
