package com.ads.abcbank.xx.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.ads.abcbank.R;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.view.AutoPollAdapter;
import com.ads.abcbank.view.AutoPollRecyclerView;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.view.IView;
import com.ads.abcbank.xx.ui.view.SliderPlayer;
import com.alibaba.fastjson.JSONObject;

public class TempV2Activity extends BaseActivity implements IView {
    private static final String TAG = "TempV2Activity";

    SliderPlayer sliderPlayer;
    AutoPollAdapter autoPollAdapter;
    AutoPollRecyclerView rvMarqueeView;
    View v_set;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_v2);
        setiView(this);

        initCtrls();
    }

    private void initCtrls() {
        sliderPlayer = findViewById(R.id.sliderPlayer);
        rvMarqueeView = findViewById(R.id.rvMarqueeView);
        v_set = findViewById(R.id.v_set);

        sliderPlayer.setDataStatusListener( items -> {
            autoPollAdapter = new AutoPollAdapter(TempV2Activity.this, items);
            rvMarqueeView.setLayoutManager(new LinearLayoutManager(TempV2Activity.this, LinearLayoutManager.HORIZONTAL, false));
            rvMarqueeView.setAdapter(autoPollAdapter);
            rvMarqueeView.start();

            new Handler().postDelayed(() -> rvMarqueeView.setVisibility(View.VISIBLE), 100);
        } );

    }

    @Override
    public void updateMainDate(JSONObject jsonObject) {
        Logger.e(TAG, jsonObject.toJSONString());
    }

    @Override
    public void updateBottomDate(JSONObject jsonObject) {
        Logger.e(TAG, jsonObject.toJSONString());
    }

    @Override
    public void updatePresetDate(JSONObject jsonObject) {
        Logger.e(TAG, jsonObject.toJSONString());
    }
}
