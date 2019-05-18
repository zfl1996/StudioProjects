package com.ads.abcbank.activity;

import android.graphics.Color;
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
import com.xiaweizi.marquee.MarqueeTextView;

public class Temp1Activity extends BaseActivity implements TempView {
    private TableLayout tlTab1;
    private TableLayout tlBottom1;
    private ScrollView svTab1;
    private View v_tab1, v_tab2, v_tab3;
    private TextView tv_item_name, tv_item_name2;
    private TempPresenter presenter;
    private MarqueeTextView marqueeTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp1);

        initViews();
        initDatas("2");
    }

    private void initViews() {
        tlTab1 = findViewById(R.id.tl_tab1);
        tlBottom1 = findViewById(R.id.tl_bottom1);
        svTab1 = findViewById(R.id.sv_tab1);
        v_tab1 = findViewById(R.id.v_tab1);
        v_tab2 = findViewById(R.id.v_tab2);
        v_tab3 = findViewById(R.id.v_tab3);
        tv_item_name = findViewById(R.id.tv_item_name);
        tv_item_name2 = findViewById(R.id.tv_item_name2);
        marqueeTextView = findViewById(R.id.marqueeTextView);
        marqueeTextView.invalidate();
    }


    @Override
    protected void onPause() {
        super.onPause();
        marqueeTextView.pauseScroll();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        marqueeTextView.startScroll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        marqueeTextView.resumeScroll();
    }

    @Override
    protected void onStop() {
        super.onStop();
        marqueeTextView.stopScroll();
    }

    public void gotoTab(View view) {
        v_tab1.setVisibility(View.INVISIBLE);
        v_tab2.setVisibility(View.INVISIBLE);
        v_tab3.setVisibility(View.INVISIBLE);
        String tagNumber = view.getTag().toString();
        tlTab1.removeAllViews();
        tv_item_name.setText(!"3".equals(tagNumber)?"项目":"币种");
        tv_item_name2.setText(!"3".equals(tagNumber)?"年利率（%）":"卖出价");
        switch (tagNumber) {
            case "1":
                v_tab1.setVisibility(View.VISIBLE);
                initDatas("1");

                break;
            case "2":
                v_tab2.setVisibility(View.VISIBLE);
                initDatas("2");
                break;
            case "3":
                v_tab3.setVisibility(View.VISIBLE);
                initDatas("3");
                break;
            default:
                break;
        }
    }

    private void initDatas(String tagNum) {
        String json = Utils.getStringFromAssets("json.json", this);
        PresetBean bean = JSON.parseObject(json, PresetBean.class);
        if (bean != null) {
            if ("1".equals(tagNum)) {
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
            } else if ("2".equals(tagNum)) {
                for (int i = 0; i < bean.data.loanRate.entry.size(); i++) {
                    PresetBean.LoanRate.LoanRateItem item = bean.data.loanRate.entry.get(i);
                    View rowView = LayoutInflater.from(this).inflate(R.layout.item_temp1, null);
                    final TextView key = rowView.findViewById(R.id.tv_key);
                    final TextView value = rowView.findViewById(R.id.tv_value);
                    if (!TextUtils.isEmpty(item.placeholder)) {
                        key.setPadding(item.placeholder.length() * 10, 0, 10, 0);
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

            } else if ("3".equals(tagNum)) {
                for (int i = 0; i < bean.data.buyInAndOutForeignExchange.entry.size(); i++) {
                    PresetBean.BIAOFE.BIAOFEItem item = bean.data.buyInAndOutForeignExchange.entry.get(i);
                    View rowView = LayoutInflater.from(this).inflate(R.layout.item_temp1, null);
                    final TextView key = rowView.findViewById(R.id.tv_key);
                    final TextView value = rowView.findViewById(R.id.tv_value);
                    if (!TextUtils.isEmpty(item.placeholder)) {
                        key.setPadding(item.placeholder.length() * 10, 0, 10, 0);
                    }
                    key.setText(item.currCName);
                    value.setText(item.sellPrice);
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
