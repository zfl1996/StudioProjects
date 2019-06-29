package com.ads.abcbank.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.InitResultBean;
import com.ads.abcbank.bean.RegisterBean;
import com.ads.abcbank.presenter.MainPresenter;
import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.HandlerUtil;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.PermissionHelper;
import com.ads.abcbank.utils.ToastUtil;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.view.IMainView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WelcomeActivity extends BaseActivity implements IMainView {
    private MainPresenter mainPresenter;
    private PermissionHelper mPermissionHelper;
    private static String TAG = "SplashActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
// 当系统为6.0以上时，需要申请权限
        mPermissionHelper = new PermissionHelper(this);
        mPermissionHelper.setOnApplyPermissionListener(new PermissionHelper.OnApplyPermissionListener() {
            @Override
            public void onAfterApplyAllPermission() {
//                Logger.i(TAG, "All of requested permissions has been granted, so run app logic.");
                runApp();
            }
        });
        if (Build.VERSION.SDK_INT < 23) {
            // 如果系统版本低于23，直接跑应用的逻辑
//            Logger.d(TAG, "The api level of system is lower than 23, so run app logic directly.");
            runApp();
        } else {
            // 如果权限全部申请了，那就直接跑应用逻辑
            if (mPermissionHelper.isAllRequestedPermissionGranted()) {
//                Logger.d(TAG, "All of requested permissions has been granted, so run app logic directly.");
                runApp();
            } else {
                // 如果还有权限为申请，而且系统版本大于23，执行申请权限逻辑
//                Logger.i(TAG, "Some of requested permissions hasn't been granted, so apply permissions first.");
                mPermissionHelper.applyPermissions();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionHelper.onActivityResult(requestCode, resultCode, data);
    }

    private void runApp() {
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

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

    @Override
    public void init(String jsonObject) {
        if (!TextUtils.isEmpty(jsonObject)) {
            InitResultBean initResultBean = JSON.parseObject(jsonObject, InitResultBean.class);
            if (initResultBean.resCode.equals("0")) {
                String timePlaylist = Utils.get(WelcomeActivity.this, Utils.KEY_TIME_PLAYLIST, "20").toString();
                int time;
                try {
                    time = Integer.parseInt(timePlaylist);
                } catch (NumberFormatException e) {
                    time = 20;
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.MINUTE, -1 * time);
                String startTime = simpleDateFormat.format(calendar.getTime());

                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(new Date());
                calendar2.add(Calendar.MINUTE, time);
                String endTime = simpleDateFormat.format(calendar2.getTime());

                if (startTime.compareTo(initResultBean.data.serverTime) < 0
                        || endTime.compareTo(initResultBean.data.serverTime) > 0) {
                    ToastUtil.showToastLong(this, "服务器时间与本地时间误差较大");
                    HandlerUtil.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ActivityManager.getInstance().finishAllActivity();
                            System.exit(0);
                        }
                    }, 1000);
                    return;
                }

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
                ToastUtil.showToastLong(this, initResultBean.resMessage);
                Logger.e("服务器主动拒绝");
                finish();
            } else if (initResultBean.resCode.equals("1")) {
                ToastUtil.showToastLong(this, initResultBean.resMessage);
                Logger.e("客户端版本过低");
//                Utils.startUpdateDownloadTask(mActivity, "test.apk", "");
                finish();
            }
        } else {
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
