package com.guo.material.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.guo.material.log.DLOG;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketService extends Service {

    private boolean isServiceDestory = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new TcpServer()).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceDestory = true;
    }

    private class TcpServer implements Runnable {

        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(8688);
            } catch (IOException e) {
                DLOG.e("serversocket connected failed");
                e.printStackTrace();
            }
            while (!isServiceDestory) {
                try {
                    final Socket client = serverSocket.accept();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                responseClient(client);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void responseClient(Socket client) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));// 接受客户端消息
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);// 回应客户端消息
        DLOG.d("server connected success");
        writer.println("欢迎来到聊天室");
        while (!isServiceDestory) {
            String message = reader.readLine();
            if (TextUtils.isEmpty(message)) {
                DLOG.d("message empty");
                break;
            }
            DLOG.d("message from client : " + message);
            writer.println("为什么" + message + "?");
        }
        writer.close();
        reader.close();
        client.close();
    }
}
