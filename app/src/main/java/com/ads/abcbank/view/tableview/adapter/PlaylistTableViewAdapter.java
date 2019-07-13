package com.ads.abcbank.view.tableview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PlaylistBodyBean;

import java.util.List;

public class PlaylistTableViewAdapter extends RecyclerView.Adapter<PlaylistTableViewAdapter.PlaylistTableViewHolder> {
    private static final int TYPE_ROW = 0;
    private static final int TYPE_ROW_COLORFUL = 1;
    List<PlaylistBodyBean> playlistBeanList;
    private Context context;

    public PlaylistTableViewAdapter(Context context, List<PlaylistBodyBean> playlistBeanList) {
        this.context = context;
        this.playlistBeanList = playlistBeanList;
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
        PlaylistBodyBean bodyBean = playlistBeanList.get(position);

        holder.txtId.setText(bodyBean.id == null ? "" : bodyBean.id);
        holder.txtName.setText(bodyBean.name == null ? "" : bodyBean.name);
        holder.txtContentType.setText(bodyBean.contentType == null ? "" : bodyBean.contentType);
        holder.txtXelUrl.setText(bodyBean.downloadLink == null ? "" : bodyBean.downloadLink);
        holder.txtPlayTime.setText((bodyBean.playDate == null ? "" : bodyBean.playDate) + " - " + (bodyBean.stopDate == null ? "" : bodyBean.stopDate));
        holder.txtIsUrg.setText(bodyBean.isUrg == null ? "0" : bodyBean.isUrg);
        holder.txtPlayStatus.setText(bodyBean.trCode == null ? "" : bodyBean.trCode);
    }

    @Override
    public int getItemCount() {
        return playlistBeanList.size();
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
        }
    }

}