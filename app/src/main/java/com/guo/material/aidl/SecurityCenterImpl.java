package com.guo.material.aidl;

import android.os.RemoteException;

/**
 * Created by guodong on 2016/8/24 18:16.
 */
public class SecurityCenterImpl extends ISecurityCenter.Stub {

    private static final char SECRET_CODE = '^';

    @Override
    public String encrypt(String password) throws RemoteException {
        char[] charArray = password.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            charArray[i] ^= SECRET_CODE;
        }
        return new String(charArray);
    }

    @Override
    public String decrypt(String content) throws RemoteException {
        return encrypt(content);
    }
}
