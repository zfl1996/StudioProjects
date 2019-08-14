package com.ads.abcbank.xx.ui.view;

import android.content.Context;
import android.content.res.Configuration;
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
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.ui.adapter.SliderMainAdapter;
import com.ads.abcbank.xx.ui.adapter.holder.SliderVideoHolder;
import com.ads.abcbank.xx.ui.widget.RecyclerPagerView;
import com.ads.abcbank.xx.utils.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class SliderPlayer extends LinearLayout {
    static String TAG = "SliderPlayer";

    Context context;
    ImageView imgHolder;
    TextView txtHint;
    RecyclerPagerView recyclerPagerView;
    LinearLayout llProgress;

    IPageChangeListener pageChangeListener;
    SliderMainAdapter sliderAdapter;
    DataStatusListener dataStatusListener;

    int displayMode = 0;
    public boolean isIntegrationMode(){
        return displayMode == 0;
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

    public void setDataStatusListener(DataStatusListener dataStatusListener) {
        this.dataStatusListener = dataStatusListener;
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
            Configuration conf = getResources().getConfiguration();

            imgHolder.setImageResource(conf.orientation == ORIENTATION_LANDSCAPE ? R.mipmap.bg_land : R.mipmap.bg_port);
            imgHolder.setVisibility(View.VISIBLE);
        }

        recyclerPagerView.setDisplayMode(displayMode);

        // init relative to slider data
        sliderAdapter = new SliderMainAdapter(context);
        sliderAdapter.setRateResourceMap(rateResourceMap);
        sliderAdapter.setIntegrationPresetData( isIntegrationMode() );
        sliderAdapter.setVideoStatusListener(new SliderVideoHolder.VideoStatusListener() {
            @Override
            public void onStartPlay() {
                recyclerPagerView.pausePlay();
            }

            @Override
            public void onPlayFinish() {
                recyclerPagerView.resumePlay();
            }
        });

        LinearLayoutManager lm = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerPagerView.setLayoutManager(lm);
        recyclerPagerView.setAdapter(sliderAdapter);
    }

    public void addPlayItems(/*boolean isMaterialManagerInitSuccessed, */List<PlayItem> items, boolean isPortionRedraw) {
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
        recyclerPagerView.startPlay();

//        if (isMaterialManagerInitSuccessed)
//            llProgress.setVisibility(GONE);
    }

//    public void onNewItemsAdded(boolean isMaterialManagerInitSuccessed, List<PlayItem> items) {
//        sliderAdapter.addItemDataAndPortionRedraw(items);
//
//        recyclerPagerView.startPlay();
//
//        if (isMaterialManagerInitSuccessed)
//            llProgress.setVisibility(GONE);
//    }

//    public void onWelcome(List<String> items) {
//        if (null != dataStatusListener)
//            dataStatusListener.onWelcome(items, true, false);
//    }
//
//    public void onNewMsgAdded(List<String> msg, boolean isAppend) {
//        if (null != dataStatusListener)
//            dataStatusListener.onWelcome(msg, false, isAppend);
//    }

//    public void addWelcomeItems(List<String> msg, boolean isAppend, boolean isDefault) {
//        if (null != dataStatusListener)
//            dataStatusListener.onWelcome(msg, isDefault, isAppend);
//    }

    public void adjustWidgetStatus(boolean isPresetLoaded, boolean isPlaylistLoaded, int code) {
        switch (code) {
            case Constants.SLIDER_PROGRESS_CODE_PLAYLIST_PRE:
            case Constants.SLIDER_PROGRESS_CODE_PRESET_PRE:
                txtHint.setText(code == Constants.SLIDER_PROGRESS_CODE_PLAYLIST_PRE ?
                        "处理播放列表数据" : "准备汇率数据");

                //ToDo......
                if (null != dataStatusListener)
                    dataStatusListener.onReady();

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
        if (displayMode == DisplayMode.Integration) {
            if (isPresetLoaded || isPlaylistLoaded) {
                llProgress.setVisibility(GONE);
                imgHolder.setVisibility(GONE);
            }
        } else if (displayMode == DisplayMode.PlaylistOnly) {
            if (isPlaylistLoaded) {
                llProgress.setVisibility(GONE);
                imgHolder.setVisibility(GONE);
            }
        } else if (displayMode == DisplayMode.PresetOnly) {
            if (isPresetLoaded) {
                llProgress.setVisibility(GONE);
                imgHolder.setVisibility(GONE);
            }
        }

//        if (isMaterialManagerInitSuccessed) {
//            llProgress.setVisibility(GONE);
//            imgHolder.setVisibility(GONE);
//        } else {
//            txtHint.setText("初次启动，初始化环境");
//        }
    }

    public interface IPageChangeListener {
        void onPageSelection(int position);
    }

    public interface DataStatusListener {
//        void onWelcome(List<String> items, boolean isDefault, boolean isAppend);
        void onReady();
    }

    public enum DisplayMode {
        Integration,
        PlaylistOnly,
        PresetOnly,
        Unknown
    }

}
