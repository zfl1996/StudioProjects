package com.ads.abcbank.xx.ui.adapter.holder;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PresetBean;
import com.ads.abcbank.xx.ui.adapter.RateSave3Adapter;

public class SliderRateLoanHolder  extends RecyclerView.ViewHolder {

    TextView txtDesc, txtTitle;
    RecyclerView rvRate;

    public SliderRateLoanHolder(View itemView) {
        super(itemView);

        txtTitle = itemView.findViewById(R.id.txtTitle);
        txtDesc = itemView.findViewById(R.id.txtDesc);
        rvRate = itemView.findViewById(R.id.rvRate);
    }

    public static void showRate(PresetBean.LoanRate loanRate, TextView txtDesc, TextView txtTitle, RecyclerView rvRate) {
        txtTitle.setText(loanRate.title);
        txtDesc.setText(loanRate.rem);

        RateSave3Adapter adapter = new RateSave3Adapter(rvRate.getContext());
        adapter.setDataSource(loanRate.entry);

        LinearLayoutManager layoutManager = new LinearLayoutManager(rvRate.getContext());
        rvRate.setLayoutManager(layoutManager);
        rvRate.setHasFixedSize(false);
//        rvRate.addItemDecoration(new DividerItemDecoration(rvRate.getContext(), DividerItemDecoration.VERTICAL));
        rvRate.setAdapter(adapter);
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

}