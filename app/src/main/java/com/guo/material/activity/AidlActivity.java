package com.guo.material.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;

import com.guo.material.R;
import com.guo.material.aidl.Book;
import com.guo.material.aidl.IBookManager;
import com.guo.material.aidl.IOnNewBookArrivedListener;
import com.guo.material.log.DLOG;
import com.guo.material.service.BookManagerService;

public class AidlActivity extends AppCompatActivity {

    private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;
    private IBookManager remoteManager;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IBookManager bookManager = IBookManager.Stub.asInterface(service);
            try {
                remoteManager = bookManager;
                DLOG.d("query book list " + bookManager.getBookList().toString());
                bookManager.addBook(new Book(3, "Android 开发艺术探索"));
                DLOG.d("query book list " + bookManager.getBookList().toString());
                bookManager.registerLister(arrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (remoteManager != null && remoteManager.asBinder().isBinderAlive()) {
            try {
                remoteManager.unRegisterLister(arrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(connection);
        super.onDestroy();
    }

    private IOnNewBookArrivedListener arrivedListener = new IOnNewBookArrivedListener.Stub() {

        @Override
        public void onNewBookArrived(Book book) throws RemoteException {
            handler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, book).sendToTarget();
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NEW_BOOK_ARRIVED:
                    DLOG.d("new book arrived " + ((Book) msg.obj).getName());
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

}
