package com.ads.abcbank.xx.utils.helper;

import android.content.Context;
import android.view.Display;

import com.ads.abcbank.R;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class DPIHelper {
    private static Display defaultDisplay;
    private static float mDensity;

    public static int dip2px(float paramFloat) {
        return (int) (mDensity * paramFloat + 0.5F);
    }

    @SuppressWarnings("deprecation")
    public static int getHeight() {
        if (null == defaultDisplay)
            return 0;
        else
            return defaultDisplay.getHeight();
    }

    @SuppressWarnings("deprecation")
    public static int getWidth() {
        if (null == defaultDisplay)
            return 0;
        else
            return defaultDisplay.getWidth();
    }

    @SuppressWarnings("deprecation")
    public static int percentHeight(float paramFloat) {
        return (int) (getHeight() * paramFloat);
    }

    @SuppressWarnings("deprecation")
    public static int percentWidth(float paramFloat) {
        return (int) (getWidth() * paramFloat);
    }

    public static int px2dip(Context paramContext, float paramFloat) {
        float f = mDensity;
        return (int) (paramFloat / f + 0.5F);
    }

    public static void setDefaultDisplay(Display paramDisplay) {
        defaultDisplay = paramDisplay;
    }

    public static void setDensity(float paramFloat) {
        mDensity = paramFloat;
    }

    public static boolean isDigit(String str) {
        if (str == null || str.length() <= 0)
            return false;

        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static int getStatusHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch(Exception e1) {
        }

        return sbar;
    }

}
