package com.ads.abcbank.fragment;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PresetBean;
import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.BaseTabFragment;
import com.ads.abcbank.view.TempView;

public class PresetTab1Fragment extends BaseTabFragment {
    private View view;
    private PresetBean.SaveRate bean;
    private TempView tempView;
    private TextView tvBottom;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_preset_tab1, null);
        tvBottom = view.findViewById(R.id.tv_bottom);
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
            PresetBean.SaveRate.SaveRateItem item = bean.entry.get(i);
            View rowView = LayoutInflater.from(mActivity).inflate(R.layout.item_preset_temp1, null);
            rowView.setBackgroundColor(i % 2 == 1 ? getResources().getColor(R.color.tab_row1) : getResources().getColor(R.color.tab_row2));
            final TextView pre = rowView.findViewById(R.id.tv_pre);
            final TextView key = rowView.findViewById(R.id.tv_key);
            final TextView value = rowView.findViewById(R.id.tv_value);
            final View vEmpty = rowView.findViewById(R.id.v_empty);
            if (!TextUtils.isEmpty(item.placeholder)) {
                pre.setText(item.placeholder.replace("\\t", "\t"));
            }
            key.setText(item.item);
            value.setText(item.saveRate);
            tlTab1.addView(rowView);
            setBottomHeight(null);
        }
        if (tvBottom != null) {
            tvBottom.setText(bean.rem);
        }
    }

    @Override
    public void setBean(Object bean) {
        if (bean instanceof PresetBean.SaveRate) {
            this.bean = (PresetBean.SaveRate) bean;
//            initData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (tlTab1 != null && tlTab1.getHeight() == 0) {
                initData();
            }
//            initData();
            if (getUserVisibleHint()) {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, delayTime);
            }
        } catch (Exception e) {
            Logger.e(e.toString());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (tlTab1 != null && tlTab1.getHeight() == 0) {
                initData();
            }
//            initData();
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, delayTime);
//        } else {
//            handler.removeCallbacks(runnable);
        }
    }

    private long delayTime = Utils.KEY_TIME_IMG_DEFAULT * 1000;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                delayTime = Integer.
                        parseInt(Utils.
                                get(context, Utils.KEY_TIME_TAB_IMG, Utils.KEY_TIME_IMG_DEFAULT + "")
                                .toString()) * 1000;
            } catch (Exception e) {
                delayTime = Utils.KEY_TIME_IMG_DEFAULT * 1000;
            }
            if (getUserVisibleHint() && isVisible() && tempView != null && ActivityManager.getInstance().getTopActivity() == tempView.getContext()) {
                tempView.nextPlay();
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, delayTime);
            } else {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, delayTime);
            }
        }
    };

    @Override
    public Object getBean() {
        return bean;
    }
}
