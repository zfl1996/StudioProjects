package com.ads.abcbank.view.tableview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.DownloadBean;
import com.ads.abcbank.bean.PlaylistBean;
import com.ads.abcbank.bean.PlaylistBodyBean;

import java.util.List;

public class DownloadlistTableViewAdapter extends RecyclerView.Adapter<DownloadlistTableViewAdapter.PlaylistTableViewHolder> {
    private static final int TYPE_ROW = 0;
    private static final int TYPE_ROW_COLORFUL = 1;
    List<DownloadBean> downloadlistBeanList;
    private Context context;

    public DownloadlistTableViewAdapter(Context context, List<DownloadBean> downloadlistBeanList) {
        this.context = context;
        this.downloadlistBeanList = downloadlistBeanList;
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0) {
            return TYPE_ROW_COLORFUL;
        }

        return TYPE_ROW;
    }

    @Override
    public PlaylistTableViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ROW) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_playlist, viewGroup, false);
            return new PlaylistTableViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_playlist_colorful,
                    viewGroup, false);
            return new PlaylistTableViewHolder(view);
        }
    }
    @Override
    public void onBindViewHolder(PlaylistTableViewHolder holder, int position) {
        DownloadBean bodyBean = downloadlistBeanList.get(position);
        holder.txtId.setText(bodyBean.id == null ? "" : bodyBean.id);
        holder.txtName.setText(bodyBean.name == null ? "" : bodyBean.name);
        holder.txtPlayStatus.setText(bodyBean.status == null ? "等待下载" : bodyBean.status);
    }
    @Override
    public int getItemCount() {
        return downloadlistBeanList.size();
    }

    public class PlaylistTableViewHolder extends RecyclerView.ViewHolder {
        public TextView txtId, txtName, txtContentType, txtXelUrl, txtPlayTime, txtIsUrg, txtPlayStatus;

        public PlaylistTableViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.txtName);
            txtId = view.findViewById(R.id.txtId);
            txtContentType = view.findViewById(R.id.txtContentType);
            txtXelUrl = view.findViewById(R.id.txtXelUrl);
            txtPlayTime = view.findViewById(R.id.txtPlayTime);
            txtIsUrg = view.findViewById(R.id.txtIsUrg);
            txtPlayStatus = view.findViewById(R.id.txtPlayStatus);
            txtContentType.setVisibility(View.GONE);
            txtXelUrl.setVisibility(View.GONE);
            txtPlayTime.setVisibility(View.GONE);
            txtIsUrg.setVisibility(View.GONE);
        }
    }

}