package com.ads.abcbank.xx.ui.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ads.abcbank.R;
import com.pili.pldroid.player.widget.PLVideoTextureView;

public class SliderVideoHolder  extends RecyclerView.ViewHolder {

    String videoPath;
    PLVideoTextureView videoContent;

    public SliderVideoHolder(View itemView) {
        super(itemView);

        videoContent = itemView.findViewById(R.id.videoContent);
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public PLVideoTextureView getVideoContent() {
        return videoContent;
    }

    public interface VideoStatusListener {
        void onStartPlay();
        void onPlayFinish();
    }
}
