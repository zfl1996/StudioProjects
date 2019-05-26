package com.ads.abcbank.fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PresetBean;
import com.ads.abcbank.view.BaseTabFragment;

public class Tab3Fragment extends BaseTabFragment {
    private View view;
    private PresetBean.BIAOFE bean;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_tab3, null);
        tlTab1 = view.findViewById(R.id.tl_tab1);
        tlBottom1 = view.findViewById(R.id.tl_bottom1);
        svTab1 = view.findViewById(R.id.sv_tab1);
        return view;
    }

    @Override
    public void initData() {
        if (tlTab1 == null || mActivity == null) return;
        tlTab1.removeAllViews();
        for (int i = 0; i < bean.entry.size(); i++) {
            PresetBean.BIAOFE.BIAOFEItem item = bean.entry.get(i);
            View rowView = LayoutInflater.from(mActivity).inflate(R.layout.item_temp3, null);
            final TextView key = rowView.findViewById(R.id.tv_key);
            final TextView value = rowView.findViewById(R.id.tv_value);
            final TextView value2 = rowView.findViewById(R.id.tv_value2);
            final TextView value3 = rowView.findViewById(R.id.tv_value3);
            if (!TextUtils.isEmpty(item.placeholder)) {
                key.setText(item.placeholder + item.currCName);
            } else {
                key.setText(item.currCName);
            }
            value.setText(item.buyPrice);
            value2.setText(item.sellPrice);
            value3.setText(item.cashPrice);
            tlTab1.addView(rowView);
            key.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int kHeight = key.getHeight();
                    int vHeight = value.getHeight();
                    int vHeight2 = value2.getHeight();
                    int vHeight3 = value3.getHeight();
                    int height = Math.max(kHeight, vHeight);
                    height = Math.max(height, vHeight2);
                    height = Math.max(height, vHeight3);
                    TableRow.LayoutParams layoutParams = (TableRow.LayoutParams) key.getLayoutParams();
                    layoutParams.height = height;
                    key.setLayoutParams(layoutParams);
                    TableRow.LayoutParams layoutParams2 = (TableRow.LayoutParams) value.getLayoutParams();
                    layoutParams2.height = height;
                    value.setLayoutParams(layoutParams2);
                    TableRow.LayoutParams layoutParams3 = (TableRow.LayoutParams) value2.getLayoutParams();
                    layoutParams3.height = height;
                    value2.setLayoutParams(layoutParams3);
                    TableRow.LayoutParams layoutParams4 = (TableRow.LayoutParams) value3.getLayoutParams();
                    layoutParams4.height = height;
                    value3.setLayoutParams(layoutParams4);
                    key.getViewTreeObserver().removeOnPreDrawListener(this);

                    setBottomHeight(this);
                    return false;
                }
            });
        }
    }

    @Override
    public void setBean(Object bean) {
        if (bean instanceof PresetBean.BIAOFE) {
            this.bean = (PresetBean.BIAOFE) bean;
            initData();
        }
    }
}
