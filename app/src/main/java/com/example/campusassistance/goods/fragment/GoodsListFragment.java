package com.example.campusassistance.goods.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.campusassistance.R;
import com.example.campusassistance.account.servlet.Login;
import com.example.campusassistance.goods.adapter.GoodsListAdapter;
import com.example.campusassistance.goods.entity.Good;
import com.example.campusassistance.init.servlet.ServerConnection;

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoodsListFragment extends Fragment implements View.OnClickListener {

    private RecyclerView mGoodsListView;
    private EditText mSearchText;
    private Button mSearchButton;

    //存放商品信息
    private ArrayList<Good> mDataList;
    private RecyclerView.LayoutManager mLayoutManager;
    private GoodsListAdapter mGoodListAdapter;
    //返回成功状态码
    private String responseSuccessCode = "200";
//    private long day = 1000 * 24 * 60 * 60;
//    private long hour = 1000 * 60 * 60;
//    private long min = 1000 * 60;

    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.goods_fragment, container, false);
        initView(view);
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        long currentTime = System.currentTimeMillis();
//        long interval = 1000 * 60 * 10;
//        long lastTime = sp.getLong("last_refresh_time", currentTime);
//        try {
//            interval = format.parse(String.valueOf(new Timestamp(currentTime))).getTime() - format.parse(String.valueOf(new Timestamp(lastTime))).getTime();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        if ((interval % day % hour) / min >= 10) {
//            requestGoodsInfo("");
//            SharedPreferences.Editor editor = sp.edit();
//            editor.putLong("last_refresh_time", currentTime);
//            editor.commit();
//        }
        requestGoodsInfo("");
        mSearchButton.setOnClickListener(this);
        return view;
    }

    private void initView(View view) {
        mDataList = new ArrayList<>();
        mGoodsListView = (RecyclerView) view.findViewById(R.id.rv_goods);
        mSearchText = (EditText) view.findViewById(R.id.et_search);
        mSearchButton = (Button) view.findViewById(R.id.bt_search);
        sp = getActivity().getSharedPreferences("refresh_interval", Context.MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_search) {
            String searchText = mSearchText.getText().toString().trim();
            requestGoodsInfo(searchText);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                mGoodsListView.setLayoutManager(new GridLayoutManager(getActivity(), 2, RecyclerView.VERTICAL, false));
                mGoodListAdapter = new GoodsListAdapter(mDataList);
                mGoodsListView.setAdapter(mGoodListAdapter);
            }
        }
    };

    private void requestGoodsInfo(String str) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String path = "http://192.168.31.80:8080/Goods/GoodsRequest";
                    String responseText = "";
                    if (TextUtils.isEmpty(str)) {
                        responseText = ServerConnection.connection(path, str);
                    } else {
                        String params = "search=" + str;
                        responseText = ServerConnection.connection(path, params);
                    }

                    if (TextUtils.isEmpty(responseText) || responseSuccessCode.equals(responseText)) {
                        Looper.prepare();
                        if (responseSuccessCode.equals(responseText)) {
                            Toast.makeText(getActivity(), "查询不到相关数据", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "服务器连接出现错误", Toast.LENGTH_SHORT).show();
                        }
                        Looper.loop();
                    } else {
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
