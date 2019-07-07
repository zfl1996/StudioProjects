package com.ads.abcbank.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.presenter.TempPresenter;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.view.BaseTempFragment;
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
        marqueeTv = findViewById(R.id.marqueeTv);
        tvTemp = findViewById(R.id.tv_temp);
        tvTemp.setType("M,H,P,N,E,L,R");
        tvTemp.getImage().setVisibility(View.GONE);
        marqueeTv.setTextArraysAndClickListener(textArrays, new MarqueeVerticalTextViewClickListener() {
                    @Override
                    public void onItemClick(int position, TextView view) {

                    }
                }
        );

        setiView(this);
        startServices("M,H,P,N,E,L,R");
        BaseTempFragment.tempView = tvTemp;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (tvTemp != null) {
            BaseTempFragment.tempView = tvTemp;
            tvTemp.setNeedUpdate(true);
        }
        setiView(this);
        startServices("M,H,P,N,E,L,R");
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
        if (tvTemp != null) {
            tvTemp.updatePreset();
        }
    }
}
