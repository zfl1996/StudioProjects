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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.ads.abcbank.R;
import com.ads.abcbank.presenter.TempPresenter;
import com.ads.abcbank.utils.QRCodeUtil;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.view.IView;
import com.ads.abcbank.view.TempView;
import com.alibaba.fastjson.JSONObject;

public class Temp3Activity extends BaseActivity implements IView {
    private TempPresenter presenter;
    private TempView tvTemp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp3);
        tvTemp = findViewById(R.id.tv_temp);
        tvTemp.setShowStaticData(true);
        tvTemp.setType("M,H,P,N,E,L,R");
        tvTemp.getImage().setVisibility(View.GONE);
//        tvTemp.setImageSrc(R.mipmap.v_sxdhb);
//        initQRCode();
    }

//    private void initQRCode() {
//        ImageView iv = (ImageView) findViewById(R.id.iv);
//        Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.movie);
//        Bitmap qrCodeBitmap = QRCodeUtil.createQRCodeBitmap("http://www.abchina.com/cn/", 480,
//                "UTF-8", "H", "4", Color.BLACK, Color.WHITE,
//                null, logoBitmap, 0.2F);
//        iv.setImageBitmap(qrCodeBitmap);
//    }

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
