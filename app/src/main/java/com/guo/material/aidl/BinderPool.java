package com.guo.material.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.guo.material.service.BindPoolService;

import java.util.concurrent.CountDownLatch;

/**
 * Created by guodong on 2016/8/24 17:49.
 */
public class BinderPool {

    public static final int BINDER_NONE = -1;
    public static final int BINDER_SECURITY_CENTER = 0;
    public static final int BINDER_COMPUTE = 1;

    private Context mContext;
    private IBinderPool mBinderPool;
    private static volatile BinderPool instance;
    private CountDownLatch mLatch;

    private BinderPool(Context context) {
        mContext = context.getApplicationContext();
        connectBinderPoolService();
    }

    public static BinderPool getInstance(Context context) {
        if (instance == null) {
            synchronized (BinderPool.class) {
                if (instance == null) {
                    instance = new BinderPool(context);
                }
            }
        }
        return instance;
    }

    private void connectBinderPoolService() {
        mLatch = new CountDownLatch(1);
        Intent intent = new Intent(mContext, BindPoolService.class);
        mContext.bindService(intent, serviceConnection, mContext.BIND_AUTO_CREATE);
        try {
            mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public IBinder queryBinder(int binderCode) {
        IBinder iBinder = null;
        try {
            if (mBinderPool != null) {
                iBinder = mBinderPool.queryBinder(binderCode);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return iBinder;
    }


    public static class IBinderPoolImpl extends IBinderPool.Stub {

        public IBinderPoolImpl() {
            super();
        }

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder iBinder = null;
            switch (binderCode) {
                case BINDER_SECURITY_CENTER:
                    iBinder = new SecurityCenterImpl();
                    break;
                case BINDER_COMPUTE:
                    iBinder = new ComputeImpl();
                    break;
            }
            return iBinder;
        }
    }

    private IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            mBinderPool.asBinder().unlinkToDeath(deathRecipient, 0);
            mBinderPool = null;
            connectBinderPoolService();
        }
    };

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderPool = IBinderPool.Stub.asInterface(service);
            if (mBinderPool != null) {
                try {
                    mBinderPool.asBinder().linkToDeath(deathRecipient, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mLatch.countDown();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
