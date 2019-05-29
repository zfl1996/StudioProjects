package com.ads.abcbank.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.activity.Temp1Activity;
import com.ads.abcbank.bean.PresetBean;
import com.ads.abcbank.fragment.Tab1Fragment;
import com.ads.abcbank.fragment.Tab2Fragment;
import com.ads.abcbank.fragment.Tab3Fragment;
import com.ads.abcbank.utils.Utils;
import com.alibaba.fastjson.JSON;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @date 2019/5/19
 */

public class PresetView extends LinearLayout {

    private List<Fragment> fragmentList;
    private List<String> list_Title;
    private ViewPager viewpager;
    private TabLayout tablayout;
    private Tab1Fragment tab1Fragment;
    private Tab2Fragment tab2Fragment;
    private Tab3Fragment tab3Fragment;
    private int delayTime = 10 * 1000;
    private int downX = -1;
    private int downY = -1;
    private Context context;

    public PresetView(Context context) {
        this(context, null);
    }

    public PresetView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PresetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.view_preset, null);
        viewpager = view.findViewById(R.id.viewpager);
        tablayout = view.findViewById(R.id.tablayout);

        tab1Fragment = new Tab1Fragment();
        tab2Fragment = new Tab2Fragment();
        tab3Fragment = new Tab3Fragment();

        tab1Fragment.initView(LayoutInflater.from(context));
        tab2Fragment.initView(LayoutInflater.from(context));
        tab3Fragment.initView(LayoutInflater.from(context));

        String json = Utils.get(context, Utils.KEY_PRESET, "").toString();
        if (TextUtils.isEmpty(json)) {
            json = Utils.getStringFromAssets("json.json", context);
        }
        PresetBean bean = JSON.parseObject(json, PresetBean.class);
        tab1Fragment.setBean(bean.data.saveRate);
        tab2Fragment.setBean(bean.data.loanRate);
        tab3Fragment.setBean(bean.data.buyInAndOutForeignExchange);

        fragmentList = new ArrayList<>();
        list_Title = new ArrayList<>();
        fragmentList.add(tab1Fragment);
        fragmentList.add(tab2Fragment);
        fragmentList.add(tab3Fragment);
        list_Title.add("人民币\n存款利率");
        list_Title.add("人民币\n贷款利率");
        list_Title.add("结售汇\n牌价");

        setTabWidth(tablayout);
        handler.postDelayed(runnable, delayTime);



        for (int i = 0; i < tablayout.getTabCount(); i++) {
            TabLayout.Tab tab = tablayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getTabView(i));
            }
        }
        addView(view);
        viewpager.setAdapter(new PresetPagerAdapter(((AppCompatActivity) context).getSupportFragmentManager()));
        tablayout.setupWithViewPager(viewpager);//此方法就是让tablayout和ViewPager联动
        viewpager.setCurrentItem(0);
    }

    public void updatePresetDate() {
        String json = Utils.get(context, Utils.KEY_PRESET, "").toString();
        if (TextUtils.isEmpty(json)) {
            json = Utils.getStringFromAssets("json.json", context);
        }
        PresetBean bean = JSON.parseObject(json, PresetBean.class);
        tab1Fragment.setBean(bean.data.saveRate);
        tab2Fragment.setBean(bean.data.loanRate);
        tab3Fragment.setBean(bean.data.buyInAndOutForeignExchange);
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int item = viewpager.getCurrentItem();
            if (item < list_Title.size() - 1) {
                viewpager.setCurrentItem(item + 1);
            } else {
                viewpager.setCurrentItem(0);
            }
            handler.postDelayed(runnable, delayTime);
        }
    };

    private View getTabView(int currentPosition) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_item, null);
        TextView textView = (TextView) view.findViewById(R.id.tab_item_textview);
        textView.setText(list_Title.get(currentPosition));
        return view;
    }

    public class PresetPagerAdapter extends FragmentPagerAdapter {

        public PresetPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return list_Title.size();
        }

        /**
         * //此方法用来显示tab上的名字
         *
         * @param position
         * @return
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return list_Title.get(position);
        }
    }

    public static void setTabWidth(final TabLayout tabLayout) {
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拿到tabLayout的mTabStrip属性
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);
                    int pWidth = mTabStrip.getWidth();

                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);

                        //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);

                        TextView mTextView = (TextView) mTextViewField.get(tabView);

                        tabView.setPadding(0, 0, 0, 0);

                        //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                        int width = 0;
                        width = mTextView.getWidth();
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }

                        //设置tab左右间距 注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width;
                        int padding = (pWidth - width * 3) / 6;
                        params.leftMargin = padding;
                        params.rightMargin = padding;
                        tabView.setLayoutParams(params);

                        tabView.invalidate();
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 让当前viewpager的父控件不去拦截touch事件
                getParent().requestDisallowInterceptTouchEvent(true);
                downX = (int) ev.getX();
                downY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getX();
                int moveY = (int) ev.getY();
                if (Math.abs(moveX - downX) >= Math.abs(moveY - downY)) {
                    // 滑动轮播图
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    // 刷新listview
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

}
