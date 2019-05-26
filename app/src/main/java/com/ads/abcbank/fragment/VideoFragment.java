package com.ads.abcbank.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.view.AutoVideoPlayer;
import com.ads.abcbank.view.BaseTempFragment;
import com.ads.abcbank.view.JZMediaSystemAssertFolder;
import com.bumptech.glide.Glide;

import java.io.IOException;

import cn.jzvd.JZDataSource;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class VideoFragment extends BaseTempFragment {
    private View view;
    private AutoVideoPlayer content;
    private PlaylistBodyBean bean;

    @Override
    protected View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_video, null);
        getViews();
        return view;
    }

    private void getViews() {
        content = view.findViewById(R.id.content);
    }

    @Override
    public void initData() {
        if (bean != null && content != null) {
            JZDataSource jzDataSource = null;
            try {
                jzDataSource = new JZDataSource(context.getAssets().openFd("local_video.mp4"));
                jzDataSource.title = "";
            } catch (IOException e) {
                e.printStackTrace();
            }

            content.setUp(jzDataSource
                    , JzvdStd.SCREEN_NORMAL);
            Glide.with(this).load(R.drawable.app_icon_your_company).into(content.thumbImageView);
            content.setMediaInterface(new JZMediaSystemAssertFolder(content));
            content.startVideo();
            content.setTempView(tempView);
        }
    }

    @Override
    public void setBean(PlaylistBodyBean bean) {
        this.bean = bean;
        initData();
        showQRs(bean);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (content != null)
            content.startVideo();
    }

    @Override
    public void onPause() {
        super.onPause();
        Jzvd.resetAllVideos();
    }
}
