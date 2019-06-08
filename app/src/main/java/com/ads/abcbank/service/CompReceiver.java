package com.ads.abcbank.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ads.abcbank.activity.WelcomeActivity;

public class CompReceiver extends BroadcastReceiver {
    public CompReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent i = new Intent(context, WelcomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
