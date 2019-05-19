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

import com.ads.abcbank.R;
import com.ads.abcbank.presenter.TempPresenter;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.view.IView;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;


public class Temp6Activity extends BaseActivity implements IView {
    private TempPresenter presenter;
    private JzvdStd videoplayer;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp6);
        videoplayer = findViewById(R.id.videoplayer);
        videoplayer.setUp("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
                , "", JzvdStd.SCREEN_NORMAL);
        Glide.with(this).load(R.drawable.app_icon_your_company).into(videoplayer.thumbImageView);

    }
    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.resetAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
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
