package com.ads.abcbank.fragment;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.activity.WebViewActivity;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.service.DownloadService;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.AutoScrollView;
import com.ads.abcbank.view.BaseTempFragment;

import java.io.File;
import java.io.IOException;

public class TxtFragment extends BaseTempFragment implements View.OnClickListener {
    private View view;
    private TextView content;
    private AutoScrollView scrollView;
    private static PlaylistBodyBean bean;
    private int rate = 30;

    @Override
    protected View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_txt, null);
        getViews();
        return view;
    }

    private void getViews() {
        content = view.findViewById(R.id.content);
        scrollView = view.findViewById(R.id.scrollView);
    }

    @Override
    public void initData() {
        if (bean != null && view != null && isVisiable && context != null) {
            try {
//                content.setText(Utils.getTxtString(context, bean.name));//TODO 此处要替换真实文件路径
                File file = new File(DownloadService.downloadFilePath, bean.name);
                if (file.exists()) {
                    content.setText(Utils.getTxtString(context, bean.name));
                } else {
                    if (tempView != null) {
                        tempView.nextPlay();
                    }
                    return;
                }
                scrollView.setAutoToScroll(true);//设置可以自动滑动
                scrollView.setFistTimeScroll(1000);//设置第一次自动滑动的时间
                scrollView.setScrollRate(rate);//设置滑动的速率
                scrollView.setScrollLoop(false);//设置是否循环滑动
                scrollView.scrollTo(0, 0);

                scrollView.setScanScrollChangedListener(new AutoScrollView.ISmartScrollChangedListener() {
                    @Override
                    public void onScrolledToBottom() {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.scrollTo(0, 0);
                                if (tempView != null) {
                                    tempView.nextPlay();
                                }
                            }
                        }, 1000);
                    }

                    @Override
                    public void onScrolledToTop() {
                    }
                });
            } catch (IOException e) {
                Logger.e(e.toString());
            }
            if (!TextUtils.isEmpty(bean.onClickLink)) {
                view.setOnClickListener(this);
            } else {
                view.setOnClickListener(null);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (scrollView != null) {
            scrollView.startScroll();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (scrollView != null) {
                scrollView.startScroll();
            }
        } else {
            if (scrollView != null) {
                scrollView.stopScroll();
            }
        }
    }

    private Handler handler = new Handler();

    @Override
    public void setBean(PlaylistBodyBean bean) {
        TxtFragment.bean = bean;
        initData();
        showQRs(bean);
    }

    @Override
    public PlaylistBodyBean getBean() {
        return bean;
    }

    @Override
    public void onClick(View v) {
        if (!TextUtils.isEmpty(bean.onClickLink)) {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(Utils.WEBURL, bean.onClickLink);
            startActivity(intent);
        }
    }
}
