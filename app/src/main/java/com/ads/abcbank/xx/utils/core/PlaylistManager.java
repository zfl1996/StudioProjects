package com.ads.abcbank.xx.utils.core;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.xx.model.MaterialInfo;
import com.ads.abcbank.xx.model.PlayItem;
import com.ads.abcbank.xx.utils.BllDataExtractor;
import com.ads.abcbank.xx.utils.helper.ResHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlaylistManager {
    final static String TAG = "PlaylistManager";

    private static final int MSG_OF_PLAYLIST = 0x001;
    private static final int MSG_OF_PLAYLIST_ADD = 0x002;

    private Context context;
    private HandlerThread plThread;
    private Handler plHandler;
    private List<MaterialInfo> materialInfos = new ArrayList<>();
    private IPlaylistStatusListener playlistStatusListener;

    public PlaylistManager(Context context, IPlaylistStatusListener playlistStatusListener) {
        this.context = context;
        this.playlistStatusListener = playlistStatusListener;

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

                  case MSG_OF_PLAYLIST_ADD:
                      List<PlayItem> list = (List<PlayItem>)msg.obj;
                      for (PlayItem pi : list) {
                          materialInfos.add(new MaterialInfo(pi.getMd5(), pi.getPlayDate(), pi.getStopDate()));
                      }

                      break;

                  default:
                      break;
              }
          }
        };

        plHandler.sendMessage(buildMessage(MSG_OF_PLAYLIST, null, false));
    }

    private void checkMaterialStatus() {
        if (null == playlistStatusListener)
            return;

        int i = 0;

        Iterator<MaterialInfo> it = materialInfos.iterator();

        while (it.hasNext()) {
            MaterialInfo mi = it.next();
            if (!ResHelper.isNullOrEmpty(mi.getId())
                    && !ResHelper.isNullOrEmpty(mi.getPlayDate())
                    && !ResHelper.isNullOrEmpty(mi.getStopDate())
                    && !BllDataExtractor.isInPlayTime(mi.getPlayDate(), mi.getStopDate())) {
                Logger.e(TAG, "NotInPlayTime-->" + mi.getId() + " index:" + i + " time:"
                 + mi.getPlayDate() + "-" + mi.getStopDate() );

                playlistStatusListener.onOutOfTime(mi.getId(), i);
                it.remove();

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                i++;
            }
        }
    }

    public void addMaterialInfo(List<PlayItem> items) {
        plHandler.sendMessage( buildMessage(MSG_OF_PLAYLIST_ADD, items, false) );
    }

    Message buildMessage(int w, Object obj, boolean isMain) {
        Message msg = isMain ? plHandler.obtainMessage() : plHandler.obtainMessage();
        msg.what = w;
        msg.obj = obj;

        return msg;
    }

    public interface IPlaylistStatusListener {
        void onOutOfTime(String id, int index);
    }
}
