package com.ads.abcbank.view.tableview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.DownloadBean;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.view.tableview.adapter.DownloadlistTableViewAdapter;
import com.ads.abcbank.view.tableview.adapter.PlaylistTableViewAdapter;
import com.ads.abcbank.view.tableview.util.FixedGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class DownloadlistTableView extends LinearLayout {
    private View rootView;
    private int scrollX;
    private TextView txtContentType, txtXelUrl, txtPlayTime, txtIsUrg, txtPlayStatus;
    private FixedGridLayoutManager manager;

    public void setPlaylistBeanList(List<PlaylistBodyBean> playlistBeanList) {
        this.playlistBeanList = playlistBeanList;
        setUpRecyclerView();
    }

    List<PlaylistBodyBean> playlistBeanList;

    RecyclerView rvPlaylist;

    HorizontalScrollView headerScroll;

    DownloadlistTableViewAdapter clubAdapter;
    Context mContext;

    public DownloadlistTableView(Context context) {
        super(context);
        this.mContext = context;
        initViews();
    }

    public DownloadlistTableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initViews();
    }

    private void initViews() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.table_playlist, null);
        rvPlaylist = rootView.findViewById(R.id.rvPlaylist);
        headerScroll = rootView.findViewById(R.id.headerScroll);
        txtContentType = rootView.findViewById(R.id.txtContentType);
        txtXelUrl = rootView.findViewById(R.id.txtXelUrl);
        txtPlayTime = rootView.findViewById(R.id.txtPlayTime);
        txtIsUrg = rootView.findViewById(R.id.txtIsUrg);
        txtPlayStatus = rootView.findViewById(R.id.txtPlayStatus);
        txtPlayStatus.setText("DownloadStatus");
        txtContentType.setVisibility(View.GONE);
        txtXelUrl.setVisibility(View.GONE);
        txtPlayTime.setVisibility(View.GONE);
        txtIsUrg.setVisibility(View.GONE);
        rvPlaylist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                scrollX += dx;

                headerScroll.scrollTo(scrollX, 0);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        addView(rootView);
        setUpRecyclerView();
    }


    /**
     * Handles RecyclerView for the action
     */
    private void setUpRecyclerView() {
        if (playlistBeanList == null) {
            playlistBeanList = new ArrayList<>();
        }
        try {
            clubAdapter = null;
            clubAdapter = new DownloadlistTableViewAdapter(mContext, playlistBeanList);
            manager = new FixedGridLayoutManager();
            manager.setTotalColumnCount(1);
            rvPlaylist.setLayoutManager(manager);
            rvPlaylist.setAdapter(clubAdapter);
            rvPlaylist.removeItemDecoration(rvPlaylist.getItemDecorationAt(0));
            rvPlaylist.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
            clubAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            clubAdapter = null;
            Logger.e(e.toString());
        }
    }
}
