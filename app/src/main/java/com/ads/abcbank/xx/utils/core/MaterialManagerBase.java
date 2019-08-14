package com.ads.abcbank.xx.utils.core;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.utils.Constants;
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


    public void initManager(boolean integrationPresetData, String filters) {
        this.filters = filters;
        managerStatus.put(Constants.MM_KEY_INTEGRATIONPRESET, integrationPresetData ? 1 : 0);

        materialThread = new HandlerThread("materialThread");
        materialThread.start();

        materialHandler = buildMaterialHandler();

        Utils.getExecutorService().submit(() -> {
            managerStatus.put(Constants.MM_STATUS_KEY_PLAYLIST_INIT, 0);
            managerStatus.put(Constants.MM_STATUS_KEY_PRESET_INIT, 0);
            managerStatus.put(Constants.MM_STATUS_KEY_PLAYLIST_LOADED, 0);
            managerStatus.put(Constants.MM_STATUS_KEY_STATUS_PRESET_LOADED, 0);

            downloadModule = new DownloadModule(context, MAX_RATE, downloadStateLisntener);
            ResHelper.sendMessage(materialHandler, Constants.SLIDER_STATUS_CODE_INIT, null);
        });
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

    Handler uiHandler = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MaterialStatusListener _materialStatusListener = getRefListener();
            if (null == _materialStatusListener)
                return;

            Logger.e(TAG, "tid:(uiHandler)" + Thread.currentThread().getId());

            switch (msg.what) {
                case Constants.SLIDER_STATUS_CODE_WELCOME:
                    _materialStatusListener.onWelcome((List<String>) msg.obj);

                    break;

                case Constants.SLIDER_STATUS_CODE_WELCOME_MSG:
                    List<String> welcomeMsg = (List<String>) msg.obj;

                    boolean isAppend = isMaterialLoaded(Constants.MM_STATUS_KEY_STATUS_WELCOME_LOADED);
                    _materialStatusListener.onNewMsgAdded(welcomeMsg, isAppend);

                    if (!isAppend && welcomeMsg.size() > 0)
                        managerStatus.put(Constants.MM_STATUS_KEY_STATUS_WELCOME_LOADED, 1);

                    break;

                case Constants.SLIDER_STATUS_CODE_INIT:
                    _materialStatusListener.onReady((List<PlayItem>)msg.obj);

                    break;

                case Constants.SLIDER_STATUS_CODE_RATE:
                    Object[] objs = (Object[])msg.obj;
                    _materialStatusListener.onRate((List<PlayItem>)objs[0], (List<String>) objs[1]);

                    break;

                case Constants.SLIDER_STATUS_CODE_PDF_CACHED:
                    _materialStatusListener.onItemPrepared((List<PlayItem>)msg.obj);

                    break;

                case Constants.SLIDER_STATUS_CODE_PROGRESS:
                    _materialStatusListener.onProgress((int)msg.obj);

                    break;

                case Constants.SLIDER_STATUS_CODE_DOWNSUCC:
                    _materialStatusListener.onItemPrepared(new ArrayList<PlayItem>(){
                        { add((PlayItem)msg.obj); }
                    } );

                    break;

                default:
                    break;
            }
        }

    };

    protected abstract Handler buildMaterialHandler();

    public interface MaterialStatusListener {
        void onProgress(int code);
        void onReady(List<PlayItem> items);
        void onItemPrepared(List<PlayItem> items);
        void onRate(List<PlayItem> items, List<String> titles);
        void onWelcome(List<String> items);
        void onNewMsgAdded(List<String> msg, boolean isAppend);
    }
}
