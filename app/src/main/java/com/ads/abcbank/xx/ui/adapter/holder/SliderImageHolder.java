package com.ads.abcbank.xx.ui.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.ads.abcbank.R;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.utils.helper.ResHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class SliderImageHolder extends RecyclerView.ViewHolder {

    public ImageView getImgContent() {
        return imgContent;
    }

    ImageView imgContent;

    public SliderImageHolder(View itemView) {
        super(itemView);

        imgContent = itemView.findViewById(R.id.imgContent);
    }

    public static void showImage(PlayItem item, ImageView imageView) {
        if (!ResHelper.isNullOrEmpty(item.getUrl()))
            Glide.with(imageView.getContext())
                    .load(item.getUrl())
                    .placeholder(R.drawable.default_background)
                    .error(R.drawable.default_background)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .dontAnimate()
                    .into(imageView);
    }

}
