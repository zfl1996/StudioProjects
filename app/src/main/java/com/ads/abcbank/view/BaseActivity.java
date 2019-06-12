package com.ads.abcbank.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.ads.abcbank.activity.MainActivity;
import com.ads.abcbank.activity.ReInitActivity;
import com.ads.abcbank.bean.CmdpollResultBean;
import com.ads.abcbank.bean.CmdresultBean;
import com.ads.abcbank.service.DownloadService;
import com.ads.abcbank.service.TimeCmdService;
import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.HTTPContants;
import com.ads.abcbank.utils.HandlerUtil;
import com.ads.abcbank.utils.Utils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class BaseActivity extends AppCompatActivity {
    private final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private NetChangeReceiver netChangeReceiver;//网络状态
    private int netType = -1;
    private boolean hasNet = false;
    private boolean hasInit = false;
    private Runnable reInitRunnable;
    private IView iView;
    public static Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        netChangeReceiver = new NetChangeReceiver();
        mActivity=this;
        registerDateTransReceiver();
    }

    private void registerDateTransReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CONNECTIVITY_CHANGE_ACTION);
        filter.setPriority(1000);
        registerReceiver(netChangeReceiver, filter);
    }

    public void startServices() {
        startService(new Intent(this, TimeCmdService.class));
//        startService(new Intent(this, TimePlaylistService.class));
//        startService(new Intent(this, TimePresetService.class));
        Intent intent = new Intent();
        intent.putExtra("name", "");
        intent.putExtra("isUrg", "0");
        intent.putExtra("url", "http://d1.music.126.net/dmusic/CloudMusic_official_4.3.2.468990.apk");
        intent.setAction(DownloadService.ADD_DOWNTASK);
        intent.setPackage(DownloadService.PACKAGE);
        startService(intent);
    }

    private int getNetworkType() {
        ConnectivityManager connectMgr = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if (info != null) {
            return info.getType();
        } else {
            return -1;
        }
    }

    private class NetChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, CONNECTIVITY_CHANGE_ACTION)) {
                netType = getNetworkType();
                if (netType == ConnectivityManager.TYPE_WIFI || netType == ConnectivityManager.TYPE_MOBILE) {
                    if (hasInit && !hasNet)
                        if (reInitRunnable != null)
                            reInitRunnable.run();
                    hasNet = true;
                } else {
                    if (!hasInit)
                        hasNet = false;
                }
                hasInit = true;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(netChangeReceiver);
        Utils.changeIntent(this);
    }

    @Override
    public void onBackPressed() {
    }

    public void setReInitRunnable(Runnable reInitRunnable) {
        this.reInitRunnable = reInitRunnable;
    }

    public IView getiView() {
        return iView;
    }

    public void setiView(IView iView) {
        this.iView = iView;
    }

    private long mLastClickTime;
    private int clickTimes;

    public void toMainView(View view) {
        if ((System.currentTimeMillis() - mLastClickTime) > 1000) {
            mLastClickTime = System.currentTimeMillis();
            clickTimes = 0;
        } else {
            if (clickTimes < 1) {
                clickTimes++;
            } else {
                clickTimes = 0;
                startActivity(new Intent(this, ReInitActivity.class));
            }
        }
    }

    public void getCmdResult(CmdpollResultBean bean) {
        if (bean != null) {
            HandlerUtil.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (bean.data.cmd.equals("prtsc")) {
                        CmdresultBean cmdresultBean = new CmdresultBean();
                        cmdresultBean.data.cmd = bean.data.cmd;
                        cmdresultBean.data.cmdresult = Utils.screenShot(BaseActivity.this);
                        cmdresultBean.flowNum = (TextUtils.isEmpty(bean.flowNum) ? 1 : Integer.parseInt(bean.flowNum)) + 1;
                        Utils.getAsyncThread().httpService(HTTPContants.CODE_CMDRESULT,
                                JSONObject.parseObject(JSONObject.toJSONString(cmdresultBean)), HandlerUtil.noCheckGet(), 0);
                    } else if (!bean.data.cmd.equals("idle")) {
                        CmdresultBean cmdresultBean = new CmdresultBean();
                        cmdresultBean.data.cmd = bean.data.cmd;
                        cmdresultBean.data.cmdresult = "";
                        cmdresultBean.flowNum = (TextUtils.isEmpty(bean.flowNum) ? 1 : Integer.parseInt(bean.flowNum)) + 1;
                        Utils.getAsyncThread().httpService(HTTPContants.CODE_CMDRESULT,
                                JSONObject.parseObject(JSONObject.toJSONString(cmdresultBean)), handler, 0);
                    }
                }
            }, 500);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ActivityManager.getInstance().finishAllActivity();
            System.exit(0);
        }
    };
}
