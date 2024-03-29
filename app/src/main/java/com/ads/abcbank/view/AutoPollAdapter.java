package com.ads.abcbank.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ads.abcbank.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/7/11.
 */

public class AutoPollAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private final Context mContext;
    private List<String> mData;

    public AutoPollAdapter(Context context, List<String> list) {
        this.mContext = context;
        this.mData = new ArrayList<>();

        mData.addAll(list);
    }

    public void addItemDataAndRedraw(List<String> data, boolean isAppend){
        if (isAppend) {
            mData.addAll(data);
            notifyItemRangeChanged(mData.size() - data.size(), data.size());
        } else {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }

    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_auto_poll, parent, false);
        BaseViewHolder holder = new BaseViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        String data;
        if (mData.size() == 0) {
            for (int i = 0; i < 6; i++) {
                mData.add("中国农业银行欢迎您");
            }
        }
        data = mData.get(position % mData.size());
        holder.setText(R.id.tv, data);
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }
}