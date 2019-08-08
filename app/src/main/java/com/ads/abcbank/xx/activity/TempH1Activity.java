package com.ads.abcbank.xx.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.xx.BaseTempletActivity;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.ui.view.SliderPlayer;
import com.ads.abcbank.xx.utils.Constants;
import com.ads.abcbank.xx.utils.interactive.TimeTransformer;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class TempH1Activity extends BaseTempletActivity {
    private static final String TAG = "TempH1Activity";

    SliderPlayer presetSliderPlayer;
    TextView txtDate, txtTime;
    TimeTransformer timeTransformer;

    @Override
    protected void initCtrls(Bundle savedInstanceState) {
        rvMarqueeView = findViewById(R.id.rvMarqueeView);
        txtDate = findViewById(R.id.tv_date);
        txtTime = findViewById(R.id.tv_time);

        presetSliderPlayer = findViewById(R.id.presetSliderPlayer);

        timeTransformer = new TimeTransformer(timeData -> {
            txtDate.setText(timeData[1]);
            txtTime.setText(timeData[0]);
        });
        timeTransformer.start();

        super.initCtrls(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_temp_v2_horizontal_1;
    }

    @Override
    protected void onRateDataPrepare(List<PlayItem> items){
        presetSliderPlayer.onReady(true, items);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeTransformer.stop();
    }

}
