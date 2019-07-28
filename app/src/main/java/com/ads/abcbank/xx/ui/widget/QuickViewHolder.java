package com.ads.abcbank.xx.ui.widget;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class QuickViewHolder extends RecyclerView.ViewHolder{
    private SparseArray<View> mViews;
    private View mConvertView;

    private QuickViewHolder(View v){
        super(v);
        mConvertView = v;
        mViews = new SparseArray<>();
    }

    public static QuickViewHolder get(ViewGroup parent, int layoutId){
        View convertView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new QuickViewHolder(convertView);
    }

    public <T extends View> T getView(int id){
        View v = mViews.get(id);
        if(v == null){
            v = mConvertView.findViewById(id);
            mViews.put(id, v);
        }
        return (T)v;
    }


}