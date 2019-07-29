package com.ads.abcbank.xx.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ads.abcbank.R;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.ui.adapter.BannerAdapter;
import com.ads.abcbank.xx.ui.widget.RecyclerPagerView;
import com.ads.abcbank.xx.utils.core.MaterialManager;

import java.util.ArrayList;
import java.util.List;

public class SliderPlayer extends LinearLayout {
    Context context;
    ImageView imgHolder;
    RecyclerPagerView rpSlider;
    MaterialManager materialManager;
    List<PlayItem> allPlayItems = new ArrayList<>();

    public SliderPlayer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        initPlayer();
    }

    private void initPlayer() {
        View v = LayoutInflater.from(context).inflate(R.layout.widget_ui_sliderplayer, this, true);

        imgHolder = findViewById(R.id.imgHolder);
        rpSlider = findViewById(R.id.rpSlider);
        materialManager = new MaterialManager(context, mainHandler);
    }

    public void start() {
        BannerAdapter bannerAdapter = new BannerAdapter(context);

        LinearLayoutManager lm = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rpSlider.setLayoutManager(lm);
        rpSlider.setAdapter(bannerAdapter);

//        bannerAdapter.setDataSource(Arrays.asList( cacheInfo.allCachePaths));
//        rpv.setOnPageChangeListener(new PdfContFragment.PagerChangeListener(cacheInfo.pageCount));
//        rpv.startPlay();
    }

    Handler mainHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                default:
                    break;
            }
        }

    };


}
