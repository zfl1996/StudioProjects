package com.ads.abcbank.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.presenter.TempPresenter;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.view.JZMediaSystemAssertFolder;
import com.ads.abcbank.view.MarqueeTextView;
import com.ads.abcbank.view.IView;
import com.ads.abcbank.view.PresetView;
import com.ads.abcbank.view.TempView;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZUtils;
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
//        initViews();
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
        tvTemp = findViewById(R.id.tv_temp);
//        videoplayer = findViewById(R.id.videoplayer);
//        cpAssertVideoToLocalPath();
//        JZDataSource jzDataSource = null;
//        try {
//            jzDataSource = new JZDataSource(getAssets().openFd("local_video.mp4"));
//            jzDataSource.title = "";
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        videoplayer.setUp(jzDataSource
//                , JzvdStd.SCREEN_NORMAL);
//        Glide.with(this).load(R.drawable.app_icon_your_company).into(videoplayer.thumbImageView);
//        videoplayer.setMediaInterface(new JZMediaSystemAssertFolder(videoplayer));
        marqueeTextView.invalidate();
        handler.post(timeRunnable);
        tvTemp.setType("M,H,P,N,E,L,R");

        tempPresenter = new TempPresenter(this, this);
        tempPresenter.getPreset();

//        videoplayer.startVideo();
        if (marqueeTextView != null)
            marqueeTextView.startScroll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JzvdStd.goOnPlayOnPause();
        if (marqueeTextView != null)
            marqueeTextView.pauseScroll();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (marqueeTextView != null)
            marqueeTextView.startScroll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (marqueeTextView != null)
            marqueeTextView.resumeScroll();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (marqueeTextView != null)
            marqueeTextView.stopScroll();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void updateMainDate(JSONObject jsonObject) {

    }

    @Override
    public void updateBottomDate(JSONObject jsonObject) {

    }

    public void cpAssertVideoToLocalPath() {
        JZUtils.verifyStoragePermissions(this);
        try {
            InputStream myInput;
            OutputStream myOutput = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/local_video.mp4");
            myInput = this.getAssets().open("local_video.mp4");
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }

            myOutput.flush();
            myInput.close();
            myOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
