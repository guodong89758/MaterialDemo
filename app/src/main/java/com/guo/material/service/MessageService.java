package com.guo.material.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.guo.material.contant.AppKey;
import com.guo.material.log.DLOG;

public class MessageService extends Service {
    private static final String TAG = "MessageService";

    private static class MessageHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppKey.MESSAGE_FROM_CLIENT:
                    DLOG.d("recive from client message : " + msg.getData().getString("msg"));
                    Messenger replyMessenger = msg.replyTo;
                    Message message = Message.obtain(null, AppKey.MESSAGE_FROM_SERVER);
                    Bundle data = new Bundle();
                    data.putString("reply", "已收到消息，稍后回复你");
                    message.setData(data);
                    try {
                        replyMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    }

    private final Messenger messenger = new Messenger(new MessageHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
