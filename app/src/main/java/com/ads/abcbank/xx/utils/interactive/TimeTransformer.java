package com.ads.abcbank.xx.utils.interactive;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.ads.abcbank.xx.utils.helper.ResHelper;

import java.text.SimpleDateFormat;

public class TimeTransformer {
    static final int CODE_OF_TIME_HANDLER = 0x001;

    boolean isStart = false;
    ITimeListener timeListener;
    TimeThread timeThread;

    public TimeTransformer(ITimeListener timeListener) {
        this.timeListener = timeListener;
    }

    public void start() {
        if (isStart)
            return;

        isStart = true;
        timeThread = new TimeThread();
        timeThread.start();
    }

    public void stop() {
        timeThread.interrupt();

        isStart = false;
    }

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CODE_OF_TIME_HANDLER:

                    if (null != timeListener)
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

                    Thread.sleep(1000 * 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            } while (!Thread.currentThread().isInterrupted());
        }
    }

    public interface ITimeListener {
        void onChange(String[] curTime);
    }
}
