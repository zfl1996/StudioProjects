package com.ads.abcbank.xx.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.ads.abcbank.R;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.view.AutoPollAdapter;
import com.ads.abcbank.view.AutoPollRecyclerView;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.view.IView;
import com.ads.abcbank.xx.ui.view.SliderPlayer;
import com.ads.abcbank.xx.utils.media.MediaController;
import com.alibaba.fastjson.JSONObject;
import com.pili.pldroid.player.widget.PLVideoView;

import java.util.ArrayList;

public class TempV2Activity extends BaseActivity implements IView {
    private static final String TAG = "TempV2Activity";

    SliderPlayer sliderPlayer;
    AutoPollAdapter autoPollAdapter;
    AutoPollRecyclerView rvMarqueeView;
    View v_set;
//    MarqueeView rvMarqueeView;
    PLVideoView mPlVideoView;

    ArrayList<String> data = new ArrayList<String>() {
        {
            add("中国农业银行欢迎您");
            add("中国农业银行欢迎您");
            add("中国农业银行欢迎您");
            add("中国农业银行欢迎您");
            add("中国农业银行欢迎您");
            add("中国农业银行欢迎您");
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_v2);
        setiView(this);

        initCtrls();
        initPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlVideoView.start();//开始播放
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlVideoView.pause();//暂停播放
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlVideoView.stopPlayback();//释放资源
    }


    void initPlayer() {
        mPlVideoView = findViewById(R.id.pl_video_view);
        String path = "/storage/emulated/0/ibcsPlayerData/videos/icbcs-jceeezjnk8y7q6kz.mp4";
//        String path = "http://hc.yinyuetai.com/uploads/videos/common/2B40015FD4683805AAD2D7D35A80F606.mp4?sc=364e86c8a7f42de3&br=783&rd=Android";
        //设置Video的路径
        mPlVideoView.setVideoPath(path);
        //设置MediaController，这里是拷贝官方Demo的MediaController，当然可以自己实现一个
        mPlVideoView.setMediaController(new MediaController(this));
        //设置视频预览模式
        mPlVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        //设置加载进度的布局
        mPlVideoView.setBufferingIndicator(findViewById(R.id.progress_bar));
    }

    private void initCtrls() {
        sliderPlayer = findViewById(R.id.sliderPlayer);
        rvMarqueeView = findViewById(R.id.rvMarqueeView);
        v_set = findViewById(R.id.v_set);

        autoPollAdapter = new AutoPollAdapter(TempV2Activity.this, data);
        rvMarqueeView.setLayoutManager(new LinearLayoutManager(TempV2Activity.this, LinearLayoutManager.HORIZONTAL, false));
        rvMarqueeView.setAdapter(autoPollAdapter);
        rvMarqueeView.start();

        new Handler().postDelayed(() -> rvMarqueeView.setVisibility(View.VISIBLE), 125);
    }

    @Override
    public void updateMainDate(JSONObject jsonObject) {
        Logger.e(TAG, jsonObject.toJSONString());
    }

    @Override
    public void updateBottomDate(JSONObject jsonObject) {
        Logger.e(TAG, jsonObject.toJSONString());
    }

    @Override
    public void updatePresetDate(JSONObject jsonObject) {
        Logger.e(TAG, jsonObject.toJSONString());
    }
}
