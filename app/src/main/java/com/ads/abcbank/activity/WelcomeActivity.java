package com.ads.abcbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.ads.abcbank.R;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.BaseActivity;

public class WelcomeActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Utils.put(this, Utils.KEY_TIME_CURRENT_CMD, "1");
        Utils.put(this, Utils.KEY_TIME_CURRENT_PLAYLIST, "1");
        Utils.put(this, Utils.KEY_TIME_CURRENT_PRESET, "1");

        if(TextUtils.isEmpty(Utils.get(this,Utils.KEY_TIME_CMD,"").toString())){
            Utils.put(this, Utils.KEY_TIME_CMD, "5");
        }
        if(TextUtils.isEmpty(Utils.get(this,Utils.KEY_TIME_PLAYLIST,"").toString())){
            Utils.put(this, Utils.KEY_TIME_PLAYLIST, "20");
        }
        if(TextUtils.isEmpty(Utils.get(this,Utils.KEY_TIME_PRESET,"").toString())){
            Utils.put(this, Utils.KEY_TIME_PRESET, "30");
        }
        if(TextUtils.isEmpty(Utils.get(this,Utils.KEY_TIME_FILE,"").toString())){
            Utils.put(this, Utils.KEY_TIME_FILE, "30");
        }
        handler.postDelayed(runnable, 3000);
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    public void onBackPressed() {
    }
}
