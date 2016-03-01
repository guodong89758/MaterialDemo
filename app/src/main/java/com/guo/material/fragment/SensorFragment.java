package com.guo.material.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.guo.material.R;

/**
 * Created by admin on 2016/2/24.
 */
public class SensorFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SensorView sensorView = new SensorView(getActivity());
        return sensorView;
    }

    public class SensorView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener, Runnable {

        private static final int TIME_IN_FRAME = 20;
        private Paint textPaint, bitmapPaint;
        private SurfaceHolder surfaceHolder;
        private SensorManager sensorManager;
        private Sensor sensor;
        private Canvas mCanvas;
        private int screenWidth = 0;
        private int screenHeight = 0;
        private int screenBallWidth = 0;
        private int screenBallHeight = 0;
        private Bitmap ballBitmap;
        private int mPosX = 0;
        private int mPosY = 50;
        private float mGx = 0;
        private float mGy = 0;
        private float mGz = 0;
        private boolean isRunning = false;

        public SensorView(Context context) {
            super(context);
            init(context);
        }

        private void init(Context context) {
            setFocusable(true);
            setFocusableInTouchMode(true);
            surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
            sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
            ballBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.van);
            mCanvas = new Canvas();
            bitmapPaint = new Paint();
            bitmapPaint.setColor(Color.WHITE);
            textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(30);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            isRunning = true;
            new Thread(this).start();
            screenWidth = this.getWidth();
            screenHeight = this.getHeight();
            screenBallWidth = screenWidth - ballBitmap.getWidth();
            screenBallHeight = screenHeight - ballBitmap.getHeight();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            isRunning = false;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            mGx = event.values[0];
            mGy = event.values[1];
            mGz = event.values[2];
            mPosX -= mGx;
            mPosY += mGy;
            if (mPosX < 0) {
                mPosX = 0;
            } else if (mPosX > screenBallWidth) {
                mPosX = screenBallWidth;
            }
            if (mPosY < 0) {
                mPosY = 0;
            } else if (mPosY > screenBallHeight) {
                mPosY = screenBallHeight;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        @Override
        public void run() {
            while (isRunning) {
                long startTime = System.currentTimeMillis();
                synchronized (surfaceHolder) {
                    mCanvas = surfaceHolder.lockCanvas(null);
                    draw();
                    surfaceHolder.unlockCanvasAndPost(mCanvas);
                }
                long endTime = System.currentTimeMillis();
                int diffTime = (int) (endTime - startTime);
                while (diffTime <= TIME_IN_FRAME) {
                    diffTime = (int) (System.currentTimeMillis() - startTime);
                    Thread.yield();
                }
            }

        }

        private void draw() {
            if (mCanvas == null) {
                return;
            }
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mCanvas.drawBitmap(ballBitmap, mPosX, mPosY, bitmapPaint);
//            mCanvas.drawText("X轴重力值：" + mGx, 0, 20, textPaint);
//            mCanvas.drawText("Y轴重力值：" + mGy, 0, 40, textPaint);
//            mCanvas.drawText("Z轴重力值：" + mGz, 0, 60, textPaint);
        }
    }
}
