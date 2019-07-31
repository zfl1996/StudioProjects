package com.ads.abcbank.xx.ui.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ads.abcbank.R;

public class RateQuadHolder extends RecyclerView.ViewHolder {

    TextView txtKey, txtVal, txtVal2, txtVal3;

    public RateQuadHolder(View itemView) {
        super(itemView);

        txtKey = itemView.findViewById(R.id.tv_key);
        txtVal = itemView.findViewById(R.id.tv_value);
        txtVal2 = itemView.findViewById(R.id.tv_value2);
        txtVal3 = itemView.findViewById(R.id.tv_value3);
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