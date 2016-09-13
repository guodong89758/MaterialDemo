package com.guo.material.record;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by guodong on 2016/6/29 17:46.
 */
public class ScreenRecorder extends Thread {
    private static final String TAG = "ScreenRecorder";

    private int mWidth;
    private int mHeight;
    private int mBitRate;
    private int mDpi;
    private String mDstPath;
    private MediaProjection mMediaProjection;
    // parameters for the encoder
    private static final String MIME_TYPE = "video/avc"; // H.264 Advanced Video Coding
    private static final int FRAME_RATE = 30; // 30 fps
    public static final int VGOP = 48;
    private static final int IFRAME_INTERVAL = 10; // 10 seconds between I-frames
    private static final int TIMEOUT_US = 10000;
    private byte[] mYuvFrameBuffer = new byte[SrsEncoder.VPREV_WIDTH * SrsEncoder.VPREV_HEIGHT * 3 / 2];
    private MediaCodecInfo vmci;
    private MediaCodec mEncoder;
    private Surface mSurface;
    private MediaMuxer mMuxer;
    private boolean mMuxerStarted = false;
    private int mVideoTrackIndex = -1;
    private AtomicBoolean mQuit = new AtomicBoolean(false);
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
    private VirtualDisplay mVirtualDisplay;
    private SrsEncoder srsEncoder;
    int vcolor;

    public ScreenRecorder(int width, int height, int bitrate, int dpi, MediaProjection mp, String dstPath, SrsEncoder srsEncoder) {
        super(TAG);
        mWidth = width;
        mHeight = height;
        mBitRate = bitrate;
        mDpi = dpi;
        mMediaProjection = mp;
        mDstPath = dstPath;
        this.srsEncoder = srsEncoder;
    }

    public ScreenRecorder(MediaProjection mp, SrsEncoder srsEncoder) {
        // 480p 2Mbps
        this(640, 480, 2000000, 1, mp, "/sdcard/test.mp4", srsEncoder);
    }

    /**
     * stop task
     */
    public final void quit() {
        mQuit.set(true);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void run() {
        try {
            try {
                prepareEncoder();
//                mMuxer = new MediaMuxer(mDstPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            mVirtualDisplay = mMediaProjection.createVirtualDisplay(TAG + "-display",
                    mWidth, mHeight, mDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                    mSurface, null, null);
            Log.d(TAG, "created virtual display: " + mVirtualDisplay);
            recordVirtualDisplay();

        } finally {
            release();
        }
    }


    private void recordVirtualDisplay() {

        while (!mQuit.get()) {
            int index = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_US);
            Log.i(TAG, "dequeue output buffer index=" + index);
            if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                resetOutputFormat();

            } else if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                Log.d(TAG, "retrieving buffers time out!");
                try {
                    // wait 10ms
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            } else if (index >= 0) {

//                if (!mMuxerStarted) {
//                    throw new IllegalStateException("MediaMuxer dose not call addTrack(format) ");
//                }
                encodeToVideoTrack(index);
                mEncoder.releaseOutputBuffer(index, false);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void encodeToVideoTrack(int index) {
        ByteBuffer encodedData = mEncoder.getOutputBuffer(index);
//
        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            // The codec config data was pulled out and fed to the muxer when we got
            // the INFO_OUTPUT_FORMAT_CHANGED status.
            // Ignore it.
            Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
            mBufferInfo.size = 0;
        }
        if (mBufferInfo.size == 0) {
            Log.d(TAG, "info.size == 0, drop it.");
            encodedData = null;
        } else {
            Log.d(TAG, "got buffer, info: size=" + mBufferInfo.size
                    + ", presentationTimeUs=" + mBufferInfo.presentationTimeUs
                    + ", offset=" + mBufferInfo.offset);
        }
        if (encodedData != null) {
            encodedData.position(mBufferInfo.offset);
            encodedData.limit(mBufferInfo.offset + mBufferInfo.size);
//            mMuxer.writeSampleData(mVideoTrackIndex, encodedData, mBufferInfo);
            Log.i(TAG, "sent " + mBufferInfo.size + " bytes to muxer...");
//            encodedData.flip();
            byte[] bytes = new byte[encodedData.remaining()];
            encodedData.get(bytes);
//            Log.e(TAG, "byte==" + Arrays.toString(bytes));
            onGetYuvFrame(bytes);

        }
//        Image image = mEncoder.getOutputImage(index);
//        if(image != null){
//            onGetYuvFrame(getDataFromImage(image, 1));
//            image.close();
//        }

    }

    private void resetOutputFormat() {
        // should happen before receiving buffers, and should only happen once
        if (mMuxerStarted) {
            throw new IllegalStateException("output format already changed!");
        }
        MediaFormat newFormat = mEncoder.getOutputFormat();
        newFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        newFormat.getByteBuffer("csd-0");    // SPS
        newFormat.getByteBuffer("csd-1");    // PPS

        Log.i(TAG, "output format changed.\n new format: " + newFormat.toString());
//        mVideoTrackIndex = mMuxer.addTrack(newFormat);
//        mMuxer.start();
//        mMuxerStarted = true;
        Log.i(TAG, "started media muxer, videoIndex=" + mVideoTrackIndex);
    }

    private void prepareEncoder() throws IOException {
        vcolor = chooseVideoEncoder();
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);

        Log.d(TAG, "created video format: " + format);
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        mSurface = mEncoder.createInputSurface();
        Log.d(TAG, "created input surface: " + mSurface);
        mEncoder.start();
    }


    private void release() {
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
        }
//        if (mMuxer != null) {
//            mMuxer.stop();
//            mMuxer.release();
//            mMuxer = null;
//        }
    }



    private void onGetYuvFrame(byte[] data) {
//        byte[] frame = new byte[data.length];
//        if (vcolor == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar) {
//            YV12toYUV420Planar(data, frame, mWidth, mHeight);
//        } else if (vcolor == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar) {
//            YV12toYUV420PackedSemiPlanar(data, frame, mWidth, mHeight);
//        } else if (vcolor == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar) {
//            YV12toYUV420PackedSemiPlanar(data, frame, mWidth, mHeight);
//        } else {
//            System.arraycopy(data, 0, frame, 0, data.length);
//        }
//        srsEncoder.onGetYuvFrame(frame);
        srsEncoder.onGetYuvFrame(data);
    }

    // the color transform, @see http://stackoverflow.com/questions/15739684/mediacodec-and-camera-color-space-incorrect
    private static byte[] YV12toYUV420PackedSemiPlanar(final byte[] input, final byte[] output, final int width, final int height) {
        /*
         * COLOR_TI_FormatYUV420PackedSemiPlanar is NV12
         * We convert by putting the corresponding U and V bytes together (interleaved).
         */
        final int frameSize = width * height;
        final int qFrameSize = frameSize / 4;

        System.arraycopy(input, 0, output, 0, frameSize); // Y

        for (int i = 0; i < qFrameSize; i++) {
            output[frameSize + i * 2] = input[frameSize + i + qFrameSize]; // Cb (U)
            output[frameSize + i * 2 + 1] = input[frameSize + i]; // Cr (V)
        }
        return output;
    }

    private static byte[] YV12toYUV420Planar(byte[] input, byte[] output, int width, int height) {
        /*
         * COLOR_FormatYUV420Planar is I420 which is like YV12, but with U and V reversed.
         * So we just have to reverse U and V.
         */
        final int frameSize = width * height;
        final int qFrameSize = frameSize / 4;

        System.arraycopy(input, 0, output, 0, frameSize); // Y
        System.arraycopy(input, frameSize, output, frameSize + qFrameSize, qFrameSize); // Cr (V)
        System.arraycopy(input, frameSize + qFrameSize, output, frameSize, qFrameSize); // Cb (U)

        return output;
    }

    public static byte[] swapYV12toI420(byte[] yv12bytes, int width, int height) {
        byte[] i420bytes = new byte[yv12bytes.length];
        for (int i = 0; i < width * height; i++)
            i420bytes[i] = yv12bytes[i];
        for (int i = width * height; i < width * height + (width / 2 * height / 2); i++)
            i420bytes[i] = yv12bytes[i + (width / 2 * height / 2)];
        for (int i = width * height + (width / 2 * height / 2); i < width * height + 2 * (width / 2 * height / 2); i++)
            i420bytes[i] = yv12bytes[i - (width / 2 * height / 2)];
        return i420bytes;
    }

    // choose the video encoder by name.
    private MediaCodecInfo chooseVideoEncoder(String name, MediaCodecInfo def) {
        int nbCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < nbCodecs; i++) {
            MediaCodecInfo mci = MediaCodecList.getCodecInfoAt(i);
            if (!mci.isEncoder()) {
                continue;
            }

            String[] types = mci.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(MIME_TYPE)) {
                    //Log.i(TAG, String.format("vencoder %s types: %s", mci.getName(), types[j]));
                    if (name == null) {
                        return mci;
                    }

                    if (mci.getName().contains(name)) {
                        return mci;
                    }
                }
            }
        }

        return def;
    }

    // choose the right supported color format. @see below:
    // https://developer.android.com/reference/android/media/MediaCodecInfo.html
    // https://developer.android.com/reference/android/media/MediaCodecInfo.CodecCapabilities.html
    private int chooseVideoEncoder() {
        // choose the encoder "video/avc":
        //      1. select one when type matched.
        //      2. perfer google avc.
        //      3. perfer qcom avc.
        vmci = chooseVideoEncoder(null, null);
        //vmci = chooseVideoEncoder("google", vmci);
        //vmci = chooseVideoEncoder("qcom", vmci);

        int matchedColorFormat = 0;
        MediaCodecInfo.CodecCapabilities cc = vmci.getCapabilitiesForType(MIME_TYPE);
        for (int i = 0; i < cc.colorFormats.length; i++) {
            int cf = cc.colorFormats[i];
            Log.i(TAG, String.format("vencoder %s supports color fomart 0x%x(%d)", vmci.getName(), cf, cf));

            // choose YUV for h.264, prefer the bigger one.
            // corresponding to the color space transform in onPreviewFrame
            if ((cf >= cc.COLOR_FormatYUV420Planar && cf <= cc.COLOR_FormatYUV420SemiPlanar)) {
                if (cf > matchedColorFormat) {
                    matchedColorFormat = cf;
                }
            }
        }

        for (int i = 0; i < cc.profileLevels.length; i++) {
            MediaCodecInfo.CodecProfileLevel pl = cc.profileLevels[i];
            Log.i(TAG, String.format("vencoder %s support profile %d, level %d", vmci.getName(), pl.profile, pl.level));
        }

        Log.i(TAG, String.format("vencoder %s choose color format 0x%x(%d)", vmci.getName(), matchedColorFormat, matchedColorFormat));
        return matchedColorFormat;
    }

    private static final int COLOR_FormatI420 = 1;
    private static final int COLOR_FormatNV21 = 2;

    private static boolean isImageFormatSupported(Image image) {
        int format = image.getFormat();
        switch (format) {
            case ImageFormat.YUV_420_888:
            case ImageFormat.NV21:
            case ImageFormat.YV12:
                return true;
        }
        return false;
    }

    private static byte[] getDataFromImage(Image image, int colorFormat) {
        if (colorFormat != COLOR_FormatI420 && colorFormat != COLOR_FormatNV21) {
            throw new IllegalArgumentException("only support COLOR_FormatI420 " + "and COLOR_FormatNV21");
        }
        if (!isImageFormatSupported(image)) {
            throw new RuntimeException("can't convert Image to byte array, format " + image.getFormat());
        }
        Rect crop = image.getCropRect();
        int format = image.getFormat();
        int width = crop.width();
        int height = crop.height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];
        Log.v(TAG, "get data from " + planes.length + " planes");
        int channelOffset = 0;
        int outputStride = 1;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0;
                    outputStride = 1;
                    break;
                case 1:
                    if (colorFormat == COLOR_FormatI420) {
                        channelOffset = width * height;
                        outputStride = 1;
                    } else if (colorFormat == COLOR_FormatNV21) {
                        channelOffset = width * height + 1;
                        outputStride = 2;
                    }
                    break;
                case 2:
                    if (colorFormat == COLOR_FormatI420) {
                        channelOffset = (int) (width * height * 1.25);
                        outputStride = 1;
                    } else if (colorFormat == COLOR_FormatNV21) {
                        channelOffset = width * height;
                        outputStride = 2;
                    }
                    break;
            }
            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();
            Log.v(TAG, "pixelStride " + pixelStride);
            Log.v(TAG, "rowStride " + rowStride);
            Log.v(TAG, "width " + width);
            Log.v(TAG, "height " + height);
            Log.v(TAG, "buffer size " + buffer.remaining());
            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            for (int row = 0; row < h; row++) {
                int length;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
            Log.v(TAG, "Finished reading data from plane " + i);
        }
        return data;
    }

}
