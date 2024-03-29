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
import com.ads.abcbank.xx.utils.helper.ResHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SliderMainAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    static String TAG = "SliderMainAdapter";

    private Context mContent;
    private List<PlayItem> dataList = new ArrayList<>();
    private LayoutInflater inflater;
    private SliderVideoHolder.VideoStatusListener videoStatusListener;
    private boolean isIntegrationPresetData;
    private Map<Integer, Integer> rateLayoutMap;
//    private String videoPath = "";
    private Map<Integer, Object> rateDataMap = new HashMap<>();
//    private Map<Integer, Integer> ratePos = new HashMap<>();
    private Integer[] mts = new Integer[]{
            Constants.SLIDER_HOLDER_RATE_SAVE,
            Constants.SLIDER_HOLDER_RATE_LOAN,
            Constants.SLIDER_HOLDER_RATE_BUY
    };

    public void setRateLayoutMap(Map<Integer, Integer> rateLayoutMap) {
        this.rateLayoutMap = rateLayoutMap;
    }

    public SliderMainAdapter(Context mContent) {
        this.mContent = mContent;
        this.inflater = LayoutInflater.from(mContent);
    }

    public void addItemDataAndRedraw(List<PlayItem> data){
        dataList.clear();
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void addItemDataAndPortionRedraw(List<PlayItem> dataItem) {
        dataList.addAll(dataItem);
        notifyItemRangeChanged(dataList.size() - dataItem.size(), dataItem.size());
    }

    public void addRateData(List<PlayItem> dataItems, boolean isPortionRedraw, boolean isCreate) {
        isCreate = rateDataMap.size() <= 0;
        Logger.e(TAG, "addRateData--> isCreate:" + isCreate + ", dataSize:" + dataList.size() + ", keys:" + rateDataMap.size());

        if (isCreate) {
            rateDataMap.clear();
            for (PlayItem pi : dataItems) {
                rateDataMap.put(pi.getMediaType(), pi.getAttData());
            }

            addRateData(dataItems, isPortionRedraw);
        } else {
            if (dataItems.size() <= 0) {
                rateDataMap.clear();

                addRateData(dataItems, isPortionRedraw);
            }

            Integer[] mmmmm = new Integer[dataItems.size()];
            for (int j = 0; j<mmmmm.length; j++)
                mmmmm[j] = dataItems.get(j).getMediaType();

            List<PlayItem> needToAdds = new ArrayList<>();
            List<Integer> addOfPosition = new ArrayList<>();
            int alreadyActualPos = -1;
            int alreadyRelativePos = -1;

            int flag = 0;
            for (PlayItem pi : dataItems) {
                if (!rateDataMap.containsKey(pi.getMediaType())) {
                    needToAdds.add(pi);
                    addOfPosition.add(flag);
                } else if (alreadyActualPos == -1) {
                    int i = 0;
                    for (PlayItem data : dataList) {
                        if (data.getMediaType() == pi.getMediaType()) {
                            alreadyActualPos = i++;
                            alreadyRelativePos = getResIndex(mmmmm, pi.getMediaType());
                            break;
                        }
                    }
                }

                rateDataMap.put(pi.getMediaType(), pi.getAttData());
                flag++;
            }

            if (alreadyActualPos != -1 && alreadyRelativePos != -1) {
                int startRefreshPos = alreadyActualPos;

                for (int i=0; i< needToAdds.size(); i++) {
                    PlayItem playItem = needToAdds.get(i);

                    int curRelativePos = getResIndex(mmmmm, playItem.getMediaType());
                    int abs = curRelativePos - alreadyRelativePos;
                    int addToPos = alreadyActualPos + abs;
                    addToPos = addToPos >= 0 ? addToPos : 0;

                    dataList.add(addToPos, playItem);
                    startRefreshPos = addToPos < startRefreshPos ? addToPos : startRefreshPos;
                    Logger.e(TAG, "addRateData--> type:" + playItem.getMediaType() + ", pos:" + addToPos + ", total:" + dataList.size());
                }

                if (dataList.size() == 1)
                    notifyDataSetChanged();
                else if (dataList.size() > 0)
                    notifyItemRangeChanged(startRefreshPos, dataItems.size());

            } else {
                if (dataItems.size() <= 0)
                    rateDataMap.clear();

                addRateData(dataItems, isPortionRedraw);
            }

        }
    }

    private void addRateData(List<PlayItem> dataItems, boolean isPortionRedraw) {
        if (isPortionRedraw)
            addItemDataAndPortionRedraw(dataItems);
        else
            addItemDataAndRedraw(dataItems);
    }

    private int getResIndex(Integer[] mtts, int mediaType) {
        int i = 0;
        for (int mt : mtts) {
            if (mt == mediaType)
                return i;

            i++;
        }

        return -1;
    }

    public void removeInvalidRateItem(Integer... mediaType) {
        Logger.e(TAG, "removeInvalidRateItem--> count:" + mediaType.length + ", dataSize:" + dataList.size() + ", keys:" + rateDataMap.size());
        boolean redraw = false;
        for (int type : mediaType) {
            Iterator<PlayItem> it = dataList.iterator();
            while (it.hasNext()) {
                PlayItem pi = it.next();

                if (pi.getMediaType() == type) {
                    Logger.e(TAG, "removeInvalidRateItem--> remove:" + type + ", dataSize:" + dataList.size());
                    it.remove();
                    rateDataMap.remove(pi.getMediaType());

                    redraw = true;
                    break;
                }
            }
        }

        if (redraw)
            notifyDataSetChanged();
    }

    public void removeAllRateItems() {
        removeInvalidRateItem(mts);
    }

    public void removeOuttimeItem(String id, int index) {
        if (dataList.get(index).getMd5().equals(id)) {
            dataList.remove(index);
            notifyItemRangeChanged(index - 1, 1);
        }
    }

    public void removeItems(List<String> ids) {
        Iterator<PlayItem> it = dataList.iterator();
        while (it.hasNext()) {
            PlayItem pi = it.next();

            if (ids.contains(pi.getMd5())) {
                it.remove();
            }
        }
        notifyDataSetChanged();
    }

    public void setVideoStatusListener(SliderVideoHolder.VideoStatusListener videoStatusListener) {
        this.videoStatusListener = videoStatusListener;
    }

    public void setIntegrationPresetData(boolean integrationPresetData) {
        isIntegrationPresetData = integrationPresetData;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (Constants.SLIDER_HOLDER_IMAGE == viewType)
            return new SliderImageHolder(inflater.inflate(R.layout.widget_ui_slider_item_img, parent, false));
        else if (Constants.SLIDER_HOLDER_VIDEO == viewType)
            return new SliderVideoHolder(inflater.inflate(R.layout.widget_ui_slider_item_video, parent, false));
        else if (Constants.SLIDER_HOLDER_RATE_SAVE == viewType)
            return new SliderRateSaveHolder(inflater.inflate(rateLayoutMap.get(Constants.SLIDER_HOLDER_RATE_SAVE), parent, false));
        else if (Constants.SLIDER_HOLDER_RATE_LOAN == viewType)
            return new SliderRateLoanHolder(inflater.inflate(rateLayoutMap.get(Constants.SLIDER_HOLDER_RATE_LOAN), parent, false));
        else if (Constants.SLIDER_HOLDER_RATE_BUY == viewType)
            return new SliderRateBuyHolder(inflater.inflate(rateLayoutMap.get(Constants.SLIDER_HOLDER_RATE_BUY), parent, false));
        else
            return new SliderImageHolder(inflater.inflate(R.layout.widget_ui_slider_item_img, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int pos) {
        int itemCount = dataList.size();
        int position = itemCount > 0 ? pos % itemCount : 0;
        PlayItem item = dataList.get(position);

        if (holder instanceof SliderImageHolder)
            SliderImageHolder.showImage(item, (SliderImageHolder)holder);
        else if (holder instanceof SliderVideoHolder)
            ((SliderVideoHolder) holder).setVideoData(item, (SliderVideoHolder) holder);
        else if (holder instanceof SliderRateSaveHolder) {
            SliderRateSaveHolder.showRate( (PresetBean.SaveRate) rateDataMap.get(item.getMediaType()), //(PresetBean.SaveRate)item.getAttData(),
                    (SliderRateSaveHolder) holder,
                    isIntegrationPresetData,
                    rateLayoutMap.get(Constants.SLIDER_HOLDER_RATE_SAVE_ITEM));
        } else if (holder instanceof SliderRateLoanHolder) {
            SliderRateLoanHolder.showRate((PresetBean.LoanRate) rateDataMap.get(item.getMediaType()), //(PresetBean.LoanRate)item.getAttData(),
                    (SliderRateLoanHolder)holder,
                    isIntegrationPresetData,
                    rateLayoutMap.get(Constants.SLIDER_HOLDER_RATE_LOAN_ITEM));
        } else if (holder instanceof SliderRateBuyHolder) {
            SliderRateBuyHolder.showRate((PresetBean.BIAOFE) rateDataMap.get(item.getMediaType()), //(PresetBean.BIAOFE)item.getAttData(),
                    (SliderRateBuyHolder)holder,
                    isIntegrationPresetData,
                    rateLayoutMap.get(Constants.SLIDER_HOLDER_RATE_BUY_ITEM));
        }

    }


    @Override
    public int getItemViewType(int pos){
        int itemCount = dataList.size();
        int position = itemCount > 0 ? pos % itemCount : 0;

        if (itemCount > position) {
            PlayItem item = dataList.get(position);
            if (null != item)
                return item.getMediaType();
        }

        return super.getItemViewType(position);
    }


    @Override
    public int getItemCount() {
        return dataList.size() > 0 ? Integer.MAX_VALUE : 0;
    }

    public int getRealItemCount() {
        return dataList.size();
    }

    public int getNoPresetCount(boolean isOnlyPlaylist) {
        return isOnlyPlaylist ? (dataList.size() - rateDataMap.size()) : dataList.size();
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof SliderVideoHolder) {
            SliderVideoHolder _holder = (SliderVideoHolder) holder;

            _holder.getVideoContent().setOnCompletionListener( () -> {
                if (null != videoStatusListener)
                    videoStatusListener.onPlayFinish();
            } );

            _holder.getVideoContent().setOnPreparedListener((int v) -> {
//                videoPath = "";
                if (null != videoStatusListener)
                    videoStatusListener.onStartPlay();
            });

//            videoPath =_holder.getVideoPath();
            _holder.getVideoContent().setVideoPath(_holder.getVideoPath());
            _holder.getVideoContent().start();
            Logger.e("SliderPlayer", "onViewAttachedToWindow-->" + _holder.getVideoPath());
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof SliderVideoHolder) {
            SliderVideoHolder _holder = (SliderVideoHolder) holder;

            Logger.e("SliderPlayer", "onViewDetachedFromWindow-->" + _holder.getVideoPath() + ",status:" + _holder.getVideoContent().isPlaying());
            if (null != videoStatusListener && _holder.getVideoContent().isPlaying()/*&& !videoPath.equals("") && _holder.getVideoPath().equals(videoPath)*/)
                videoStatusListener.onPlayFinish();

            _holder.getVideoContent().pause();
            _holder.getVideoContent().stopPlayback();
        }
    }
}
