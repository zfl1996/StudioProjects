package com.ads.abcbank.xx.utils.core;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.bean.PresetBean;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.utils.BllDataExtractor;
import com.ads.abcbank.xx.utils.Constants;
import com.ads.abcbank.xx.utils.core.DownloadModule.DownloadStateLisntener;
import com.ads.abcbank.xx.utils.helper.PdfHelper;
import com.ads.abcbank.xx.utils.helper.ResHelper;
import com.alibaba.fastjson.JSON;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MaterialManager {
    static String TAG = "MaterialManager";

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
    ConcurrentHashMap<String, Integer> envStatus = new ConcurrentHashMap<>();

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
                        Utils.getExecutorService().submit(() -> {
                            if (resCode == Constants.NET_MANAGER_DATA_PLAYLIST) {
                                if (envStatus.get(Constants.MM_STATUS_KEY_PLAYLIST_INIT) != 1)
                                    loadPlaylist();
                            } else if(resCode == Constants.NET_MANAGER_DATA_PRESET) {
                                if (envStatus.get(Constants.MM_STATUS_KEY_PRESET_INIT) != 1)
                                    loadPreset();
                            }
                        });

                        break;
                    case Constants.SLIDER_STATUS_CODE_INIT:
                        Utils.getExecutorService().submit(() -> loadPlaylist());
                        Utils.getExecutorService().submit(() -> loadPreset());
                        Utils.getExecutorService().submit(() -> getWelcome());

                        break;

                    case Constants.SLIDER_STATUS_CODE_DOWNSUCC:
                        String[] downInfo = (String[]) msg.obj;
                        String _fileKey = downInfo[1].substring(downInfo[1].lastIndexOf("/") + 1,
                                downInfo[1].lastIndexOf(".") );

                        itemStatus.put(_fileKey, 1);

                        Utils.getExecutorService().submit(() -> {
                            String fileKey = downInfo[1].substring(downInfo[1].lastIndexOf("/") + 1,
                                    downInfo[1].lastIndexOf(".") );
                            String fileName = downInfo[1].substring(downInfo[1].lastIndexOf("/") + 1);
                            String suffix = downInfo[1].substring(downInfo[1].lastIndexOf(".") + 1);

                            int identityType = BllDataExtractor.getIdentityType(suffix);

                            if (identityType == Constants.SLIDER_HOLDER_VIDEO
                                || identityType == Constants.SLIDER_HOLDER_PDF ) {
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            String[] ids = itemStatus.keySet().toArray(new String[0]);
                            Utils.put(context, Constants.MM_STATUS_FINISHED_TASKID, ResHelper.join(ids, ","));

                            if (suffix.toLowerCase().equals("pdf")) {
                                Logger.e(TAG, fileName);
                                List<PlayItem> list = PdfHelper.cachePdfToImage( fileName, fileKey  );
                                uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_PDF_CACHED, list, true));
                            } else {
                                PlayItem playItem = new PlayItem(fileKey,
                                        downInfo[1],
                                        BllDataExtractor.getIdentityType(suffix) );

                                uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_DOWNSUCC, playItem, true));
                            }
                        });

                        break;

                    default:
                        break;
                }
            }
        };

        Utils.getExecutorService().submit(() -> {
            envStatus.put(Constants.MM_STATUS_KEY_PLAYLIST_INIT, 0);
            envStatus.put(Constants.MM_STATUS_KEY_PRESET_INIT, 0);

            downloadModule = new DownloadModule(context, 0, downloadStateLisntener);
            playerHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_INIT, null, false));
        });
    }

    public boolean isInitSuccessed() {
        return envStatus.get(Constants.MM_STATUS_KEY_PLAYLIST_INIT) == 1
                && envStatus.get(Constants.MM_STATUS_KEY_PRESET_INIT) == 1;
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

//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_RATE, presetItems, true));
        }

        envStatus.put(Constants.MM_STATUS_KEY_PRESET_INIT, 1);
        uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_PROGRESS,
                 Constants.SLIDER_PROGRESS_CODE_OK, true));
    }

    private void loadPlaylist() {
        uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_PROGRESS, Constants.SLIDER_PROGRESS_CODE_PRE, true));

//        String jsonFinish = Utils.get(context, Utils.KEY_PLAY_LIST_DOWNLOAD_FINISH, "").toString();
//        if (!TextUtils.isEmpty(jsonFinish)) {
//            JSONArray jsonArray = JSON.parseArray(jsonFinish);
//
//            for (int i = 0; i < jsonArray.size(); i++) {
//                DownloadBean bean = JSON.parseObject(jsonArray.getString(i), DownloadBean.class);
//
//                if (null != bean) {
//                    itemStatus.put(bean.id, 1);
//                }
//            }
////            playlistResultBean.data.items.removeAll(finished);
//        }
        String jsonFinish = Utils.get(context, Constants.MM_STATUS_FINISHED_TASKID, "").toString();
        if (!TextUtils.isEmpty(jsonFinish)) {
            String[] ids = jsonFinish.split(",");
            for (String id : ids) {
                if (!ResHelper.isNullOrEmpty(id))
                    itemStatus.put(id, 1);
            }
        }

        String json = Utils.get(context, Utils.KEY_PLAY_LIST, "").toString();
        if (!ResHelper.isNullOrEmpty(json)) {
            try {
                List<PlaylistBodyBean> playlistBodyBeans = JSON.parseArray(json, PlaylistBodyBean.class);
                List<PlayItem> allPlayItems = new ArrayList<>();
                List<String> waitForDownload = new ArrayList<>();
                List<String> waitForDownloadFilePath = new ArrayList<>();


                for (PlaylistBodyBean bodyBean:playlistBodyBeans) {
                    if (!itemStatus.containsKey(bodyBean.id) || itemStatus.get(bodyBean.id) != 1) {
                        String[] pathSegments = ResHelper.getSavePathDataByUrl(bodyBean.downloadLink);
                        if (pathSegments.length <= 0)
                            continue;

                        waitForDownload.add(bodyBean.downloadLink);
                        waitForDownloadFilePath.add(pathSegments[1] + bodyBean.id + "." + pathSegments[0]);

                        continue;
                    }

                    String suffix = bodyBean.name.substring(bodyBean.name.lastIndexOf(".") + 1).toLowerCase();
                    if ("txt".equals(suffix)) {
                        txtlist.add(bodyBean);
                    } else {
                        playlist.add(bodyBean);
                    }

                    if (suffix.equals("pdf")) {
                        allPlayItems.addAll(PdfHelper.getCachedPdfImage(bodyBean.id + ".pdf"));
                    } else
                        allPlayItems.add(new PlayItem(bodyBean.id,
                                ResHelper.getSavePath(bodyBean.downloadLink, bodyBean.id),
                                BllDataExtractor.getIdentityType(bodyBean) ));
                }

                envStatus.put(Constants.MM_STATUS_KEY_PLAYLIST_INIT, 1);
                downloadModule.start(waitForDownload, ResHelper.getRootDir(), waitForDownloadFilePath);
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

                case Constants.SLIDER_STATUS_CODE_PDF_CACHED:
                    if (null != _itemStatusListener)
                        _itemStatusListener.onNewItemsAdded((List<PlayItem>)msg.obj);

                default:
                    break;
            }
        }

    };

    DownloadModule.DownloadStateLisntener downloadStateLisntener = new DownloadStateLisntener() {
        @Override
        public void onSucc(String url, String path) {
            playerHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_DOWNSUCC, new String[]{
                url, path }, false));
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
