package com.ads.abcbank.xx.utils.core;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.ads.abcbank.bean.DownloadBean;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.bean.PresetBean;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.utils.BllDataExtractor;
import com.ads.abcbank.xx.utils.Constants;
import com.ads.abcbank.xx.utils.core.DownloadModule.DownloadStateLisntener;
import com.ads.abcbank.xx.utils.helper.ResHelper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MaterialManager {
    Context context;
    DownloadModule downloadModule;

    // worker thread
    HandlerThread playerThread;
    Handler playerHandler;

    // bll data
    WeakReference<ItemStatusListener> itemStatusListener = null;
    String deviceModeData;
    List<PlaylistBodyBean> playlist = new ArrayList<>();
    List<PlaylistBodyBean> txtlist = new ArrayList<>();
//    Map<String, Integer> itemStatus = new HashMap<>();
    ConcurrentHashMap<String, Integer> itemStatus = new ConcurrentHashMap<>();

    public MaterialManager(Context context, ItemStatusListener itemStatusListener) {
        this.context = context;
        this.itemStatusListener = new WeakReference<>(itemStatusListener);
    }


    public void initManager() {
        playerThread = new HandlerThread("playerThread");
        playerThread.start();

        playerHandler = new Handler(playerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {

                    case Constants.SLIDER_STATUS_CODE_UPDATE:
                        int resCode = (int)msg.obj;
                        if ()

                        break;
                    case Constants.SLIDER_STATUS_CODE_INIT:
                        Utils.getExecutorService().submit(() -> loadPlaylist());
                        Utils.getExecutorService().submit(() -> loadPreset());
                        Utils.getExecutorService().submit(() -> getWelcome());

                        break;

                    case Constants.SLIDER_STATUS_CODE_DOWNSUCC:
                        String[] downInfo = (String[]) msg.obj;

                        Utils.getExecutorService().submit(() -> {
                            itemStatus.put(downInfo[0], 1);
                            PlayItem playItem = new PlayItem(downInfo[0],
                                    downInfo[1],
                                    BllDataExtractor.getIdentityType(downInfo[1]) );

                            uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_DOWNSUCC, playItem, true));
                        });

                        break;

                    default:
                        break;
                }
            }
        };

        Utils.getExecutorService().submit(() -> {
            downloadModule = new DownloadModule(context, 512, downloadStateLisntener);
            playerHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_INIT, null, false));
        });
    }

    public void reload(int resCode) {
        playerHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_UPDATE, resCode, false));
    }

    private void loadPreset() {
        uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_PROGRESS, Constants.SLIDER_PROGRESS_CODE_PRESET, true));

        String json = Utils.get(context, Utils.KEY_PRESET, "").toString();

        if (!ResHelper.isNullOrEmpty(json)) {
            PresetBean bean = JSON.parseObject(json, PresetBean.class);
            if (null == bean || !"0".equals(bean.resCode))
                return;

            List<PlayItem> presetItems = new ArrayList<>();

            presetItems.add( new PlayItem(Constants.SLIDER_HOLDER_RATE_SAVE, bean.data.saveRate ) );
            presetItems.add( new PlayItem(Constants.SLIDER_HOLDER_RATE_LOAN, bean.data.loanRate ) );
            presetItems.add( new PlayItem(Constants.SLIDER_HOLDER_RATE_BUY, bean.data.buyInAndOutForeignExchange ) );

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_RATE, presetItems, true));
        }

        uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_PROGRESS, Constants.SLIDER_PROGRESS_CODE_OK, true));
    }

    private void loadPlaylist() {
        uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_PROGRESS, Constants.SLIDER_PROGRESS_CODE_PRE, true));

        String jsonFinish = Utils.get(context, Utils.KEY_PLAY_LIST_DOWNLOAD_FINISH, "").toString();
        if (!TextUtils.isEmpty(jsonFinish)) {
            JSONArray jsonArray = JSON.parseArray(jsonFinish);

            for (int i = 0; i < jsonArray.size(); i++) {
                DownloadBean bean = JSON.parseObject(jsonArray.getString(i), DownloadBean.class);

                if (null != bean) {
                    itemStatus.put(bean.id, 1);
                }
            }
//            playlistResultBean.data.items.removeAll(finished);
        }

        String json = Utils.get(context, Utils.KEY_PLAY_LIST, "").toString();
        if (!ResHelper.isNullOrEmpty(json)) {
            try {
                List<PlaylistBodyBean> playlistBodyBeans = JSON.parseArray(json, PlaylistBodyBean.class);
                List<PlayItem> allPlayItems = new ArrayList<>();
//                List<String> waitForDownload = new ArrayList<>();
//                List<String> waitForDownloadFilePath = new ArrayList<>();


                for (PlaylistBodyBean bodyBean:playlistBodyBeans) {
                    if (!itemStatus.containsKey(bodyBean.id) || itemStatus.get(bodyBean.id) != 1) {
                        String[] pathSegments = ResHelper.getSavePathDataByUrl(bodyBean.downloadLink);
                        if (pathSegments.length <= 0)
                            continue;

//                        waitForDownload.add(bodyBean.downloadLink);
//                        waitForDownloadFilePath.add(pathSegments[1] + bodyBean.md5 + "." + pathSegments[0]);

                        downloadModule.start(bodyBean.downloadLink, ResHelper.getSavePath(bodyBean.downloadLink, bodyBean.id), bodyBean.id);

                        continue;
                    }

                    String suffix = bodyBean.name.substring(bodyBean.name.lastIndexOf(".") + 1).toLowerCase();
                    if ("txt".equals(suffix)) {
                        txtlist.add(bodyBean);
                    } else {
                        playlist.add(bodyBean);
                    }

                    allPlayItems.add(new PlayItem(bodyBean.id,
                            BllDataExtractor.getIdentityPath(bodyBean),
                            BllDataExtractor.getIdentityType(bodyBean) ));

                }

//                downloadModule.start(waitForDownload, ResHelper.getRootDir(), waitForDownloadFilePath);
                uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_INIT, allPlayItems, true));

            } catch (Exception e) {
                Logger.e("解析播放列表出错" + json);
            }
        } else {
            txtlist.clear();
            playlist.clear();
        }
    }

    private void getWelcome() {
        List<String> welcomeItems = new ArrayList<String>() {
            {
                add("中国农业银行欢迎您");
                add("中国农业银行欢迎您");
                add("中国农业银行欢迎您");
                add("中国农业银行欢迎您");
                add("中国农业银行欢迎您");
                add("中国农业银行欢迎您");
            }
        };

        uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_WELCOME, welcomeItems, true));
    }

    private void updateItemStatus(String md5) {
        itemStatus.put(md5, 1);

        for (PlaylistBodyBean bodyBean : playlist) {
            if (bodyBean.md5.equals(md5)) {
//                bodyBean.status = "1";

                break;
            }
        }
    }

    Message buildMessage(int w, Object obj, boolean isMain) {
        Message msg = isMain ? uiHandler.obtainMessage() : playerHandler.obtainMessage();
        msg.what = w;
        msg.obj = obj;

        return msg;
    }

    ItemStatusListener getRefListener() {
        if (null != itemStatusListener) {
            return itemStatusListener.get();
        }

        return null;
    }

    Handler uiHandler = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ItemStatusListener _itemStatusListener = getRefListener();

            switch (msg.what) {
                case Constants.SLIDER_STATUS_CODE_INIT:
                    List<PlayItem> allPlayItems = (List<PlayItem>)msg.obj;

                    if (null != _itemStatusListener) {
                        _itemStatusListener.onReady(allPlayItems);
                    }

                    break;

                case Constants.SLIDER_STATUS_CODE_WELCOME:
                    List<String> welcomeItems = (List<String>) msg.obj;

                    if (null != _itemStatusListener) {
                        _itemStatusListener.onWelcome(welcomeItems);
                    }


                    break;

                case Constants.SLIDER_STATUS_CODE_RATE:
                    if (null != _itemStatusListener) {
                        _itemStatusListener.onNewItemsAdded((List<PlayItem>)msg.obj);
                    }

                    break;

                case Constants.SLIDER_STATUS_CODE_PROGRESS:
                    if (null != _itemStatusListener)
                        _itemStatusListener.onProgress((int)msg.obj);

                    break;

                case Constants.SLIDER_STATUS_CODE_DOWNSUCC:
                    if (null != _itemStatusListener)
                        _itemStatusListener.onNewItemAdded((PlayItem)msg.obj);

                    break;

                default:
                    break;
            }
        }

    };

    DownloadModule.DownloadStateLisntener downloadStateLisntener = new DownloadStateLisntener() {
        @Override
        public void onSucc(String identity, String url, String path) {
            playerHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_DOWNSUCC, new String[]{
                identity, url, path }, false));
        }

        @Override
        public void onFail(String url, String code) {

        }
    };

    public interface ItemStatusListener {
        void onReady(List<PlayItem> items);
        void onNewItemAdded(PlayItem item);
        void onNewItemsAdded(List<PlayItem> items);
        void onWelcome(List<String> items);
        void onProgress(int code);
    }

}
