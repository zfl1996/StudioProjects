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
import com.ads.abcbank.xx.utils.helper.ResHelper;
import com.alibaba.fastjson.JSON;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialManager {
    Context context;

    // worker thread
    HandlerThread playerThread;
    Handler playerHandler;

    // bll data
    WeakReference<ItemStatusListener> itemStatusListener = null;
    String deviceModeData;
    List<PlaylistBodyBean> playlist = new ArrayList<>();
    List<PlaylistBodyBean> txtlist = new ArrayList<>();
    Map<String, Integer> itemStatus = new HashMap<>();

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
                    case Constants.SLIDER_STATUS_CODE_INIT:
                        Utils.getExecutorService().submit(() -> loadPlaylist());
                        Utils.getExecutorService().submit(() -> loadPreset());
                        Utils.getExecutorService().submit(() -> getWelcome());

                        break;

                    case Constants.SLIDER_STATUS_CODE_UPDATE:
                        if (msg.obj instanceof String) {
                            String md5 = (String)msg.obj;
                            updateItemStatus(md5);
                        }

                        break;

                    default:
                        break;
                }
            }
        };

        playerHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_INIT, null, false));
    }

    private void loadPreset() {
        String json = Utils.get(context, Utils.KEY_PRESET, "").toString();

        if (!ResHelper.isNullOrEmpty(json)) {
            PresetBean bean = JSON.parseObject(json, PresetBean.class);

            List<PlayItem> presetItems = new ArrayList<>();

            presetItems.add( new PlayItem(Constants.SLIDER_HOLDER_RATE_SAVE,
                    bean.data.saveRate ) );
            presetItems.add( new PlayItem(Constants.SLIDER_HOLDER_RATE_LOAN,
                    bean.data.loanRate ) );
            presetItems.add( new PlayItem(Constants.SLIDER_HOLDER_RATE_BUY,
                    bean.data.buyInAndOutForeignExchange ) );
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            uiHandler.sendMessage(buildMessage(Constants.SLIDER_STATUS_CODE_RATE, presetItems, true));
        }
    }

    private void loadPlaylist() {
        String json = Utils.get(context, Utils.KEY_PLAY_LIST, "").toString();
        if (!ResHelper.isNullOrEmpty(json)) {
            try {
                List<PlaylistBodyBean> playlistBodyBeans = JSON.parseArray(json, PlaylistBodyBean.class);
                List<PlayItem> allPlayItems = new ArrayList<>();
int i=0;
                for (PlaylistBodyBean bodyBean:playlistBodyBeans) {
                    itemStatus.put(bodyBean.md5, 0);

                    String suffix = bodyBean.name.substring(bodyBean.name.lastIndexOf(".") + 1).toLowerCase();
                    if ("txt".equals(suffix)) {
                        txtlist.add(bodyBean);
                    } else {
                        playlist.add(bodyBean);
                    }
if (i >7 )
                    allPlayItems.add(new PlayItem(bodyBean.md5,
                            BllDataExtractor.getIdentityPath(bodyBean),
                            BllDataExtractor.getIdentityType(bodyBean) ));
i++;
                }

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

                        uiHandler.postDelayed(() -> {
                            _itemStatusListener.onNewItemAdded( new PlayItem("sds",
                                    "https://pics0.baidu.com/feed/08f790529822720e8a822cb4f2e03f43f31fabe5.jpeg?token=a031733d29d2483ebcdd70c6d069842c&s=84FA7B849BFB11863488CD3203008091",
                                    0));
                        }, 3000);
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

                default:
                    break;
            }
        }

    };

    public interface ItemStatusListener {
        void onReady(List<PlayItem> items);
        void onNewItemAdded(PlayItem item);
        void onNewItemsAdded(List<PlayItem> items);
        void onWelcome(List<String> items);
    }

}
