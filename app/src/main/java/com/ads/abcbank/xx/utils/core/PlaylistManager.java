package com.ads.abcbank.xx.utils.core;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Pair;

import com.ads.abcbank.xx.model.MaterialInfo;
import com.ads.abcbank.xx.utils.BllDataExtractor;

import java.util.ArrayList;
import java.util.List;

public class PlaylistManager {
    private static final int MSG_OF_PLAYLIST = 0x001;

    private Context context;
    private HandlerThread plThread;
    private Handler plHandler;
    private List<MaterialInfo> materialInfos = new ArrayList<>();
    private IPlaylistStatusListener playlistStatusListener;

    public PlaylistManager(Context context) {
        this.context = context;

        plThread = new HandlerThread("PlaylistManager");
        plThread.start();

        plHandler = new Handler(plThread.getLooper()) {
          @Override
          public void handleMessage(Message msg) {
              super.handleMessage(msg);

              switch (msg.what) {
                  case MSG_OF_PLAYLIST:
                      checkMaterialStatus();

                      plHandler.postDelayed(() -> {
                          plHandler.sendMessage(buildMessage(MSG_OF_PLAYLIST, null, false));
                      }, 1000 * 30);

                      break;

                  default:
                      break;
              }
          }
        };

        plHandler.sendMessage(buildMessage(MSG_OF_PLAYLIST, null, false));
    }

    private /*Map<String, Integer>*/void checkMaterialStatus() {
//        Map<String, Integer> outtimeItems = new HashMap<>();

        for (MaterialInfo mi : materialInfos) {
            int index = getOuttimeItem(materialInfos, mi);
            if (index != -1) {
                materialInfos.remove(mi);
//                outtimeItems.put(mi.getId(), index);

                if (null != playlistStatusListener) {
                    playlistStatusListener.onOuttime(new Pair<>(mi.getId(), index));
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

//        return outtimeItems;
    }

    private int getOuttimeItem(List<MaterialInfo> materialInfos, MaterialInfo currentItem) {
        for (int i=0; i<materialInfos.size(); i++) {
            MaterialInfo mi = materialInfos.get(i);
            if (!BllDataExtractor.isInPlayTime(mi.getPlayDate(), mi.getStopDate())) {
                return i;
            }
        }

        return -1;
    }

    public void addMaterialInfo(MaterialInfo materialInfo) {
        materialInfos.add(materialInfo);
    }

    Message buildMessage(int w, Object obj, boolean isMain) {
        Message msg = isMain ? plHandler.obtainMessage() : plHandler.obtainMessage();
        msg.what = w;
        msg.obj = obj;

        return msg;
    }

    public interface IPlaylistStatusListener {
        void onOuttime(Pair<String, Integer> item);
    }
}
