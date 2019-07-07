package com.ads.abcbank.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.service.DownloadService;
import com.ads.abcbank.utils.JZMediaIjk;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.view.AutoVideoPlayer;
import com.ads.abcbank.view.BaseTempFragment;
import com.ads.abcbank.view.JZMediaSystemAssertFolder;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

import cn.jzvd.JZDataSource;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class VideoFragment extends BaseTempFragment {
    private View view;
    private AutoVideoPlayer content;
    private PlaylistBodyBean bean;
    private JZMediaSystemAssertFolder jzMediaSystemAssertFolder;
    private JZMediaIjk jzMediaIjk;

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
            } catch (Exception e) {
                Logger.e(e.toString());
            }
            content.setTempView(tempView);

            try {
                File file = new File(DownloadService.downloadVideoPath + bean.name);
                if (!file.exists()) {
                    jzMediaSystemAssertFolder = new JZMediaSystemAssertFolder(content);
                    content.setUp(jzDataSource
                            , JzvdStd.SCREEN_NORMAL);
                    content.setMediaInterface(jzMediaSystemAssertFolder);
                } else {
                    jzMediaIjk = new JZMediaIjk(content);
                    content.setUp(DownloadService.downloadVideoPath + bean.name
                            , "", Jzvd.SCREEN_NORMAL);
                    content.setMediaInterface(new JZMediaIjk(content));
                }
            } catch (Exception e) {
                Logger.e(e.toString());
            }
//            Glide.with(this).load(R.drawable.app_icon_your_company).into(content.thumbImageView);
            playerSeekTo();
            content.startVideo();
        }
    }

    private void playerSeekTo() {
        if (jzMediaSystemAssertFolder != null) {
            jzMediaSystemAssertFolder.seekTo(0);
        } else if (jzMediaIjk != null) {
            jzMediaIjk.seekTo(0);
        }
    }

    @Override
    public void setBean(PlaylistBodyBean bean) {
        this.bean = bean;
        initData();
        showQRs(bean);
    }

    @Override
    public PlaylistBodyBean getBean() {
        return bean;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (content != null && isVisiable) {
            playerSeekTo();
            content.startVideo();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (content != null && isVisiable) {
                playerSeekTo();
                content.startVideo();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (content != null) {
                playerSeekTo();
                content.startVideo();
            }
        } else {
            if (content != null) {
                AutoVideoPlayer.goOnPlayOnPause();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Jzvd.resetAllVideos();
    }
}
