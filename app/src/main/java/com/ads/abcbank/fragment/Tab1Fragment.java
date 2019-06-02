package com.ads.abcbank.fragment;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PresetBean;
import com.ads.abcbank.view.BaseTabFragment;

public class Tab1Fragment extends BaseTabFragment {
    private View view;
    private PresetBean.SaveRate bean;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_tab1, null);
        return view;
    }


    @Override
    public void initData() {
        if (tlTab1 == null || mActivity == null || bean == null || bean.entry == null) return;
        tlTab1.removeAllViews();
        for (int i = 0; i < bean.entry.size(); i++) {
            PresetBean.SaveRate.SaveRateItem item = bean.entry.get(i);
            View rowView = LayoutInflater.from(mActivity).inflate(R.layout.item_temp1, null);
            final TextView pre = rowView.findViewById(R.id.tv_pre);
            final TextView key = rowView.findViewById(R.id.tv_key);
            final TextView value = rowView.findViewById(R.id.tv_value);
            final View v_empty = rowView.findViewById(R.id.v_empty);
            if (!TextUtils.isEmpty(item.placeholder)) {
                pre.setText(item.placeholder);
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
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) key.getLayoutParams();
                    layoutParams.height = height;
                    key.setLayoutParams(layoutParams);
                    TableRow.LayoutParams layoutParams2 = (TableRow.LayoutParams) value.getLayoutParams();
                    layoutParams2.height = height;
                    value.setLayoutParams(layoutParams2);
                    LinearLayout.LayoutParams layoutParams4 = (LinearLayout.LayoutParams) pre.getLayoutParams();
                    layoutParams4.height = height;
                    pre.setLayoutParams(layoutParams4);
                    key.getViewTreeObserver().removeOnPreDrawListener(this);

                    setBottomHeight(this);
                    return false;
                }
            });
        }
//        tlTab1.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                setBottomHeight(this);
//                return false;
//            }
//        });
    }

    @Override
    public void setBean(Object bean) {
        if (bean instanceof PresetBean.SaveRate) {
            this.bean = (PresetBean.SaveRate) bean;
            initData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            initData();
        }
    }

    @Override
    public Object getBean() {
        return bean;
    }
}
