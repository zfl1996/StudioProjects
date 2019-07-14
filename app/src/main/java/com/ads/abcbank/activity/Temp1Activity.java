package com.ads.abcbank.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PlaylistBean;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.presenter.TempPresenter;
import com.ads.abcbank.service.DownloadService;
import com.ads.abcbank.utils.HandlerUtil;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.AutoPollAdapter;
import com.ads.abcbank.view.AutoPollRecyclerView;
import com.ads.abcbank.view.BaseActivity;
//import com.ads.abcbank.view.HorizontalListView;
//import com.ads.abcbank.view.HorizontalListViewAdapter;
import com.ads.abcbank.view.BaseTempFragment;
import com.ads.abcbank.view.MarqueeTextView;
import com.ads.abcbank.view.IView;
import com.ads.abcbank.view.PresetView;
import com.ads.abcbank.view.TempView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import cn.jzvd.JzvdStd;

public class Temp1Activity extends BaseActivity implements IView {
//    private MarqueeTextView marqueeTextView;

    private int delayDateTime = 60 * 1000;
    private TempPresenter tempPresenter;

    private TextView tvTime;
    private TextView tvDate;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private PresetView presetView;

    private TempView tvTemp;
    private AutoPollRecyclerView mRecyclerView;
    private AutoPollAdapter autoPollAdapter;
    private List<String> list = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp1);
        setiView(this);

        HandlerUtil.postDelayed(new Runnable() {
            @Override
            public void run() {
                initViews();
                startServices("M,H,P,N,E,L,R");
            }
        }, 100);
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
//        marqueeTextView = findViewById(R.id.marqueeTextView);
        mRecyclerView = findViewById(R.id.rv_recycleView);
//        hListView = findViewById(R.id.hlv_main);
        presetView = findViewById(R.id.pv_preset);
        tvTime = findViewById(R.id.tv_time);
        tvDate = findViewById(R.id.tv_date);
        tvTemp = findViewById(R.id.tv_temp);
//        marqueeTextView.invalidate();
        handler.post(timeRunnable);
        tvTemp.setType("M,H,P,N,E,L,R");
        tvTemp.getImage().setVisibility(View.GONE);
        tempPresenter = new TempPresenter(this, this);
//        if (marqueeTextView != null) {
//            marqueeTextView.startScroll();
//        }
        presetView.updatePresetDate();

        for (int i = 0; i < 10; i++) {
            list.add("    中国农业银行欢迎您！    ");
        }
        autoPollAdapter = new AutoPollAdapter(this, list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(autoPollAdapter);
        mRecyclerView.start();
        BaseTempFragment.tempView = tvTemp;
    }

    @Override
    protected void onPause() {
        super.onPause();
        JzvdStd.goOnPlayOnPause();
//        if (marqueeTextView != null) {
//            marqueeTextView.pauseScroll();
//        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (tvTemp != null) {
            BaseTempFragment.tempView = tvTemp;
            tvTemp.setNeedUpdate(true);
        }
        HandlerUtil.postDelayed(new Runnable() {
            @Override
            public void run() {
                initViews();
                startServices("M,H,P,N,E,L,R");
            }
        }, 100);
//        if (marqueeTextView != null) {
//            marqueeTextView.startScroll();
//        }
        if (mRecyclerView != null) {
            mRecyclerView.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (marqueeTextView != null) {
//            marqueeTextView.resumeScroll();
//        }
    }


    @Override
    protected void onStop() {
        super.onStop();
//        if (marqueeTextView != null) {
//            marqueeTextView.stopScroll();
//        }
        if (mRecyclerView != null) {
            mRecyclerView.stop();
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

    public void updateTxtBeans(List<PlaylistBodyBean> beans) {
        List<String> listString = new ArrayList<>();
        for (int i = 0; i < beans.size(); i++) {
            PlaylistBodyBean bean = beans.get(i);
            if (Utils.isInPlayTime(bean)) {
                try {
                    File file = new File(DownloadService.downloadFilePath, bean.name);
                    if (file.exists()) {
                        listString.add(Utils.getTxtString(this, bean.name));
                    }
                } catch (Exception e) {
                    Logger.e("TXT ERROR", e.toString());
                }
            }
        }
        if (!isSimpleTxt(listString)) {
            updateTextList(listString);
        }
    }

    private boolean isSimpleTxt(List<String> listString) {
        if (listString.size() == 0) {
            return true;
        }
        return JSON.toJSONString(listString).equals(JSON.toJSONString(list));
    }

    private void updateTextList(List<String> listString) {
        if (mRecyclerView != null) {
            mRecyclerView.stop();
        }
        list.clear();
        list.addAll(listString);
        long length = getListTxtLength(list);
        if (length == 0) {
            for (int i = 0; i < 6; i++) {
                list.add("中国农业银行欢迎您！");
            }
        } else if (length < 60) {
            int s = (int) (60 / length) + 1;
            for (int i = 0; i < s; i++) {
                list.addAll(listString);
            }
        }
        autoPollAdapter.notifyDataSetChanged();
        if (mRecyclerView != null) {
            mRecyclerView.start();
        }
    }

    private long getListTxtLength(List<String> listString) {
        if (listString == null) {
            return 0;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < listString.size(); i++) {
            stringBuffer.append(listString.get(i));
        }
        return stringBuffer.length();
    }

}
