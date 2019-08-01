package com.ads.abcbank;

import android.app.Application;
import android.content.Context;

import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.ScreenAdaptation;
import com.arialyy.aria.core.Aria;

public class MyApplication extends Application {
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        new ScreenAdaptation(this, 1080, 1920).register();
        context = this;

        registerActivityLifecycleCallbacks(ActivityManager.getInstance());
//        Logger.init(MyApplication.this);
        Aria.init(this);
    }

    public Context getContext() {
        return context;
    }
}
