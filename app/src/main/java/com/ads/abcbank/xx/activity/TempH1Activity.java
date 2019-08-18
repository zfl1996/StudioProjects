package com.ads.abcbank.xx.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.xx.BaseTempletActivity;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.ui.view.SliderPlayer;
import com.ads.abcbank.xx.utils.helper.GuiHelper;
import com.ads.abcbank.xx.utils.interactive.TimeTransformer;

import java.util.List;

public class TempH1Activity extends BaseTempletActivity {
    private static final String TAG = "TempH1Activity";

    SliderPlayer presetSliderPlayer;
    TabLayout tabIndicator;
    TextView txtDate, txtTime;
    TimeTransformer timeTransformer;
    boolean isTabInited = false;

    @Override
    protected void initCtrls(Bundle savedInstanceState) {
        rvMarqueeView = findViewById(R.id.rvMarqueeView);
        txtDate = findViewById(R.id.tv_date);
        txtTime = findViewById(R.id.tv_time);
        tabIndicator = findViewById(R.id.tabIndicator);
        presetSliderPlayer = findViewById(R.id.presetSliderPlayer);

        presetSliderPlayer.setPageChangeListener( position -> {
            int tabs = tabIndicator.getTabCount();
            if(tabs == 0){
                tabs = 1;
            }
            tabIndicator.setScrollPosition(position % tabs, 0, true);
        } );

        timeTransformer = new TimeTransformer(timeData -> {
            txtDate.setText(timeData[1]);
            txtTime.setText(timeData[0]);
        });
        timeTransformer.start();
        super.initCtrls(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        type = Utils.TYPES_TEMP1;
        return R.layout.activity_temp_v2_horizontal_1;
    }

    @Override
    protected void onRateDataPrepared(List<PlayItem> items, List<String> titles){
//        presetSliderPlayer.addPlayItems(items, false);

        mainSliderPlayer.addRateItem(items, false, isPlaylistLoaded());

        if (!isTabInited) {
            isTabInited = true;
            for (String title : titles)
                tabIndicator.addTab(tabIndicator.newTab().setText(title));

            GuiHelper.setTabWidth(tabIndicator);
        }
    }

    @Override
    protected void onRateDataProgress(int code) {
        presetSliderPlayer.adjustWidgetStatus(isPresetLoaded(), isPlaylistLoaded(), code);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeTransformer.stop();
    }

}
