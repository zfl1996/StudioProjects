package com.ads.abcbank.xx.activity;

import android.os.Bundle;

import com.ads.abcbank.R;
import com.ads.abcbank.xx.BaseTempletActivity;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.ui.view.SliderPlayer;
import com.ads.abcbank.xx.utils.Constants;
import com.ads.abcbank.xx.utils.core.MaterialManager;
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

        materialItemStatusListener = new MaterialManager.ItemStatusListener() {
            @Override
            public void onReady(List<PlayItem> items) {
                mainSliderPlayer.onReady(isMaterialManagerInitSuccessed(), items);
            }

            @Override
            public void onItemPrepared(List<PlayItem> items) {
                mainSliderPlayer.onNewItemsAdded(isMaterialManagerInitSuccessed(), items);
            }

            @Override
            public void onRate(List<PlayItem> items) {
                presetSliderPlayer.onReady(true, items);
//                mainSliderPlayer.onNewItemsAdded(isMaterialManagerInitSuccessed(), items);
            }

            @Override
            public void onWelcome(List<String> items) {
                mainSliderPlayer.onWelcome(items);
            }

            @Override
            public void onNewMsgAdded(List<String> msg, boolean isAppend) {
                mainSliderPlayer.onNewMsgAdded(msg, isAppend);
            }

            @Override
            public void onProgress(int code) {
                mainSliderPlayer.onProgress(isMaterialManagerInitSuccessed(), code);
            }
        };
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_temp_v2_horizontal_1;
    }

    @Override
    protected void onPlaylistLoaded(JSONObject jsonObject) {
        reload(Constants.NET_MANAGER_DATA_PLAYLIST);
    }

    @Override
    protected void onPresetLoaded(JSONObject jsonObject) {
        reload(Constants.NET_MANAGER_DATA_PRESET);
    }

    @Override
    protected boolean isIntegratedSlider() {
        return false;
    }
}
