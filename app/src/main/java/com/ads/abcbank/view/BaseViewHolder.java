package com.ads.abcbank.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Administrator on 2019/7/11.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {
    private View view;

    public BaseViewHolder(View view) {
        super(view);
        this.view = view;
    }

    public void setText(int id, String value) {
        TextView textView = view.findViewById(id);
        textView.setText(value);
    }
}
