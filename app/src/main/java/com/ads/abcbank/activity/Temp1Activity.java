package com.ads.abcbank.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.presenter.TempPresenter;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.view.MarqueeTextView;
import com.ads.abcbank.view.IView;
import com.ads.abcbank.view.PresetView;
import com.ads.abcbank.view.TempView;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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

    private TempView tvTemp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp1);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initViews();
            }
        }, 100);
        setiView(this);
        startServices("M,H,P,N,E,L,R");
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
                default:
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
        tvTemp = findViewById(R.id.tv_temp);
        marqueeTextView.invalidate();
        handler.post(timeRunnable);
        tvTemp.setType("M,H,P,N,E,L,R");
        tvTemp.getImage().setVisibility(View.GONE);
        tempPresenter = new TempPresenter(this, this);
        if (marqueeTextView != null) {
            marqueeTextView.startScroll();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JzvdStd.goOnPlayOnPause();
        if (marqueeTextView != null) {
            marqueeTextView.pauseScroll();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (marqueeTextView != null) {
            marqueeTextView.startScroll();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (marqueeTextView != null) {
            marqueeTextView.resumeScroll();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (marqueeTextView != null) {
            marqueeTextView.stopScroll();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void updateMainDate(JSONObject jsonObject) {
        if (tvTemp != null) {
            tvTemp.setNeedUpdate(true);
        }
    }

    @Override
    public void updateBottomDate(JSONObject jsonObject) {

    }

    @Override
    public void updatePresetDate(JSONObject jsonObject) {
        if (presetView != null) {
            presetView.updatePresetDate();
        }
    }

}
