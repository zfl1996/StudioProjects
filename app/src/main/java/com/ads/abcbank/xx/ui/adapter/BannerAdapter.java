package com.ads.abcbank.xx.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.xx.ui.widget.QuickViewHolder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class BannerAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContent;
    private List<String> dataList = new ArrayList<>();
    private LayoutInflater inflater;
    private static BannerRecyclerViewClickListener clickListener;

    public BannerAdapter(Context mContent) {
        this.mContent = mContent;
        this.inflater = LayoutInflater.from(mContent);

        this.dataList = dataList;
    }

    public void setDataSource(List<String> data){
        dataList.clear();
        dataList = data;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MainViewHolder viewHolder = new MainViewHolder(inflater.inflate(R.layout.fragment_pdf_cache_item, parent, false));
        viewHolder.setDelayAdapter(this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MainViewHolder _holder = (MainViewHolder) holder;
        String url = dataList.get(position);

        Glide.with(mContent)
                .load(url)
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

    public void setOnRechargeRecyclerViewClickListener(BannerRecyclerViewClickListener itemOnClickListener){
        clickListener = itemOnClickListener;
    }



    public interface BannerRecyclerViewClickListener {
        void onItemClick(int pos);
    }

    class MainViewHolder extends RecyclerView.ViewHolder {
        ImageView imgContent;

        private WeakReference<BannerAdapter> ref;
        private BannerAdapter adapter;

        MainViewHolder(View itemView){
            super(itemView);

            imgContent = (ImageView) itemView.findViewById(R.id.imgContent);
        }

        public void setDelayAdapter(BannerAdapter adapter){
            if (null != adapter)
                ref = new WeakReference<BannerAdapter>(adapter);

            adapter = ref.get();
        }

    }

}