package com.ads.abcbank.xx.activity;

import android.os.Bundle;

import com.ads.abcbank.R;
import com.ads.abcbank.xx.BaseTempletActivity;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.utils.Constants;
import com.ads.abcbank.xx.utils.core.MaterialManager;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class TempH1Activity extends BaseTempletActivity {
    private static final String TAG = "TempH1Activity";

    @Override
    protected void initCtrls(Bundle savedInstanceState) {
        rvMarqueeView = findViewById(R.id.rvMarqueeView);
        sliderPlayer = findViewById(R.id.sliderPlayer);

        materialItemStatusListener = new MaterialManager.ItemStatusListener() {
            @Override
            public void onReady(List<PlayItem> items) {
                sliderPlayer.onReady(isMaterialManagerInitSuccessed(), items);
            }

            @Override
            public void onNewItemAdded(PlayItem item) {
                sliderPlayer.onNewItemAdded(isMaterialManagerInitSuccessed(), item);
            }

            @Override
            public void onNewItemsAdded(List<PlayItem> items) {
                sliderPlayer.onNewItemsAdded(isMaterialManagerInitSuccessed(), items);
            }

            @Override
            public void onWelcome(List<String> items) {
                sliderPlayer.onWelcome(items);
            }

            @Override
            public void onNewMsgAdded(List<String> msg, boolean isAppend) {
                sliderPlayer.onNewMsgAdded(msg, isAppend);
            }

            @Override
            public void onProgress(int code) {
                sliderPlayer.onProgress(isMaterialManagerInitSuccessed(), code);
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
    protected boolean isPresetSlider() {
        return false;
    }
}
