package com.guo.material.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.guo.material.R;
import com.guo.material.log.DLOG;
import com.guo.material.service.SocketService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SocketActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MESSAGE_SOCKET_CONNECTED = 1;
    private static final int MESSAGE_SOCKET_RECEIVE_MESSAGE = 2;
    private EditText et_input;
    private Button btn_send;
    private TextView tv_content;
    private Socket clientSocket;
    private PrintWriter printWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        et_input = (EditText) findViewById(R.id.et_input);
        btn_send = (Button) findViewById(R.id.btn_send);
        tv_content = (TextView) findViewById(R.id.tv_content);

        btn_send.setOnClickListener(this);
        btn_send.setEnabled(false);
        Intent intent = new Intent(this, SocketService.class);
        startService(intent);
        connectSocket();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (clientSocket != null) {
            try {
                clientSocket.shutdownInput();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                String message = et_input.getText().toString().trim();
                if (printWriter != null && !TextUtils.isEmpty(message)) {
                    printWriter.println(message);
                    et_input.setText("");
                    tv_content.setText(tv_content.getText().toString() + "self " + formatTime() + " : " + message + "\n");
                }

                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SOCKET_CONNECTED:
                    btn_send.setEnabled(true);
                    break;
                case MESSAGE_SOCKET_RECEIVE_MESSAGE:
                    tv_content.setText(tv_content.getText().toString() + msg.obj.toString());
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private void connectSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket client = null;
                while (client == null) {
                    try {
                        client = new Socket(InetAddress.getLocalHost(), 8688);
                        clientSocket = client;
                        printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                        mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
                    } catch (IOException e) {
                        e.printStackTrace();
                        SystemClock.sleep(1000);
                        DLOG.e("socket connected failed, retry......");
                    }
                }
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    while (!SocketActivity.this.isFinishing()) {
                        String message = reader.readLine();
                        if (!TextUtils.isEmpty(message)) {
                            DLOG.d("from server message : " + message);
                            mHandler.obtainMessage(MESSAGE_SOCKET_RECEIVE_MESSAGE, "server " + formatTime() + " : " + message + "\n").sendToTarget();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String formatTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }
}
