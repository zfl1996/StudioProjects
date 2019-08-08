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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    ConcurrentHashMap<String, Integer> materialStatus = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Integer> managerStatus = new ConcurrentHashMap<>();

    public MaterialManager(Context context, ItemStatusListener itemStatusListener) {
        this.context = context;
        this.itemStatusListener = new WeakReference<>(itemStatusListener);
    }


    public void initManager(boolean integrationPresetData) {
        managerStatus.put(Constants.MM_KEY_INTEGRATIONPRESET, integrationPresetData ? 1 : 0);

        playerThread = new HandlerThread("playerThread");
        playerThread.start();

        playerHandler = new Handler(playerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (null == msg)
                    return;

                switch (msg.what) {

                    case Constants.SLIDER_STATUS_CODE_UPDATE:
                        Logger.e(TAG, "tid:(SLIDER_STATUS_CODE_UPDATE)" + Thread.currentThread().getId());
                        int resCode = (int)msg.obj;
                        Utils.getExecutorService().submit(() -> {
                            if (resCode == Constants.NET_MANAGER_DATA_PLAYLIST) {
                                if (managerStatus.get(Constants.MM_STATUS_KEY_PLAYLIST_LOADED) != 1)
                                    loadPlaylist();
                            } else if(resCode == Constants.NET_MANAGER_DATA_PRESET) {
                                if (managerStatus.get(Constants.MM_STATUS_KEY_STATUS_PRESET_LOADED) != 1)
                                    loadPreset();
                            }
                        });

                        break;

                    case Constants.SLIDER_STATUS_CODE_INIT:
                        Logger.e(TAG, "tid:(SLIDER_STATUS_CODE_INIT)" + Thread.currentThread().getId());
                        Utils.getExecutorService().submit(() -> loadPlaylist());
                        Utils.getExecutorService().submit(() -> loadPreset());
                        Utils.getExecutorService().submit(() -> showWelcome(null));

                        break;

                    case Constants.SLIDER_STATUS_CODE_DOWNSUCC:
                        String taskStr = "SLIDER_STATUS_CODE_DOWNSUCC-->";
                        if (msg.obj instanceof String[] ){
                            String[] downInfo = (String[]) msg.obj;
                            if (downInfo.length > 1) {
                                taskStr += downInfo[1];
                                finishDownload(downInfo[1]);
                            }
                        }
                        Logger.e(TAG, taskStr);

                        break;

                    default:
                        break;
                }
            }
        };

        Utils.getExecutorService().submit(() -> {
            managerStatus.put(Constants.MM_STATUS_KEY_PLAYLIST_INIT, 0);
            managerStatus.put(Constants.MM_STATUS_KEY_PRESET_INIT, 0);
            managerStatus.put(Constants.MM_STATUS_KEY_PLAYLIST_LOADED, 0);
            managerStatus.put(Constants.MM_STATUS_KEY_STATUS_PRESET_LOADED, 0);

            downloadModule = new DownloadModule(context, 0, downloadStateLisntener);
            playerHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_INIT, null, false));
        });
    }

    /*
    * 处理资源下载完成通知
    * */
    private void finishDownload(String filePath) {
        // 更新素材集状态
        String _fileKey = filePath.substring(filePath.lastIndexOf("/") + 1,
                filePath.lastIndexOf(".") );
        if (materialStatus.containsKey(_fileKey) && materialStatus.get(_fileKey) == 1)
            return;

        materialStatus.put(_fileKey, 1);

        // 更新已下载素材状态
        String[] ids = materialStatus.keySet().toArray(new String[0]);
        Utils.put(context, Constants.MM_STATUS_FINISHED_TASKID, ResHelper.join(ids, ","));

        // 处理pdf缓存或者通知前端显示
//                        Utils.getExecutorService().submit(() -> {
        String fileKey = filePath.substring(filePath.lastIndexOf("/") + 1,
                filePath.lastIndexOf(".") );
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        String suffix = filePath.substring(filePath.lastIndexOf(".") + 1);

        if (suffix.toLowerCase().equals("pdf")) {
            Logger.e(TAG, fileName);
            List<PlayItem> list = PdfHelper.cachePdfToImage( fileName, fileKey  );
            uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_PDF_CACHED, list, true));
        } else if (BllDataExtractor.getIdentityType(suffix) == Constants.SLIDER_HOLDER_IMAGE
                || BllDataExtractor.getIdentityType(suffix) == Constants.SLIDER_HOLDER_VIDEO) {
            PlayItem playItem = new PlayItem(fileKey,
                    filePath,
                    BllDataExtractor.getIdentityType(suffix) );

            uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_DOWNSUCC, playItem, true));
        } else if (suffix.toLowerCase().equals("txt")) {
            String wmsg = ResHelper.readFile2String(filePath);
            if (!ResHelper.isNullOrEmpty(wmsg)) {
                List<String> list = new ArrayList<>();
                list.add(wmsg);
                uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_WELCOME_MSG, list, true));
            }
        }
//                        });
    }

    public boolean isInitSuccessed() {
        return managerStatus.get(Constants.MM_STATUS_KEY_PLAYLIST_INIT) == 1
                && (!isIntegrationPresetData() || (isIntegrationPresetData() && managerStatus.get(Constants.MM_STATUS_KEY_STATUS_PRESET_LOADED) == 1));
    }

    public boolean isMaterialLoaded(String materialCode) {
        return managerStatus.containsKey(materialCode) && managerStatus.get(materialCode) == 1;
    }

    public boolean isIntegrationPresetData() {
        return managerStatus.contains(Constants.MM_KEY_INTEGRATIONPRESET)
                && managerStatus.get(Constants.MM_KEY_INTEGRATIONPRESET) == 1;
    }

    public void reload(int resCode) {
        playerHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_UPDATE, resCode, false));
    }

    /*
    * 加载汇率数据
    * */
    private void loadPreset() {
        uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_PROGRESS, Constants.SLIDER_PROGRESS_CODE_PRESET, true));

        String json = Utils.get(context, Utils.KEY_PRESET, "").toString();

        if (!ResHelper.isNullOrEmpty(json)) {
            managerStatus.put(Constants.MM_STATUS_KEY_STATUS_PRESET_LOADED, 1);

            PresetBean bean = JSON.parseObject(json, PresetBean.class);
            if (null == bean || !"0".equals(bean.resCode))
                return;

            List<PlayItem> presetItems = new ArrayList<>();

            presetItems.add( new PlayItem(Constants.SLIDER_HOLDER_RATE_SAVE, bean.data.saveRate) );
            presetItems.add( new PlayItem(Constants.SLIDER_HOLDER_RATE_LOAN, bean.data.loanRate) );
            presetItems.add( new PlayItem(Constants.SLIDER_HOLDER_RATE_BUY, bean.data.buyInAndOutForeignExchange) );

            uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_RATE, presetItems, true));
        } else {
//            managerStatus.put(Constants.MM_STATUS_KEY_PRESET_INIT, 1);
        }

        managerStatus.put(Constants.MM_STATUS_KEY_PRESET_INIT, 1);
//        managerStatus.put(Constants.MM_STATUS_KEY_PRESET_INIT, 1);
        uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_PROGRESS,
                 Constants.SLIDER_PROGRESS_CODE_OK, true));
    }

    /*
    * 处理播放列表信息
    * */
    private void loadPlaylist() {
        uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_PROGRESS, Constants.SLIDER_PROGRESS_CODE_PRE, true));

        // 同步已下载完成数据项
        String jsonFinish = Utils.get(context, Constants.MM_STATUS_FINISHED_TASKID, "").toString();
        if (!TextUtils.isEmpty(jsonFinish)) {
            String[] ids = jsonFinish.split(",");
            for (String id : ids) {
                if (!ResHelper.isNullOrEmpty(id))
                    materialStatus.put(id, 1);
            }
        }

        String json = Utils.get(context, Utils.KEY_PLAY_LIST, "").toString();
        if (!ResHelper.isNullOrEmpty(json)) {
            try {
                managerStatus.put(Constants.MM_STATUS_KEY_PLAYLIST_LOADED, 1);

                List<PlaylistBodyBean> playlistBodyBeans = JSON.parseArray(json, PlaylistBodyBean.class);
                List<PlayItem> allPlayItems = new ArrayList<>();
                List<String> waitForDownload = new ArrayList<>();
                List<String> waitForDownloadFilePath = new ArrayList<>();
                Map<String, Integer> waitForFiles = new HashMap<>();
                List<String> welcomeItems = new ArrayList<>();

                for (PlaylistBodyBean bodyBean:playlistBodyBeans) {
                    // 过滤非下载时段和已下载项
                    if (!Utils.isInDownloadTime(bodyBean)
//                            || materialStatus.containsKey(bodyBean.id)
                            || waitForFiles.containsKey(bodyBean.downloadLink) )
                        continue;

                    waitForFiles.put(bodyBean.downloadLink, 0);
//                    materialStatus.put(bodyBean.id, 0);

                    // 构建待下载数据
                    if (!materialStatus.containsKey(bodyBean.id) || materialStatus.get(bodyBean.id) != 1) {
                        String[] pathSegments = ResHelper.getSavePathDataByUrl(bodyBean.downloadLink);
                        if (pathSegments.length <= 0)
                            continue;

                        waitForDownload.add(bodyBean.downloadLink);
                        waitForDownloadFilePath.add(pathSegments[1] + bodyBean.id + "." + pathSegments[0]);

                        continue;
                    }

                    String suffix = bodyBean.name.substring(bodyBean.name.lastIndexOf(".") + 1).toLowerCase();

                    if (suffix.equals("pdf")) {
                        allPlayItems.addAll(PdfHelper.getCachedPdfImage(bodyBean.id + ".pdf"));
                    } else if(BllDataExtractor.getIdentityType(suffix) == Constants.SLIDER_HOLDER_IMAGE
                        || BllDataExtractor.getIdentityType(suffix) == Constants.SLIDER_HOLDER_VIDEO) {
                        allPlayItems.add(new PlayItem(bodyBean.id,
                                ResHelper.getSavePath(bodyBean.downloadLink, bodyBean.id),
                                BllDataExtractor.getIdentityType(bodyBean)));
                    } else if (suffix.equals("txt")) {
                        String wmsg = ResHelper.readFile2String(ResHelper.getSavePath(bodyBean.downloadLink, bodyBean.id));
                        if (!ResHelper.isNullOrEmpty(wmsg))
                            welcomeItems.add(wmsg);
                    }
                }

                if (waitForDownload.size() > 0 && waitForDownload.size() == waitForDownloadFilePath.size()) {
                    Logger.e(TAG, "tid:(loadPlaylist)" + Thread.currentThread().getId());
                    downloadModule.start(waitForDownload, ResHelper.getRootDir(), waitForDownloadFilePath);
                }

                if (allPlayItems.size() > 0)
                    uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_INIT, allPlayItems, true));

                if (welcomeItems.size() > 0)
                    showWelcome(welcomeItems);

                if (!isIntegrationPresetData())
                    uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_PROGRESS, Constants.SLIDER_PROGRESS_CODE_OK, true));
                Logger.e(TAG, "loadPlaylist-->" + ResHelper.join((String[]) waitForDownloadFilePath.toArray(), "@@\r\n"));

            } catch (Exception e) {
                Logger.e("解析播放列表出错" + json);
            }
        } else {

        }

        managerStatus.put(Constants.MM_STATUS_KEY_PLAYLIST_INIT, 1);
    }

    private void showWelcome(List<String> welcomeItems) {
        if (null == welcomeItems || welcomeItems.size() <= 0) {
            welcomeItems = new ArrayList<String>() {
                {
                    add("中国农业银行欢迎您");
                    add("中国农业银行欢迎您");
                    add("中国农业银行欢迎您");
                    add("中国农业银行欢迎您");
                    add("中国农业银行欢迎您");
                    add("中国农业银行欢迎您");
                }
            };

            managerStatus.put(Constants.MM_STATUS_KEY_STATUS_WELCOME_LOADED, 0);
            uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_WELCOME, welcomeItems, true));
        } else {
            managerStatus.put(Constants.MM_STATUS_KEY_STATUS_WELCOME_LOADED, 1);
            uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_WELCOME_MSG, welcomeItems, true));
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

            Logger.e(TAG, "tid:(uiHandler)" + Thread.currentThread().getId());

            switch (msg.what) {
                case Constants.SLIDER_STATUS_CODE_WELCOME:
                    List<String> welcomeItems = (List<String>) msg.obj;

                    if (null != _itemStatusListener) {
                        _itemStatusListener.onWelcome(welcomeItems);
                    }

                    break;

                case Constants.SLIDER_STATUS_CODE_WELCOME_MSG:
                    List<String> welcomeMsg = (List<String>) msg.obj;

                    if (null != _itemStatusListener) {
                        _itemStatusListener.onNewMsgAdded(welcomeMsg, isMaterialLoaded(Constants.MM_STATUS_KEY_STATUS_WELCOME_LOADED));
                    }

                    break;

                case Constants.SLIDER_STATUS_CODE_INIT:
                    List<PlayItem> allPlayItems = (List<PlayItem>)msg.obj;

                    if (null != _itemStatusListener) {
                        _itemStatusListener.onReady(allPlayItems);
                    }

                    break;

                case Constants.SLIDER_STATUS_CODE_RATE:
                    if (null != _itemStatusListener)
                        _itemStatusListener.onRate((List<PlayItem>)msg.obj);

                    break;

                case Constants.SLIDER_STATUS_CODE_PDF_CACHED:
                    if (null != _itemStatusListener)
                        _itemStatusListener.onItemPrepared((List<PlayItem>)msg.obj);

                    break;

                case Constants.SLIDER_STATUS_CODE_PROGRESS:
                    if (null != _itemStatusListener)
                        _itemStatusListener.onProgress((int)msg.obj);

                    break;

                case Constants.SLIDER_STATUS_CODE_DOWNSUCC:
                    if (null != _itemStatusListener)
                        _itemStatusListener.onItemPrepared(new ArrayList<PlayItem>(){
                            { add((PlayItem)msg.obj); }
                        } );

                    break;

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
        void onProgress(int code);
        void onReady(List<PlayItem> items);
        void onItemPrepared(List<PlayItem> items);
        void onRate(List<PlayItem> items);
        void onWelcome(List<String> items);
        void onNewMsgAdded(List<String> msg, boolean isAppend);
    }

}
