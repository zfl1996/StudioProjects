package com.ads.abcbank.xx.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.ui.adapter.holder.SliderImageHolder;
import com.ads.abcbank.xx.ui.adapter.holder.SliderVideoHolder;
import com.ads.abcbank.xx.utils.BllDataExtractor;
import com.ads.abcbank.xx.utils.Constants;
import com.ads.abcbank.xx.utils.helper.ResHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SliderMainAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContent;
    private List<PlayItem> dataList = new ArrayList<>();
    private LayoutInflater inflater;
    private static SliderAdapter.BannerRecyclerViewClickListener clickListener;

    public SliderMainAdapter(Context mContent) {
        this.mContent = mContent;
        this.inflater = LayoutInflater.from(mContent);

        this.dataList = dataList;
    }

    public void addItemDataAndRedraw(List<PlayItem> data){
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void addItemDataAndRedraw(PlayItem dataItem) {
        dataList.add(dataItem);
        notifyItemRangeChanged(dataList.size() - 2, 1);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (Constants.SLIDER_HOLDER_IMAGE == viewType)
            return new SliderImageHolder(inflater.inflate(R.layout.widget_ui_slider_item_img, parent, false));
        else if (Constants.SLIDER_HOLDER_VIDEO == viewType)
            return new SliderVideoHolder(inflater.inflate(R.layout.widget_ui_slider_item_video, parent, false));
        else
            return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PlayItem item = dataList.get(position);

        if (holder instanceof SliderImageHolder)
            SliderImageHolder.showImage(item, ((SliderImageHolder)holder).getImgContent());
        else if (holder instanceof SliderVideoHolder)
            ((SliderVideoHolder) holder).setVideoPath(item.getUrl());

    }


    @Override
    public int getItemViewType(int position){
        PlayItem item = dataList.get(position);
        if (null != item)
            return item.getMediaType();

        return super.getItemViewType(position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof SliderVideoHolder) {
            SliderVideoHolder _holder = (SliderVideoHolder) holder;

            _holder.getVideoContent().setVideoPath(_holder.getVideoPath());
            _holder.getVideoContent().start();
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof SliderVideoHolder) {
            SliderVideoHolder _holder = (SliderVideoHolder) holder;

            _holder.getVideoContent().pause();
            _holder.getVideoContent().stopPlayback();
        }
    }

}
