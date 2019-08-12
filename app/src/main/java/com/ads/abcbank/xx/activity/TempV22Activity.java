package com.ads.abcbank.xx.activity;

import android.os.Bundle;
import android.view.View;

import com.ads.abcbank.R;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.xx.BaseTempletActivity;

import java.util.List;

public class TempV22Activity extends BaseTempletActivity {
    private static final String TAG = "TempV21Activity";

    View v_set;

    @Override
    protected int getLayoutResourceId() {
        type = Utils.TYPES_TEMP7;
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
