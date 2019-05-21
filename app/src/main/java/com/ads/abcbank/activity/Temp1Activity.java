package com.ads.abcbank.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.presenter.TempPresenter;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.view.MarqueeTextView;
import com.ads.abcbank.view.IView;
import com.ads.abcbank.view.PresetView;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class Temp1Activity extends BaseActivity implements IView {
    private MarqueeTextView marqueeTextView;

    private int delayDateTime = 60 * 1000;
    private TempPresenter tempPresenter;

    private TextView tvTime;
    private TextView tvDate;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private PresetView presetView;

    private JzvdStd videoplayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp1);

        initViews();
    }

    private Handler handler = new Handler();

    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            Calendar calendar = Calendar.getInstance();
            Date date = new Date();
            calendar.setTime(date);
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            String weekStr = "";
            switch (week) {
                case 1:
                    weekStr = "星期日";
                    break;
                case 2:
                    weekStr = "星期一";
                    break;
                case 3:
                    weekStr = "星期二";
                    break;
                case 4:
                    weekStr = "星期三";
                    break;
                case 5:
                    weekStr = "星期四";
                    break;
                case 6:
                    weekStr = "星期五";
                    break;
                case 7:
                    weekStr = "星期六";
                    break;
            }
            tvTime.setText(timeFormat.format(calendar.getTime()));
            tvDate.setText(weekStr + "\n" + dateFormat.format(calendar.getTime()));
            handler.postDelayed(timeRunnable, delayDateTime);
        }
    };

    private void initViews() {
        marqueeTextView = findViewById(R.id.marqueeTextView);
        presetView = findViewById(R.id.pv_preset);
        tvTime = findViewById(R.id.tv_time);
        tvDate = findViewById(R.id.tv_date);
        videoplayer = findViewById(R.id.videoplayer);

        videoplayer.setUp("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
                , "", JzvdStd.SCREEN_NORMAL);
        Glide.with(this).load(R.drawable.app_icon_your_company).into(videoplayer.thumbImageView);

        marqueeTextView.invalidate();
        handler.post(timeRunnable);

        tempPresenter = new TempPresenter(this, this);
        tempPresenter.getPreset();

        videoplayer.startVideo();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Jzvd.resetAllVideos();
        JzvdStd.goOnPlayOnPause();
        marqueeTextView.pauseScroll();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        marqueeTextView.startScroll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        marqueeTextView.resumeScroll();
        JzvdStd.goOnPlayOnResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        marqueeTextView.stopScroll();
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void updateMainDate(JSONObject jsonObject) {

    }

    @Override
    public void updateBottomDate(JSONObject jsonObject) {

    }

    @Override
    public void updatePresetDate(JSONObject jsonObject) {
        presetView.updatePresetDate();
    }


    public class JZVideoPlayerStandardLoopVideo extends JzvdStd {
        public JZVideoPlayerStandardLoopVideo(Context context) {
            super(context);
        }

        public JZVideoPlayerStandardLoopVideo(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void onAutoCompletion() {
            super.onAutoCompletion();
            startVideo();
        }
    }
}
