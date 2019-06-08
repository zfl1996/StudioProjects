package com.ads.abcbank.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ads.abcbank.R;


public class ToastUtil {

    public static void showToast(Context context, String message) {
        Toast toast = new Toast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.view_toast, null);
        TextView tvMessage = (TextView) view.findViewById(R.id.tv_msg);
        tvMessage.setEllipsize(TextUtils.TruncateAt.END);
        tvMessage.setMaxEms(15);
        tvMessage.setSingleLine(true);
        tvMessage.setText(message);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }

    public static void showToastLong(Context context, String message) {
        Toast toast = new Toast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.view_toast, null);
        TextView tvMessage = (TextView) view.findViewById(R.id.tv_msg);
        tvMessage.setEllipsize(TextUtils.TruncateAt.END);
        tvMessage.setMaxEms(15);
        tvMessage.setSingleLine(true);
        tvMessage.setText(message);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }
}
