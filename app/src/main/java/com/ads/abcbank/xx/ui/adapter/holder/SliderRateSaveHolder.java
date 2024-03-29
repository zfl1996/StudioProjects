package com.ads.abcbank.xx.ui.adapter.holder;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PresetBean;
import com.ads.abcbank.xx.ui.adapter.RateSave2Adapter;

public class SliderRateSaveHolder extends RecyclerView.ViewHolder {

    TextView txtDesc, txtTitle;
    RecyclerView rvRate;

    LinearLayout llHeader;

    public SliderRateSaveHolder(View itemView) {
        super(itemView);

        txtTitle = itemView.findViewById(R.id.txtTitle);
        txtDesc = itemView.findViewById(R.id.txtDesc);
        rvRate = itemView.findViewById(R.id.rvRate);
        llHeader = itemView.findViewById(R.id.llHeader);
    }

    public static void showRate(PresetBean.SaveRate saveRate,
                                SliderRateSaveHolder holder,
                                boolean isShowHeader,
                                int itemLayout) {
        holder.getTxtTitle().setText(saveRate.title);
        holder.getTxtDesc().setText(saveRate.rem);

        if (null != holder.getLlHeader())
            holder.getLlHeader().setVisibility(isShowHeader ? View.VISIBLE : View.GONE);

        RateSave2Adapter adapter = new RateSave2Adapter(holder.getRvRate().getContext(), itemLayout);
        adapter.setDataSource(saveRate.entry);

        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.getRvRate().getContext());
        holder.getRvRate().setLayoutManager(layoutManager);
        holder.getRvRate().setHasFixedSize(false);
//        rvRate.addItemDecoration(new DividerItemDecoration(rvRate.getContext(), DividerItemDecoration.VERTICAL));
        holder.getRvRate().setAdapter(adapter);
    }

    public TextView getTxtDesc() {
        return txtDesc;
    }

    public TextView getTxtTitle() {
        return txtTitle;
    }

    public RecyclerView getRvRate() {
        return rvRate;
    }

    public LinearLayout getLlHeader() {
        return llHeader;
    }

}
