package com.example.campusassistance.goods.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusassistance.R;
import com.example.campusassistance.goods.entity.Good;
import com.example.campusassistance.message.activity.OneByOneChatAcitvity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import com.example.campusassistance.common.StringToBitmap;

public class GoodsDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView back;
    private ImageView goodPicture;
    private TextView seller;
    private TextView goodDescription;
    private TextView telephone;
    private TextView goodPrices;
    private TextView goodReleaseTime;
    private LinearLayout chatWithSeller;

    private Good good;
    private String sendUserId;
    private String receiveUserId;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO：UI怎么排列
        setContentView(R.layout.activity_goods_detail);
        init();
        back.setOnClickListener(this);
        chatWithSeller.setOnClickListener(this);
        loadGoodDate();
    }

    private void init() {
        back = findViewById(R.id.iv_back);
        goodPicture = findViewById(R.id.iv_good_picture);
        seller = findViewById(R.id.tv_seller);
        goodDescription = findViewById(R.id.tv_description);
        telephone = findViewById(R.id.tv_telephone);
        goodPrices = findViewById(R.id.tv_prices);
        goodReleaseTime = findViewById(R.id.tv_released_time);
        chatWithSeller = findViewById(R.id.ll_chat_with_seller);
        good = (Good) getIntent().getSerializableExtra("good_detail");
        sendUserId = getSharedPreferences("Login", MODE_PRIVATE).getString("userId", "");
        receiveUserId = good.getUserId();
    }

    private void loadGoodDate() {
        goodPicture.setImageBitmap(StringToBitmap.stringToBitmap(GoodsDetailActivity.this, good.getPicture()));
        seller.setText(good.getUserId());
        goodDescription.setText(good.getDescription());
        telephone.setText(good.getTelephone());
        goodPrices.setText(good.getPrices());
        goodReleaseTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(good.getTime()));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_chat_with_seller) {
            if (sendUserId.equals(receiveUserId)) {
                Toast.makeText(GoodsDetailActivity.this, "不能与自己聊天", Toast.LENGTH_SHORT).show();
                return;
            } else if (("").equals(sendUserId)) {
                Toast.makeText(GoodsDetailActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(GoodsDetailActivity.this, OneByOneChatAcitvity.class);
            intent.putExtra("sendUserId", sendUserId);
            intent.putExtra("receiveUserId", receiveUserId);
            startActivity(intent);
        } else if (v.getId() == R.id.iv_back) {
            finish();
        }
    }
}