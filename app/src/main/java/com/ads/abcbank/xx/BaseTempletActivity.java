package com.ads.abcbank.xx;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.ads.abcbank.view.AutoPollAdapter;
import com.ads.abcbank.view.AutoPollRecyclerView;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.xx.ui.view.SliderPlayer;
import com.ads.abcbank.xx.utils.core.MaterialManager;
import com.ads.abcbank.xx.utils.core.NetTaskManager;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public abstract class BaseTempletActivity extends AppCompatActivity {

    protected final String TAG = BaseActivity.class.getSimpleName();

    private NetTaskManager netTaskManager;
    private MaterialManager materialManager;
    protected MaterialManager.ItemStatusListener materialItemStatusListener;
    protected AutoPollAdapter autoPollAdapter;
    protected AutoPollRecyclerView rvMarqueeView;
    protected SliderPlayer mainSliderPlayer;

    protected AppCompatActivity activity;
    protected Handler mainHandler = new Handler();
    protected Toast toast = null;
    protected ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.getLayoutResourceId());

        initCtrls(savedInstanceState);
        initPlayer();
    }

    private void initPlayer() {
        netTaskManager = new NetTaskManager(this, new NetTaskManager.NetTaskListener() {
            @Override
            public void onPlaylistArrived(JSONObject jsonObject) {
                onPlaylistLoaded(jsonObject);
            }

            @Override
            public void onPresetArrived(JSONObject jsonObject) {
                onPresetLoaded(jsonObject);
            }
        });

        if (null != mainSliderPlayer) {
            mainSliderPlayer.setIsIntegrationPresetData(isIntegratedSlider());
            mainSliderPlayer.setDataStatusListener(new SliderPlayer.DataStatusListener() {
                @Override
                public void onWelcome(List<String> items, boolean isDefault, boolean isAppend) {
                    onWelcomeLoaded(items, isDefault, isAppend);
                }

                @Override
                public void onReady() {
                    netTaskManager.initNetManager();
                }
            });
        }

        // start data process...
        materialManager = new MaterialManager(this, materialItemStatusListener);
        materialManager.initManager(isIntegratedSlider());
    }

    protected void reload(int resCode) {
        materialManager.reload(resCode);
    }

    protected boolean isMaterialManagerInitSuccessed() {
        return materialManager.isInitSuccessed();
    }

    protected void onWelcomeLoaded(List<String> items, boolean isDefault, boolean isAppend) {
        if (isDefault) {
            autoPollAdapter = new AutoPollAdapter(BaseTempletActivity.this, items);
            rvMarqueeView.setLayoutManager(new LinearLayoutManager(BaseTempletActivity.this,
                    LinearLayoutManager.HORIZONTAL, false));
            rvMarqueeView.setAdapter(autoPollAdapter);
            rvMarqueeView.start();

            mainHandler.postDelayed(() -> rvMarqueeView.setVisibility(View.VISIBLE), 100);
        } else {
            autoPollAdapter.addItemDataAndRedraw(items, isAppend);
        }
    }

    protected abstract void initCtrls(Bundle savedInstanceState);
    protected abstract int getLayoutResourceId();
    protected abstract void onPlaylistLoaded(JSONObject jsonObject);
    protected abstract void onPresetLoaded(JSONObject jsonObject);
    protected abstract boolean isIntegratedSlider();


}
