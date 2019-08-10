package com.ads.abcbank.xx.activity;

import com.ads.abcbank.R;
import com.ads.abcbank.xx.BaseTempletActivity;

import java.util.List;

public class TempH2Activity extends BaseTempletActivity {
    private static final String TAG = "TempV2Activity";

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_temp_v2_horizontal_2;
    }

    @Override
    protected void onWelcomeLoaded(List<String> items, boolean isDefault, boolean isAppend) {}

}
