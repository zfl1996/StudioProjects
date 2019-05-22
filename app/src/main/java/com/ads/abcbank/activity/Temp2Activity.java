package com.ads.abcbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ads.abcbank.R;
import com.ads.abcbank.presenter.TempPresenter;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.view.IView;
import com.ads.abcbank.view.MarqueeVerticalTextView;
import com.ads.abcbank.view.MarqueeVerticalTextViewClickListener;
import com.alibaba.fastjson.JSONObject;

public class Temp2Activity extends BaseActivity implements IView {
    private TempPresenter presenter;
    private MarqueeVerticalTextView marqueeTv;
    private String [] textArrays = new String[]{"this is content No.1","this is content No.2","this is content No.3"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp2);
        marqueeTv = (MarqueeVerticalTextView) findViewById(R.id.marqueeTv);

        marqueeTv.setTextArraysAndClickListener(textArrays, new MarqueeVerticalTextViewClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public void updateMainDate(JSONObject jsonObject) {

    }

    @Override
    public void updateBottomDate(JSONObject jsonObject) {

    }

    @Override
    public void updatePresetDate(JSONObject jsonObject) {

    }
}
