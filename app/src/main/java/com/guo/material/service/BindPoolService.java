package com.guo.material.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.guo.material.aidl.BinderPool;

public class BindPoolService extends Service {

    private Binder mBinderPool = new BinderPool.IBinderPoolImpl();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinderPool;
    }


}
