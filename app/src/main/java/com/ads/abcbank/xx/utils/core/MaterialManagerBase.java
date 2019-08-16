package com.ads.abcbank.xx.utils.core;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.utils.Constants;
import com.ads.abcbank.xx.utils.helper.ResHelper;
import com.alibaba.fastjson.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MaterialManagerBase {

    protected final String TAG = MaterialManagerBase.class.getSimpleName();

    Context context;
    DownloadModule downloadModule;
    NetTaskManager netTaskManager;

    // worker thread
    HandlerThread materialThread;
    Handler materialHandler;

    // bll data
    WeakReference<MaterialStatusListener> itemStatusListener = null;
    String deviceModeData;
    ConcurrentHashMap<String, PlaylistBodyBean> loadedMaterial = new ConcurrentHashMap<>();
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
        initNetTaskManager();
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

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            netTaskManager.initNetManager();
        });
    }

    private void initNetTaskManager() {
        netTaskManager = new NetTaskManager(context, new NetTaskManager.NetTaskListener() {
            @Override
            public void onPlaylistArrived(JSONObject jsonObject) {
                reload(Constants.NET_MANAGER_DATA_PLAYLIST);
            }

            @Override
            public void onPresetArrived(JSONObject jsonObject) {
                reload(Constants.NET_MANAGER_DATA_PRESET);
            }
        });
    }

    public boolean isActionExecuted(String actionCode) {
        return managerStatus.containsKey(actionCode) && managerStatus.get(actionCode) == 1;
    }

    public void reload(int resCode) {
        ResHelper.sendMessage(materialHandler, Constants.SLIDER_STATUS_CODE_UPDATE, resCode);
    }

    public void clearResource() {
        Utils.put(context, Constants.MM_STATUS_FINISHED_TASKID, "");
    }

    public void quit() {
        if (null != downloadModule)
            downloadModule.stop();

        if (null != netTaskManager)
            netTaskManager.cancalTask();

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


    Handler uiHandler = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MaterialStatusListener _materialStatusListener = getRefListener();
            if (null == _materialStatusListener)
                return;

//            Logger.e(TAG, "tid:(uiHandler)" + Thread.currentThread().getId());

            switch (msg.what) {
                case Constants.SLIDER_STATUS_CODE_WELCOME_DEFAULT:
                    _materialStatusListener.onWelcomePrepared((List<String>) msg.obj, false, true);

                    break;

                case Constants.SLIDER_STATUS_CODE_WELCOME_LOADED: {
                    boolean isLoaded = isActionExecuted(Constants.MM_STATUS_KEY_WELCOME_LOADED);
                    if (!isLoaded)
                        managerStatus.put(Constants.MM_STATUS_KEY_WELCOME_LOADED, 1);

                    _materialStatusListener.onWelcomePrepared((List<String>) msg.obj, isLoaded, false);
                }

                    break;

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
        void onRatePrepared(List<PlayItem> items, List<String> titles);
        void onPlayItemPrepared(List<PlayItem> items);
        void onWelcomePrepared(List<String> msg, boolean isAppend, boolean isDefault);
    }
}
