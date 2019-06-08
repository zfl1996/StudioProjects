package com.ads.abcbank.view;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.ads.abcbank.activity.MainActivity;
import com.ads.abcbank.service.TimeCmdService;
import com.ads.abcbank.service.TimePlaylistService;
import com.ads.abcbank.service.TimePresetService;
import com.ads.abcbank.utils.Utils;

public class BaseActivity extends AppCompatActivity {
    private final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private NetChangeReceiver netChangeReceiver;//网络状态
    private int netType = -1;
    private boolean hasNet = false;
    private boolean hasInit = false;
    private Runnable reInitRunnable;
    private IView iView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        netChangeReceiver = new NetChangeReceiver();
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
                startActivity(new Intent(this, MainActivity.class));
            }
        }
    }
}
