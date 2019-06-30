package com.ads.abcbank.view;

import android.app.Activity;
import android.app.Dialog;
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

public class KeyboardWindow extends Dialog {
    private EditText editText;
    private boolean isNum;

    public KeyboardWindow(Activity activity, EditText editText, boolean isNum) {
        super(activity, R.style.keyboard_dialog_style);
        this.isNum = isNum;
        this.editText = editText;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView(activity);
    }

    private void initView(Activity activity) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View conentView;
        if (isNum) {
            conentView = inflater.inflate(R.layout.keyboard_safe_num_layout, null);
            KeyboardNumView view = conentView.findViewById(R.id.k_keyboard);
            view.setEditText(editText);
        } else {
            conentView = inflater.inflate(R.layout.keyboard_safe_layout, null);
            KeyboardSafeView view = conentView.findViewById(R.id.k_keyboard);
            view.setEditText(editText);
            view.setKeyboardWindow(this);
        }
        ViewGroup.LayoutParams viewGroupLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(conentView, viewGroupLayoutParams);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        TextView tvFinish = conentView.findViewById(R.id.tv_finish);
        if (tvFinish != null) {
            tvFinish.setVisibility(View.VISIBLE);
            tvFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        TextView tvDel = conentView.findViewById(R.id.tv_del);
        if (tvDel != null) {
            tvDel.setVisibility(View.VISIBLE);
            tvDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editText != null) {
                        if (!TextUtils.isEmpty(editText.getText()) && editText.getText().toString().length() >= 1) {
                            editText.setText(editText.getText().toString().subSequence(0, editText.getText().toString().length() - 1));
                        }
                        editText.setSelection(editText.getText().toString().length());
                    }
                }
            });
        }
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //因为我的dialog背景图片是圆弧型，不设置背景透明的话圆弧处显示黑色
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        }
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        conentView.measure(width, height);
    }

}
