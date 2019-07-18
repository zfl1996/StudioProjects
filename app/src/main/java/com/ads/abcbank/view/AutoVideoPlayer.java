package com.ads.abcbank.view;

import android.content.Context;
import android.util.AttributeSet;

import com.ads.abcbank.R;
import com.ads.abcbank.utils.Logger;

import cn.jzvd.JzvdStd;

public class AutoVideoPlayer extends JzvdStd {
    public AutoVideoPlayer(Context context) {
        super(context);
    }

    public AutoVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private TempView tempView;

    public void setTempView(TempView tempView) {
        this.tempView = tempView;
    }

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        if (tempView != null) {
            tempView.nextPlay();
        }
//        startVideo();
    }

    @Override
    public void onStateError() {
        Logger.e(this.getClass().toString(), "播放出错");
        if (tempView != null) {
            tempView.fileHadDel();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.jz_layout_std;
    }
}