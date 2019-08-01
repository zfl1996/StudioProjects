package com.ads.abcbank.xx.utils.net;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.ads.abcbank.R;

public class DownloadNotification {

    private NotificationManager mManager;
    private Context mContext;
    private NotificationCompat.Builder mBuilder;
    private static final int mNotifiyId = 0;

    public DownloadNotification(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        mManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setContentTitle("Aria Download Test")
                .setContentText("进度条")
                .setProgress(100, 0, false)
                .setSmallIcon(R.mipmap.ic_launcher);
        mManager.notify(mNotifiyId, mBuilder.build());
    }

    public void upload(int progress){
        if (mBuilder != null) {
            mBuilder.setProgress(100, progress, false);
            mManager.notify(mNotifiyId, mBuilder.build());
        }
    }
}