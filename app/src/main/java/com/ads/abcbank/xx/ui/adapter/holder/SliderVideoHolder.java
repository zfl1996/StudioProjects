package com.ads.abcbank.xx.ui.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.ui.view.QRView;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.util.List;

public class SliderVideoHolder  extends RecyclerView.ViewHolder {

    PlayItem item;
//    String videoPath;
    PLVideoTextureView videoContent;

    ViewStub qrStub;

    public SliderVideoHolder(View itemView) {
        super(itemView);

        videoContent = itemView.findViewById(R.id.videoContent);
        qrStub = itemView.findViewById(R.id.qrStub);
    }

    public String getVideoPath() {
        return item.getUrl();
    }

    public ViewStub getQrStub() {
        return qrStub;
    }

//    public void setVideoPath(String videoPath) {
//        this.videoPath = videoPath;
//    }

    public void setVideoData(PlayItem item) {
        this.item = item;
    }

    public void showQrs(SliderVideoHolder holder) {
        if (null != item.getAttData()){
            try {
                List<PlaylistBodyBean.QR> qrs = (List<PlaylistBodyBean.QR>)item.getAttData();
                View v = holder.getQrStub().inflate();
                QRView qrView = v.findViewById(R.id.qrContainer).findViewById(R.id.qrView);
                qrView.showQRs(qrs);

            } catch (Exception e) {
                Logger.e("SliderImageHolder", e.getMessage());
            }
        }
    }

    public PLVideoTextureView getVideoContent() {
        return videoContent;
    }

    public interface VideoStatusListener {
        void onStartPlay();
        void onPlayFinish();
    }
}
