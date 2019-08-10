package com.ads.abcbank.xx.ui.adapter.holder;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PresetBean;
import com.ads.abcbank.xx.ui.adapter.RateSave4Adapter;

public class SliderRateBuyHolder  extends RecyclerView.ViewHolder {

    TextView txtDesc, txtTitle;
    RecyclerView rvRate;
    LinearLayout llHeader;

    public SliderRateBuyHolder(View itemView) {
        super(itemView);

        txtTitle = itemView.findViewById(R.id.txtTitle);
        txtDesc = itemView.findViewById(R.id.txtDesc);
        rvRate = itemView.findViewById(R.id.rvRate);
        llHeader = itemView.findViewById(R.id.llHeader);
    }

    public static void showRate(PresetBean.BIAOFE biaofe,
                                TextView txtDesc,
                                TextView txtTitle,
                                RecyclerView rvRate,
                                LinearLayout llHeader,
                                boolean isShowHeader,
                                int itemLayout) {
        txtTitle.setText(biaofe.title);
        txtDesc.setText(biaofe.rem);

        if (null != llHeader)
            llHeader.setVisibility(isShowHeader ? View.VISIBLE : View.GONE);

        RateSave4Adapter adapter = new RateSave4Adapter(rvRate.getContext(), itemLayout);
        adapter.setDataSource(biaofe.entry);

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

    public LinearLayout getLlHeader() {
        return llHeader;
    }

}