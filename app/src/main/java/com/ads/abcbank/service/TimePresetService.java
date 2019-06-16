package com.ads.abcbank.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.ads.abcbank.ProcessConnection;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;

import java.util.Date;

/**
 * 主进程 双进程通讯
 */

public class TimePresetService extends Service {
    public TimePresetService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return new ProcessConnection.Stub() {
        };
    }

    /*每次调用startService启动该服务都会执行*/
    public int onStartCommand(Intent intent, int flags, int startId) {

        Logger.e("TAG", "启动获取汇率列表服务_主进程：" + new Date().toString());

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent i = new Intent(this, PresetService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, Utils.getTimePreset(), pi);

        Notification notification = new Notification();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        startForeground(1, notification);
        //绑定建立链接
        bindService(new Intent(this, ProtectPresetService.class), mServiceConnection, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //链接上
            Logger.e("test", "GuardService:建立链接");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //断开链接
            startService(new Intent(TimePresetService.this, ProtectPresetService.class));
            //重新绑定
            bindService(new Intent(TimePresetService.this, ProtectPresetService.class),
                    mServiceConnection, Context.BIND_IMPORTANT);
        }
    };
}