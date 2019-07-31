package com.ads.abcbank.xx.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PresetBean;
import com.ads.abcbank.xx.utils.helper.ResHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RateSave4Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContent;
    private List<PresetBean.BIAOFE.BIAOFEItem> dataList = new ArrayList<>();
    private LayoutInflater inflater;

    public RateSave4Adapter(Context mContent) {
        this.mContent = mContent;
        this.inflater = LayoutInflater.from(mContent);
    }

    public void setDataSource(List<PresetBean.BIAOFE.BIAOFEItem> data){
        dataList.clear();
        dataList = data;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RateQuadHolder viewHolder = new RateQuadHolder(inflater.inflate(R.layout.widget_ui_slider_item_rate_item_4, parent, false));
        viewHolder.setDelayAdapter(this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RateSave4Adapter.RateQuadHolder _holder = (RateSave4Adapter.RateQuadHolder) holder;
        PresetBean.BIAOFE.BIAOFEItem rate = dataList.get(position);

        if (!ResHelper.isNullOrEmpty(rate.placeholder)) {
            _holder.getTxtKey().setText(rate.placeholder.replace("\\t", "\t") + rate.currCName);
        } else {
            _holder.getTxtKey().setText(rate.currCName);
        }
        _holder.getTxtVal().setText(rate.buyPrice);
        _holder.getTxtVal2().setText(rate.sellPrice);
        _holder.getTxtVal3().setText(rate.cashPrice);

    }

    @Override
    public int getItemViewType(int position){
        return super.getItemViewType(position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class RateQuadHolder extends RecyclerView.ViewHolder {

        TextView txtKey, txtVal, txtVal2, txtVal3;

        private WeakReference<RateSave4Adapter> ref;
        private RateSave4Adapter adapter;

        public RateQuadHolder(View itemView) {
            super(itemView);

            txtKey = itemView.findViewById(R.id.tv_key);
            txtVal = itemView.findViewById(R.id.tv_value);
            txtVal2 = itemView.findViewById(R.id.tv_value2);
            txtVal3 = itemView.findViewById(R.id.tv_value3);
        }

        public void setDelayAdapter(RateSave4Adapter adapter){
            if (null != adapter)
                ref = new WeakReference<RateSave4Adapter>(adapter);

            adapter = ref.get();
        }

        public TextView getTxtKey() {
            return txtKey;
        }

        public TextView getTxtVal() {
            return txtVal;
        }

        public TextView getTxtVal2() {
            return txtVal2;
        }

        public TextView getTxtVal3() {
            return txtVal3;
        }

    }

}
