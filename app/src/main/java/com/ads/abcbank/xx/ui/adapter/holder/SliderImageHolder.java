package com.ads.abcbank.xx.ui.adapter.holder;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.ads.abcbank.R;
import com.ads.abcbank.activity.WebViewActivity;
import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.utils.helper.ResHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class SliderImageHolder extends RecyclerView.ViewHolder {

    public ImageView getImgContent() {
        return imgContent;
    }

    public View getIvGif() {
        return ivGif;
    }

    ImageView imgContent;
    View ivGif;

    public SliderImageHolder(View itemView) {
        super(itemView);

        imgContent = itemView.findViewById(R.id.imgContent);
        ivGif = itemView.findViewById(R.id.iv_gif);
    }

    public static void showImage(PlayItem item, SliderImageHolder holder) {
        if (!ResHelper.isNullOrEmpty(item.getUrl()))
            Glide.with(holder.getImgContent().getContext())
                    .load(item.getUrl())
                    .placeholder(R.drawable.default_background)
                    .error(R.drawable.default_background)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .dontAnimate()
                    .into(holder.getImgContent());


        if (!TextUtils.isEmpty(item.getClickLink()) && holder.getIvGif() != null) {
            holder.getIvGif().setVisibility(View.VISIBLE);
            holder.getIvGif().setOnClickListener(e -> {
                Intent intent = new Intent(ActivityManager.getInstance().getTopActivity(), WebViewActivity.class);
                intent.putExtra(Utils.WEBURL, item.getClickLink());
                ActivityManager.getInstance().getTopActivity().startActivity(intent);
            });
        }
    }

}
