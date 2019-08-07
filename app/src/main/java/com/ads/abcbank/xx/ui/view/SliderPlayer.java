package com.ads.abcbank.xx.ui.view;

import android.content.Context;
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

import java.util.List;

public class SliderPlayer extends LinearLayout {
    static String TAG = "SliderPlayer";

    Context context;
    ImageView imgHolder;
    TextView txtHint;
    RecyclerPagerView rpSlider;
    LinearLayout llProgress;

    SliderMainAdapter sliderAdapter;
    DataStatusListener dataStatusListener;

    public void setDataStatusListener(DataStatusListener dataStatusListener) {
        this.dataStatusListener = dataStatusListener;
    }


    public SliderPlayer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        initPlayer();
    }

    private void initPlayer() {
        // init player view
        View v = LayoutInflater.from(context).inflate(R.layout.widget_ui_sliderplayer, this, true);

        imgHolder = findViewById(R.id.imgHolder);
        txtHint = findViewById(R.id.txtHint);
        rpSlider = findViewById(R.id.rpSlider);
        llProgress = findViewById(R.id.llProgress);

        // init relative to slider data
        sliderAdapter = new SliderMainAdapter(context);
        sliderAdapter.setPlayStatusListener(new SliderVideoHolder.PlayStatusListener() {
            @Override
            public void onStarted() {
                rpSlider.pausePlay();
            }

            @Override
            public void onEnded() {
                rpSlider.resumePlay();
            }
        });

        LinearLayoutManager lm = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rpSlider.setLayoutManager(lm);
        rpSlider.setAdapter(sliderAdapter);
    }

    public void onReady(boolean isMaterialManagerInitSuccessed, List<PlayItem> items) {
        sliderAdapter.addItemDataAndRedraw(items);
        rpSlider.setOnPageChangeListener(new PagerChangeListener(items.size()));
        rpSlider.startPlay();

        if (isMaterialManagerInitSuccessed)
            llProgress.setVisibility(GONE);
    }

    public void onNewItemAdded(boolean isMaterialManagerInitSuccessed, PlayItem item) {
        sliderAdapter.addItemDataAndRedraw(item);
        rpSlider.startPlay();

        if (isMaterialManagerInitSuccessed)
            llProgress.setVisibility(GONE);
    }

    public void onNewItemsAdded(boolean isMaterialManagerInitSuccessed, List<PlayItem> items) {
        sliderAdapter.addItemDataAndPortionRedraw(items);
        rpSlider.startPlay();

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
                txtHint.setText("初始化播放列表");

                break;

            case Constants.SLIDER_PROGRESS_CODE_PRESET:
                txtHint.setText("准备汇率数据");

                break;

            case Constants.SLIDER_PROGRESS_CODE_OK:
                Logger.e(TAG, isMaterialManagerInitSuccessed ? "materialManager.isMaterialManagerInitSuccessed" : "not succ");
                if (isMaterialManagerInitSuccessed) {
                    llProgress.setVisibility(GONE);
                } else {
                    txtHint.setText("初次启动，初始化环境");
                }

                if (null != dataStatusListener)
                    dataStatusListener.onReady();

                break;

            default:
                break;
        }
    }


    public class PagerChangeListener extends RecyclerPagerView.OnPageChangeListener {
        private int size;

        public void setSize(int size) {
            this.size += size;
        }

        public PagerChangeListener(int size) {
            this.size = size;
        }

        @Override
        public void onPageSelection(int position) {

//            int pos = size == 0 ? 0 : (position%size+1);
//
//            if (pos == size) {
//                txtHint.setText(pos + " / " + size + " / " + position);
//            }
        }

    }

    public interface DataStatusListener {
        void onWelcome(List<String> items, boolean isDefault, boolean isAppend);
        void onReady();
    }

}
