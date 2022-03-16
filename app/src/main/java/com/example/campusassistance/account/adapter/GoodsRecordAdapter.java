package com.example.campusassistance.account.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusassistance.R;
import com.example.campusassistance.account.activity.GoodsReleasedRecord;
import com.example.campusassistance.goods.entity.Good;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import common.StringToBitmap;

public class GoodsRecordAdapter extends RecyclerView.Adapter<GoodsRecordAdapter.ViewHolder> {

    private ArrayList<Good> mDataList;
    private Context mContext;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public GoodsRecordAdapter(ArrayList<Good> list) {
        this.mDataList = list;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Good good = mDataList.get(position);
        holder.mPicture.setImageBitmap(StringToBitmap.stringToBitmap(mContext, good.getPicture()));
        holder.mDescription.setText(good.getDescription());
        holder.mPrices.setText(good.getPrices());
        holder.mTelephone.setText(good.getTelephone());
        holder.mReleasedTime.setText(df.format(good.getTime()));
        holder.mGoodsReocrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_view_goods_record, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mGoodsReocrd;
        public ImageView mPicture;
        public TextView mDescription;
        public TextView mTelephone;
        public TextView mPrices;
        public TextView mReleasedTime;

        public ViewHolder(View itemView) {
            super(itemView);
            mGoodsReocrd = itemView.findViewById(R.id.ll_goods_record);
            mPicture = itemView.findViewById(R.id.iv_good_picture);
            mDescription = itemView.findViewById(R.id.tv_description_record);
            mTelephone = itemView.findViewById(R.id.tv_telephone_record);
            mPrices = itemView.findViewById(R.id.tv_prices_record);
            mReleasedTime = itemView.findViewById(R.id.tv_released_time_record);
        }
    }

}
