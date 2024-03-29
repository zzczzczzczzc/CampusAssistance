package com.example.campusassistance.account.adapter;

import android.content.Context;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusassistance.R;
import com.example.campusassistance.goods.entity.Good;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.example.campusassistance.common.StringToBitmap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
                //TODO
                mDataList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
                deleteMsg(good.getGoodId());
            }
        });
    }

    private void deleteMsg(String goodId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String path = "http://192.168.31.80:8080/Goods/DeleteGoodMsg";
                    OkHttpClient client = new OkHttpClient();
                    FormBody.Builder builder = new FormBody.Builder();
                    builder.add("goodId", goodId);
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
                            responseText = responseText.replace("\r", "");
                            responseText = responseText.replace("\n", "");
                            Looper.prepare();
                            if (responseText.equals("True")) {
                                Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
                            }
                            Looper.loop();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_goods_record, parent, false);
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
