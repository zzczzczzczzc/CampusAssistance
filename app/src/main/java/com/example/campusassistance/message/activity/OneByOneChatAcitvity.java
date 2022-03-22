package com.example.campusassistance.message.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.campusassistance.R;
import com.example.campusassistance.init.activity.MainActivity;
import com.example.campusassistance.message.adapter.MessageChatAdapter;
import com.example.campusassistance.message.adapter.MessageListAdapter;
import com.example.campusassistance.message.entity.User;
import com.example.campusassistance.message.service.MessageService;
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

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MessageService.MessageBinder binder = (MessageService.MessageBinder) service;
            MessageService messageService = binder.getService();
            //由于mWebSocket的值为异步获取（在子线程中赋值），第一次连接的时候值为null，
            // 第二次连接重新赋值，但第一次连接并没有用到该变量，此时为第二次连接，因此此处不用同步处理
            mWebSocket = messageService.messageWebSocket;
            messageService.setCallback(new MessageService.Callback() {
                @Override
                public void receiveMsg(User user) {
                    Message msg = new Message();
                    msg.what = 2;
                    msg.obj = user;
                    handler.sendMessage(msg);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_by_one_chat_acitvity);
        init();
        chatName.setText(receiveUserId);
        getChatMessage();
        sendMessage.setOnClickListener(this);
        back.setOnClickListener(this);
        Intent intentService = new Intent(OneByOneChatAcitvity.this, MessageService.class);
        bindService(intentService, connection, BIND_AUTO_CREATE);
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
                if (mWebSocket != null) {
                    mWebSocket.send(user.toString());
                } else {
                    //TODO
                }
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
            } else if (msg.what == 2) {
                User user = (User) msg.obj;
                if (user.getSendUserId().equals(receiveUserId) && user.getReceiveUserId().equals(sendUserId)) {
                    mUsers.add((User) msg.obj);
                    updateMessage();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

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
//                    if (responseSuccessCode.equals(String.valueOf(response.code()))) {
//                        return;
//                    }
                    if (!StringUtils.isNullOrEmpty(responText)) {
                        JSONArray array = JSON.parseArray(responText);
                        for (int i = 0; i < array.size(); ++i) {
                            JSONObject object = array.getJSONObject(i);
                            User user = JSON.toJavaObject(object, User.class);
                            mUsers.add(user);
                        }
                        Message message = handler.obtainMessage();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                }
            });
        }).start();
    }

    //更新聊天内容
    private void updateMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyItemInserted(mUsers.size() - 1);
                chatMessage.scrollToPosition(mUsers.size() - 1);
            }
        });
    }
}