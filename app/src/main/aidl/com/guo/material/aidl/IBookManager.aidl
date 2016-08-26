// IBookManager.aidl
package com.guo.material.aidl;

import com.guo.material.aidl.Book;
import com.guo.material.aidl.IOnNewBookArrivedListener;

interface IBookManager {

    List<Book> getBookList();

    void addBook(in Book book);

    void registerLister(IOnNewBookArrivedListener listener);

    void unRegisterLister(IOnNewBookArrivedListener listener);

}
