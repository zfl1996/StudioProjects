package com.ads.abcbank.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.ads.abcbank.R;
import com.ads.abcbank.activity.WebViewActivity;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.service.DownloadService;
import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.BaseTempFragment;

import java.io.File;

public class ImageFragment extends BaseTempFragment implements View.OnClickListener {
    private View view;
    private ImageView content;
    private PlaylistBodyBean bean;

    @Override
    protected View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_image, null);
        getViews();
        return view;
    }

    private void getViews() {
        content = view.findViewById(R.id.content);
    }

    @Override
    public void initData() {
        if (bean != null && view != null && isVisiable) {
            try {
                Utils.loadImage(content, Uri.fromFile(new File(DownloadService.downloadImagePath + bean.name)));
            } catch (Exception e) {
                Utils.loadImage(content, "");
            }
            if (!TextUtils.isEmpty(bean.onClickLink)) {
                view.setOnClickListener(this);
            } else {
                view.setOnClickListener(null);
            }
        } else {
            Utils.loadImage(content, "");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, delayTime);
//        } else {
//            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (bean != null) {
                try {
                    Utils.loadImage(content, Uri.fromFile(new File(DownloadService.downloadImagePath + bean.name)));
                } catch (Exception e) {
                    Utils.loadImage(content, "");
                }
            } else {
                Utils.loadImage(content, "");
            }
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, delayTime);
//        } else {
//            handler.removeCallbacks(runnable);
        }
    }

    private long delayTime = 5000;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            if (!isVisiable) {
//                handler.removeCallbacks(runnable);
//                return;
//            }
            try {
                delayTime = Integer.
                        parseInt(Utils.
                                get(context, Utils.KEY_TIME_TAB_IMG, "5")
                                .toString()) * 1000;
            } catch (Exception e) {
                delayTime = 5000;
            }
            if (isVisiable && tempView != null && ActivityManager.getInstance().getTopActivity() == tempView.getContext()) {
                tempView.nextPlay();
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, delayTime);
            } else {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, delayTime);
            }
            if (isVisiable && tempView2 != null && ActivityManager.getInstance().getTopActivity() == tempView.getContext()) {
                tempView2.nextPlay();
            }
        }
    };

    @Override
    public void setBean(PlaylistBodyBean bean) {
        this.bean = bean;
        initData();
//        showQRs(bean);
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
