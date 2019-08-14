package com.ads.abcbank.xx.utils.core;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.utils.BllDataExtractor;
import com.ads.abcbank.xx.utils.Constants;
import com.ads.abcbank.xx.utils.helper.IOHelper;
import com.ads.abcbank.xx.utils.helper.PdfHelper;
import com.ads.abcbank.xx.utils.helper.ResHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MaterialManagerBase {

    protected final String TAG = MaterialManagerBase.class.getSimpleName();

    Context context;
    DownloadModule downloadModule;

    // worker thread
    HandlerThread materialThread;
    Handler materialHandler;

    // bll data
    WeakReference<MaterialStatusListener> itemStatusListener = null;
    String deviceModeData;
    ConcurrentHashMap<String, PlaylistBodyBean> waitForDownloadMaterial = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Integer> materialStatus = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Integer> managerStatus = new ConcurrentHashMap<>();
    String filters;
    int MAX_RATE = 0;

    /**
     * 初始化资源管理器
     * @param integrationPresetData 全屏模式时播放器集成显示汇率数据
     * @param filters 模板对应的资源过滤符
     */
    public void initManager(boolean integrationPresetData, String filters) {
        this.filters = filters;
        managerStatus.put(Constants.MM_STATUS_KEY_IS_INTEGRATION_PRESET, integrationPresetData ? 1 : 0);

        materialThread = new HandlerThread("materialThread");
        materialThread.start();

        materialHandler = buildMaterialHandler();

        Utils.getExecutorService().submit(() -> {
            managerStatus.put(Constants.MM_STATUS_KEY_PLAYLIST_INIT, 0);
            managerStatus.put(Constants.MM_STATUS_KEY_PRESET_INIT, 0);
            managerStatus.put(Constants.MM_STATUS_KEY_PLAYLIST_LOADED, 0);
            managerStatus.put(Constants.MM_STATUS_KEY_PRESET_LOADED, 0);

            downloadModule = new DownloadModule(context, MAX_RATE, downloadStateLisntener);
            ResHelper.sendMessage(materialHandler, Constants.SLIDER_STATUS_CODE_INIT, null);
        });
    }

//    public boolean isInitSuccessed() {
//        return managerStatus.get(Constants.MM_STATUS_KEY_PLAYLIST_INIT) == 1
//                && (!isIntegrationPresetData() || (isIntegrationPresetData() && managerStatus.get(Constants.MM_STATUS_KEY_PRESET_LOADED) == 1));
//    }

    public boolean isActionExecuted(String actionCode) {
        return managerStatus.containsKey(actionCode) && managerStatus.get(actionCode) == 1;
    }

    public boolean isIntegrationPresetData() {
        return managerStatus.contains(Constants.MM_STATUS_KEY_IS_INTEGRATION_PRESET)
                && managerStatus.get(Constants.MM_STATUS_KEY_IS_INTEGRATION_PRESET) == 1;
    }

    public void reload(int resCode) {

        ResHelper.sendMessage(materialHandler, Constants.SLIDER_STATUS_CODE_UPDATE, resCode);
    }

    public void clear() {
        if (null != downloadModule)
            downloadModule.stop();

        if (null != materialThread)
            materialThread.quitSafely();
    }

    protected MaterialStatusListener getRefListener() {
        if (null != itemStatusListener) {
            return itemStatusListener.get();
        }

        return null;
    }

    protected DownloadModule.DownloadStateLisntener downloadStateLisntener = new DownloadModule.DownloadStateLisntener() {
        @Override
        public void onSucc(String url, String path) {
            ResHelper.sendMessage(materialHandler, Constants.SLIDER_STATUS_CODE_DOWNSUCC, new String[]{ url, path });
        }

        @Override
        public void onFail(String url, String code) {

        }
    };

    protected List<String> buildWelcomeWords() {
        return new ArrayList<String>() {
            {
                add("中国农业银行欢迎您");
                add("中国农业银行欢迎您");
                add("中国农业银行欢迎您");
                add("中国农业银行欢迎您");
                add("中国农业银行欢迎您");
                add("中国农业银行欢迎您");
            }
        };
    }



    /*
     * 处理资源下载完成通知
     * */
    protected void finishDownload(String fileUrl, String filePath) {
        if (Constants.SYS_CONFIG_IS_CHECKMD5) {
            if (!waitForDownloadMaterial.containsKey(fileUrl)
                    || !IOHelper.fileToMD5(filePath).equals(waitForDownloadMaterial.get(fileUrl))) {
                IOHelper.deleteFile(filePath);
                waitForDownloadMaterial.remove(fileUrl);
                return;
            }
        }

        PlaylistBodyBean bodyBean = waitForDownloadMaterial.get(fileUrl);

        // 更新素材集状态
        String _fileKey = filePath.substring(filePath.lastIndexOf("/") + 1,
                filePath.lastIndexOf(".") );
        if (materialStatus.containsKey(_fileKey) && materialStatus.get(_fileKey) == 1)
            return;

        String correctionFilePath = ResHelper.getSavePath(bodyBean.downloadLink, bodyBean.id);

        if (!IOHelper.copyOrMoveFile(filePath, correctionFilePath, true)) {
            return;
        }

        filePath = correctionFilePath;
        materialStatus.put(_fileKey, 1);

        // 更新已下载素材状态
        String[] ids = materialStatus.keySet().toArray(new String[0]);
        Utils.put(context, Constants.MM_STATUS_FINISHED_TASKID, ResHelper.join(ids, ","));
        Logger.e(TAG, "MM_STATUS_FINISHED_TASKID-->" + ResHelper.join(ids, ","));

        // 处理pdf缓存或者通知前端显示
//                        Utils.getExecutorService().submit(() -> {
        String fileKey = filePath.substring(filePath.lastIndexOf("/") + 1,
                filePath.lastIndexOf(".") );
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        String suffix = filePath.substring(filePath.lastIndexOf(".") + 1);

        if (suffix.toLowerCase().equals("pdf")) {
            Logger.e(TAG, fileName);
            List<PlayItem> list = PdfHelper.cachePdfToImage( fileName, fileKey,
                    bodyBean.playDate, bodyBean.stopDate,
                    bodyBean.onClickLink, bodyBean.QRCode  );

            ResHelper.sendMessage(uiHandler, Constants.SLIDER_STATUS_CODE_PDF_CACHED, list);
        } else if (BllDataExtractor.getIdentityType(suffix) == Constants.SLIDER_HOLDER_IMAGE
                || BllDataExtractor.getIdentityType(suffix) == Constants.SLIDER_HOLDER_VIDEO) {
            PlayItem playItem = new PlayItem(fileKey,
                    filePath,
                    BllDataExtractor.getIdentityType(suffix),
                    bodyBean.playDate, bodyBean.stopDate,
                    bodyBean.onClickLink, bodyBean.QRCode);

            ResHelper.sendMessage(uiHandler, Constants.SLIDER_STATUS_CODE_DOWNSUCC, new ArrayList<PlayItem>(){
                { add(playItem); }
            });
        } else if (suffix.toLowerCase().equals("txt")) {
            String wmsg = ResHelper.readFile2String(filePath);
            if (!ResHelper.isNullOrEmpty(wmsg)) {
                List<String> list = new ArrayList<>();
                list.add(wmsg);

                ResHelper.sendMessage(uiHandler, Constants.SLIDER_STATUS_CODE_WELCOME_LOADED, list);
            }
        }
//                        });
    }

    Handler uiHandler = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MaterialStatusListener _materialStatusListener = getRefListener();
            if (null == _materialStatusListener)
                return;

            Logger.e(TAG, "tid:(uiHandler)" + Thread.currentThread().getId());

            switch (msg.what) {
                case Constants.SLIDER_STATUS_CODE_WELCOME_DEFAULT:
//                    _materialStatusListener.onWelcomeReady((List<String>) msg.obj);
                    _materialStatusListener.onWelcomePrepared((List<String>) msg.obj, false, true);

                    break;

                case Constants.SLIDER_STATUS_CODE_WELCOME_LOADED:

                    _materialStatusListener.onWelcomePrepared((List<String>) msg.obj, isActionExecuted(Constants.MM_STATUS_KEY_WELCOME_LOADED), false);

                    break;

//                case Constants.SLIDER_STATUS_CODE_PLAYLIST_LOADED:
//                    _materialStatusListener.onCachedItemPrepared((List<PlayItem>)msg.obj);
//
//                    break;

                case Constants.SLIDER_STATUS_CODE_RATE_LOADED:
                    Object[] objs = (Object[])msg.obj;
                    _materialStatusListener.onRatePrepared((List<PlayItem>)objs[0], (List<String>) objs[1]);

                    break;

                case Constants.SLIDER_STATUS_CODE_PLAYLIST_LOADED:
                case Constants.SLIDER_STATUS_CODE_PDF_CACHED:
                case Constants.SLIDER_STATUS_CODE_DOWNSUCC:
                    _materialStatusListener.onPlayItemPrepared( (List<PlayItem>)msg.obj );

                    break;

                case Constants.SLIDER_STATUS_CODE_PROGRESS:
                    _materialStatusListener.onProgress((int)msg.obj);

                    break;

                default:
                    break;
            }
        }

    };

    protected abstract Handler buildMaterialHandler();

    public interface MaterialStatusListener {
        void onProgress(int code);
//        void onCachedItemPrepared(List<PlayItem> items);
//        void onWelcomeReady(List<String> items);
        void onRatePrepared(List<PlayItem> items, List<String> titles);
        void onPlayItemPrepared(List<PlayItem> items);
        void onWelcomePrepared(List<String> msg, boolean isAppend, boolean isDefault);
    }
}
