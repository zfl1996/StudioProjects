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
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.ui.adapter.SliderAdapter;
import com.ads.abcbank.xx.ui.widget.RecyclerPagerView;
import com.ads.abcbank.xx.utils.core.MaterialManager;

import java.util.List;

public class SliderPlayer extends LinearLayout {
    Context context;
    ImageView imgHolder;
    TextView txtHint;
    RecyclerPagerView rpSlider;

//    List<PlayItem> allPlayItems = new ArrayList<>();
    MaterialManager materialManager;
    SliderAdapter sliderAdapter;

    public SliderPlayer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        initPlayer();
    }

    private void initPlayer() {
        View v = LayoutInflater.from(context).inflate(R.layout.widget_ui_sliderplayer, this, true);

        imgHolder = findViewById(R.id.imgHolder);
        rpSlider = findViewById(R.id.rpSlider);
        txtHint = findViewById(R.id.txtHint);

        materialManager = new MaterialManager(context, itemStatusListener);
        materialManager.initManager();
    }

    public void start(List<PlayItem> items) {
        sliderAdapter = new SliderAdapter(context);

        LinearLayoutManager lm = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rpSlider.setLayoutManager(lm);
        rpSlider.setAdapter(sliderAdapter);

        sliderAdapter.addItemDataAndRedraw(items);
        rpSlider.setOnPageChangeListener(new PagerChangeListener(items.size()));
        rpSlider.startPlay();
    }


    MaterialManager.ItemStatusListener itemStatusListener = new MaterialManager.ItemStatusListener() {
        @Override
        public void onReady(List<PlayItem> items) {
            start(items);
        }

        @Override
        public void onNewItemAdded(PlayItem item) {
            sliderAdapter.addItemDataAndRedraw(item);
            rpSlider.startPlay();
        }
    };

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

            int pos = size == 0 ? 0 : (position%size+1);

            if (pos == size) {
                txtHint.setText(pos + " / " + size + " / " + position);
            }
        }

    }


}
