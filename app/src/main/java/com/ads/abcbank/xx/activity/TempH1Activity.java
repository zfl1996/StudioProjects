package com.ads.abcbank.xx.activity;

import android.os.Bundle;

import com.ads.abcbank.R;
import com.ads.abcbank.xx.BaseTempletActivity;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.ui.view.SliderPlayer;
import com.ads.abcbank.xx.utils.Constants;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class TempH1Activity extends BaseTempletActivity {
    private static final String TAG = "TempH1Activity";

    SliderPlayer presetSliderPlayer;

    @Override
    protected void initCtrls(Bundle savedInstanceState) {
        rvMarqueeView = findViewById(R.id.rvMarqueeView);
        mainSliderPlayer = findViewById(R.id.sliderPlayer);

        presetSliderPlayer = findViewById(R.id.presetSliderPlayer);
        presetSliderPlayer.setIsIntegrationMode(false);

        super.initCtrls(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_temp_v2_horizontal_1;
    }

    @Override
    protected void onRateDataPrepare(List<PlayItem> items){
        presetSliderPlayer.onReady(true, items);
    }

    @Override
    protected boolean isIntegratedSlider() {
        return false;
    }
}
