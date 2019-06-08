package com.ads.abcbank.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
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
    private boolean showToast = false;

    private List<View> getAllViews(Activity act) {
        List<View> list = getAllChildViews(act.getWindow().getDecorView());
        return list;
    }

    private List<View> getAllChildViews(View view) {
        List<View> allchildren = new ArrayList<View>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                allchildren.add(viewchild);
                //再次 调用本身（递归）
                allchildren.addAll(getAllChildViews(viewchild));
            }
        }
        return allchildren;
    }

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
            Log.e(TAG, activity.getLocalClassName());
            if (!activityStack.contains(activity)) {
                activityStack.push(activity);
            }
        } catch (Throwable e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
//        TDConfig_.getInstance_(LauncherApplicationAgent.getInstance().getApplicationContext()).onPageStart(activity, activity.getClass().getSimpleName());
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
            Log.e(TAG, e.toString());
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
            Log.e(TAG, e.toString());
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
                if (activityStack.empty()) break;
                pop = activityStack.pop();
            }
        } catch (Throwable e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * @return
     * @brief APP是否处于前台唤醒状态
     */
    public boolean isAppOnForeground(Context context) {
        android.app.ActivityManager activityManager = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getPackageName();
        List<android.app.ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
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

    public Stack<Activity> getActivityStack() {
        return activityStack;
    }
}
