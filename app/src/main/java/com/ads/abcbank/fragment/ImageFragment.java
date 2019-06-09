package com.ads.abcbank.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.ads.abcbank.R;
import com.ads.abcbank.activity.WebViewActivity;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.BaseTempFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.lang.ref.WeakReference;

public class ImageFragment extends BaseTempFragment implements View.OnClickListener {
    private View view;
    private ImageView content;
    private static PlaylistBodyBean bean;

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
            Utils.loadImage(content, bean.downloadLink);
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
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
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
            if (tempView != null && ActivityManager.getInstance().getTopActivity() == tempView.getContext())
                tempView.nextPlay();
            else
                handler.postDelayed(runnable, delayTime);
            if (tempView2 != null && ActivityManager.getInstance().getTopActivity() == tempView.getContext())
                tempView2.nextPlay();
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
