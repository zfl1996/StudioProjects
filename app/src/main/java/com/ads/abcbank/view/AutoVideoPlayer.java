package com.ads.abcbank.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.jzvd.JzvdStd;

/**
 * Created by Administrator on 2019/5/22.
 */

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
}