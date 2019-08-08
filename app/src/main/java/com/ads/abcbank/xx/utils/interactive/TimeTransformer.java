package com.ads.abcbank.xx.utils.interactive;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.DateFormat;

import com.ads.abcbank.xx.utils.helper.ResHelper;

import java.text.SimpleDateFormat;

public class TimeTransformer {
    static final int CODE_OF_TIME_HANDLER = 0x001;

    boolean isStart = false;
    ITimeListener timeListener;

    public TimeTransformer(ITimeListener timeListener) {
        this.timeListener = timeListener;
    }

    public void start() {
        if (isStart)
            return;

        isStart = true;
        new TimeThread().start();
    }

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CODE_OF_TIME_HANDLER:

                    timeListener.onChange((String[])msg.obj);

                    break;

                default:
                    break;

            }
        }
    };

    class TimeThread extends Thread {

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        @Override
        public void run() {
            do {
                try {
                    Message msg = handler.obtainMessage(CODE_OF_TIME_HANDLER);
                    msg.obj = ResHelper.getTimeString(dateFormat, timeFormat);

                    handler.sendMessage(msg);

                    Thread.sleep(1000 * 60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    public interface ITimeListener {
        void onChange(String[] curTime);
    }
}
