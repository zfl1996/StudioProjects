package com.ads.abcbank.fragment;

import android.graphics.Color;
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
import com.ads.abcbank.view.IView;
import com.ads.abcbank.view.TempView;
import com.alibaba.fastjson.JSONObject;

public class Tab2Fragment extends BaseTabFragment {
    private View view;
    private PresetBean.LoanRate bean;
    private TempView tempView;
    private TextView tvBottom;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_tab1, null);
        tvBottom = view.findViewById(R.id.tv_bottom);
        return view;
    }
//
//    public void setTempView(TempView tempView) {
//        this.tempView = tempView;
//    }
//
//    public TempView getTempView() {
//        return tempView;
//    }
//
//    public Handler getHandler() {
//        return handler;
//    }
//
//    public void setHandler(Handler handler) {
//        this.handler = handler;
//    }
//
//
//    public Runnable getRunnable() {
//        return runnable;
//    }
//
//    public void setRunnable(Runnable runnable) {
//        this.runnable = runnable;
//    }

    @Override
    public void initData() {
        if (tlTab1 == null || mActivity == null || bean == null || bean.entry == null) {
            return;
        }
        tlTab1.removeAllViews();
        for (int i = 0; i < bean.entry.size(); i++) {
            PresetBean.LoanRate.LoanRateItem item = bean.entry.get(i);
            View rowView = LayoutInflater.from(mActivity).inflate(R.layout.item_temp1, null);
            final TextView pre = rowView.findViewById(R.id.tv_pre);
            final TextView key = rowView.findViewById(R.id.tv_key);
            final TextView value = rowView.findViewById(R.id.tv_value);
            final View vEmpty = rowView.findViewById(R.id.v_empty);
            if (!TextUtils.isEmpty(item.placeholder)) {
                pre.setText(item.placeholder.replace("\\t", "\t"));
            }
            key.setText(item.item);
            value.setText(item.loanRate);
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
        if (tvBottom != null) {
            tvBottom.setText(bean.rem);
        }
    }

    @Override
    public void setBean(Object bean) {
        if (bean instanceof PresetBean.LoanRate) {
            this.bean = (PresetBean.LoanRate) bean;
            initData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            initData();
//            if (getUserVisibleHint()) {
//                handler.removeCallbacks(runnable);
//                handler.postDelayed(runnable, delayTime);
//            }
        } catch (Exception e) {
            Logger.e(e.toString());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            initData();
//            handler.removeCallbacks(runnable);
//            handler.postDelayed(runnable, delayTime);
//        } else {
//            handler.removeCallbacks(runnable);
        }
    }

//    private long delayTime = 5000;
//    private Handler handler = new Handler();
//    private Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                delayTime = Integer.
//                        parseInt(Utils.
//                                get(context, Utils.KEY_TIME_TAB_IMG, "5")
//                                .toString()) * 1000;
//            } catch (Exception e) {
//                delayTime = 5000;
//            }
//            if (getUserVisibleHint() && tempView != null && ActivityManager.getInstance().getTopActivity() == tempView.getContext()) {
//                tempView.nextPlay();
//                handler.removeCallbacks(runnable);
//                handler.postDelayed(runnable, delayTime);
//            } else {
//                handler.removeCallbacks(runnable);
//                handler.postDelayed(runnable, delayTime);
//            }
//        }
//    };

    @Override
    public Object getBean() {
        return bean;
    }
}

