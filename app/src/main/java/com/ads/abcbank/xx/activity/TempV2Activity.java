package com.ads.abcbank.xx.activity;

import android.os.Bundle;
import android.view.View;

import com.ads.abcbank.R;
import com.ads.abcbank.xx.BaseTempletActivity;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.utils.Constants;
import com.ads.abcbank.xx.utils.core.MaterialManager;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class TempV2Activity extends BaseTempletActivity {
    private static final String TAG = "TempV2Activity";

    View v_set;

    @Override
    protected void initCtrls(Bundle savedInstanceState) {
        mainSliderPlayer = findViewById(R.id.sliderPlayer);
        rvMarqueeView = findViewById(R.id.rvMarqueeView);
        v_set = findViewById(R.id.v_set);

        materialItemStatusListener = new MaterialManager.ItemStatusListener() {
            @Override
            public void onReady(List<PlayItem> items) {
                mainSliderPlayer.onReady(isMaterialManagerInitSuccessed(), items);
            }

            @Override
            public void onNewItemAdded(PlayItem item) {
                mainSliderPlayer.onNewItemAdded(isMaterialManagerInitSuccessed(), item);
            }

            @Override
            public void onNewItemsAdded(List<PlayItem> items) {
                mainSliderPlayer.onNewItemsAdded(isMaterialManagerInitSuccessed(), items);
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
        return R.layout.activity_temp_v2_vertical_2;
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
        return true;
    }
}
