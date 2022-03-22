package com.example.campusassistance.message.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.example.campusassistance.init.fragment.NavigationFragment;
import com.example.campusassistance.message.entity.User;
import com.mysql.cj.util.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class MessageService extends Service {

    public WebSocket messageWebSocket;
    private String sendUserId;

    public Callback callback;
    public ReceiveMsgCallback receiveMsgCallback;

    public MessageService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sendUserId = getSharedPreferences("Login", MODE_PRIVATE).getString("userId", "");
        new InitSocketThread().start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MessageBinder();
    }

    public class MessageBinder extends android.os.Binder {
        public MessageService getService() {
            return MessageService.this;
        }
    }

    class InitSocketThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                initSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void initSocket() {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(300, TimeUnit.SECONDS)//设置读取超时时间
                    .writeTimeout(300, TimeUnit.SECONDS)//设置写的超时时间
                    .connectTimeout(300, TimeUnit.SECONDS)//设置连接超时时间
                    .build();

            Request request = new Request.Builder().url("ws://192.168.31.80:8081/chat/" + sendUserId).build();
            client.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                    super.onClosed(webSocket, code, reason);
                }

                @Override
                public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                    super.onClosing(webSocket, code, reason);
                }

                @Override
                public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                    super.onFailure(webSocket, t, response);
                }

                @Override
                public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                    super.onMessage(webSocket, text);
                    if (!StringUtils.isNullOrEmpty(text)) {
                        User user = JSON.parseObject(text, User.class);
                        if (callback != null) {
                            callback.receiveMsg(user);
                        }
                        if (receiveMsgCallback != null) {
                            receiveMsgCallback.toastReceiveMsg();
                        }
                    }
                }

                @Override
                public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                    super.onOpen(webSocket, response);
                    messageWebSocket = webSocket;
                }
            });
            client.dispatcher().executorService().shutdown();
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    //一对一聊天时回调接口
    public static interface Callback {
        void receiveMsg(User user);
    }

    public void setReceiveMsgCallback(ReceiveMsgCallback receiveMsgCallback) {
        this.receiveMsgCallback = receiveMsgCallback;
    }

    //有新消息通知时回调接口
    public static interface ReceiveMsgCallback {
        void toastReceiveMsg();
    }
}