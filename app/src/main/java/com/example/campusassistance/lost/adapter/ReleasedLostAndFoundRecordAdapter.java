package com.example.campusassistance.lost.adapter;

import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.campusassistance.R;
import com.example.campusassistance.common.StringToBitmap;
import com.example.campusassistance.lost.activity.ReleaseLostAndFoundGood;
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

public class ReleasedLostAndFoundRecordAdapter extends RecyclerView.Adapter<ReleasedLostAndFoundRecordAdapter.RecordViewHolder> {

    private ArrayList<LostAndFoundGood> mLostAndFoundGoodsList;
    private Context mContext;

    public ReleasedLostAndFoundRecordAdapter(ArrayList<LostAndFoundGood> dataList) {
        this.mLostAndFoundGoodsList = dataList;
    }

    @NonNull
    @NotNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_lost_and_found_good, parent, false);
        RecordViewHolder viewHolder = new RecordViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecordViewHolder holder, int position) {
        LostAndFoundGood good = mLostAndFoundGoodsList.get(position);
        holder.lostAndFoundPicture.setImageBitmap(StringToBitmap.stringToBitmap(mContext, good.getPicture()));
        holder.lostAndFonudDescription.setText(good.getDescription());
        holder.lostAndFoundTelephone.setText(good.getContactWay());
        holder.lostAndFoundGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO：点击删除
                mLostAndFoundGoodsList.remove(position);
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
                    final String path = "http://192.168.31.80:8080/LostAndFound/DeleteLostAndFoundMsg";
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
        return mLostAndFoundGoodsList.size();
    }

    class RecordViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout lostAndFoundGood;
        public ImageView lostAndFoundPicture;
        public TextView lostAndFonudDescription;
        public TextView lostAndFoundTelephone;

        public RecordViewHolder(View itemView) {
            super(itemView);
            lostAndFoundGood = itemView.findViewById(R.id.ll_release_lost_and_found_good);
            lostAndFoundPicture = itemView.findViewById(R.id.iv_lost_and_found_good);
            lostAndFonudDescription = itemView.findViewById(R.id.tv_lost_and_found_good_description);
            lostAndFoundTelephone = itemView.findViewById(R.id.tv_lost_and_found_good_telephone);
        }
    }
}
