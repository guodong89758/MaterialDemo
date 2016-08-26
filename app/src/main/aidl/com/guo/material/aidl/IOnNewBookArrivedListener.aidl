// IOnNewBookArrivedListener.aidl
package com.guo.material.aidl;
import com.guo.material.aidl.Book;


interface IOnNewBookArrivedListener {

    void onNewBookArrived(in Book book);

}
