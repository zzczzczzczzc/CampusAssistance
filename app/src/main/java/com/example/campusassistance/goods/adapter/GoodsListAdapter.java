package com.example.campusassistance.goods.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.campusassistance.R;
import com.example.campusassistance.goods.activity.OneByOneChatAcitvity;
import com.example.campusassistance.goods.entity.Good;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import common.StringToBitmap;

public class GoodsListAdapter extends RecyclerView.Adapter<GoodsListAdapter.ViewHolder> {

    private ArrayList<Good> dataList;
    private Context mContext;

    public GoodsListAdapter(ArrayList<Good> dataList) {
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_goods, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_description.setText(dataList.get(position).getDescription());
        holder.tv_prices.setText("价格：" + dataList.get(position).getPrices() + "元");
        holder.iv_picture.setImageBitmap(StringToBitmap.stringToBitmap(mContext, dataList.get(position).getPicture()));
        holder.ll_goodsDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, OneByOneChatAcitvity.class);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout ll_goodsDisplay;
        public ImageView iv_picture;
        public TextView tv_description;
        public TextView tv_prices;

        public ViewHolder(View itemView) {
            super(itemView);
            ll_goodsDisplay = (LinearLayout) itemView.findViewById(R.id.ll_goods_display);
            iv_picture = (ImageView) itemView.findViewById(R.id.iv_thing);
            tv_description = (TextView) itemView.findViewById(R.id.tv_description);
            tv_prices = (TextView) itemView.findViewById(R.id.tv_prices);
        }
    }
}
