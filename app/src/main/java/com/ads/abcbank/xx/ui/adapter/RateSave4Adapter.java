package com.ads.abcbank.xx.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
    private int itemLayoutId;

    public RateSave4Adapter(Context mContent, int itemLayoutId) {
        this.mContent = mContent;
        this.inflater = LayoutInflater.from(mContent);
        this.itemLayoutId = itemLayoutId;
    }

    public void setDataSource(List<PresetBean.BIAOFE.BIAOFEItem> data){
        dataList.clear();
        dataList = data;
        int item = 30 - dataList.size();
        for (int i = 0; i < item; i++) {
            PresetBean.BIAOFE.BIAOFEItem item1 = new PresetBean.BIAOFE.BIAOFEItem();
            item1.currCName = "";
            item1.buyPrice = "";
            item1.sellPrice = "";
            item1.cashPrice = "";
            dataList.add(item1);
        }
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RateQuadHolder viewHolder = new RateQuadHolder(inflater.inflate(/*R.layout.widget_ui_slider_item_rate_item_4*/itemLayoutId, parent, false));
        viewHolder.setDelayAdapter(this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RateSave4Adapter.RateQuadHolder _holder = (RateSave4Adapter.RateQuadHolder) holder;
        PresetBean.BIAOFE.BIAOFEItem rate = dataList.get(position);

        if (null != rate.placeholder) {
            _holder.getTxtKey().setText(rate.placeholder.replace("\\t", "\t") + rate.currCName);
        } else {
            _holder.getTxtKey().setText(rate.currCName);
        }

        _holder.getTxtVal().setText(ResHelper.isNullOrEmpty(rate.buyPrice) ? "" : rate.buyPrice );
        _holder.getTxtVal2().setText(ResHelper.isNullOrEmpty(rate.sellPrice) ? "" : rate.sellPrice);
        _holder.getTxtVal3().setText(ResHelper.isNullOrEmpty(rate.cashPrice) ? "" : rate.cashPrice);

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
