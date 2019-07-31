package com.ads.abcbank.xx.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PresetBean;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RateSave2Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContent;
    private List<PresetBean.SaveRate.SaveRateItem> dataList = new ArrayList<>();
    private LayoutInflater inflater;

    public RateSave2Adapter(Context mContent) {
        this.mContent = mContent;
        this.inflater = LayoutInflater.from(mContent);
    }

    public void setDataSource(List<PresetBean.SaveRate.SaveRateItem> data){
        dataList.clear();
        dataList = data;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RateDoubleHolder viewHolder = new RateDoubleHolder(inflater.inflate(R.layout.widget_ui_slider_item_rate_item_2, parent, false));
        viewHolder.setDelayAdapter(this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RateDoubleHolder _holder = (RateDoubleHolder) holder;
        PresetBean.SaveRate.SaveRateItem rate = dataList.get(position);

        _holder.txtPre.setText(rate.item);
        _holder.txtKey.setText(rate.saveRate);

    }

    @Override
    public int getItemViewType(int position){
        return super.getItemViewType(position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class RateDoubleHolder extends RecyclerView.ViewHolder {

        TextView txtPre, txtKey;
        private WeakReference<RateSave2Adapter> ref;
        private RateSave2Adapter adapter;

        public RateDoubleHolder(View itemView) {
            super(itemView);

            txtPre = itemView.findViewById(R.id.tv_pre);
            txtKey = itemView.findViewById(R.id.tv_key);
        }

        public void setDelayAdapter(RateSave2Adapter adapter){
            if (null != adapter)
                ref = new WeakReference<RateSave2Adapter>(adapter);

            adapter = ref.get();
        }

        public TextView getTxtPre() {
            return txtPre;
        }

        public TextView getTxtKey() {
            return txtKey;
        }
    }

}

