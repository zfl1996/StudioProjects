package com.ads.abcbank.xx.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.xx.ui.widget.RecyclerPagerView;
import com.ads.abcbank.xx.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class SliderPlayer extends LinearLayout {
    Context context;
    ImageView imgHolder;
    RecyclerPagerView rpSlider;

    // bll data
    String type;
    List<PlaylistBodyBean> playlist = new ArrayList<>();

    // worker thread
    HandlerThread playerThread;
    Handler playerHandler;


    public SliderPlayer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        initCtrls();
        initPlayerStatus();
    }

    private void initCtrls() {
        View v = LayoutInflater.from(context).inflate(R.layout.widget_ui_sliderplayer, this, true);

        imgHolder = findViewById(R.id.imgHolder);
        rpSlider = findViewById(R.id.rpSlider);
    }

    private void initPlayerStatus() {
        playerThread = new HandlerThread("playerThread");
        playerThread.start();

        playerHandler = new Handler(playerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case Constants.SLIDER_STATUS_CODE_INIT:
                        loadPlaylist();

                        break;

                    default:
                        break;
                }
            }
        };
    }

    private void loadPlaylist() {
        String json = Utils.get(context, Utils.KEY_PLAY_LIST, "").toString();
        if (!TextUtils.isEmpty(json)) {

        }
    }

    public void start() {


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
