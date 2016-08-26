package com.guo.material.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.guo.material.aidl.Book;
import com.guo.material.aidl.IBookManager;
import com.guo.material.aidl.IOnNewBookArrivedListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookManagerService extends Service {

    private AtomicBoolean isServiceDestory = new AtomicBoolean(false);

    private CopyOnWriteArrayList<Book> bookList = new CopyOnWriteArrayList<>();

    private RemoteCallbackList<IOnNewBookArrivedListener> listeners = new RemoteCallbackList<>();

    private Binder mBider = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return bookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            bookList.add(book);
        }

        @Override
        public void registerLister(IOnNewBookArrivedListener listener) throws RemoteException {
            listeners.register(listener);
        }

        @Override
        public void unRegisterLister(IOnNewBookArrivedListener listener) throws RemoteException {
            listeners.unregister(listener);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBider;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bookList.add(new Book(1, "android"));
        bookList.add(new Book(2, "ios"));

        new Thread(new ServiceWorker()).start();
    }

    private void onNewBookArrvied(Book book) throws RemoteException {
        bookList.add(book);
        int count = listeners.beginBroadcast();
        for (int i = 0; i < count; i++) {
            IOnNewBookArrivedListener listener = listeners.getBroadcastItem(i);
            if (listener != null) {
                listener.onNewBookArrived(book);
            }
        }
        listeners.finishBroadcast();
    }

    private class ServiceWorker implements Runnable {

        @Override
        public void run() {
            while (!isServiceDestory.get()) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = bookList.size();
                Book book = new Book(bookId + 1, "book" + bookId);
                try {
                    onNewBookArrvied(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
