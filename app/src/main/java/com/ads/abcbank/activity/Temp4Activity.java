/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ads.abcbank.activity;

import android.os.Bundle;
import android.view.View;

import com.ads.abcbank.R;
import com.ads.abcbank.presenter.TempPresenter;
import com.ads.abcbank.utils.HandlerUtil;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.view.BaseTempFragment;
import com.ads.abcbank.view.IView;
import com.ads.abcbank.view.TempView;
import com.ads.abcbank.view.TempView2;
import com.alibaba.fastjson.JSONObject;

import cn.jzvd.JzvdStd;

public class Temp4Activity extends BaseActivity implements IView {
    private TempPresenter presenter;
    private TempView tvTemp;
    private TempView2 tvTemp2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp4);
        setiView(this);

        initViews();
        startServices(Utils.TYPES_TEMP4);

    }

    private void initViews() {
        tvTemp = findViewById(R.id.tv_temp);
        tvTemp2 = findViewById(R.id.tv_temp2);
        tvTemp.setType("H,L");
        tvTemp2.setType("N");
        tvTemp.getImage().setVisibility(View.GONE);
        tvTemp2.getImage().setVisibility(View.GONE);
        BaseTempFragment.tempView = tvTemp;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (tvTemp != null) {
            BaseTempFragment.tempView = tvTemp;
            tvTemp.setNeedUpdate(true);
        }
        HandlerUtil.postDelayed(new Runnable() {
            @Override
            public void run() {
                initViews();
                startServices(Utils.TYPES_TEMP4);
            }
        }, 100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            JzvdStd.goOnPlayOnPause();
        } catch (Exception e) {
        }
    }

    @Override
    public void updateMainDate(JSONObject jsonObject) {
        if (tvTemp != null) {
            tvTemp.setNeedUpdate(true);
        }
        if (tvTemp2 != null) {
            tvTemp2.setNeedUpdate(true);
        }
    }

    @Override
    public void updateBottomDate(JSONObject jsonObject) {

    }

    @Override
    public void updatePresetDate(JSONObject jsonObject) {
    }
}
