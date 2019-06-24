package com.ads.abcbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.presenter.TempPresenter;
import com.ads.abcbank.utils.ZipUtil;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.view.IView;
import com.ads.abcbank.view.MarqueeVerticalTextView;
import com.ads.abcbank.view.MarqueeVerticalTextViewClickListener;
import com.ads.abcbank.view.TempView;
import com.alibaba.fastjson.JSONObject;

public class Temp2Activity extends BaseActivity implements IView {
    private TempPresenter presenter;
    private MarqueeVerticalTextView marqueeTv;
    private String[] textArrays = new String[]{
            "中国农业银行欢迎您！    中国农业银行欢迎您！    中国农业银行欢迎您！",
            "中国农业银行欢迎您！    中国农业银行欢迎您！    中国农业银行欢迎您！",
            "中国农业银行欢迎您！    中国农业银行欢迎您！    中国农业银行欢迎您！"
    };
    private TempView tvTemp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp2);
        marqueeTv = (MarqueeVerticalTextView) findViewById(R.id.marqueeTv);
        tvTemp = findViewById(R.id.tv_temp);
//        tvTemp.setShowStaticData(true);
        tvTemp.setType("M,H,P,N,E,L,R");
//        tvTemp.setImageSrc(R.mipmap.v_zysys);
        tvTemp.getImage().setVisibility(View.GONE);
        marqueeTv.setTextArraysAndClickListener(textArrays, new MarqueeVerticalTextViewClickListener() {
                    @Override
                    public void onItemClick(int position, TextView view) {

                    }
                }
        );

//        ZipUtil.copyDbFile(this, "a.jpg");
//        ZipUtil.copyDbFile(this, "b.jpg");
//        ZipUtil.copyDbFile(this, "c.jpg");
//        ZipUtil.copyDbFile(this, "audio.wav");
//        ZipUtil.compressFile(marqueeTv.getRootView());
//        ZipUtil.unZip(marqueeTv.getRootView());
        setiView(this);
        startServices("M,H,P,N,E,L,R");
    }

    @Override
    public void updateMainDate(JSONObject jsonObject) {
        tvTemp.setNeedUpdate(true);
    }

    @Override
    public void updateBottomDate(JSONObject jsonObject) {

    }

    @Override
    public void updatePresetDate(JSONObject jsonObject) {
        tvTemp.updatePreset();
    }
}
