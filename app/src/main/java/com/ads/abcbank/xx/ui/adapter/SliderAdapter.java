package com.ads.abcbank.xx.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ads.abcbank.R;
import com.ads.abcbank.xx.model.PlayItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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
        dataList.clear();
//        dataList = data;
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
        SliderAdapter.MainViewHolder viewHolder = new SliderAdapter.MainViewHolder(inflater.inflate(R.layout.fragment_pdf_cache_item, parent, false));
        viewHolder.setDelayAdapter(this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SliderAdapter.MainViewHolder _holder = (SliderAdapter.MainViewHolder) holder;
        PlayItem item = dataList.get(position);

        Glide.with(mContent)
                .load(item.getUrl())
                .placeholder(R.drawable.default_background)
                .error(R.drawable.default_background)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .into(_holder.imgContent);
    }

    @Override
    public int getItemViewType(int position){
        return super.getItemViewType(position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setOnRechargeRecyclerViewClickListener(SliderAdapter.BannerRecyclerViewClickListener itemOnClickListener){
        clickListener = itemOnClickListener;
    }


    public interface BannerRecyclerViewClickListener {
        void onItemClick(int pos);
    }

    class MainViewHolder extends RecyclerView.ViewHolder {
        ImageView imgContent;

        private WeakReference<SliderAdapter> ref;
        private SliderAdapter adapter;

        MainViewHolder(View itemView){
            super(itemView);

            imgContent = (ImageView) itemView.findViewById(R.id.imgContent);
        }

        public void setDelayAdapter(SliderAdapter adapter){
            if (null != adapter)
                ref = new WeakReference<SliderAdapter>(adapter);

            adapter = ref.get();
        }

    }

}