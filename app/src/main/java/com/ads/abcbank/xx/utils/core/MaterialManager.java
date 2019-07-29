package com.ads.abcbank.xx.utils.core;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.xx.utils.Constants;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialManager {
    Context context;
    Handler uiHandler;

    // worker thread
    HandlerThread playerThread;
    Handler playerHandler;

    // bll data
    String deviceModeData;
    List<PlaylistBodyBean> playlist = new ArrayList<>();
    List<PlaylistBodyBean> txtlist = new ArrayList<>();
    List<String> imgArr = new ArrayList<>();
    Map<String, Integer> itemStatus = new HashMap<>();

    public MaterialManager(Context context, Handler uiHandler) {
        this.context = context;
        this.uiHandler = uiHandler;
    }


    private void initManager() {
        playerThread = new HandlerThread("playerThread");
        playerThread.start();

        playerHandler = new Handler(playerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case Constants.SLIDER_STATUS_CODE_INIT:
                        loadPlaylist();

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

    private void loadPlaylist() {
        String json = Utils.get(context, Utils.KEY_PLAY_LIST, "").toString();
        if (!TextUtils.isEmpty(json)) {
            try {
                List<PlaylistBodyBean> playlistBodyBeans = JSON.parseArray(json, PlaylistBodyBean.class);

                for (PlaylistBodyBean bodyBean:playlistBodyBeans) {
                    itemStatus.put(bodyBean.md5, 0);

                    String suffix = bodyBean.name.substring(bodyBean.name.lastIndexOf(".") + 1).toLowerCase();
                    if ("txt".equals(suffix)) {
                        txtlist.add(bodyBean);
                    } else {
                        playlist.add(bodyBean);
                    }
                }

            } catch (Exception e) {
                Logger.e("解析播放列表出错" + json);
            }
        } else {
            txtlist.clear();
            playlist.clear();
        }
    }
}
