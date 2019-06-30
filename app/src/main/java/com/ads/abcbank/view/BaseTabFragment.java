package com.ads.abcbank.view;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TableLayout;

import com.ads.abcbank.R;
import com.ads.abcbank.fragment.Tab1Fragment;
import com.ads.abcbank.fragment.Tab2Fragment;
import com.ads.abcbank.fragment.Tab3Fragment;
import com.ads.abcbank.utils.Logger;

public abstract class BaseTabFragment extends Fragment {
    public Activity mActivity;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = getActivity();
    }

    //根部view
    private View rootView;
    protected Context context;
    private Boolean hasInitData = false;
    private boolean isVisiable;


    public TableLayout tlTab1;
    public TableLayout tlBottom1;
    public ScrollView svTab1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = getActivity();
        if (rootView == null) {
            rootView = initView(inflater);
        }

        tlTab1 = rootView.findViewById(R.id.tl_tab1);
        tlBottom1 = rootView.findViewById(R.id.tl_bottom1);
        svTab1 = rootView.findViewById(R.id.sv_tab1);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isVisiable && !hasInitData) {
            initData();
            hasInitData = true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null && rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    /**
     * 子类实现初始化View操作
     */
    public abstract View initView(LayoutInflater inflater);

    /**
     * 子类实现初始化数据操作(子类自己调用)
     */
    public abstract void initData();

    /**
     * 子类实现赋值数据操作(子类自己调用)
     */
    public abstract void setBean(Object bean);

    public abstract Object getBean();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisiable = getUserVisibleHint();
        if (rootView != null && isVisiable && !hasInitData) {
            try {
                initData();
                hasInitData = true;
            } catch (Exception e) {
                Logger.e(e.toString());
            }
        }
    }


    public void setBottomHeight(ViewTreeObserver.OnPreDrawListener preDrawListener) {
        if (svTab1 == null || tlTab1 == null || tlBottom1 == null) {
            return;
        }
        int sHeight = svTab1.getHeight();
        int tHeight = tlTab1.getHeight();
        int bHeight = sHeight - tHeight;
        bHeight = Math.max(bHeight, 0);
        ViewGroup.LayoutParams layoutParams = tlBottom1.getLayoutParams();
        layoutParams.height = bHeight;
        tlBottom1.setLayoutParams(layoutParams);
        tlTab1.getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
    }

    public static BaseTabFragment newInstance(BaseTabFragment baseTabFragment) {
        if (baseTabFragment instanceof Tab1Fragment) {
            Tab1Fragment fragment = new Tab1Fragment();
            fragment.setBean(baseTabFragment.getBean());
            fragment.setTempView(((Tab1Fragment) baseTabFragment).getTempView());
            fragment.setHandler(((Tab1Fragment) baseTabFragment).getHandler());
            fragment.setRunnable(((Tab1Fragment) baseTabFragment).getRunnable());
            return fragment;
        } else if (baseTabFragment instanceof Tab2Fragment) {
            Tab2Fragment fragment = new Tab2Fragment();
            fragment.setBean(baseTabFragment.getBean());
            fragment.setTempView(((Tab2Fragment) baseTabFragment).getTempView());
            fragment.setHandler(((Tab2Fragment) baseTabFragment).getHandler());
            fragment.setRunnable(((Tab2Fragment) baseTabFragment).getRunnable());
            return fragment;
        } else if (baseTabFragment instanceof Tab3Fragment) {
            Tab3Fragment fragment = new Tab3Fragment();
            fragment.setBean(baseTabFragment.getBean());
            fragment.setTempView(((Tab3Fragment) baseTabFragment).getTempView());
            fragment.setHandler(((Tab3Fragment) baseTabFragment).getHandler());
            fragment.setRunnable(((Tab3Fragment) baseTabFragment).getRunnable());
            return fragment;
        }
        return null;
    }

}
