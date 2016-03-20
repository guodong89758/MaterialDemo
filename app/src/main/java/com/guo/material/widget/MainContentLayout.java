package com.guo.material.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by guodong on 16/3/20.
 */
public class MainContentLayout extends RelativeLayout {
    private SlidingDragLayout mDragLayout;

    public MainContentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainContentLayout(Context context) {
        super(context);
    }

    public void setSlidingDragLayout(SlidingDragLayout mDragLayout) {
        this.mDragLayout = mDragLayout;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mDragLayout.getStatus() == SlidingDragLayout.Status.Close){
            return super.onInterceptTouchEvent(ev);
        }else {
            return true;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mDragLayout.getStatus() == SlidingDragLayout.Status.Close){
            return super.onTouchEvent(event);
        }else {
            if(MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_UP){
                mDragLayout.close();
            }
            return true;
        }

    }
}
