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

public class RateSave3Adapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContent;
    private List<PresetBean.LoanRate.LoanRateItem> dataList = new ArrayList<>();
    private LayoutInflater inflater;
    private int itemLayoutId;

    public RateSave3Adapter(Context mContent, int itemLayoutId) {
        this.mContent = mContent;
        this.inflater = LayoutInflater.from(mContent);
        this.itemLayoutId = itemLayoutId;
    }

    public void setDataSource(List<PresetBean.LoanRate.LoanRateItem> data){
        dataList.clear();
        dataList = data;
        int item = 20 - dataList.size();
        for (int i = 0; i < item; i++) {
            PresetBean.LoanRate.LoanRateItem item1 = new PresetBean.LoanRate.LoanRateItem();
            item1.item = "";
            item1.loanRate = "";
            dataList.add(item1);
        }
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RateSave3Adapter.RateDoubleHolder viewHolder = new RateSave3Adapter.RateDoubleHolder(inflater.inflate(/*R.layout.widget_ui_slider_item_rate_item_2*/itemLayoutId, parent, false));
        viewHolder.setDelayAdapter(this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        RateSave3Adapter.RateDoubleHolder _holder = (RateSave3Adapter.RateDoubleHolder) holder;
        PresetBean.LoanRate.LoanRateItem rate = dataList.get(position);
        String pre = (null == rate.placeholder) ? "" : rate.placeholder.replace("\\t", "\t");

        _holder.txtPre.setText(pre + (ResHelper.isNullOrEmpty(rate.item) ? "" : rate.item));
        _holder.txtKey.setText(ResHelper.isNullOrEmpty(rate.loanRate) ? "" : rate.loanRate );

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
        private WeakReference<RateSave3Adapter> ref;
        private RateSave3Adapter adapter;

        public RateDoubleHolder(View itemView) {
            super(itemView);

            txtPre = itemView.findViewById(R.id.tv_pre);
            txtKey = itemView.findViewById(R.id.tv_key);
        }

        public void setDelayAdapter(RateSave3Adapter adapter){
            if (null != adapter)
                ref = new WeakReference<RateSave3Adapter>(adapter);

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