package com.example.campusassistance.lost.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.campusassistance.R;
import com.example.campusassistance.account.servlet.Login;
import com.example.campusassistance.goods.entity.Good;
import com.example.campusassistance.lost.adapter.LostAndFoundAdapter;
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

public class LostAndFoundListFragment extends Fragment {

    private ArrayList<LostAndFoundGood> mLostAndFoundGoodsList;
    private LostAndFoundAdapter mAdapter;

    private Button search;
    private EditText inputSearchMessage;
    private RecyclerView lostAndFoundGood;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lost_and_found_fragment, container, false);
        init(view);
        getLostAndFoundGoodMessage("");
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputMessage = inputSearchMessage.getText().toString().trim();
                if (inputMessage != null) {
                    getLostAndFoundGoodMessage(inputMessage);
                }
            }
        });
        return view;
    }

    private void init(View view) {
        mLostAndFoundGoodsList = new ArrayList<>();
        search = view.findViewById(R.id.bt_search);
        inputSearchMessage = view.findViewById(R.id.et_search);
        lostAndFoundGood = view.findViewById(R.id.rv_lost_and_found_good);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                lostAndFoundGood.setLayoutManager(new LinearLayoutManager(getActivity()));
                mAdapter = new LostAndFoundAdapter(mLostAndFoundGoodsList);
                lostAndFoundGood.setAdapter(mAdapter);
            }
        }
    };

    private void getLostAndFoundGoodMessage(String str) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String path = "http://192.168.31.80:8080/LostAndFound/LostAndFoundListRequest";
                    OkHttpClient client = new OkHttpClient();
                    FormBody.Builder builder = new FormBody.Builder();
                    if (!TextUtils.isEmpty(str)) {
                        builder.add("searchMessage", str);
                    }
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
                            JSONArray array = JSONObject.parseArray(responseText);
                            if (array != null) {
                                mLostAndFoundGoodsList.clear();
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
