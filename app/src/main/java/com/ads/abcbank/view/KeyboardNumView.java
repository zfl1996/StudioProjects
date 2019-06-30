package com.ads.abcbank.view;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ads.abcbank.R;

/**
 * @author ynet
 * @brief 键盘数字界面
 */
public class KeyboardNumView extends LinearLayout implements View.OnClickListener {
    private static final int KEY_HEIGHT = 60;
    public static final int KEY_TEXT_SIZE = 26;
    private static final String[][] AMOUNT_KEY = {
            {"1", "2", "3"},
            {"4", "5", "6"},
            {"7", "8", "9"},
            {".", "0", ":"}};
    private EditText editText;

    public EditText getEditText() {
        return editText;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public KeyboardNumView(Context context) {
        super(context);
        initView(context);
    }

    public KeyboardNumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public KeyboardNumView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        if (context != null) {
            setOrientation(VERTICAL);
            initKeyCodeNormal(context, AMOUNT_KEY);
        }
    }

    private void initKeyCodeNormal(Context context, String[][] codes) {
        removeAllViews();

        if (context != null) {
            for (int i = 0; i < codes.length; i++) {
                LinearLayout row = new LinearLayout(context);
                row.setOrientation(HORIZONTAL);
                row.setGravity(Gravity.CENTER_VERTICAL);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                for (int j = 0; j < codes[i].length; j++) {
                    LayoutParams keyLp = new LayoutParams(0, KEY_HEIGHT);
                    keyLp.weight = 1.0f;
                    {
                        TextView key = new TextView(context);
                        key.setLayoutParams(keyLp);
                        key.setPadding(0, 10, 0, 10);
                        key.setTextColor(Color.parseColor("#333333"));
                        key.setTextSize(TypedValue.COMPLEX_UNIT_PX, KEY_TEXT_SIZE);
                        key.setGravity(Gravity.CENTER);
                        key.setBackgroundResource(R.drawable.selector_key_num_btn_bg);
                        key.setTag(codes[i][j]);
                        key.setText(codes[i][j]);
                        key.setOnClickListener(this);
                        row.addView(key);
                    }
                    if (j != codes[i].length - 1) {
                        View divider = new View(context);
                        LayoutParams dividerLp = new LayoutParams(1, KEY_HEIGHT);
                        divider.setLayoutParams(dividerLp);
                        divider.setBackgroundColor(context.getResources().getColor(R.color.key_divider));
                        row.addView(divider);
                    }
                }
                addView(row);
                if (i != codes.length - 1) {
                    View rDivider = new View(context);
                    LayoutParams dividerLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                    rDivider.setLayoutParams(dividerLp);
                    rDivider.setBackgroundColor(context.getResources().getColor(R.color.key_divider));
                    addView(rDivider);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (editText != null && !TextUtils.isEmpty(tag)) {
            if (!TextUtils.isEmpty(editText.getText())) {
                editText.setText(editText.getText().toString() + tag);
            } else {
                editText.setText(tag);
            }
            editText.setSelection(editText.getText().toString().length());
        }
    }
}
