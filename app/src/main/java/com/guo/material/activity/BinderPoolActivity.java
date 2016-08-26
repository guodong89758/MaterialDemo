package com.guo.material.activity;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;

import com.guo.material.R;
import com.guo.material.aidl.BinderPool;
import com.guo.material.aidl.ComputeImpl;
import com.guo.material.aidl.ICompute;
import com.guo.material.aidl.ISecurityCenter;
import com.guo.material.aidl.SecurityCenterImpl;
import com.guo.material.log.DLOG;

public class BinderPoolActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder_pool);
        new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
            }
        }).start();

    }

    private void doWork() {
        BinderPool binderPool = BinderPool.getInstance(BinderPoolActivity.this);
        IBinder securityBinder = binderPool.queryBinder(BinderPool.BINDER_SECURITY_CENTER);
        ISecurityCenter securityCenter = SecurityCenterImpl.asInterface(securityBinder);
        String message = "hellow-安卓";
        try {
            String password = securityCenter.encrypt(message);
            DLOG.d("encrypt : " + password);
            DLOG.d("decrypt : " + securityCenter.encrypt(password));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        IBinder computeBinder = binderPool.queryBinder(BinderPool.BINDER_COMPUTE);
        ICompute compute = ComputeImpl.asInterface(computeBinder);
        try {
            DLOG.d("comput : " + compute.add(3, 2));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
