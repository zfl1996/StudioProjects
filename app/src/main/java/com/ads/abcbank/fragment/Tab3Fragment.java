package com.ads.abcbank.fragment;

import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TableRow;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PresetBean;
import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.BaseTabFragment;
import com.ads.abcbank.view.IView;
import com.ads.abcbank.view.TempView;
import com.alibaba.fastjson.JSONObject;

public class Tab3Fragment extends BaseTabFragment {
    private View view;
    private PresetBean.BIAOFE bean;
    private TempView tempView;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_tab3, null);
        return view;
    }

    public void setTempView(TempView tempView) {
        this.tempView = tempView;
    }

    public TempView getTempView() {
        return tempView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }


    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void initData() {
        if (tlTab1 == null || mActivity == null || bean == null || bean.entry == null) {
            return;
        }
        tlTab1.removeAllViews();
        for (int i = 0; i < bean.entry.size(); i++) {
            PresetBean.BIAOFE.BIAOFEItem item = bean.entry.get(i);
            View rowView = LayoutInflater.from(mActivity).inflate(R.layout.item_temp3, null);
            final TextView key = rowView.findViewById(R.id.tv_key);
            final TextView value = rowView.findViewById(R.id.tv_value);
            final TextView value2 = rowView.findViewById(R.id.tv_value2);
            final TextView value3 = rowView.findViewById(R.id.tv_value3);
            if (!TextUtils.isEmpty(item.placeholder)) {
                key.setText(item.placeholder.replace("\\t", "\t") + item.currCName);
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

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (tempView != null && view != null && view.findViewById(R.id.ll_root) != null) {
                view.findViewById(R.id.ll_root).setBackgroundResource(Utils.isDirectionVertical(context) ? R.mipmap.presetbg_v : R.mipmap.presetbg_h3);
                if (Utils.isDirectionVertical(context)) {
                    view.findViewById(R.id.ll_root).setPadding(0, 110, 0, 0);
                }
            }
            initData();
            if (getUserVisibleHint()) {
                handler.postDelayed(runnable, delayTime);
            } else {
                handler.removeCallbacks(runnable);
            }
        } catch (Exception e) {
            Logger.e(e.toString());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (tempView != null && view != null && view.findViewById(R.id.ll_root) != null) {
                view.findViewById(R.id.ll_root).setBackgroundResource(Utils.isDirectionVertical(context) ? R.mipmap.presetbg_v : R.mipmap.presetbg_h3);
                if (Utils.isDirectionVertical(context)) {
                    view.findViewById(R.id.ll_root).setPadding(0, 110, 0, 0);
                }
            }
            initData();
            handler.postDelayed(runnable, delayTime);
        } else {
            handler.removeCallbacks(runnable);
        }
    }

    private long delayTime = 5000;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                delayTime = Integer.
                        parseInt(Utils.
                                get(getActivity(), Utils.KEY_TIME_TAB_PRESET, "5")
                                .toString()) * 1000;
            } catch (Exception e) {
                delayTime = 5000;
            }
            if (getUserVisibleHint() && tempView != null) {
                tempView.nextPlay();
            } else {
                handler.postDelayed(runnable, delayTime);
            }
        }
    };

    @Override
    public Object getBean() {
        return bean;
    }
}
