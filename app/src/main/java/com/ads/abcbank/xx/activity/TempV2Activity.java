package com.ads.abcbank.xx.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.ads.abcbank.R;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.view.AutoPollAdapter;
import com.ads.abcbank.view.AutoPollRecyclerView;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.view.IView;
import com.ads.abcbank.xx.ui.view.SliderPlayer;
import com.ads.abcbank.xx.utils.Constants;
import com.ads.abcbank.xx.utils.core.NetTaskManager;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class TempV2Activity extends BaseActivity implements IView {
    private static final String TAG = "TempV2Activity";

    SliderPlayer sliderPlayer;
    AutoPollAdapter autoPollAdapter;
    AutoPollRecyclerView rvMarqueeView;
    View v_set;

    NetTaskManager netTaskManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_v2);
        setiView(this);

        initCtrls();
    }

    private void initCtrls() {
        sliderPlayer = findViewById(R.id.sliderPlayer);
        rvMarqueeView = findViewById(R.id.rvMarqueeView);
        v_set = findViewById(R.id.v_set);

        sliderPlayer.setDataStatusListener(new SliderPlayer.DataStatusListener() {
            @Override
            public void onWelcome(List<String> items, boolean isDefault, boolean isAppend) {
                if (isDefault) {
                    autoPollAdapter = new AutoPollAdapter(TempV2Activity.this, items);
                    rvMarqueeView.setLayoutManager(new LinearLayoutManager(TempV2Activity.this, LinearLayoutManager.HORIZONTAL, false));
                    rvMarqueeView.setAdapter(autoPollAdapter);
                    rvMarqueeView.start();

                    new Handler().postDelayed(() -> rvMarqueeView.setVisibility(View.VISIBLE), 100);
                } else {
                    autoPollAdapter.addItemDataAndRedraw(items, isAppend);
                }
            }

            @Override
            public void onReady() {
                netTaskManager.initNetManager();
            }
        });


        netTaskManager = new NetTaskManager(this, new NetTaskManager.NetTaskListener() {
            @Override
            public void onPlaylistArrived(JSONObject jsonObject) {
                if (null != sliderPlayer)
                    sliderPlayer.reload(Constants.NET_MANAGER_DATA_PLAYLIST);
            }

            @Override
            public void onPresetArrived(JSONObject jsonObject) {
                if (null != sliderPlayer)
                    sliderPlayer.reload(Constants.NET_MANAGER_DATA_PRESET);
            }
        });

//        netTaskManager.initNetManager();

    }

    @Override
    public void updateMainDate(JSONObject jsonObject) {
//        sliderPlayer.reload(Constants.);
        Logger.e(TAG, jsonObject.toJSONString());
    }

    @Override
    public void updateBottomDate(JSONObject jsonObject) {
//        sliderPlayer.reload();
        Logger.e(TAG, jsonObject.toJSONString());
    }

    @Override
    public void updatePresetDate(JSONObject jsonObject) {
//        sliderPlayer.reload();
        Logger.e(TAG, jsonObject.toJSONString());
    }
}
