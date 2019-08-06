package com.ads.abcbank.xx.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PresetBean;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.ui.adapter.holder.SliderImageHolder;
import com.ads.abcbank.xx.ui.adapter.holder.SliderRateBuyHolder;
import com.ads.abcbank.xx.ui.adapter.holder.SliderRateLoanHolder;
import com.ads.abcbank.xx.ui.adapter.holder.SliderRateSaveHolder;
import com.ads.abcbank.xx.ui.adapter.holder.SliderVideoHolder;
import com.ads.abcbank.xx.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.pili.pldroid.player.PLOnInfoListener.MEDIA_INFO_VIDEO_RENDERING_START;

public class SliderMainAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    static String TAG = "SliderMainAdapter";

    private Context mContent;
    private List<PlayItem> dataList = new ArrayList<>();
    private LayoutInflater inflater;
    //    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private SliderVideoHolder.PlayStatusListener playStatusListener;

    public SliderMainAdapter(Context mContent) {
        this.mContent = mContent;
        this.inflater = LayoutInflater.from(mContent);

        this.dataList = dataList;
    }

    public void addItemDataAndRedraw(List<PlayItem> data){
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void addItemDataAndRedraw(PlayItem dataItem) {
        dataList.add(dataItem);
//        notifyItemRangeChanged(dataList.size() - 2, 1);
        notifyItemRangeChanged(dataList.size() - 1, 1);
    }

    public void addItemDataAndPortionRedraw(List<PlayItem> dataItem) {
        dataList.addAll(dataItem);

//        notifyDataSetChanged();
        notifyItemRangeChanged(dataList.size() - dataItem.size(), dataItem.size());
    }

    public void setPlayStatusListener(SliderVideoHolder.PlayStatusListener playStatusListener) {
        this.playStatusListener = playStatusListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (Constants.SLIDER_HOLDER_IMAGE == viewType)
            return new SliderImageHolder(inflater.inflate(R.layout.widget_ui_slider_item_img, parent, false));
        else if (Constants.SLIDER_HOLDER_VIDEO == viewType)
            return new SliderVideoHolder(inflater.inflate(R.layout.widget_ui_slider_item_video, parent, false));
        else if (Constants.SLIDER_HOLDER_RATE_SAVE == viewType)
            return new SliderRateSaveHolder(inflater.inflate(R.layout.widget_ui_slider_item_rateview_nor, parent, false));
        else if (Constants.SLIDER_HOLDER_RATE_LOAN == viewType)
            return new SliderRateLoanHolder(inflater.inflate(R.layout.widget_ui_slider_item_rateview_nor, parent, false));
        else if (Constants.SLIDER_HOLDER_RATE_BUY == viewType)
            return new SliderRateBuyHolder(inflater.inflate(R.layout.widget_ui_slider_item_rateview_quad, parent, false));
        else
            return new SliderImageHolder(inflater.inflate(R.layout.widget_ui_slider_item_img, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PlayItem item = dataList.get(position);

        if (holder instanceof SliderImageHolder)
            SliderImageHolder.showImage(item, ((SliderImageHolder)holder).getImgContent());
        else if (holder instanceof SliderVideoHolder)
            ((SliderVideoHolder) holder).setVideoPath(item.getUrl());
        else if (holder instanceof SliderRateSaveHolder) {
            SliderRateSaveHolder _holder = ((SliderRateSaveHolder) holder);
            SliderRateSaveHolder.showRate((PresetBean.SaveRate)item.getAttData(),
                    _holder.getTxtDesc(),
                    _holder.getTxtTitle(),
                    _holder.getRvRate());
        }
        else if (holder instanceof SliderRateLoanHolder) {
            SliderRateLoanHolder _holder = (SliderRateLoanHolder)holder;
            SliderRateLoanHolder.showRate((PresetBean.LoanRate)item.getAttData(),
                    _holder.getTxtDesc(),
                    _holder.getTxtTitle(),
                    _holder.getRvRate());
        }
        else if (holder instanceof SliderRateBuyHolder) {
            SliderRateBuyHolder _holder = (SliderRateBuyHolder)holder;
            SliderRateBuyHolder.showRate((PresetBean.BIAOFE)item.getAttData(),
                    _holder.getTxtDesc(),
                    _holder.getTxtTitle(),
                    _holder.getRvRate());
        }

    }


    @Override
    public int getItemViewType(int position){
        PlayItem item = dataList.get(position);
        if (null != item)
            return item.getMediaType();

        return super.getItemViewType(position);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof SliderVideoHolder) {
            SliderVideoHolder _holder = (SliderVideoHolder) holder;

            _holder.getVideoContent().setOnCompletionListener( () -> {
                if (null != playStatusListener)
                    playStatusListener.onEnded();
            } );

            _holder.getVideoContent().setOnPreparedListener((int v) -> {
                if (null != playStatusListener)
                    playStatusListener.onStarted();
            });

            _holder.getVideoContent().setVideoPath(_holder.getVideoPath());
            _holder.getVideoContent().start();

            Logger.e(TAG, "startPlay --> " + _holder.getVideoPath());
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof SliderVideoHolder) {
            SliderVideoHolder _holder = (SliderVideoHolder) holder;

            _holder.getVideoContent().pause();
            _holder.getVideoContent().stopPlayback();

            if (null != playStatusListener)
                playStatusListener.onEnded();

            Logger.e(TAG, "endPlay --> " + _holder.getVideoPath());
        }
    }

}
