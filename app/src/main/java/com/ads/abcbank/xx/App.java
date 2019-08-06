package com.ads.abcbank.xx;

import android.support.v7.app.AppCompatActivity;

import com.ads.abcbank.MyApplication;

public class App extends MyApplication {
    public static AppCompatActivity getCurActivity() {
        return curActivity;
    }

    public static void setCurActivity(AppCompatActivity curActivity) {
        App.curActivity = curActivity;
    }

    private static AppCompatActivity curActivity;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
