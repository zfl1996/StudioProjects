package com.ads.abcbank.xx.ui.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ads.abcbank.R;

public class RateDoubleHolder extends RecyclerView.ViewHolder {

    TextView txtPre, txtKey;

    public RateDoubleHolder(View itemView) {
        super(itemView);

        txtPre = itemView.findViewById(R.id.tv_pre);
        txtKey = itemView.findViewById(R.id.tv_key);
    }

    public TextView getTxtPre() {
        return txtPre;
    }

    public TextView getTxtKey() {
        return txtKey;
    }
}
