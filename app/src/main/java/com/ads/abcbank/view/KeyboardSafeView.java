package com.ads.abcbank.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ads.abcbank.R;

import java.util.regex.Pattern;

/**
 * @author ynet
 * @brief 键盘安全界面
 */
public class KeyboardSafeView extends LinearLayout implements View.OnTouchListener {
    public static final int KEY_TEXT_SIZE = 26;
    public static final int KEY_HEIGHT = 60;
    public static final int KEY_MARGIN = 1;
    private static final String[][] LITTLE_LETTER = {{"q", "w", "e", "r", "t", "y", "u", "i", "o", "p"},
            {"a", "s", "d", "f", "g", "h", "j", "k", "l"},
            {KeyTpye.KEY_UP, "z", "x", "c", "v", "b", "n", "m"},
            {KeyTpye.KEY_123, KeyTpye.KEY_SYMBOL}};
    private static final String[][] CAPITAL_LETTER = {{"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
            {"A", "S", "D", "F", "G", "H", "J", "K", "L"},
            {KeyTpye.KEY_UP_CAP, "Z", "X", "C", "V", "B", "N", "M"},
            {KeyTpye.KEY_123, KeyTpye.KEY_SYMBOL}};
    private static final String[][] SYMBOL_LETTER = {
            {"~", "`", "!", "@", "#", "$", "%", "^", "&", "*"},
            {"(", ")", "_", "-", "+", "=", "{", "}", "[", "]"},
            {"|", "\\", ":", ";", "\"", "'", "<", ",", ">", "."},
            {"?", "/", KeyTpye.KEY_123, KeyTpye.KEY_ABC}};
    private static final String[][] NUM_KEY = {
            {"1", "2", "3"},
            {"4", "5", "6"},
            {"7", "8", "9"},
            {KeyTpye.KEY_SYMBOL, "0", KeyTpye.KEY_ABC}};

    /**
     * @brief 左边第一个字符
     */
    private static final String[] LEFT_FIRST_LETTER = {"q", "Q", "~", "(", "|", "?"};
    /**
     * @brief 右边第一个字符
     */
    private static final String[] RIGHT_FIRST_LETTER = {"p", "P", "*", "]", "."};

    private EditText editText;
    private KeyboardWindow keyboardWindow;

    public EditText getEditText() {
        return editText;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public KeyboardWindow getKeyboardWindow() {
        return keyboardWindow;
    }

    public void setKeyboardWindow(KeyboardWindow keyboardWindow) {
        this.keyboardWindow = keyboardWindow;
    }

    public KeyboardSafeView(Context context) {
        super(context);
        initView(context);
    }

    public KeyboardSafeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public KeyboardSafeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        if (context != null) {
            setOrientation(VERTICAL);
            initKeyNum(getContext());
        }
    }

    private boolean isSpecialLetter(String str) {
        if (!TextUtils.isEmpty(str)) {
            if (str.equals(KeyTpye.KEY_123)
                    || str.equals(KeyTpye.KEY_DEL)
                    || str.equals(KeyTpye.KEY_FINISH)
                    || str.equals(KeyTpye.KEY_SYMBOL)
                    || str.equals(KeyTpye.KEY_UP)
                    || str.equals(KeyTpye.KEY_UP_CAP)
                    || str.equals(KeyTpye.KEY_ABC)) {
                return true;
            }
        }
        return false;
    }

    private boolean isFirstLetter(String str) {
        if (!TextUtils.isEmpty(str)) {
            if ("q".equals(str)
                    || "a".equals(str)
                    || "z".equals(str)
                    || "Q".equals(str)
                    || "A".equals(str)
                    || "Z".equals(str)
                    || "~".equals(str)
                    || "(".equals(str)
                    || "|".equals(str)
                    || "?".equals(str)) {
                return true;
            }
        }
        return false;
    }

    private void initKeyLetter(Context context, String[][] letter, boolean isLittle) {
        removeAllViews();
        setGravity(Gravity.CENTER_HORIZONTAL);
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if (context != null && letter != null && letter.length > 0) {
            for (int i = 0; i < letter.length; i++) {
                LinearLayout row = new LinearLayout(context);
                row.setOrientation(HORIZONTAL);
                row.setGravity(Gravity.CENTER_VERTICAL);
                LayoutParams lp = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.topMargin = KEY_MARGIN;
                row.setLayoutParams(lp);
                for (int j = 0; j < letter[i].length; j++) {
                    if (!isSpecialLetter(letter[i][j])) {
                        LayoutParams keyLp = new LayoutParams(0, KEY_HEIGHT);
                        keyLp.weight = 1.0f;
                        if (!isFirstLetter(letter[i][j])) {
                            keyLp.leftMargin = KEY_MARGIN;
                        } else {
                            if ("z".equals(letter[i][j]) || "Z".equals(letter[i][j])) {
                                keyLp.leftMargin = KEY_MARGIN;
                            }
                        }
                        TextView key = new TextView(context);
                        key.setLayoutParams(keyLp);
                        key.setTextColor(context.getResources().getColor(R.color.text_color_333333));
                        key.setTextSize(TypedValue.COMPLEX_UNIT_PX, KEY_TEXT_SIZE);
                        key.setGravity(Gravity.CENTER);
                        key.setBackgroundResource(R.drawable.selector_safe_key_num_bg);
                        key.setTag(letter[i][j]);
                        key.setText(letter[i][j]);
                        key.setOnTouchListener(this);
                        row.addView(key);
                    } else {
                        if (letter[i][j].equals(KeyTpye.KEY_UP)
                                || letter[i][j].equals(KeyTpye.KEY_UP_CAP)) {                                                                        //大小写按键
                            LayoutParams imgLp = new LayoutParams(0, KEY_HEIGHT);
                            imgLp.weight = 1.0f;
                            ImageView keyImage = new ImageView(context);
                            keyImage.setLayoutParams(imgLp);
                            keyImage.setScaleType(ImageView.ScaleType.CENTER);
                            if (isLittle) {
                                keyImage.setBackgroundResource(R.drawable.selector_safe_key_num_bg);
                                keyImage.setImageResource(R.mipmap.ic_key_up);
                            } else {
                                keyImage.setBackgroundResource(R.drawable.selector_safe_key_num_bg);
                                keyImage.setImageResource(R.mipmap.ic_key_up_cap);
                            }
                            keyImage.setTag(letter[i][j]);
                            keyImage.setOnTouchListener(this);
                            row.addView(keyImage);
                        } else if (letter[i][j].equals(KeyTpye.KEY_DEL)) {                                                               //删除按键
                            LayoutParams imgLp = new LayoutParams(0, KEY_HEIGHT);
                            imgLp.weight = 1.0f;
                            ImageView keyImage = new ImageView(context);
                            keyImage.setLayoutParams(imgLp);
                            keyImage.setScaleType(ImageView.ScaleType.CENTER);
                            if (isLittle) {
                                keyImage.setBackgroundResource(R.drawable.selector_safe_key_num_bg);
                                keyImage.setImageResource(R.mipmap.ic_key_safe_del);
                            } else {
                                keyImage.setBackgroundResource(R.drawable.selector_safe_key_num_bg);
                                keyImage.setImageResource(R.mipmap.ic_key_safe_del_cap);
                            }
                            keyImage.setTag(letter[i][j]);
                            keyImage.setOnTouchListener(this);
                            row.addView(keyImage);
                        } else if (letter[i][j].equals(KeyTpye.KEY_123)) {                                                               //数字键盘
                            LayoutParams keyLp = new LayoutParams(0, KEY_HEIGHT);
                            keyLp.weight = 1.0f;
                            TextView key = new TextView(context);
                            key.setLayoutParams(keyLp);
                            key.setTextColor(context.getResources().getColor(R.color.text_color_333333));
                            key.setTextSize(TypedValue.COMPLEX_UNIT_PX, KEY_TEXT_SIZE);
                            key.setGravity(Gravity.CENTER);
                            key.setBackgroundResource(R.drawable.selector_safe_key_num_bg);
                            key.setTag(letter[i][j]);
                            key.setText(letter[i][j]);
                            key.setOnTouchListener(this);
                            row.addView(key);
                        } else if (letter[i][j].equals(KeyTpye.KEY_SYMBOL)) {                                                           //字符
                            LayoutParams keyLp = new LayoutParams(0, KEY_HEIGHT);
                            keyLp.weight = 1.0f;
                            keyLp.leftMargin = KEY_MARGIN;
                            TextView key = new TextView(context);
                            key.setLayoutParams(keyLp);
                            key.setTextColor(context.getResources().getColor(R.color.text_color_333333));
                            key.setTextSize(TypedValue.COMPLEX_UNIT_PX, KEY_TEXT_SIZE);
                            key.setGravity(Gravity.CENTER);
                            key.setBackgroundResource(R.drawable.selector_safe_key_num_bg);
                            key.setTag(letter[i][j]);
                            key.setText("符");
                            key.setOnTouchListener(this);
                            row.addView(key);
                        } else if (letter[i][j].equals(KeyTpye.KEY_FINISH)) {                                                          //完成
                            LayoutParams keyLp = new LayoutParams(0, KEY_HEIGHT);
                            keyLp.weight = 1.0f;
                            keyLp.leftMargin = KEY_MARGIN;
                            TextView key = new TextView(context);
                            key.setLayoutParams(keyLp);
                            key.setTextColor(context.getResources().getColor(R.color.text_color_333333));
                            key.setTextSize(TypedValue.COMPLEX_UNIT_PX, KEY_TEXT_SIZE);
                            key.setGravity(Gravity.CENTER);
                            key.setBackgroundResource(R.drawable.selector_safe_key_num_bg);
                            key.setTag(letter[i][j]);
                            key.setText("完成");
                            key.setOnTouchListener(this);
                            row.addView(key);
                        }
                    }
                }
                addView(row);
            }
        }
    }

    /**
     * @param context
     * @brief 字符键盘
     */
    private void initKeySymbol(Context context) {
        removeAllViews();
        setGravity(Gravity.CENTER_HORIZONTAL);
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if (context != null && SYMBOL_LETTER != null && SYMBOL_LETTER.length > 0) {
            for (int i = 0; i < SYMBOL_LETTER.length; i++) {
                LinearLayout row = new LinearLayout(context);
                row.setOrientation(HORIZONTAL);
                row.setGravity(Gravity.CENTER_VERTICAL);
                LayoutParams lp = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.topMargin = KEY_MARGIN;
                row.setLayoutParams(lp);
                for (int j = 0; j < SYMBOL_LETTER[i].length; j++) {
                    if (!isSpecialLetter(SYMBOL_LETTER[i][j])) {
                        LayoutParams keyLp = new LayoutParams(0, KEY_HEIGHT);
                        keyLp.weight = 1.0f;
                        if (!isFirstLetter(SYMBOL_LETTER[i][j])) {
                            keyLp.leftMargin = KEY_MARGIN;
                        }
                        TextView key = new TextView(context);
                        key.setLayoutParams(keyLp);
                        key.setTextColor(context.getResources().getColor(R.color.text_color_333333));
                        key.setTextSize(TypedValue.COMPLEX_UNIT_PX, KEY_TEXT_SIZE);
                        key.setGravity(Gravity.CENTER);
                        key.setBackgroundResource(R.drawable.selector_safe_key_num_bg);
                        key.setTag(SYMBOL_LETTER[i][j]);
                        key.setText(SYMBOL_LETTER[i][j]);
                        key.setOnTouchListener(this);
                        row.addView(key);
                    } else {
                        if (SYMBOL_LETTER[i][j].equals(KeyTpye.KEY_DEL)) {                                                               //删除按键
                            LayoutParams imgLp = new LayoutParams(0, KEY_HEIGHT);
                            imgLp.weight = 1.0f;
                            imgLp.leftMargin = KEY_MARGIN;
                            ImageView keyImage = new ImageView(context);
                            keyImage.setLayoutParams(imgLp);
                            keyImage.setScaleType(ImageView.ScaleType.CENTER);
                            keyImage.setBackgroundResource(R.drawable.selector_safe_key_num_bg);
                            keyImage.setImageResource(R.mipmap.ic_key_safe_del);
                            keyImage.setTag(SYMBOL_LETTER[i][j]);
                            keyImage.setOnTouchListener(this);
                            row.addView(keyImage);
                        } else if (SYMBOL_LETTER[i][j].equals(KeyTpye.KEY_123)) {                                                               //数字键盘
                            LayoutParams keyLp = new LayoutParams(0, KEY_HEIGHT);
                            keyLp.weight = 1.0f;
                            keyLp.leftMargin = KEY_MARGIN;
                            TextView key = new TextView(context);
                            key.setLayoutParams(keyLp);
                            key.setTextColor(context.getResources().getColor(R.color.text_color_333333));
                            key.setTextSize(TypedValue.COMPLEX_UNIT_PX, KEY_TEXT_SIZE);
                            key.setGravity(Gravity.CENTER);
                            key.setBackgroundResource(R.drawable.selector_safe_key_num_bg);
                            key.setTag(SYMBOL_LETTER[i][j]);
                            key.setText(SYMBOL_LETTER[i][j]);
                            key.setOnTouchListener(this);
                            row.addView(key);
                        } else if (SYMBOL_LETTER[i][j].equals(KeyTpye.KEY_ABC)) {                                                           //ABC
                            LayoutParams keyLp = new LayoutParams(0, KEY_HEIGHT);
                            keyLp.weight = 1.0f;
                            keyLp.leftMargin = KEY_MARGIN;
                            TextView key = new TextView(context);
                            key.setLayoutParams(keyLp);
                            key.setTextColor(context.getResources().getColor(R.color.text_color_333333));
                            key.setTextSize(TypedValue.COMPLEX_UNIT_PX, KEY_TEXT_SIZE);
                            key.setGravity(Gravity.CENTER);
                            key.setBackgroundResource(R.drawable.selector_safe_key_num_bg);
                            key.setTag(SYMBOL_LETTER[i][j]);
                            key.setText("ABC");
                            key.setOnTouchListener(this);
                            row.addView(key);
                        } else if (SYMBOL_LETTER[i][j].equals(KeyTpye.KEY_FINISH)) {                                                          //完成
                            LayoutParams keyLp = new LayoutParams(0, KEY_HEIGHT);
                            keyLp.weight = 1.0f;
                            keyLp.leftMargin = KEY_MARGIN;
                            TextView key = new TextView(context);
                            key.setLayoutParams(keyLp);
                            key.setTextColor(context.getResources().getColor(R.color.text_color_333333));
                            key.setTextSize(TypedValue.COMPLEX_UNIT_PX, KEY_TEXT_SIZE);
                            key.setGravity(Gravity.CENTER);
                            key.setBackgroundResource(R.drawable.selector_safe_key_num_bg);
                            key.setTag(SYMBOL_LETTER[i][j]);
                            key.setText("完成");
                            key.setOnTouchListener(this);
                            row.addView(key);
                        }
                    }
                }
                addView(row);
            }
        }
    }

    private void initKeyNum(Context context) {
        removeAllViews();
        setGravity(Gravity.CENTER_HORIZONTAL);
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if (context != null && NUM_KEY != null && NUM_KEY.length > 0) {
            for (int i = 0; i < NUM_KEY.length; i++) {
                LinearLayout row = new LinearLayout(context);
                row.setOrientation(HORIZONTAL);
                row.setGravity(Gravity.CENTER_VERTICAL);
                LayoutParams lp = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.topMargin = KEY_MARGIN;
                row.setLayoutParams(lp);
                for (int j = 0; j < NUM_KEY[i].length; j++) {
                    if (!isSpecialLetter(NUM_KEY[i][j])) {
                        LayoutParams keyLp = new LayoutParams(0, KEY_HEIGHT);
                        keyLp.weight = 1.0f;
                        if (j != 0) {
                            keyLp.leftMargin = KEY_MARGIN;
                        }
                        TextView key = new TextView(context);
                        key.setLayoutParams(keyLp);
                        key.setTextColor(context.getResources().getColor(R.color.text_color_333333));
                        key.setTextSize(TypedValue.COMPLEX_UNIT_PX, KEY_TEXT_SIZE);
                        key.setGravity(Gravity.CENTER);
                        key.setBackgroundResource(R.drawable.selector_safe_key_num_bg);
                        key.setTag(NUM_KEY[i][j]);
                        key.setText(NUM_KEY[i][j]);
                        key.setOnTouchListener(this);
                        row.addView(key);
                    } else {
                        if (NUM_KEY[i][j].equals(KeyTpye.KEY_DEL)) {                                                               //删除按键
                            LayoutParams imgLp = new LayoutParams(0, KEY_HEIGHT);
                            imgLp.weight = 1.0f;
                            imgLp.leftMargin = KEY_MARGIN;
                            ImageView keyImage = new ImageView(context);
                            keyImage.setLayoutParams(imgLp);
                            keyImage.setScaleType(ImageView.ScaleType.CENTER);
                            keyImage.setBackgroundResource(R.drawable.selector_safe_key_num_bg);
                            keyImage.setImageResource(R.mipmap.ic_key_safe_del);
                            keyImage.setTag(NUM_KEY[i][j]);
                            keyImage.setOnTouchListener(this);
                            row.addView(keyImage);
                        } else if (NUM_KEY[i][j].equals(KeyTpye.KEY_SYMBOL)) {                                                               //符号键盘
                            LayoutParams keyLp = new LayoutParams(0, KEY_HEIGHT);
                            keyLp.weight = 1.0f;
                            TextView key = new TextView(context);
                            key.setLayoutParams(keyLp);
                            key.setTextColor(context.getResources().getColor(R.color.text_color_333333));
                            key.setTextSize(TypedValue.COMPLEX_UNIT_PX, KEY_TEXT_SIZE);
                            key.setGravity(Gravity.CENTER);
                            key.setBackgroundResource(R.drawable.selector_safe_key_num_bg);
                            key.setTag(NUM_KEY[i][j]);
                            key.setText("符");
                            key.setOnTouchListener(this);
                            row.addView(key);
                        } else if (NUM_KEY[i][j].equals(KeyTpye.KEY_ABC)) {                                                           //ABC
                            LayoutParams keyLp = new LayoutParams(0, KEY_HEIGHT);
                            keyLp.weight = 1.0f;
                            keyLp.leftMargin = KEY_MARGIN;
                            TextView key = new TextView(context);
                            key.setLayoutParams(keyLp);
                            key.setTextColor(context.getResources().getColor(R.color.text_color_333333));
                            key.setTextSize(TypedValue.COMPLEX_UNIT_PX, KEY_TEXT_SIZE);
                            key.setGravity(Gravity.CENTER);
                            key.setBackgroundResource(R.drawable.selector_safe_key_num_bg);
                            key.setTag(NUM_KEY[i][j]);
                            key.setText("ABC");
                            key.setOnTouchListener(this);
                            row.addView(key);
                        } else if (NUM_KEY[i][j].equals(KeyTpye.KEY_FINISH)) {                                                          //完成
                            LayoutParams keyLp = new LayoutParams(0, KEY_HEIGHT);
                            keyLp.weight = 1.0f;
                            keyLp.leftMargin = KEY_MARGIN;
                            TextView key = new TextView(context);
                            key.setLayoutParams(keyLp);
                            key.setTextColor(context.getResources().getColor(R.color.text_color_333333));
                            key.setTextSize(TypedValue.COMPLEX_UNIT_PX, KEY_TEXT_SIZE);
                            key.setGravity(Gravity.CENTER);
                            key.setBackgroundResource(R.drawable.selector_safe_key_num_bg);
                            key.setTag(NUM_KEY[i][j]);
                            key.setText("完成");
                            key.setOnTouchListener(this);
                            row.addView(key);
                        }
                    }
                }
                addView(row);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (view.getTag() != null) {
                if (!isSpecialLetter((String) view.getTag())) {
                    String tag = view.getTag().toString();
                    if (editText != null && !TextUtils.isEmpty(tag)) {
                        if (!TextUtils.isEmpty(editText.getText())) {
                            editText.setText(editText.getText().toString() + tag);
                        } else {
                            editText.setText(tag);
                        }
                        editText.setSelection(editText.getText().toString().length());
                    }
                } else {
                    if (view.getTag().equals(KeyTpye.KEY_UP)) {
                        initKeyLetter(getContext(), CAPITAL_LETTER, false);
                    } else if (view.getTag().equals(KeyTpye.KEY_UP_CAP)) {
                        initKeyLetter(getContext(), LITTLE_LETTER, true);
                    } else if (view.getTag().equals(KeyTpye.KEY_SYMBOL)) {
                        initKeySymbol(getContext());
                    } else if (view.getTag().equals(KeyTpye.KEY_ABC)) {
                        initKeyLetter(getContext(), LITTLE_LETTER, true);
                    } else if (view.getTag().equals(KeyTpye.KEY_123)) {
                        initKeyNum(getContext());
                    } else if (view.getTag().equals(KeyTpye.KEY_DEL)) {
                        if (editText != null) {
                            if (!TextUtils.isEmpty(editText.getText()) && editText.getText().toString().length() >= 1) {
                                editText.setText(editText.getText().toString().subSequence(0, editText.getText().toString().length() - 1));
                            }
                            editText.setSelection(editText.getText().toString().length());
                        }
                    } else if (view.getTag().equals(KeyTpye.KEY_FINISH) && keyboardWindow != null) {
                        keyboardWindow.dismiss();
                    }
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (isNumber((String) view.getTag())) {
                view.setBackgroundResource(R.drawable.selector_safe_key_num_bg);
            }
        }
        return true;
    }

    private String regNum = "^[0-9]*";

    public boolean isNumber(String str) {
        Pattern intPattern = Pattern.compile(regNum); //整数样式匹配
        return intPattern.matcher(str).matches();
    }

}
