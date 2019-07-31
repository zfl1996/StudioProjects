package com.ads.abcbank.xx.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RecyclerPagerView extends RecyclerView implements Handler.Callback {

    private static final long TASK_TIMEOUT = 5000;
    public OnPageChangeListener onPageChangeListener;

    private final Handler mRecyclerHandler;
    private final int MSG_PLAY_NEXT  = 112233;
    private volatile boolean isPlaying = false;
    private boolean lastIsPlayState = false;
    private int realPosition = -1;

    public RecyclerPagerView(Context context) {
        this(context,null);
    }

    public RecyclerPagerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RecyclerPagerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mRecyclerHandler = new Handler(Looper.getMainLooper(),this);
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
        if(this.onPageChangeListener!=null){
            addOnScrollListener(this.onPageChangeListener);
            int currentItem = getCurrentItem();
            this.onPageChangeListener.onPageSelection(currentItem);
        }
    }

    public int getCurrentItem(){
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();
        return linearLayoutManager.findFirstVisibleItemPosition();
    }

    public void setCurrentItem(int position,boolean isAnimate){
        Adapter adapter = getAdapter();
        if(adapter==null || adapter.getItemCount()<=position){
            return;
        }
        if(!isAnimate)
        {
            scrollToPosition(position);
        }else {
            smoothScrollToPosition(position);
        }
    }
    public void setCurrentItem(int position ){
        setCurrentItem(position,true);
    }


    @Override
    public boolean fling(int velocityX, int velocityY) {

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();

        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        // views on the screen
        int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
        View lastView = linearLayoutManager.findViewByPosition(lastVisibleItemPosition);
        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        View firstView = linearLayoutManager.findViewByPosition(firstVisibleItemPosition);

        // distance we need to scroll
        int leftMargin = (screenWidth - lastView.getWidth()) / 2;
        int rightMargin = (screenWidth - firstView.getWidth()) / 2 + firstView.getWidth();
        int leftEdge = lastView.getLeft();
        int rightEdge = firstView.getRight();
        int scrollDistanceLeft = leftEdge - leftMargin;
        int scrollDistanceRight = rightMargin - rightEdge;

        int targetPosition;

        if (Math.abs(velocityX) < 1500) {
            // The fling is slow -> stay at the current page if we are less than half through,
            // or go to the next page if more than half through

            if (leftEdge > screenWidth / 2) {
                // go to next page
                smoothScrollBy(-scrollDistanceRight, 0);
                targetPosition = firstVisibleItemPosition;

            } else if (rightEdge < screenWidth / 2) {
                // go to next page
                smoothScrollBy(scrollDistanceLeft, 0);
                targetPosition = firstVisibleItemPosition+1;
            } else {
                // stay at current page
                if (velocityX > 0) {
                    smoothScrollBy(-scrollDistanceRight, 0);
                } else {
                    smoothScrollBy(scrollDistanceLeft, 0);
                }
                targetPosition = firstVisibleItemPosition;
            }
        } else {
            // The fling is fast -> go to next page

            if (velocityX > 0) {
                smoothScrollBy(scrollDistanceLeft, 0);
                targetPosition = firstVisibleItemPosition+1;
            } else {
                smoothScrollBy(-scrollDistanceRight, 0);
                targetPosition = firstVisibleItemPosition;
            }

        }

        Log.e("RecyclerPagerView","nextPage="+targetPosition);
        if(this.onPageChangeListener!=null){
            realPosition = targetPosition;
            this.onPageChangeListener.onPageSelection(targetPosition);
        }
        return true;
    }



    @Override
    public void onScrollStateChanged(final  int state) {
        super.onScrollStateChanged(state);

        if (state == SCROLL_STATE_IDLE) {

            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();

            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

            int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            View lastView = linearLayoutManager.findViewByPosition(lastVisibleItemPosition);
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            View firstView = linearLayoutManager.findViewByPosition(firstVisibleItemPosition);

            // distance we need to scroll
            int leftMargin = (screenWidth - lastView.getWidth()) / 2;
            int rightMargin = (screenWidth - firstView.getWidth()) / 2 + firstView.getWidth();
            int leftEdge = lastView.getLeft();
            int rightEdge = firstView.getRight();
            int scrollDistanceLeft = leftEdge - leftMargin;
            int scrollDistanceRight = rightMargin - rightEdge;
            int  targetPosition = -1;
            if (leftEdge > screenWidth / 2) {
                smoothScrollBy(-scrollDistanceRight, 0);
                targetPosition = firstVisibleItemPosition+1;
            } else if (rightEdge < screenWidth / 2) {
                smoothScrollBy(scrollDistanceLeft, 0);
                targetPosition = lastVisibleItemPosition;
            }else{
                targetPosition = firstVisibleItemPosition;
            }
            if(this.onPageChangeListener!=null){
                realPosition = targetPosition;
                this.onPageChangeListener.onPageSelection(targetPosition);
            }
        }

    }

    @Override
    public boolean handleMessage(Message msg) {
        int what = msg.what;
        switch (what){
            case MSG_PLAY_NEXT:
                showNextPage();
                break;
        }

        return false;
    }

    private void showNextPage() {
        if(!isPlaying){
            return;
        }
        if(!canRecyclePlaying()){
            isPlaying = false;
            return;
        }
        Adapter adapter = getAdapter();
        int currentItem = getCurrentItem();
        if(adapter!=null && adapter.getItemCount()>0) {
            if (currentItem == NO_POSITION  ) {
                setCurrentItem(0);
            }else {
                setCurrentItem(currentItem+1);
            }
        }

        mRecyclerHandler.sendEmptyMessageDelayed(MSG_PLAY_NEXT,TASK_TIMEOUT);
    }

    public void startPlay(){
        if(isPlaying){
            stopPlay();
        }
        if (!canRecyclePlaying()){
            isPlaying = false;
            return;
        }

        isPlaying = true;
        mRecyclerHandler.sendEmptyMessageDelayed(MSG_PLAY_NEXT,TASK_TIMEOUT);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if(canRecyclePlaying()){
            if(realPosition==-1){
                realPosition = 1000;
            }
            setCurrentItem(realPosition,false);
        }
    }

    private boolean canRecyclePlaying() {
        Adapter adapter = getAdapter();
        if(adapter==null || adapter.getItemCount()<1) return false;
        return true;
    }

    private void stopPlay() {
        isPlaying = false;
        mRecyclerHandler.removeMessages(MSG_PLAY_NEXT);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(lastIsPlayState){
            startPlay();
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        lastIsPlayState = isPlaying;
        stopPlay();
    }

    public static    abstract class OnPageChangeListener extends RecyclerView.OnScrollListener{
        public abstract  void onPageSelection(int position);
    }

}