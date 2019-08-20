package com.ads.abcbank.xx.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.xx.BaseTempletActivity;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.ui.view.SliderPlayer;
import com.ads.abcbank.xx.utils.helper.GuiHelper;
import com.ads.abcbank.xx.utils.interactive.TimeTransformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TempH1Activity extends BaseTempletActivity {
    private static final String TAG = "TempH1Activity";

    SliderPlayer presetSliderPlayer;
    TabLayout tabIndicator;
    TextView txtDate, txtTime;
    TimeTransformer timeTransformer;
    boolean isTabInited = false;
    Map<Integer, TabLayout.Tab> tabItemsMap = new HashMap<>();
    int curPos = -1;

    @Override
    protected void initCtrls(Bundle savedInstanceState) {
        rvMarqueeView = findViewById(R.id.rvMarqueeView);
        txtDate = findViewById(R.id.tv_date);
        txtTime = findViewById(R.id.tv_time);
        tabIndicator = findViewById(R.id.tabIndicator);
        presetSliderPlayer = findViewById(R.id.presetSliderPlayer);

        presetSliderPlayer.setPageChangeListener( position -> {
            curPos = position;
            showIndicator(position);
        } );

        timeTransformer = new TimeTransformer(timeData -> {
            txtDate.setText(timeData[1]);
            txtTime.setText(timeData[0]);
        });
        timeTransformer.start();
        super.initCtrls(savedInstanceState);
    }

    private void showIndicator(int position) {
        int tabs = tabIndicator.getTabCount();
        if(tabs == 0){
            tabs = 1;
        }
        tabIndicator.setScrollPosition(position % tabs, 0, true);
    }

    @Override
    protected int getLayoutResourceId() {
        type = Utils.TYPES_TEMP1;
        return R.layout.activity_temp_v2_horizontal_1;
    }

    @Override
    protected void onRateDataPrepared(List<PlayItem> items, List<String> titles){
        presetSliderPlayer.addRateItem(items, false, isPresetLoaded());

        if (!isTabInited) {
            isTabInited = true;

            int i = 0;
            for (String title : titles) {
                TabLayout.Tab tab = tabIndicator.newTab().setText(title);
                tabItemsMap.put(items.get(i++).getMediaType(), tab);
                tabIndicator.addTab(tab);
            }

            GuiHelper.setTabWidth(tabIndicator);
        } else {
            List<PlayItem> needToAdds = new ArrayList<>();
            List<String> needToAddTitles = new ArrayList<>();
            List<Integer> addOfPosition = new ArrayList<>();

            int i = 0;
            for (PlayItem pi : items) {
                if (!tabItemsMap.containsKey(pi.getMediaType())) {
                    needToAdds.add(pi);
                    addOfPosition.add(i);
                    needToAddTitles.add(titles.get(i));
                }

                i++;
            }

            for (i = 0; i<needToAdds.size(); i++) {
                TabLayout.Tab tab = tabIndicator.newTab().setText(needToAddTitles.get(i));
                tabItemsMap.put(items.get(i).getMediaType(), tab);
                tabIndicator.addTab(tab, addOfPosition.get(i));
            }

            if (needToAdds.size() > 0) {
                GuiHelper.setTabWidth(tabIndicator);
                tabIndicator.invalidate();

                if (curPos >= 0)
                    showIndicator(curPos);
            }
        }
    }

    @Override
    protected void onRateDataProgress(int code) {
        presetSliderPlayer.adjustWidgetStatus(isPresetLoaded(), isPlaylistLoaded(), code);
    }

    @Override
    protected void onNetworkError(int code) {
        presetSliderPlayer.removeAllRateItem();
        tabIndicator.removeAllTabs();
        tabItemsMap.clear();

        tabIndicator.invalidate();

        super.onNetworkError(code);
    }

    @Override
    protected void onRateRemoved(Integer... mediaTypes) {
        presetSliderPlayer.removeRateItem(mediaTypes);

        for (Integer mediaType : mediaTypes) {
            if (tabItemsMap.containsKey(mediaType)) {
                tabIndicator.removeTab(tabItemsMap.get(mediaType));
                tabItemsMap.remove(mediaType);
            }
        }

        tabIndicator.invalidate();

        if (mediaTypes.length > 0 && curPos >= 0)
            showIndicator(curPos);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != timeTransformer)
            timeTransformer.stop();
    }

}
