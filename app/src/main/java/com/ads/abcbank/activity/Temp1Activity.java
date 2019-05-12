package com.ads.abcbank.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PresetBean;
import com.ads.abcbank.presenter.TempPresenter;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.TempView;
import com.alibaba.fastjson.JSON;

public class Temp1Activity extends BaseActivity implements TempView {
    private TableLayout tlTab1;
    private TableLayout tlBottom1;
    private ScrollView svTab1;
    private TempPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp1);

        initViews();
        initDatas();
    }

    private void initViews() {
        tlTab1 = findViewById(R.id.tl_tab1);
        tlBottom1 = findViewById(R.id.tl_bottom1);
        svTab1 = findViewById(R.id.sv_tab1);
    }

    private void initDatas() {
        String json = Utils.getStringFromAssets("json.json", this);
        PresetBean bean = JSON.parseObject(json, PresetBean.class);

        if (bean != null) {
            for (int i = 0; i < bean.data.saveRate.entry.size(); i++) {
                PresetBean.SaveRate.SaveRateItem item = bean.data.saveRate.entry.get(i);
                View rowView = LayoutInflater.from(this).inflate(R.layout.item_temp1, null);
                final TextView key = rowView.findViewById(R.id.tv_key);
                final TextView value = rowView.findViewById(R.id.tv_value);
                if (!TextUtils.isEmpty(item.placeholder)) {
                    key.setPadding(item.placeholder.length() * 10, 0, 10, 0);
                }

                key.setText(item.item);
                value.setText(item.saveRate);
                tlTab1.addView(rowView);
                key.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        int kHeight = key.getHeight();
                        int vHeight = value.getHeight();
                        int height = Math.max(kHeight, vHeight);
                        TableRow.LayoutParams layoutParams = (TableRow.LayoutParams) key.getLayoutParams();
                        layoutParams.height = height;
                        key.setLayoutParams(layoutParams);
                        TableRow.LayoutParams layoutParams2 = (TableRow.LayoutParams) key.getLayoutParams();
                        layoutParams2.height = height;
                        value.setLayoutParams(layoutParams2);
                        key.getViewTreeObserver().removeOnPreDrawListener(this);

                        setBottomHeight(this);
                        return false;
                    }
                });
            }

        }

        tlTab1.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                setBottomHeight(this);
                return false;
            }
        });
    }

    private void setBottomHeight(ViewTreeObserver.OnPreDrawListener preDrawListener) {
        int sHeight = svTab1.getHeight();
        int tHeight = tlTab1.getHeight();
        int bHeight = sHeight - tHeight;
        bHeight = Math.max(bHeight, 0);
        ViewGroup.LayoutParams layoutParams = tlBottom1.getLayoutParams();
        layoutParams.height = bHeight;
        tlBottom1.setLayoutParams(layoutParams);
        tlTab1.getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
    }

}
