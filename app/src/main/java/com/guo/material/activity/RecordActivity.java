package com.guo.material.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.guo.material.R;
import com.guo.material.log.DLOG;
import com.guo.material.record.ScreenRecorder;
import com.guo.material.record.SrsEncoder;
import com.guo.material.record.SrsFlvMuxer;
import com.guo.material.record.SrsMp4Muxer;
import com.guo.material.record.rtmp.RtmpPublisher;
import com.guo.material.utils.ToastUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RecordLiveActivity";
    private static final int RESULT_RECORD = 1;
    public TextView tv_rtmp_url, time_tv;
    public Button btn_live_portrait, btn_live_rec_landscape, btn_live_stop;
    private MediaProjectionManager mpManager;
    private ScreenRecorder mRecorder;
    private AudioRecord mic = null;
    private boolean aloop = false;
    private Thread aworker = null;
    //    private String rtmpUrl = "rtmp://ossrs.net/" + getRandomAlphaString(3) + '/' + getRandomAlphaDigitString(5);
    private String rtmpUrl = "rtmp://live.hkstv.hk.lxdns.com/live/123";
    //    private String rtmpUrl = "rtmp://182.92.80.26:1935/live/livestream";
    private String recPath = Environment.getExternalStorageDirectory().getPath() + "/test.mp4";
    private String mNotifyMsg;
    private SrsFlvMuxer flvMuxer = new SrsFlvMuxer(new RtmpPublisher.EventHandler() {
        @Override
        public void onRtmpConnecting(String msg) {
            mNotifyMsg = msg;
            ToastUtil.showToast(mNotifyMsg);
        }

        @Override
        public void onRtmpConnected(String msg) {
            mNotifyMsg = msg;
            ToastUtil.showToast(mNotifyMsg);
        }

        @Override
        public void onRtmpVideoStreaming(String msg) {
        }

        @Override
        public void onRtmpAudioStreaming(String msg) {
        }

        @Override
        public void onRtmpStopped(String msg) {
            mNotifyMsg = msg;
            ToastUtil.showToast(mNotifyMsg);
        }

        @Override
        public void onRtmpDisconnected(String msg) {
            mNotifyMsg = msg;
            ToastUtil.showToast(mNotifyMsg);
        }

        @Override
        public void onRtmpOutputFps(final double fps) {
            Log.i(TAG, String.format("Output Fps: %f", fps));
        }
    });

    private SrsMp4Muxer mp4Muxer = new SrsMp4Muxer(new SrsMp4Muxer.EventHandler() {
        @Override
        public void onRecordPause(String msg) {
            mNotifyMsg = msg;
            ToastUtil.showToast(mNotifyMsg);
        }

        @Override
        public void onRecordResume(String msg) {
            mNotifyMsg = msg;
            ToastUtil.showToast(mNotifyMsg);
        }

        @Override
        public void onRecordStarted(String msg) {
            mNotifyMsg = "Recording file: " + msg;
            ToastUtil.showToast(mNotifyMsg);
        }

        @Override
        public void onRecordFinished(String msg) {
            mNotifyMsg = "MP4 file saved: " + msg;
            ToastUtil.showToast(mNotifyMsg);
        }
    });
    private SrsEncoder mEncoder = new SrsEncoder(flvMuxer, mp4Muxer);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        tv_rtmp_url = (TextView) findViewById(R.id.tv_rtmp_url);
        btn_live_portrait = (Button) findViewById(R.id.btn_live_portrait);
        btn_live_rec_landscape = (Button) findViewById(R.id.btn_live_rec_landscape);
        btn_live_stop = (Button) findViewById(R.id.btn_live_stop);

        btn_live_portrait.setOnClickListener(this);
        btn_live_rec_landscape.setOnClickListener(this);
        btn_live_stop.setOnClickListener(this);

        mpManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        DLOG.d("rtmp url = " + rtmpUrl);
        tv_rtmp_url.setText(rtmpUrl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_live_portrait:
                Dexter.checkPermission(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        portLive();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {

                    }

                }, Manifest.permission.RECORD_AUDIO);

                break;
            case R.id.btn_live_rec_landscape:
                Dexter.checkPermission(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        landLive();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {

                    }

                }, Manifest.permission.RECORD_AUDIO);
                break;
            case R.id.btn_live_stop:
                stopEncoder();
                flvMuxer.stop();
                mp4Muxer.stop();
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void portLive() {
        try {
            flvMuxer.start(rtmpUrl);
        } catch (IOException e) {
            Log.e(TAG, "start FLV muxer failed.");
            e.printStackTrace();
            return;
        }
        flvMuxer.setVideoResolution(mEncoder.VCROP_WIDTH, mEncoder.VCROP_HEIGHT);
//                    try {
//                        mp4Muxer.record(new File(recPath));
//                    } catch (IOException e) {
//                        Log.e(TAG, "start MP4 muxer failed.");
//                        e.printStackTrace();
//                    }
        Intent captureIntent = mpManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, RESULT_RECORD);
        startEncoder();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void landLive() {
        try {
            flvMuxer.start(rtmpUrl);
        } catch (IOException e) {
            Log.e(TAG, "start FLV muxer failed.");
            e.printStackTrace();
            return;
        }
        flvMuxer.setVideoResolution(mEncoder.VCROP_WIDTH, mEncoder.VCROP_HEIGHT);
//                    try {
//                        mp4Muxer.record(new File(recPath));
//                    } catch (IOException e) {
//                        Log.e(TAG, "start MP4 muxer failed.");
//                        e.printStackTrace();
//                    }
        Intent captureIntent = mpManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, RESULT_RECORD);
        startEncoder();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_RECORD) {
            MediaProjection mediaProjection = mpManager.getMediaProjection(resultCode, data);
            if (mediaProjection == null) {
                return;
            }
            // video size
//            final int width = 1280;
//            final int height = 720;
            final int width = mEncoder.VPREV_WIDTH;
            final int height = mEncoder.VPREV_HEIGHT;
            File file = new File(Environment.getExternalStorageDirectory(),
                    "record-" + width + "x" + height + "-" + System.currentTimeMillis() + ".mp4");
//            final int bitrate = 6000000;
            mRecorder = new ScreenRecorder(width, height, SrsEncoder.ABITRATE, 1, mediaProjection, file.getAbsolutePath(), mEncoder);
            mRecorder.start();
            ToastUtil.showToast("Screen recorder is running...");
            moveTaskToBack(true);

        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        }
        stopEncoder();
        mp4Muxer.stop();
        mEncoder.setScreenOrientation(newConfig.orientation);
        startEncoder();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopEncoder();
        flvMuxer.stop();
        mp4Muxer.stop();
        if (mRecorder != null) {
            mRecorder = null;
        }
    }

    private void startEncoder() {
        int ret = mEncoder.start();
        if (ret < 0) {
            return;
        }
/**
 * 开始录屏
 */
        aworker = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
                startAudio();
            }
        });
        aloop = true;
        aworker.start();
    }

    private void stopEncoder() {
        stopAudio();
        if (mRecorder != null) {
            mRecorder.quit();
        }
        mEncoder.stop();
    }

    private void startAudio() {
        if (mic != null) {
            return;
        }

        int bufferSize = 2 * AudioRecord.getMinBufferSize(SrsEncoder.ASAMPLERATE, SrsEncoder.ACHANNEL, SrsEncoder.AFORMAT);
        mic = new AudioRecord(MediaRecorder.AudioSource.MIC, SrsEncoder.ASAMPLERATE, SrsEncoder.ACHANNEL, SrsEncoder.AFORMAT, bufferSize);
        mic.startRecording();

        byte pcmBuffer[] = new byte[4096];
        while (aloop && !Thread.interrupted()) {
            int size = mic.read(pcmBuffer, 0, pcmBuffer.length);
            if (size <= 0) {
                Log.e(TAG, "***** audio ignored, no data to read.");
                break;
            }
            onGetPcmFrame(pcmBuffer, size);
        }
    }

    private void stopAudio() {
        aloop = false;
        if (aworker != null) {
            Log.i(TAG, "stop audio worker thread");
            aworker.interrupt();
            try {
                aworker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                aworker.interrupt();
            }
            aworker = null;
        }

        if (mic != null) {
            mic.setRecordPositionUpdateListener(null);
            mic.stop();
            mic.release();
            mic = null;
        }
    }

    private void onGetYuvFrame(byte[] data) {
        mEncoder.onGetYuvFrame(data);
    }

    private void onGetPcmFrame(byte[] pcmBuffer, int size) {
        mEncoder.onGetPcmFrame(pcmBuffer, size);
    }

    private static String getRandomAlphaString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    private static String getRandomAlphaDigitString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
