package com.example.campusassistance.account.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.campusassistance.R;
import com.example.campusassistance.account.adapter.GoodsRecordAdapter;
import com.example.campusassistance.goods.adapter.GoodsListAdapter;
import com.example.campusassistance.goods.entity.Good;
import com.example.campusassistance.init.servlet.ServerConnection;

import java.util.ArrayList;

public class GoodsReleasedRecord extends AppCompatActivity {

    //返回成功状态码
    private String responseSuccessCode = "200";
    //存放商品信息
    private ArrayList<Good> mDataList;
    private RecyclerView.LayoutManager mLayoutManager;
    private GoodsRecordAdapter mAdapter;

    private TextView mNoReleasedRecord;
    private RecyclerView mGoodsRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_released_record);
        init();
        requestGoodsInfo(getSharedPreferences("Login", MODE_PRIVATE).getString("userId", ""));
    }

    private void init() {
        mDataList = new ArrayList<>();
        mNoReleasedRecord = findViewById(R.id.tv_no_released_record);
        mGoodsRecord = findViewById(R.id.rv_good_record);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                mGoodsRecord.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                mAdapter = new GoodsRecordAdapter(mDataList);
                mGoodsRecord.setAdapter(mAdapter);
                mNoReleasedRecord.setVisibility(View.GONE);
            }
        }
    };

    private void requestGoodsInfo(String str) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String path = "http://192.168.31.80:8080/Goods/GoodsReleasedRecord";
                    String params = "userId=" + str;
                    String responseText = ServerConnection.connection(path, params);

                    if (TextUtils.isEmpty(responseText)) {
                        Looper.prepare();
                        Toast.makeText(GoodsReleasedRecord.this, "服务器连接出现错误", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } else if (!responseSuccessCode.equals(responseText)) {
                        //解析返回的json数据，并放进list中
                        JSONArray array = JSONObject.parseArray(responseText);
                        if (array != null) {
                            mDataList.clear();
                            for (int i = 0; i < array.size(); ++i) {
                                JSONObject object = array.getJSONObject(i);
                                mDataList.add(JSON.toJavaObject(object, Good.class));
                            }
                        }
                        //数据加载完成，通过handler发送一个msg
                        Message message = handler.obtainMessage();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }).start();
    }
}