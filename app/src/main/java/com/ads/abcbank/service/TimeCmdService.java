package com.ads.abcbank.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;

import com.ads.abcbank.ProcessConnection;
import com.ads.abcbank.utils.Logger;

import java.util.Date;

/**
 * 主进程 双进程通讯
 */

public class TimeCmdService extends Service {
    public class MyBinder extends Binder {

        public TimeCmdService getService() {
            return TimeCmdService.this;
        }
    }

    //通过binder实现了 调用者（client）与 service之间的通信
    private MyBinder binder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        initService();
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    public TimeCmdService() {

    }

//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
////        throw new UnsupportedOperationException("Not yet implemented");
//        return new ProcessConnection.Stub() {
//        };
//    }

    private void initService() {
        Logger.e("TAG", "启动获取轮询命令服务_主进程：" + new Date().toString());

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (manager != null) {
            Intent i = new Intent(this, CmdService.class);
            PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 60 * 1000, pi);
        }

        Notification notification = new Notification();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        startForeground(1, notification);
        //绑定建立链接
        bindService(new Intent(this, ProtectCmdService.class), mServiceConnection, Context.BIND_IMPORTANT);
    }


    /*每次调用startService启动该服务都会执行*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        Logger.e("TAG", "启动获取轮询命令服务_主进程：" + new Date().toString());
//
//        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        if (manager != null) {
//            Intent i = new Intent(this, CmdService.class);
//            PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
//            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 60 * 1000, pi);
//        }
//
//        Notification notification = new Notification();
//        notification.flags = Notification.FLAG_ONGOING_EVENT;
//        notification.flags |= Notification.FLAG_NO_CLEAR;
//        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
//        startForeground(1, notification);
//        //绑定建立链接
//        bindService(new Intent(this, ProtectCmdService.class), mServiceConnection, Context.BIND_IMPORTANT);
        initService();
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
//            //断开链接
//            startService(new Intent(TimeCmdService.this, ProtectCmdService.class));
            //重新绑定
            bindService(new Intent(TimeCmdService.this, ProtectCmdService.class),
                    mServiceConnection, Context.BIND_IMPORTANT);
        }
    };
}