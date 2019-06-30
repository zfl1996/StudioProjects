package com.ads.abcbank.utils;


import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import java.util.Collection;
import java.util.Iterator;

public class HandlerUtil {
    private static Handler sHandler;

    public HandlerUtil() {
    }

    private static void init() {
        if (sHandler == null) {
            sHandler = new Handler(Looper.getMainLooper());
        }

    }

    public static void destroy() {
        if (sHandler != null) {
            sHandler.removeCallbacksAndMessages(null);
            sHandler = null;
        }

    }

    public static Handler noCheckGet() {
        return sHandler;
    }


    public static boolean post(Runnable r, boolean repost) {
        if (sHandler != null) {
            if (Looper.myLooper() == sHandler.getLooper()) {
                r.run();
            } else {
                if (repost) {
                    sHandler.removeCallbacks(r);
                }

                sHandler.post(r);
            }

            return true;
        } else {
            return false;
        }
    }

    public static void postDelayed(Runnable r, long delayMillis) {
        postDelayed(r, delayMillis, false);
    }

    public static void postDelayed(Runnable r, long delayMillis, boolean repost) {
        if (sHandler != null) {
            if (repost) {
                sHandler.removeCallbacks(r);
            }

            sHandler.postAtTime(r, SystemClock.uptimeMillis() + delayMillis);
        }

    }

    public static void postAtFront(Runnable r) {
        postAtFront(r, false);
    }

    public static void postAtFront(Runnable r, boolean repost) {
        if (sHandler != null) {
            if (repost) {
                sHandler.removeCallbacks(r);
            }

            sHandler.postAtFrontOfQueue(r);
        }

    }

    public static void removeCallbacks(Runnable r) {
        if (sHandler != null) {
            sHandler.removeCallbacks(r);
        }

    }

    public static void removeCallbacks(Collection<Runnable> rs) {
        if (sHandler != null) {
            Iterator var1 = rs.iterator();

            while (var1.hasNext()) {
                Runnable r = (Runnable) var1.next();
                sHandler.removeCallbacks(r);
            }
        }
    }

    static {
        init();
    }
}
