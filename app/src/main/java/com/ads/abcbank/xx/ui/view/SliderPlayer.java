package com.ads.abcbank.xx.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.ui.adapter.SliderMainAdapter;
import com.ads.abcbank.xx.ui.adapter.holder.SliderVideoHolder;
import com.ads.abcbank.xx.ui.widget.CusLinearLayoutManager;
import com.ads.abcbank.xx.ui.widget.RecyclerPagerView;
import com.ads.abcbank.xx.utils.Constants;
import com.ads.abcbank.xx.utils.helper.GuiHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SliderPlayer extends LinearLayout {
    static String TAG = "SliderPlayer";

    Context context;
    ImageView imgHolder;
    TextView txtHint;
    RecyclerPagerView recyclerPagerView;
    LinearLayout llProgress;

    IPageChangeListener pageChangeListener;
    SliderMainAdapter sliderAdapter;

    int displayMode = 0;
    public boolean isIntegrationMode(){
        return displayMode != 2;
    }

    public DisplayMode getDisplayMode() {
        if (displayMode == 0)
            return DisplayMode.Integration;
        else if (displayMode == 1)
            return DisplayMode.PlaylistOnly;
        else if (displayMode == 2)
            return DisplayMode.PresetOnly;

        return DisplayMode.Unknown;
    }

    public void setPageChangeListener(IPageChangeListener pageChangeListener) {
        this.pageChangeListener = pageChangeListener;
    }

    public SliderPlayer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        Map<Integer, Integer> rateResourceMap = new HashMap<>();

        TypedArray attrArr = context.obtainStyledAttributes(attrs, R.styleable.RecyclerPagerView);
        displayMode = attrArr.getInt(R.styleable.RecyclerPagerView_displayMode, 0);
        int saveLayoutId = attrArr.getResourceId(R.styleable.RecyclerPagerView_rate_save_layout, R.layout.widget_ui_slider_item_rateview_nor);
        int loanLayoutId = attrArr.getResourceId(R.styleable.RecyclerPagerView_rate_loan_layout, R.layout.widget_ui_slider_item_rateview_nor);
        int buyLayoutId = attrArr.getResourceId(R.styleable.RecyclerPagerView_rate_buy_layout, R.layout.widget_ui_slider_item_rateview_quad);
        int saveItemLayoutId = attrArr.getResourceId(R.styleable.RecyclerPagerView_rate_save_item_layout, R.layout.widget_ui_slider_item_rate_item_2);
        int loanItemLayoutId = attrArr.getResourceId(R.styleable.RecyclerPagerView_rate_loan_item_layout, R.layout.widget_ui_slider_item_rate_item_2);
        int buyItemLayoutId = attrArr.getResourceId(R.styleable.RecyclerPagerView_rate_buy_item_layout, R.layout.widget_ui_slider_item_rate_item_4);
        attrArr.recycle();

        rateResourceMap.put(Constants.SLIDER_HOLDER_RATE_SAVE, saveLayoutId);
        rateResourceMap.put(Constants.SLIDER_HOLDER_RATE_LOAN, loanLayoutId);
        rateResourceMap.put(Constants.SLIDER_HOLDER_RATE_BUY, buyLayoutId);
        rateResourceMap.put(Constants.SLIDER_HOLDER_RATE_SAVE_ITEM, saveItemLayoutId);
        rateResourceMap.put(Constants.SLIDER_HOLDER_RATE_LOAN_ITEM, loanItemLayoutId);
        rateResourceMap.put(Constants.SLIDER_HOLDER_RATE_BUY_ITEM, buyItemLayoutId);
        initPlayer(rateResourceMap);
    }

    private void initPlayer(Map<Integer, Integer> rateResourceMap) {
        // init player view
        View v = LayoutInflater.from(context).inflate(R.layout.widget_ui_sliderplayer, this, true);

        imgHolder = findViewById(R.id.imgHolder);
        txtHint = findViewById(R.id.txtHint);
        recyclerPagerView = findViewById(R.id.rpSlider);
        llProgress = findViewById(R.id.llProgress);

        // set imgholder if not narrow mode
        if (displayMode != 2) {
            imgHolder.setImageResource(GuiHelper.getBackgroundResource(context));
            imgHolder.setVisibility(View.VISIBLE);
        }

        recyclerPagerView.setDisplayMode(displayMode);

        // init relative to slider data
        sliderAdapter = new SliderMainAdapter(context);

//        sliderAdapter.setHasStableIds(true);
        sliderAdapter.setRateLayoutMap(rateResourceMap);
        sliderAdapter.setIntegrationPresetData( isIntegrationMode() );
        sliderAdapter.setVideoStatusListener(new SliderVideoHolder.VideoStatusListener() {
            @Override
            public void onStartPlay() {
                Logger.e(TAG, "onStartPlay");
                recyclerPagerView.pausePlay();
            }

            @Override
            public void onPlayFinish() {
                Logger.e(TAG, "onPlayFinish");
                recyclerPagerView.resumePlay();
            }
        });

        LinearLayoutManager lm = new CusLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerPagerView.setLayoutManager(lm);
        recyclerPagerView.setAdapter(sliderAdapter);
    }

    public void addPlayItems(List<PlayItem> items, boolean isPortionRedraw) {
        if (isPortionRedraw)
            sliderAdapter.addItemDataAndPortionRedraw(items);
        else
            sliderAdapter.addItemDataAndRedraw(items);

        if (null != pageChangeListener)
            recyclerPagerView.setOnPageChangeListener(new RecyclerPagerView.OnPageChangeListener() {
                @Override
                public void onPageSelection(int position) {
                        pageChangeListener.onPageSelection(position);
                }
            });

        switchPlayerStatus();
    }

    public void addRateItem(List<PlayItem> items, boolean isPortionRedraw, boolean isCreate) {
        Logger.e(TAG, "addRateItem--> isPortionRedraw:" + isPortionRedraw + " isCreate: " + isCreate);
        sliderAdapter.addRateData(items, isPortionRedraw, isCreate);

        if (displayMode == DisplayMode.PresetOnly.ordinal()) {
            if (null != pageChangeListener)
                recyclerPagerView.setOnPageChangeListener(new RecyclerPagerView.OnPageChangeListener() {
                    @Override
                    public void onPageSelection(int position) {
                        pageChangeListener.onPageSelection(position);
                    }
                });

        }

        switchPlayerStatus();
    }

    public void removePlayItems(List<String> ids) {
        sliderAdapter.removeItems(ids);

        switchPlayerStatus();
    }

    public void removeAllRateItem() {
        sliderAdapter.removeAllRateItems();

//        if (hasData())
//            recyclerPagerView.startPlay();
//        else
//            recyclerPagerView.pausePlay();
        switchPlayerStatus();
    }

    public void removeRateItem(Integer... mediaType) {
        sliderAdapter.removeInvalidRateItem(mediaType);

//        if (displayMode == DisplayMode.PresetOnly.ordinal()) {
//            if (hasData())
//                recyclerPagerView.startPlay();
//            else
//                recyclerPagerView.pausePlay();
//        } else
            switchPlayerStatus();
    }

    public void adjustWidgetStatus(boolean isPresetLoaded, boolean isPlaylistLoaded, int code) {
        switch (code) {
            case Constants.SLIDER_PROGRESS_CODE_PLAYLIST_PRE:
            case Constants.SLIDER_PROGRESS_CODE_PRESET_PRE:
                txtHint.setText(code == Constants.SLIDER_PROGRESS_CODE_PLAYLIST_PRE ?
                        "处理播放列表数据" : "准备汇率数据");

                break;

            case Constants.SLIDER_PROGRESS_CODE_PLAYLIST_EMPTY:
                txtHint.setText("初次加载，初始化数据中");

                break;

            case Constants.SLIDER_PROGRESS_CODE_PLAYLIST_OK:
            case Constants.SLIDER_PROGRESS_CODE_PRESET_OK:
                showHintMsg(isPresetLoaded, isPlaylistLoaded, code);

                break;

            default:
                break;
        }
    }

    public void onItemOuttime(String id, int index) {
        sliderAdapter.removeOuttimeItem(id, index);
    }

    private void showHintMsg(boolean isPresetLoaded, boolean isPlaylistLoaded, int code) {
        DisplayMode displayMode = getDisplayMode();
        boolean showContent = false;
        if (displayMode == DisplayMode.Integration) {
            if ((isPresetLoaded || isPlaylistLoaded) && sliderAdapter.getRealItemCount() > 0)
                showContent = true;
        } else if (displayMode == DisplayMode.PlaylistOnly) {
            if (isPlaylistLoaded && sliderAdapter.getRealItemCount() > 0)
                showContent = true;
        } else if (displayMode == DisplayMode.PresetOnly) {
            if (isPresetLoaded && sliderAdapter.getRealItemCount() > 0)
                showContent = true;
        }

        if (showContent) {
            llProgress.setVisibility(GONE);
            imgHolder.setVisibility(GONE);
            recyclerPagerView.setVisibility(VISIBLE);
        }
    }

    private void switchPlayerStatus() {
        Logger.e(TAG, "switchPlayerStatus-->displayMode:" + displayMode + ", count:" + (sliderAdapter.getNoPresetCount(displayMode == DisplayMode.PlaylistOnly.ordinal())));

        imgHolder.setVisibility( sliderAdapter.getRealItemCount() > 0 ? GONE : VISIBLE );
        recyclerPagerView.setVisibility(sliderAdapter.getRealItemCount() > 0 ? VISIBLE : GONE);

        if (hasData())
            recyclerPagerView.startPlay();
        else
            recyclerPagerView.pausePlay();
    }

    private boolean hasData() {
//        return sliderAdapter.getNoPresetCount(displayMode == DisplayMode.Integration.ordinal()) > 1;
        return sliderAdapter.getRealItemCount() > 1;
    }

    public interface IPageChangeListener {
        void onPageSelection(int position);
    }

    public enum DisplayMode {
        Integration,
        PlaylistOnly,
        PresetOnly,
        Unknown
    }

}
