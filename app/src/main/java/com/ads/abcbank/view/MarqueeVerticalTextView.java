package com.ads.abcbank.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.ads.abcbank.R;
import com.ads.abcbank.utils.Utils;


public class MarqueeVerticalTextView extends LinearLayout {

    private Context mContext;
    private ViewFlipper viewFlipper;
    private View marqueeTextView;
    private int textSize = 14;
    private int textColor = 0xff000000;
    private boolean singleLine = true;

    private int gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
    private static final int GRAVITY_LEFT = 0;
    private static final int GRAVITY_CENTER = 1;
    private static final int GRAVITY_RIGHT = 2;
    private String[] textArrays;
    private MarqueeVerticalTextViewClickListener marqueeTextViewClickListener;

    public MarqueeVerticalTextView(Context context) {
        super(context);
        mContext = context;
    }


    public MarqueeVerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initBasicView(attrs,0);
    }

    public void setTextArraysAndClickListener(String[] textArrays, MarqueeVerticalTextViewClickListener marqueeTextViewClickListener) {
        this.textArrays = textArrays;
        this.marqueeTextViewClickListener = marqueeTextViewClickListener;
        initMarqueeTextView(textArrays, marqueeTextViewClickListener);
    }

    public void initBasicView( AttributeSet attrs, int defStyleAttr) {
        marqueeTextView = LayoutInflater.from(mContext).inflate(R.layout.marquee_vertical_textview_layout, null);
        LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(marqueeTextView, layoutParams);
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.MarqueeVerticalTextViewStyle, defStyleAttr, 0);

        if (typedArray.hasValue(R.styleable.MarqueeVerticalTextViewStyle_mvTextSize)) {
            textSize = (int) typedArray.getDimension(R.styleable.MarqueeVerticalTextViewStyle_mvTextSize, textSize);
            textSize = Utils.px2sp(mContext, textSize);
        }
        textColor = typedArray.getColor(R.styleable.MarqueeVerticalTextViewStyle_mvTextColor, textColor);
        int gravityType = typedArray.getInt(R.styleable.MarqueeVerticalTextViewStyle_mvGravity, GRAVITY_LEFT);
        switch (gravityType) {
            case GRAVITY_LEFT:
                gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                break;
            case GRAVITY_CENTER:
                gravity = Gravity.CENTER;
                break;
            case GRAVITY_RIGHT:
                gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                break;
        }
        viewFlipper = (ViewFlipper) marqueeTextView.findViewById(R.id.viewFlipper);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_out_top));
        viewFlipper.startFlipping();
        typedArray.recycle();
    }

    public void initMarqueeTextView(String[] textArrays, MarqueeVerticalTextViewClickListener marqueeTextViewClickListener) {
        if (textArrays.length == 0) {
            return;
        }

        int i = 0;
        viewFlipper.removeAllViews();
        while (i < textArrays.length) {
            TextView textView = new TextView(mContext);
            textView.setText(textArrays[i]);
            textView.setTag(i);
            textView.setGravity(gravity | Gravity.CENTER_VERTICAL);
            textView.setTextColor(textColor);
            textView.setTextSize(textSize);
            textView.setIncludeFontPadding(true);
            textView.setSingleLine(singleLine);
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (marqueeTextViewClickListener != null) {
                        marqueeTextViewClickListener.onItemClick((Integer) textView.getTag(), (TextView) v);
                    }
                }
            });
            LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            viewFlipper.addView(textView, lp);
            i++;
        }
    }

    public void releaseResources() {
        if (marqueeTextView != null) {
            if (viewFlipper != null) {
                viewFlipper.stopFlipping();
                viewFlipper.removeAllViews();
                viewFlipper = null;
            }
            marqueeTextView = null;
        }
    }

}