package com.guo.material.widget;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by guodong on 16/3/20.
 */
public class SlidingDragLayout extends FrameLayout {
    private static final String TAG = "SlidingDragLayout";
    private ViewDragHelper dragHelper;
    private GestureDetectorCompat mDetectorCompat;
    private Status mStatus = Status.Close;
    private View mainContent, rightContent;
    private OnSlidingDragListener dragListener;
    private int mWidth;
    private int mHeight;
    private int menuWidth;
    private int mainLeft;
    private int mDragRange;
    private boolean canDrag = true;

    public SlidingDragLayout(Context context) {
        this(context, null);
    }

    public SlidingDragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingDragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dragHelper = ViewDragHelper.create(this, 1.0f, dragCallback);
        mDetectorCompat = new GestureDetectorCompat(context, gestureListener);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount < 2) {
            throw new IllegalStateException(
                    "You need two childrens in your content");
        }
        if (!(getChildAt(0) instanceof ViewGroup)
                || !(getChildAt(1) instanceof ViewGroup)) {
            throw new IllegalArgumentException(
                    "Your childrens must be an instance of ViewGroup");
        }
        mainContent = getChildAt(0);
        rightContent = getChildAt(1);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mainContent.layout(mainLeft, 0, mainLeft + mWidth, mHeight);
        rightContent.layout(mainLeft + mWidth, 0, mainLeft + mWidth + menuWidth, mHeight);
        Log.d(TAG, "onlayout mainLeft = " + mWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        menuWidth = rightContent.getMeasuredWidth();
        mDragRange = menuWidth;
    }

    public void setDrag(boolean canDrag) {
        this.canDrag = canDrag;
        if (canDrag) {
            dragHelper.abort();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return dragHelper.shouldInterceptTouchEvent(ev) && mDetectorCompat.onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            dragHelper.processTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    ViewDragHelper.Callback dragCallback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            Log.d(TAG, "tryCaptureView "+ String.valueOf(child == rightContent));
            return child == rightContent || child == mainContent;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
//            super.onEdgeTouched(edgeFlags, pointerId);
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            dragHelper.captureChildView(mainContent, pointerId);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (mainLeft + dx > 0) {
                return 0;
            } else if (mainLeft + dx < -mDragRange) {
                return -mDragRange;
            }
            return left;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mDragRange;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {

            if (changedView == mainContent) {
                mainLeft = left;
            } else {
                mainLeft -= dx;
            }

            if (mainLeft > 0) {
                mainLeft = 0;
            } else if (mainLeft < -mDragRange) {
                mainLeft = -mDragRange;
            }
            Log.d(TAG, "position changed mainLeft = " + mainLeft);
            if (changedView == rightContent) {
                layoutContent();
            }
            dispatchDragEvent(mainLeft);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            Log.d(TAG, "xvel = " + xvel);
            if (xvel < 0) {
                open();
            } else if (xvel == 0 && mainLeft < -mDragRange * 0.5f) {
                open();
            } else {
                close();
            }
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
        }
    };

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if ((Math.abs(distanceX) > Math.abs(distanceY)) && distanceX < 0 && canDrag != false && mStatus == Status.Close) {
                return true;
            } else if ((Math.abs(distanceX) > Math.abs(distanceY)) && distanceX > 0 && canDrag != false && mStatus == Status.Open) {
                return true;
            } else {
                return false;
            }

        }
    };

    private void layoutContent() {
        Log.d(TAG, "mainLeft = " + mainLeft);
        Log.d(TAG, "mWidth = " + mWidth);
        Log.d(TAG, "menuWidth = " + menuWidth);
        mainContent.layout(mainLeft, 0, mainLeft + mWidth, mHeight);
        rightContent.layout(mainLeft + mWidth, 0, mainLeft + mWidth + menuWidth, mHeight);
    }

    private void dispatchDragEvent(int mainLeft) {
        float percent = mainLeft / (float) mDragRange;
        ViewHelper.setTranslationX(rightContent, mainLeft);
        if (dragListener != null) {
            dragListener.draging(percent);
        }
        Status lastStatus = mStatus;
        if (updateStatus(mainLeft) != lastStatus) {
            if (dragListener == null) {
                return;
            }
            if (lastStatus == Status.Draging) {
                if (mStatus == Status.Close) {
                    dragListener.close();
                } else if (mStatus == Status.Open) {
                    dragListener.open();
                }
            }
        }
    }

    public void open() {
        open(true);
    }

    public void close() {
        close(true);
    }

    private void open(boolean isSmooth) {
        mainLeft = -mDragRange;
        if (isSmooth) {
            if (dragHelper.smoothSlideViewTo(mainContent, mainLeft, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            layoutContent();
        }
    }

    private void close(boolean isSmooth) {
        mainLeft = 0;
        if (isSmooth) {
            if (dragHelper.smoothSlideViewTo(mainContent, mainLeft, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            layoutContent();
        }
    }


    public static enum Status {
        Open, Close, Draging
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status mStatus) {
        this.mStatus = mStatus;
    }

    private Status updateStatus(int mainLeft) {
        if (mainLeft == 0) {
            mStatus = Status.Close;
        } else if (mainLeft == -mDragRange) {
            mStatus = Status.Open;
        } else {
            mStatus = Status.Draging;
        }
        return mStatus;
    }

    public static interface OnSlidingDragListener {

        void open();

        void close();

        void draging(float percent);
    }

    public void setOnSlidingDragListener(OnSlidingDragListener dragListener) {
        this.dragListener = dragListener;
    }
}
