package com.ads.abcbank.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * @date 2019/5/19
 */

public class TempView extends LinearLayout {
    private Context context;
    private String type;

    public TempView(Context context) {
        this(context, null);
    }

    public TempView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TempView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initView();
    }

    private void initView() {
//        View view = LayoutInflater.from(context).inflate(R.layout.view_preset, null);
//        addView(view);
    }

    public void setType(String type) {
        this.type = type;
    }

}
