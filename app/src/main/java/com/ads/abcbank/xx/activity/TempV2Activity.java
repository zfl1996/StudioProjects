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

import java.util.ArrayList;

public class TempV2Activity extends BaseActivity implements IView {
    private static final String TAG = "TempV2Activity";

    SliderPlayer sliderPlayer;
    AutoPollAdapter autoPollAdapter;
    AutoPollRecyclerView rvMarqueeView;
    View v_set;
//    MarqueeView rvMarqueeView;

    ArrayList<String> data = new ArrayList<String>() {
        {
            add("中国农业银行欢迎您");
            add("中国农业银行欢迎您");
            add("中国农业银行欢迎您");
            add("中国农业银行欢迎您");
            add("中国农业银行欢迎您");
            add("中国农业银行欢迎您");
        }
    };

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

        autoPollAdapter = new AutoPollAdapter(TempV2Activity.this, data);
        rvMarqueeView.setLayoutManager(new LinearLayoutManager(TempV2Activity.this, LinearLayoutManager.HORIZONTAL, false));
        rvMarqueeView.setAdapter(autoPollAdapter);
        rvMarqueeView.start();

        new Handler().postDelayed(() -> rvMarqueeView.setVisibility(View.VISIBLE), 120);

//        LinearLayoutManager lm=new LinearLayoutManager(this);
//        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
//        rvMarqueeView.setLayoutManager(lm);
//        rvMarqueeView.setAdapter(new MarqueeView.InnerAdapter(data,this));
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
