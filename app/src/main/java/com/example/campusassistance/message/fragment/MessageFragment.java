package com.example.campusassistance.message.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.campusassistance.R;
import com.example.campusassistance.account.adapter.GoodsRecordAdapter;
import com.example.campusassistance.account.servlet.Login;
import com.example.campusassistance.goods.entity.Good;
import com.example.campusassistance.message.adapter.MessageListAdapter;
import com.example.campusassistance.message.entity.User;
import com.mysql.cj.util.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Array;
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

public class MessageFragment extends Fragment {

    //返回成功状态码
    private String responseSuccessCode = "200";
    private TextView mNoMessageRecord;
    private RecyclerView mMessageList;
    private ArrayList<User> mUsers;
    private MessageListAdapter mAdapter;
    private String mUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_fragment, container, false);
        init(view);
        if (!("").equals(mUserId)) {
            requestMessageList();
        }
        return view;
    }

    private void init(View view) {
        mNoMessageRecord = view.findViewById(R.id.tv_no_chat_record);
        mMessageList = view.findViewById(R.id.rv_chat_message_list);
        mUsers = new ArrayList<>();
        mUserId = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE).getString("userId", "");
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                mMessageList.setLayoutManager(new LinearLayoutManager(getActivity()));
                mAdapter = new MessageListAdapter(mUsers);
                mMessageList.setAdapter(mAdapter);
                mNoMessageRecord.setVisibility(View.GONE);
            }
        }
    };

    private void requestMessageList() {
        new Thread(() -> {
            String path = "http://192.168.31.80:8080/Message/requestMessageList";
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(300, TimeUnit.SECONDS)//设置读取超时时间
                    .writeTimeout(300, TimeUnit.SECONDS)//设置写的超时时间
                    .connectTimeout(300, TimeUnit.SECONDS)//设置连接超时时间
                    .build();
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("userId", mUserId);
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

}
