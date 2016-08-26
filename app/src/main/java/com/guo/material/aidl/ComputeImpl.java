package com.guo.material.aidl;

import android.os.RemoteException;

/**
 * Created by guodong on 2016/8/24 18:17.
 */
public class ComputeImpl extends ICompute.Stub {
    @Override
    public int add(int a, int b) throws RemoteException {
        return a + b;
    }
}
