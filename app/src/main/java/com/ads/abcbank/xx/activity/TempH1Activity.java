package com.ads.abcbank.xx.activity;

import android.os.Bundle;

import com.ads.abcbank.R;
import com.ads.abcbank.xx.BaseTempletActivity;
import com.ads.abcbank.xx.utils.Constants;
import com.alibaba.fastjson.JSONObject;

public class TempH1Activity extends BaseTempletActivity {
    private static final String TAG = "TempH1Activity";

    @Override
    protected void initCtrls(Bundle savedInstanceState) {
        rvMarqueeView = findViewById(R.id.rvMarqueeView);
        sliderPlayer = findViewById(R.id.sliderPlayer);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_temp_v2_horizontal_1;
    }

    @Override
    protected void onPlaylistLoaded(JSONObject jsonObject) {

    }

    @Override
    protected void onPresetLoaded(JSONObject jsonObject) {
        if (null != sliderPlayer)
            sliderPlayer.reload(Constants.NET_MANAGER_DATA_PRESET);
    }
}
