package com.ads.abcbank.activity;

import android.os.Bundle;

import com.ads.abcbank.R;
import com.ads.abcbank.presenter.TempPresenter;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.view.IView;
import com.alibaba.fastjson.JSONObject;

public class Temp2Activity extends BaseActivity implements IView {
    private TempPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp2);

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
