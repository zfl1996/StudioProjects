package com.ads.abcbank.xx.activity;

import android.os.Bundle;
import android.view.View;

import com.ads.abcbank.R;
import com.ads.abcbank.xx.BaseTempletActivity;

import java.util.List;

public class TempV21Activity extends BaseTempletActivity {
    private static final String TAG = "TempV21Activity";

    View v_set;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_temp_v2_vertical_21;
    }

    @Override
    protected void initCtrls(Bundle savedInstanceState) {
        v_set = findViewById(R.id.v_set);

        super.initCtrls(savedInstanceState);
    }

    @Override
    protected void onWelcomeLoaded(List<String> items, boolean isDefault, boolean isAppend) {}
}
