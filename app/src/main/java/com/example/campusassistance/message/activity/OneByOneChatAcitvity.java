package com.example.campusassistance.message.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.campusassistance.R;
import com.example.campusassistance.message.adapter.MessageChatAdapter;
import com.example.campusassistance.message.adapter.MessageListAdapter;
import com.example.campusassistance.message.entity.User;
import com.mysql.cj.util.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class OneByOneChatAcitvity extends AppCompatActivity implements View.OnClickListener {

    private String receiveUserId;
    private String sendUserId;
    //返回成功状态码
    private String responseSuccessCode = "200";
    private WebSocket mWebSocket;

    private ImageView back;
    private TextView chatName;
    private EditText inputMessage;
    private Button sendMessage;
    private RecyclerView chatMessage;

    private MessageChatAdapter mAdapter;
    private ArrayList<User> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_by_one_chat_acitvity);
        init();
        chatName.setText(receiveUserId);
        getChatMessage();
        connect();
        sendMessage.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    private void init() {
        Intent intent = this.getIntent();
        receiveUserId = intent.getStringExtra("receiveUserId");
        sendUserId = intent.getStringExtra("sendUserId");
        back = findViewById(R.id.iv_back);
        chatName = findViewById(R.id.tv_chat_name);
        inputMessage = findViewById(R.id.et_chat_message);
        sendMessage = findViewById(R.id.bt_send_message);
        chatMessage = findViewById(R.id.rv_chat_message);
        mUsers = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        //返回并刷新messageList列表中的信息
        if (v.getId() == R.id.iv_back) {
            finish();
//            TODO
        } else if (v.getId() == R.id.bt_send_message) {
            String message = inputMessage.getText().toString();
            if (!TextUtils.isEmpty(message)) {
                User user = new User();
                user.setReceiveUserId(receiveUserId);
                user.setSendUserId(sendUserId);
                user.setMessage(message);
                user.setDateTime(new Timestamp(System.currentTimeMillis()));
                user.setBothUserId(receiveUserId + "/" + sendUserId);
                mWebSocket.send(user.toString());
                mUsers.add(user);
                updateMessage();
                inputMessage.setText("");
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                chatMessage.setLayoutManager(new LinearLayoutManager(OneByOneChatAcitvity.this));
                mAdapter = new MessageChatAdapter(mUsers);
                chatMessage.setAdapter(mAdapter);
                chatMessage.scrollToPosition(mUsers.size() - 1);
            }
        }
    };

    //获取之前的聊天记录
    private void getChatMessage() {
        new Thread(() -> {
            String path = "http://192.168.31.80:8080/Message/requestOneByOneChatMessage";
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(300, TimeUnit.SECONDS)//设置读取超时时间
                    .writeTimeout(300, TimeUnit.SECONDS)//设置写的超时时间
                    .connectTimeout(300, TimeUnit.SECONDS)//设置连接超时时间
                    .build();
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("receiveUserId", receiveUserId);
            builder.add("sendUserId", sendUserId);
            FormBody body = builder.build();
            Request request = new Request.Builder().url(path).post(body).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                    Looper.prepare();
//                    Toast.makeText(getActivity(), "服务器连接出现错误", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responText = response.body().string();
                    if (responseSuccessCode.equals(responText)) {
                        return;
                    }
                    if (!StringUtils.isNullOrEmpty(responText)) {
                        JSONArray array = JSON.parseArray(responText);
                        for (int i = 0; i < array.size(); ++i) {
                            JSONObject object = array.getJSONObject(i);
                            User user = JSON.toJavaObject(object, User.class);
                            mUsers.add(JSON.toJavaObject(object, User.class));
                        }
                        Message message = handler.obtainMessage();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                }
            });
        }).start();
    }

    private void updateMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyItemInserted(mUsers.size() - 1);
                chatMessage.scrollToPosition(mUsers.size() - 1);
            }
        });
    }

    //当前聊天连接
    private void connect() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(300, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(300, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(300, TimeUnit.SECONDS)//设置连接超时时间
                .build();

        Request request = new Request.Builder().url("ws://192.168.31.80:8081/chat/" + sendUserId + "/" + receiveUserId).build();
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
                    mUsers.add(user);
                    updateMessage();
                }
            }

            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                super.onOpen(webSocket, response);
                mWebSocket = webSocket;
            }
        });
        client.dispatcher().executorService().shutdown();
    }
}