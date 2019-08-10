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
import com.ads.abcbank.xx.ui.widget.RecyclerPagerView;
import com.ads.abcbank.xx.utils.Constants;

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

    public void setPageChangeListener(IPageChangeListener pageChangeListener) {
        this.pageChangeListener = pageChangeListener;
    }

    IPageChangeListener pageChangeListener;
    SliderMainAdapter sliderAdapter;
    DataStatusListener dataStatusListener;

    int displayMode = 0;
    public boolean isIntegrationMode(){
        return displayMode == 0;
    }

    public void setDataStatusListener(DataStatusListener dataStatusListener) {
        this.dataStatusListener = dataStatusListener;
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

    public void onReady(boolean isMaterialManagerInitSuccessed, List<PlayItem> items) {
        sliderAdapter.addItemDataAndRedraw(items);
        recyclerPagerView.setOnPageChangeListener(new RecyclerPagerView.OnPageChangeListener() {
            @Override
            public void onPageSelection(int position) {
                if (null != pageChangeListener) {
                    pageChangeListener.onPageSelection(position);
                }
            }
        });
        recyclerPagerView.startPlay();

        if (isMaterialManagerInitSuccessed)
            llProgress.setVisibility(GONE);
    }

    public void onNewItemsAdded(boolean isMaterialManagerInitSuccessed, List<PlayItem> items) {
        sliderAdapter.addItemDataAndPortionRedraw(items);
        recyclerPagerView.startPlay();

        if (isMaterialManagerInitSuccessed)
            llProgress.setVisibility(GONE);
    }

    public void onWelcome(List<String> items) {
        if (null != dataStatusListener)
            dataStatusListener.onWelcome(items, true, false);
    }

    public void onNewMsgAdded(List<String> msg, boolean isAppend) {
        if (null != dataStatusListener)
            dataStatusListener.onWelcome(msg, false, isAppend);
    }

    public void onProgress(boolean isMaterialManagerInitSuccessed, int code) {
        switch (code) {
            case Constants.SLIDER_PROGRESS_CODE_PRE:
                showHintMsg(isMaterialManagerInitSuccessed, "初始化播放列表");

                break;

            case Constants.SLIDER_PROGRESS_CODE_PRESET:
                showHintMsg(isMaterialManagerInitSuccessed, "准备汇率数据");

                break;

            case Constants.SLIDER_PROGRESS_CODE_OK:
                Logger.e(TAG, isMaterialManagerInitSuccessed ? "materialManager.isMaterialManagerInitSuccessed" : "not succ");
                showHintMsg(isMaterialManagerInitSuccessed, "初次启动，初始化环境");

                if (null != dataStatusListener)
                    dataStatusListener.onReady();

                break;

            default:
                break;
        }
    }

    private void showHintMsg(boolean isMaterialManagerInitSuccessed, String msg) {
        if (isMaterialManagerInitSuccessed) {
            llProgress.setVisibility(GONE);
        } else {
            txtHint.setText("初次启动，初始化环境");
        }
    }


//    public class PagerChangeListener extends RecyclerPagerView.OnPageChangeListener {
//        private int size;
//
//        public void setSize(int size) {
//            this.size += size;
//        }
//
//        public PagerChangeListener(int size) {
//            this.size = size;
//        }
//
//        @Override
//        public void onPageSelection(int position) {
//
////            int pos = size == 0 ? 0 : (position%size+1);
////
////            if (pos == size) {
////                txtHint.setText(pos + " / " + size + " / " + position);
////            }
//        }
//
//    }

    public interface IPageChangeListener {
        void onPageSelection(int position);
    }

    public interface DataStatusListener {
        void onWelcome(List<String> items, boolean isDefault, boolean isAppend);
        void onReady();
    }

}
