// ISecurityCenter.aidl
package com.guo.material.aidl;


interface ISecurityCenter {

    String encrypt(String password);

    String decrypt(String content);
}
