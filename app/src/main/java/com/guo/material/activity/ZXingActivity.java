package com.guo.material.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.guo.material.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ZXingActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_scanning, btn_make_qrcode, btn_make_barcode;
    private EditText et_input;
    private TextView tv_result;
    private ImageView iv_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zxing);
        btn_scanning = (Button) findViewById(R.id.btn_scanning);
        btn_make_qrcode = (Button) findViewById(R.id.btn_make_qrcode);
        btn_make_barcode = (Button) findViewById(R.id.btn_make_barcode);
        et_input = (EditText) findViewById(R.id.et_input);
        iv_code = (ImageView) findViewById(R.id.iv_code);

        btn_scanning.setOnClickListener(this);
        btn_make_qrcode.setOnClickListener(this);
        btn_make_barcode.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scanning:
                Dexter.checkPermission(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        startActivity(new Intent(ZXingActivity.this, ScanActivity.class));
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }, Manifest.permission.CAMERA);
                break;
            case R.id.btn_make_qrcode:
                final String content = et_input.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    Observable.create(new Observable.OnSubscribe<Bitmap>() {
                        @Override
                        public void call(Subscriber<? super Bitmap> subscriber) {
                            Bitmap bitmap = QRCodeEncoder.syncEncodeQRCode(content, 360);
                            subscriber.onNext(bitmap);
                        }
                    }).subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Bitmap>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(Bitmap bitmap) {
                                    iv_code.setImageBitmap(bitmap);
                                }
                            });
                }
                break;
            case R.id.btn_make_barcode:
                final String content1 = et_input.getText().toString();
                if (!TextUtils.isEmpty(content1)) {
                    Observable.create(new Observable.OnSubscribe<Bitmap>() {
                        @Override
                        public void call(Subscriber<? super Bitmap> subscriber) {
                            Bitmap bitmap = QRCodeEncoder.syncEncodeQRCode(content1, 360, Color.BLACK, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                            subscriber.onNext(bitmap);
                        }
                    }).subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Bitmap>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(Bitmap bitmap) {
                                    iv_code.setImageBitmap(bitmap);
                                }
                            });
                }
                break;
        }
    }
}
