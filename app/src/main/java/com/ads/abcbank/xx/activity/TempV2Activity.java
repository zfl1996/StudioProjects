package com.ads.abcbank.xx.activity;

import android.os.Bundle;
import android.view.View;

import com.ads.abcbank.R;
import com.ads.abcbank.xx.BaseTempletActivity;
import com.ads.abcbank.xx.utils.Constants;
import com.alibaba.fastjson.JSONObject;

public class TempV2Activity extends BaseTempletActivity {
    private static final String TAG = "TempV2Activity";

    View v_set;

    @Override
    protected void initCtrls(Bundle savedInstanceState) {
        sliderPlayer = findViewById(R.id.sliderPlayer);
        rvMarqueeView = findViewById(R.id.rvMarqueeView);
        v_set = findViewById(R.id.v_set);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_temp_v2_vertical_2;
    }

    @Override
    protected void onPlaylistLoaded(JSONObject jsonObject) {
        if (null != sliderPlayer)
            sliderPlayer.reload(Constants.NET_MANAGER_DATA_PLAYLIST);
    }

    @Override
    protected void onPresetLoaded(JSONObject jsonObject) {
        if (null != sliderPlayer)
            sliderPlayer.reload(Constants.NET_MANAGER_DATA_PRESET);
    }
}
