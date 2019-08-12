package com.ads.abcbank.view;

import android.app.Activity;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.utils.ToastUtil;
import com.ads.abcbank.utils.Utils;

public class ExitWindow extends Dialog {

    public ExitWindow(Activity activity) {
        super(activity, R.style.keyboard_dialog_style);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView(activity);
    }

    private void initView(Activity activity) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View conentView;
        conentView = inflater.inflate(R.layout.window_exit_layout, null);
        KeyboardNumView view = conentView.findViewById(R.id.k_keyboard);
        EditText editText = conentView.findViewById(R.id.et_pwd);
        TextView tvCancel = conentView.findViewById(R.id.tv_cancel);
        TextView tvReset = conentView.findViewById(R.id.tv_reset);
        TextView tvSubmit = conentView.findViewById(R.id.tv_submit);
        view.setEditText(editText);
        ViewGroup.LayoutParams viewGroupLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(conentView, viewGroupLayoutParams);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText != null) {
                    editText.setText("");
                }
            }
        });
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText != null) {
                    Editable str = editText.getText();
                    String pwd = Utils.get(getContext(), Utils.KEY_EXIT_PWD, Utils.KEY_DEFAULT_PWD).toString();
                    if (!TextUtils.isEmpty(str) && pwd.equals(str.toString())) {
                        System.exit(0);
                    } else {
                        ToastUtil.showToast(getContext(), "密码错误");
                    }
                }
            }
        });
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.TOP | Gravity.LEFT);
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //因为我的dialog背景图片是圆弧型，不设置背景透明的话圆弧处显示黑色
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        }
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        conentView.measure(width, height);
    }

}
