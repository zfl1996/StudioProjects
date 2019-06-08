package com.ads.abcbank;

import android.app.Application;
import android.content.Context;

import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.ScreenAdaptation;

public class MyApplication extends Application {
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        new ScreenAdaptation(this, 1080, 1920).register();
        context = this;
        registerActivityLifecycleCallbacks(ActivityManager.getInstance());
    }

    public Context getContext() {
        return context;
    }
}
