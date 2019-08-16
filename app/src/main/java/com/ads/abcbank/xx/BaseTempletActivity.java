package com.ads.abcbank.xx;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.ads.abcbank.R;
import com.ads.abcbank.activity.ReInitActivity;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.view.AutoPollAdapter;
import com.ads.abcbank.view.AutoPollRecyclerView;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.view.ExitWindow;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.ui.view.SliderPlayer;
import com.ads.abcbank.xx.utils.Constants;
import com.ads.abcbank.xx.utils.core.MaterialManager;
import com.ads.abcbank.xx.utils.core.NetTaskManager;
import com.ads.abcbank.xx.utils.core.PlaylistManager;
import com.ads.abcbank.xx.utils.helper.DPIHelper;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public abstract class BaseTempletActivity extends AppCompatActivity {

    protected final String TAG = BaseActivity.class.getSimpleName();

    private NetTaskManager netTaskManager;
    private MaterialManager materialManager;
    protected MaterialManager.MaterialStatusListener materialStatusListener;
    protected AutoPollAdapter autoPollAdapter;
    protected AutoPollRecyclerView rvMarqueeView;
    protected SliderPlayer mainSliderPlayer;
    protected PlaylistManager playlistManager;

    protected Handler mainHandler = new Handler();
    protected Toast toast = null;
    protected ProgressDialog mProgressDialog;
    protected String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.getLayoutResourceId());

        mainSliderPlayer = findViewById(R.id.sliderPlayer);

        DPIHelper.setDensity(getResources().getDisplayMetrics().density);
        DPIHelper.setDefaultDisplay(getWindowManager().getDefaultDisplay());
        initCtrls(savedInstanceState);
        initPlayer();
    }

    protected void initCtrls(Bundle savedInstanceState){

        materialStatusListener = new MaterialManager.MaterialStatusListener() {

            @Override
            public void onPlayItemPrepared(List<PlayItem> items) {
                mainSliderPlayer.addPlayItems(items, true);
                playlistManager.addMaterialInfo(items);
            }

            @Override
            public void onRatePrepared(List<PlayItem> items, List<String> titles) {
                onRateDataPrepared(items, titles);
            }

            @Override
            public void onWelcomePrepared(List<String> msg, boolean isAppend, boolean isDefault) {
                onWelcomeLoaded(msg, isAppend, isDefault);
            }

            @Override
            public void onDownloadSucceed(Object notifyData) {
                netTaskManager.sendDownloadFinishNotify(notifyData);
            }

            @Override
            public void onProgress(int code) {
                if (code == Constants.SLIDER_PROGRESS_CODE_PRESET_OK ||
                    code == Constants.SLIDER_PROGRESS_CODE_PRESET_PRE)
                    onRateDataProgress(code);
                else
                    mainSliderPlayer.adjustWidgetStatus(isPresetLoaded(), isPlaylistLoaded(), code);
            }
        };
    }

    private void initPlayer() {
        netTaskManager = new NetTaskManager(this, new NetTaskManager.NetTaskListener() {
            @Override
            public void onPlaylistArrived(JSONObject jsonObject) {
                onPlaylistResponsed(jsonObject);
            }

            @Override
            public void onPresetArrived(JSONObject jsonObject) {
                onPresetResponsed(jsonObject);
            }
        });

        mainSliderPlayer.setDataStatusListener(() -> netTaskManager.initNetManager());

        // start data process...
        materialManager = new MaterialManager(this, materialStatusListener);
        materialManager.initManager(mainSliderPlayer.isIntegrationMode(), type);

        playlistManager = new PlaylistManager(this, (id, index) -> {
            mainHandler.post(() -> mainSliderPlayer.onItemOuttime(id, index));
        });
    }

    protected boolean isPlaylistLoaded() {
        return materialManager.isActionExecuted(Constants.MM_STATUS_KEY_PLAYLIST_LOADED);
    }

    protected boolean isPresetLoaded() {
        return materialManager.isActionExecuted(Constants.MM_STATUS_KEY_PRESET_LOADED);
    }

    protected void onPlaylistResponsed(JSONObject jsonObject) {
        reload(Constants.NET_MANAGER_DATA_PLAYLIST);
    }

    protected void onPresetResponsed(JSONObject jsonObject) {
        reload(Constants.NET_MANAGER_DATA_PRESET);
    }

    protected void onWelcomeLoaded(List<String> items, boolean isAppend, boolean isDefault) {
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

    protected void onRateDataPrepared(List<PlayItem> items, List<String> titles){
        mainSliderPlayer.addPlayItems(items, true);
        playlistManager.addMaterialInfo(items);
    }

    protected void onRateDataProgress(int code) {
        mainSliderPlayer.adjustWidgetStatus(isPresetLoaded(), isPlaylistLoaded(), code);
    }

    protected void reload(int resCode) {
        materialManager.reload(resCode);
    }

    protected abstract int getLayoutResourceId();

    private long mLastClickTime;
    private int clickTimes;

    public void toMainView(View view) {
        if ((System.currentTimeMillis() - mLastClickTime) > 1000) {
            mLastClickTime = System.currentTimeMillis();
            clickTimes = 0;
        } else {
            if (clickTimes < 1) {
                clickTimes++;
            } else {
                clickTimes = 0;
                startActivity(new Intent(this, ReInitActivity.class));
                finish();
            }
        }
    }

    private ExitWindow exitWindow;

    public void exitSys(View view) {
        if ((System.currentTimeMillis() - mLastClickTime) > 1000) {
            mLastClickTime = System.currentTimeMillis();
            clickTimes = 0;
        } else {
            if (clickTimes < 1) {
                clickTimes++;
            } else {
                clickTimes = 0;
                if (exitWindow != null) {
                    exitWindow.dismiss();
                }
                exitWindow = new ExitWindow(this);
                exitWindow.show();
            }
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (null != netTaskManager)
                netTaskManager.cancalTask();

            if (null != materialManager)
                materialManager.clear();
        } catch (Exception e) {
            Logger.e(TAG, "onDestroy:" + e.getMessage());
        }
    }
}
