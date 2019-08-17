package com.ads.abcbank.xx.utils.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.utils.Logger;

import java.lang.reflect.Field;

public class GuiHelper {
    public static void setBottomDrawable(TextView txtView, boolean isShow) {
//        if (isShow) {
//            int imgSize = DPIUtil.dip2px(23);
//
//            Drawable img = getResources().getDrawable(resId);
//            img.setBounds(0, 0, imgSize, imgSize);
//            txtToolbarSubTitle.setCompoundDrawables(img, null, null, null);
//        } else {
//            Drawable img = getResources().getDrawable(R.drawable.holder);
//            img.setBounds(0, 0, 0, 0);
//            txtToolbarSubTitle.setCompoundDrawables(img, null, null, null);
//        }
    }

    public static int getBackgroundResource(Context context) {
        if (context.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE)
            return R.mipmap.bg_land;
        else
            return R.mipmap.bg_port;
    }

    public static void setTabWidth(final TabLayout tabLayout) {
        tabLayout.post(() ->  {
            try {
                //拿到tabLayout的mTabStrip属性
                LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);
                int pWidth = mTabStrip.getWidth();

                for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                    View tabView = mTabStrip.getChildAt(i);
                    tabView.setClickable(false);
                    //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                    Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                    mTextViewField.setAccessible(true);

                    TextView mTextView = (TextView) mTextViewField.get(tabView);

                    tabView.setPadding(0, 0, 0, 0);

                    //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                    int width;
                    width = mTextView.getWidth();
                    if (width == 0) {
                        mTextView.measure(0, 0);
                        width = mTextView.getMeasuredWidth();
                    }

                    //设置tab左右间距 注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                    params.width = width;
                    int padding = (pWidth - width * 3) / 6;
                    params.leftMargin = padding;
                    params.rightMargin = padding;
                    tabView.setLayoutParams(params);

                    tabView.invalidate();
                }

            } catch (NoSuchFieldException e) {
                Logger.e(e.toString());
            } catch (IllegalAccessException e) {
                Logger.e(e.toString());
            }
        });

    }
}
