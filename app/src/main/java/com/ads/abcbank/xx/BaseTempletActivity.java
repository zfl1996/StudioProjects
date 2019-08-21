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
import com.ads.abcbank.xx.utils.helper.DPIHelper;

import java.util.List;

public abstract class BaseTempletActivity extends AppCompatActivity {

    protected final String TAG = BaseActivity.class.getSimpleName();

    private MaterialManager materialManager;
    protected MaterialManager.MaterialStatusListener materialStatusListener;
    protected AutoPollAdapter autoPollAdapter;
    protected AutoPollRecyclerView rvMarqueeView;
    protected SliderPlayer mainSliderPlayer;
//    protected PlaylistManager playlistManager;

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
            public void onPlayItemPrepared(List<PlayItem> items, boolean isImportant) {
                mainSliderPlayer.addPlayItems(items, !isImportant);
//                playlistManager.addMaterialInfo(items);
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
            public void onPlayItemRemoved(List<String> ids) {
                mainSliderPlayer.removePlayItems(ids);
            }

            @Override
            public void onRateItemRemoved(Integer... mediaTypes) {
                onRateRemoved(mediaTypes);
            }

            @Override
            public void onNetError(int code) {
                mainHandler.post(() -> onNetworkError(code));
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
        // start data process...
        materialManager = new MaterialManager(this, materialStatusListener);
        materialManager.initManager(mainSliderPlayer.isIntegrationMode(), type);

//        playlistManager = new PlaylistManager(this, ids -> {
//            materialManager.removeTimeoutItem(ids);
//            mainHandler.post( () -> mainSliderPlayer.removePlayItems(ids) );
//        });
    }

    protected boolean isPlaylistLoaded() {
//        return materialManager.isActionExecuted(Constants.MM_STATUS_KEY_PLAYLIST_LOADED);
        return materialManager.hasMaterials();
    }

    protected boolean isPresetLoaded() {
        return materialManager.isActionExecuted(Constants.MM_STATUS_KEY_PRESET_LOADED);
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
        mainSliderPlayer.addRateItem(items, true, isPresetLoaded());
//        mainSliderPlayer.addPlayItems(items, true);
//        playlistManager.addMaterialInfo(items);
    }

    protected void onRateDataProgress(int code) {
        mainSliderPlayer.adjustWidgetStatus(isPresetLoaded(), isPlaylistLoaded(), code);
    }

    protected void onNetworkError(int code) {
        Logger.e(TAG, "onNetworkError:" + code);
        mainSliderPlayer.removeAllRateItem();
    }

    protected void onRateRemoved(Integer... mediaTypes) {
        mainSliderPlayer.removeRateItem(mediaTypes);
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
                reInit();
            }
        }
    }

    public void reInit() {
        materialManager.quit();
        materialManager.clearResource();

        startActivity(new Intent(this, ReInitActivity.class));
        finish();
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
            if (null != materialManager)
                materialManager.quit();
        } catch (Exception e) {
            Logger.e(TAG, "onDestroy:" + e.getMessage());
        }
    }
}
