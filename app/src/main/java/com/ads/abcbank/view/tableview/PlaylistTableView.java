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

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.view.tableview.adapter.PlaylistTableViewAdapter;
import com.ads.abcbank.view.tableview.util.FixedGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class PlaylistTableView extends LinearLayout {
    private View rootView;
    private int scrollX;

    public void setPlaylistBeanList(List<PlaylistBodyBean> playlistBeanList) {
        this.playlistBeanList = playlistBeanList;
        setUpRecyclerView();
    }

    List<PlaylistBodyBean> playlistBeanList;

    RecyclerView rvPlaylist;

    HorizontalScrollView headerScroll;

    PlaylistTableViewAdapter clubAdapter;
    Context mContext;

    public PlaylistTableView(Context context) {
        super(context);
        this.mContext = context;
        initViews();
    }

    public PlaylistTableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initViews();
    }

    private void initViews() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.table_playlist, null);
        rvPlaylist = rootView.findViewById(R.id.rvPlaylist);
        headerScroll = rootView.findViewById(R.id.headerScroll);
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
            clubAdapter = new PlaylistTableViewAdapter(mContext, playlistBeanList);
            FixedGridLayoutManager manager = new FixedGridLayoutManager();
            manager.setTotalColumnCount(1);
            rvPlaylist.setLayoutManager(manager);
            rvPlaylist.setAdapter(clubAdapter);
            rvPlaylist.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
            clubAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Logger.e(e.toString());
        }
    }
}
