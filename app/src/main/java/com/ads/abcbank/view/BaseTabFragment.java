package com.ads.abcbank.view;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.ads.abcbank.R;

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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisiable = getUserVisibleHint();
        if (rootView != null && isVisiable && !hasInitData) {
            try {
                initData();
                hasInitData = true;
            } catch (Exception e) {
            }
        }
    }


    public void setBottomHeight(ViewTreeObserver.OnPreDrawListener preDrawListener) {
        if (svTab1 == null || tlTab1 == null || tlBottom1 == null) return;
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
