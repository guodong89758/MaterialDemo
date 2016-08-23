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


/**
 * Created by guodong on 16/3/20.
 */
public class SlidingLeftDragLayout extends FrameLayout {
    private static final String TAG = "SlidingDragLayout";
    private ViewDragHelper dragHelper;
    private GestureDetectorCompat mDetectorCompat;
    private Status mStatus = Status.Close;
    private View mainContent, leftContent;
    private OnSlidingDragListener dragListener;
    private int mWidth;
    private int mHeight;
    private int menuWidth;
    private int mainLeft;
    private int mDragRange;
    private boolean canDrag = true;

    public SlidingLeftDragLayout(Context context) {
        this(context, null);
    }

    public SlidingLeftDragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingLeftDragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dragHelper = ViewDragHelper.create(this, 0.8f, dragCallback);
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
        leftContent = getChildAt(0);
        mainContent = getChildAt(1);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mainContent.layout(mainLeft, 0, mainLeft + mWidth, mHeight);
        leftContent.layout(mainLeft - mWidth, 0, mainLeft - mWidth + menuWidth, mHeight);
        Log.d(TAG, "onlayout mainLeft = " + mWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        menuWidth = leftContent.getMeasuredWidth();
        mDragRange = menuWidth;
    }

    public void setDrag(boolean canDrag) {
        this.canDrag = canDrag;
        if (canDrag) {
            dragHelper.abort();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getStatus() == Status.Close) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = ev.getX();
                    downY = ev.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    dsX = ev.getX() - downX;
                    dsY = ev.getY() - downY;
                    Log.d(TAG, "downX = " + downX);
                    Log.d(TAG, "downY = " + downY);
                    Log.d(TAG, "curY = " + ev.getY());
                    Log.d(TAG, "curX = " + ev.getX());
                    Log.d(TAG, "dsX = " + dsX + " dsY = " + dsY);
                    break;
                default:
                    break;
            }
        } else if (mStatus == Status.Open && dragHelper.isViewUnder(mainContent, (int) ev.getX(), (int) ev.getY())) {
            mDetectorCompat.onTouchEvent(ev);
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (getStatus() == Status.Close) {
            if (Math.abs(dsY) >= Math.abs(dsX) || Math.abs(dsX) == 0) {
                return super.onInterceptTouchEvent(ev);
            }
        }

        return dragHelper.shouldInterceptTouchEvent(ev);

    }

    private float downX, downY, dsX, dsY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getStatus() == Status.Close) {

            if (event.getAction() == MotionEvent.ACTION_MOVE && Math.abs(dsY) >= Math.abs(dsX)) {
                return super.onTouchEvent(event);
            }
        }

        mDetectorCompat.onTouchEvent(event);
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
            Log.d(TAG, "tryCaptureView " + String.valueOf(child == leftContent));
            return child == mainContent || child == leftContent;
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
            if (mainLeft + dx < 0) {
                return 0;
            } else if (mainLeft + dx > mDragRange) {
                return mDragRange;
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
                mainLeft += dx;
            }

            if (mainLeft < 0) {
                mainLeft = 0;
            } else if (mainLeft > mDragRange) {
                mainLeft = mDragRange;
            }
            Log.d(TAG, "position changed mainLeft = " + mainLeft);
            if (changedView == leftContent) {
                layoutContent();
            }
            dispatchDragEvent(mainLeft);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            Log.d(TAG, "xvel = " + xvel);
            if (xvel > 0) {
                open();
            } else if (xvel == 0 && mainLeft > mDragRange * 0.3f) {
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
            Log.d(TAG, "distanceX = " + distanceX + "  distanceY = " + distanceY);
            if ((Math.abs(distanceX) > Math.abs(distanceY)) && distanceX > 0 && canDrag != false && mStatus == Status.Close) {
                return true;
            } else if ((Math.abs(distanceX) > Math.abs(distanceY)) && distanceX > 0 && canDrag != false && mStatus == Status.Open) {
                close();
                return true;
            } else {
                return false;
            }

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTapUp");
            if (mStatus == Status.Open && dragHelper.isViewUnder(mainContent, (int) e.getX(), (int) e.getY())) {
                close();
                return true;
            }
            return super.onSingleTapUp(e);
        }
    };

    private void layoutContent() {
        Log.d(TAG, "mainLeft = " + mainLeft);
        Log.d(TAG, "mWidth = " + mWidth);
        Log.d(TAG, "menuWidth = " + menuWidth);
        mainContent.layout(mainLeft, 0, mainLeft + mWidth, mHeight);
        leftContent.layout(mainLeft - mWidth, 0, mainLeft - mWidth + menuWidth, mHeight);
    }

    private void dispatchDragEvent(int mainLeft) {
        float percent = mainLeft / (float) mDragRange;
        leftContent.setTranslationX(mainLeft);
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
        mainLeft = mDragRange;
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
        } else if (mainLeft == mDragRange) {
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
