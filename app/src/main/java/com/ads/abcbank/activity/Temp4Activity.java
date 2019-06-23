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
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.view.IView;
import com.ads.abcbank.view.TempView;
import com.ads.abcbank.view.TempView2;
import com.alibaba.fastjson.JSONObject;

public class Temp4Activity extends BaseActivity implements IView {
    private TempPresenter presenter;
    private TempView tvTemp;
    private TempView2 tvTemp2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp4);
        tvTemp = findViewById(R.id.tv_temp);
        tvTemp2 = findViewById(R.id.tv_temp2);

        tvTemp.setShowStaticData(true);
        tvTemp2.setShowStaticData(true);

        tvTemp.setType("H,L");
        tvTemp2.setType("N");

        tvTemp.getImage().setVisibility(View.GONE);
        tvTemp2.getImage().setVisibility(View.GONE);
        tvTemp.setImageSrc(R.mipmap.v_wkqk);
        tvTemp2.setImageSrc(R.mipmap.v_zysys);
        setiView(this);
        startServices("H,L,N");
    }

    @Override
    public void updateMainDate(JSONObject jsonObject) {
        tvTemp.setNeedUpdate(true);
    }

    @Override
    public void updateBottomDate(JSONObject jsonObject) {

    }

    @Override
    public void updatePresetDate(JSONObject jsonObject) {

    }
}
