package com.ads.abcbank.xx.ui.adapter.holder;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;

import com.ads.abcbank.R;
import com.ads.abcbank.activity.WebViewActivity;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.ui.view.QRView;
import com.ads.abcbank.xx.utils.helper.GuiHelper;
import com.ads.abcbank.xx.utils.helper.ResHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class SliderImageHolder extends RecyclerView.ViewHolder {

    public ImageView getImgContent() {
        return imgContent;
    }

    public View getIvGif() {
        return ivGif;
    }

    public ViewStub getQrStub() {
        return qrStub;
    }

    ImageView imgContent;
    View ivGif;

    ViewStub qrStub;

    public QRView getQrView() {
        return qrView;
    }

    public void setQrView(QRView qrView) {
        this.qrView = qrView;
    }

    QRView qrView;

    public SliderImageHolder(View itemView) {
        super(itemView);

        imgContent = itemView.findViewById(R.id.imgContent);
        ivGif = itemView.findViewById(R.id.iv_gif);
        qrStub = itemView.findViewById(R.id.qrStub);
    }

    public static void showImage(PlayItem item, SliderImageHolder holder) {
        if (!ResHelper.isNullOrEmpty(item.getUrl()))
            Glide.with(holder.getImgContent().getContext())
                    .load(item.getUrl())
                    .placeholder(GuiHelper.getBackgroundResource(holder.getImgContent().getContext()))
                    .error(GuiHelper.getBackgroundResource(holder.getImgContent().getContext()))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .dontAnimate()
                    .into(holder.getImgContent())
                    ;


//        if (!ResHelper.isNullOrEmpty(item.getClickLink()) && holder.getIvGif() != null) {
//            holder.getIvGif().setVisibility(View.VISIBLE);
//            holder.getIvGif().setOnClickListener(e -> {
//                Intent intent = new Intent(ActivityManager.getInstance().getTopActivity(), WebViewActivity.class);
//                intent.putExtra(Utils.WEBURL, item.getClickLink());
//                ActivityManager.getInstance().getTopActivity().startActivity(intent);
//            });
//        }


//        if (null != item.getAttData()){
//            try {
//                List<PlaylistBodyBean.QR> qrs = (List<PlaylistBodyBean.QR>)item.getAttData();
//
//                if (holder.getQrView() == null) {
//                    View v = holder.getQrStub().inflate();
////                QRView qrView = v.findViewById(R.id.qrContainer).findViewById(R.id.qrView);
////                qrView.showQRs(qrs);
//                    holder.setQrView(v.findViewById(R.id.qrContainer).findViewById(R.id.qrView));
//                }
//
//                holder.getQrView().showQRs(qrs);
//
//            } catch (Exception e) {
//                Logger.e("SliderImageHolder", e.getMessage());
//            }
//        }
    }

}
