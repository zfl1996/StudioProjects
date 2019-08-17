package com.ads.abcbank.xx.activity;

import android.os.Bundle;
import android.view.View;

import com.ads.abcbank.R;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.xx.BaseTempletActivity;
import com.ads.abcbank.xx.utils.Constants;
import com.alibaba.fastjson.JSONObject;

public class TempV2Activity extends BaseTempletActivity {
    private static final String TAG = "TempV2Activity";

    View v_set;

    @Override
    protected void initCtrls(Bundle savedInstanceState) {
        rvMarqueeView = findViewById(R.id.rvMarqueeView);
        v_set = findViewById(R.id.v_set);

        rvMarqueeView.getBackground().setAlpha(179);
        super.initCtrls(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        type = Utils.TYPES_TEMP2;
        return R.layout.activity_temp_v2_vertical_2;
    }

}
