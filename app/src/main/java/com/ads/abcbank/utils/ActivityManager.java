package com.ads.abcbank.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import java.util.List;
import java.util.Stack;

public class ActivityManager implements Application.ActivityLifecycleCallbacks {
    private final String TAG = "ActivityManager";
    private static ActivityManager mActivityManager;
    private Stack<Activity> activityStack = new Stack<Activity>();
    /**
     * @brief 记录当前已经进入后台
     */
    private boolean isActive = false;

    private ActivityManager() {
    }

    public static ActivityManager getInstance() {
        if (mActivityManager == null) {
            synchronized (ActivityManager.class) {
                if (mActivityManager == null) {
                    mActivityManager = new ActivityManager();
                }
            }
        }
        return mActivityManager;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        try {
            Logger.e(TAG, activity.getLocalClassName());
            if (!activityStack.contains(activity)) {
                activityStack.push(activity);
            }
        } catch (Throwable e) {
            Logger.e(TAG, e.toString());
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (!isActive) {
            //app 从后台唤醒，进入前台
            isActive = true;
        }
    }

    @Override
    public void onActivityPaused(final Activity activity) {
        try {
            if (activity.isFinishing()) {
                return;
            }
        } catch (Exception e) {
            Logger.e(TAG, e.toString());
        }
    }

    @Override
    public void onActivityStopped(final Activity activity) {
        if (activity.isFinishing()) {
            return;
        }
        if (!isAppOnForeground(activity)) {
            //app 进入后台
            isActive = false;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        try {
            activityStack.remove(activity);
        } catch (Throwable e) {
            Logger.e(TAG, e.toString());
        }
    }

    public void finishAllActivity() {
        try {
            if (activityStack.empty()) {
                return;
            }
            Activity pop = activityStack.pop();
            while (pop != null) {
                pop.finish();
                //TODO onActivityDestroyed的时候会remove，可能会影响到这里代码运行
                if (activityStack.empty()) {
                    break;
                }
                pop = activityStack.pop();
            }
        } catch (Throwable e) {
            Logger.e(TAG, e.toString());
        }
    }

    /**
     * @return
     * @brief APP是否处于前台唤醒状态
     */
    public boolean isAppOnForeground(Context context) {
        android.app.ActivityManager activityManager = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        String packageName = context.getPackageName();
        List<android.app.ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (android.app.ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    public Activity getTopActivity() {
        if (activityStack != null && !activityStack.empty()) {
            return activityStack.peek();
        }
        return null;
    }

}
