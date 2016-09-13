package com.guo.material.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.guo.material.R;
import com.guo.material.RT;
import com.guo.material.log.DLOG;

public class ToastUtil {

    private static Toast mToast;

    public static void showToast(String obj) {
        if (obj != null) {
            if (RT.application != null) {
                handler.obtainMessage(0, obj).sendToTarget();
            }
        }

    }

    public static void showBottomToast(String obj) {
        if (obj != null) {
            if (RT.application != null) {
                handler.obtainMessage(0, 1, 0, obj).sendToTarget();
            }
        }

    }

    public static void showToastWithIcon(String obj, int icon) {
        if (obj != null) {
            if (RT.application != null) {
                Bundle bundle = new Bundle();
                bundle.putString("text", obj);
                bundle.putInt("icon", icon);
                handler.obtainMessage(1, bundle).sendToTarget();
            }
        }
    }

    static Handler handler = new Handler(RT.application.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:

                    if (msg.arg1 == 0) {
                        if (mToast == null) {
                            mToast = new Toast(RT.application);
                            mToast.setDuration(Toast.LENGTH_SHORT);
                            mToast.setGravity(Gravity.CENTER, 0, 0);
                            View view = LayoutInflater.from(RT.application).inflate(R.layout.toast_layout, null);
                            mToast.setView(view);
                        }
                        if (msg.obj != null) {
                            DLOG.d("toast", "msg:" + msg.obj.toString());
                            View views = mToast.getView();
                            TextView tv = (TextView) views.findViewById(R.id.toast);
                            tv.setText(msg.obj.toString());
                            ImageView iv = (ImageView) views.findViewById(R.id.icon_tip);
                            iv.setVisibility(View.GONE);
                            // mToast.setText(msg.obj.toString());
                            if (!TextUtils.isEmpty(msg.obj.toString())) {
                                mToast.show();
                            }
                        }
                    } else {
                        Toast.makeText(RT.application, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    if (mToast == null) {
                        mToast = new Toast(RT.application);
                        mToast.setDuration(Toast.LENGTH_SHORT);
                        mToast.setGravity(Gravity.CENTER, 0, 0);
                        View view = LayoutInflater.from(RT.application).inflate(R.layout.toast_layout, null);
                        mToast.setView(view);
                    }
                    if (msg.obj != null) {
                        // DLOG.d("toast", "msg:" + msg.obj.toString());
                        Bundle bundle = (Bundle) msg.obj;
                        String text = bundle.getString("text");
                        int icon = bundle.getInt("icon");
                        View view = mToast.getView();
                        TextView tv = (TextView) view.findViewById(R.id.toast);
                        tv.setText(text);
                        ImageView iv = (ImageView) view.findViewById(R.id.icon_tip);
                        iv.setImageResource(icon);
                        iv.setVisibility(View.VISIBLE);
//                     mToast.setText(msg.obj.toString());
                        mToast.show();
                    }
                    break;
            }

        }

        ;
    };

}
