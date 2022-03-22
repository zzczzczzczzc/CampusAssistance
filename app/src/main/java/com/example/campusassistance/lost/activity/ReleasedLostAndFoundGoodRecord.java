package com.example.campusassistance.lost.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.campusassistance.R;
import com.example.campusassistance.lost.adapter.ReleasedLostAndFoundRecordAdapter;
import com.example.campusassistance.lost.entity.LostAndFoundGood;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReleasedLostAndFoundGoodRecord extends AppCompatActivity {

    private RecyclerView mLostAndFoundGoodsRecordRecyclerView;
    private TextView mNoRecordTextView;

    private ReleasedLostAndFoundRecordAdapter mAdapter;
    private ArrayList<LostAndFoundGood> mLostAndFoundGoodsList;
    private String userId;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_released_lost_and_found_good_record);
        init();
        requestLostAndFoundGoodsMessageRecord();
    }

    private void init() {
        mNoRecordTextView = findViewById(R.id.tv_no_released_record);
        mLostAndFoundGoodsRecordRecyclerView = findViewById(R.id.rv_lost_and_found_good_record);

        mLostAndFoundGoodsList = new ArrayList<>();
        userId = getSharedPreferences("Login", MODE_PRIVATE).getString("userId", "");
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                mAdapter = new ReleasedLostAndFoundRecordAdapter(mLostAndFoundGoodsList);
                mLostAndFoundGoodsRecordRecyclerView.setAdapter(mAdapter);
                mLostAndFoundGoodsRecordRecyclerView.setLayoutManager(new LinearLayoutManager(ReleasedLostAndFoundGoodRecord.this));
            }
        }
    };

    private void requestLostAndFoundGoodsMessageRecord() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String path = "http://192.168.31.80:8080/LostAndFound/LostAndFoundRecord";
                    OkHttpClient client = new OkHttpClient();
                    FormBody.Builder builder = new FormBody.Builder();
                    builder.add("userId", userId);
                    FormBody body = builder.build();
                    Request request = new Request.Builder().url(path).post(body).build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                            Looper.prepare();
//                            Toast.makeText(Login.this, "服务器连接出现错误", Toast.LENGTH_SHORT).show();
//                            Looper.loop();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String responseText = response.body().string();
                            //解析返回的json数据，并放进list中
                            //TODO：怎么判断返回的数据是不是JSON
                            JSONArray array = JSONObject.parseArray(responseText);
                            if (array != null) {
                                for (int i = 0; i < array.size(); ++i) {
                                    JSONObject object = array.getJSONObject(i);
                                    mLostAndFoundGoodsList.add(JSON.toJavaObject(object, LostAndFoundGood.class));
                                }
                            }
                            //数据加载完成，通过handler发送一个msg
                            Message message = handler.obtainMessage();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}