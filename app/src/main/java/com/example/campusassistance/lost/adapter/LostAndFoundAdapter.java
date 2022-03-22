package com.example.campusassistance.lost.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusassistance.R;
import com.example.campusassistance.common.StringToBitmap;
import com.example.campusassistance.lost.entity.LostAndFoundGood;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class LostAndFoundAdapter extends RecyclerView.Adapter<LostAndFoundAdapter.LostAndFoundViewHolder> {

    private ArrayList<LostAndFoundGood> mLostAndFoundGoods;
    private Context mContext;

    public LostAndFoundAdapter(ArrayList<LostAndFoundGood> lostAndFoundGoods) {
        this.mLostAndFoundGoods = lostAndFoundGoods;
    }

    @NonNull
    @NotNull
    @Override
    public LostAndFoundViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_lost_and_found_good, parent, false);
        LostAndFoundViewHolder viewHolder = new LostAndFoundViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull LostAndFoundViewHolder holder, int position) {
        LostAndFoundGood good = mLostAndFoundGoods.get(position);
        holder.lostAndFoundPicture.setImageBitmap(StringToBitmap.stringToBitmap(mContext, good.getPicture()));
        holder.lostAndFonudDescription.setText(good.getDescription());
        holder.lostAndFoundTelephone.setText(good.getContactWay());
    }

    @Override
    public int getItemCount() {
        return mLostAndFoundGoods.size();
    }

    class LostAndFoundViewHolder extends RecyclerView.ViewHolder {

        public ImageView lostAndFoundPicture;
        public TextView lostAndFonudDescription;
        public TextView lostAndFoundTelephone;

        public LostAndFoundViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            lostAndFoundPicture = itemView.findViewById(R.id.iv_lost_and_found_good);
            lostAndFonudDescription = itemView.findViewById(R.id.tv_lost_and_found_good_description);
            lostAndFoundTelephone = itemView.findViewById(R.id.tv_lost_and_found_good_telephone);
        }
    }
}
