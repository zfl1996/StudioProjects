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
import com.ads.abcbank.xx.utils.helper.ResHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContent;
    private List<PlayItem> dataList = new ArrayList<>();
    private LayoutInflater inflater;
    private static SliderAdapter.BannerRecyclerViewClickListener clickListener;

    public SliderAdapter(Context mContent) {
        this.mContent = mContent;
        this.inflater = LayoutInflater.from(mContent);

        this.dataList = dataList;
    }

    public void addItemDataAndRedraw(List<PlayItem> data){
//        dataList.clear();
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void addItemDataAndRedraw(PlayItem dataItem) {
        dataList.add(dataItem);
//        notifyDataSetChanged();
        notifyItemRangeChanged(dataList.size() - 2, 1);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SliderAdapter.MainViewHolder viewHolder = new SliderAdapter.MainViewHolder(inflater.inflate(R.layout.widget_ui_slider_item, parent, false));
        viewHolder.setDelayAdapter(this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SliderAdapter.MainViewHolder _holder = (SliderAdapter.MainViewHolder) holder;
        PlayItem item = dataList.get(position);
        _holder.videoPath = "";

        if (item.getMediaType() == 0) {
            Glide.with(mContent)
                    .load(item.getUrl())
                    .placeholder(R.drawable.default_background)
                    .error(R.drawable.default_background)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .dontAnimate()
                    .into(_holder.getImgContent());
            _holder.imgContent.setVisibility(View.VISIBLE);
        } else if (item.getMediaType() == 2)
            _holder.videoPath = item.getUrl();

        _holder.txtHint.setText("" + position);
    }


    @Override
    public int getItemViewType(int position){
        return super.getItemViewType(position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        SliderAdapter.MainViewHolder _holder = (SliderAdapter.MainViewHolder) holder;

        if (!ResHelper.isNullOrEmpty(_holder.videoPath)) {
            _holder.imgContent.setVisibility(View.GONE);
//            _holder.getmVideoView().setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_PAVED_PARENT);
            _holder.getmVideoView().setVideoPath(_holder.videoPath);
            _holder.getmVideoView().start();
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        SliderAdapter.MainViewHolder _holder = (SliderAdapter.MainViewHolder) holder;

        if (!ResHelper.isNullOrEmpty(_holder.videoPath)) {
            _holder.getmVideoView().pause();
            _holder.getmVideoView().stopPlayback();
        }
    }

    public void setOnRechargeRecyclerViewClickListener(SliderAdapter.BannerRecyclerViewClickListener itemOnClickListener){
        clickListener = itemOnClickListener;
    }


    public interface BannerRecyclerViewClickListener {
        void onItemClick(int pos);
    }

    class MainViewHolder extends RecyclerView.ViewHolder {
        public ImageView getImgContent() {
            if (null == imgContent) {
                imgContent = imgStub.inflate().findViewById(R.id.imgContent);
            }

            return imgContent;
        }

        public PLVideoTextureView getmVideoView() {
            if (null == videoContent) {
                videoContent = videoStub.inflate().findViewById(R.id.videoContent);
            }

            return videoContent;
        }

        PLVideoTextureView videoContent;
        ImageView imgContent;
        ViewStub imgStub, videoStub;
        String videoPath;
        TextView txtHint;

        private WeakReference<SliderAdapter> ref;
        private SliderAdapter adapter;

        MainViewHolder(View itemView){
            super(itemView);

            imgStub = itemView.findViewById(R.id.imgStub);
            videoStub = itemView.findViewById(R.id.videoStub);
//            imgContent = (ImageView) itemView.findViewById(R.id.imgContent);
            txtHint = itemView.findViewById(R.id.txtHint);
        }

        public void setDelayAdapter(SliderAdapter adapter){
            if (null != adapter)
                ref = new WeakReference<SliderAdapter>(adapter);

            adapter = ref.get();
        }

    }

}