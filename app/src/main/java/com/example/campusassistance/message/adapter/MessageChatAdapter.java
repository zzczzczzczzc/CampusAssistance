package com.example.campusassistance.message.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusassistance.R;
import com.example.campusassistance.message.entity.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MessageChatAdapter extends RecyclerView.Adapter<MessageChatAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<User> mUsers;
    private String receiveUserId;
    private String sendUserId;
    private String selfUserId;

    public MessageChatAdapter(ArrayList<User> users) {
        this.mUsers = users;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_message, parent, false);
        selfUserId = mContext.getSharedPreferences("Login", Context.MODE_PRIVATE).getString("userId", "");
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        User user = mUsers.get(position);
        if (user.getSendUserId().equals(selfUserId)) {
            holder.sendMessage.setVisibility(View.VISIBLE);
            holder.sendAvatar.setVisibility(View.VISIBLE);
            holder.sendMessage.setText(user.getMessage());
            holder.receiveAvatar.setVisibility(View.GONE);
            holder.receiveMessage.setVisibility(View.GONE);
        } else {
            holder.receiveMessage.setVisibility(View.VISIBLE);
            holder.receiveAvatar.setVisibility(View.VISIBLE);
            holder.receiveMessage.setText(user.getMessage());
            holder.sendAvatar.setVisibility(View.GONE);
            holder.sendMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView receiveAvatar;
        public ImageView sendAvatar;
        public TextView receiveMessage;
        public TextView sendMessage;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            receiveMessage = (TextView) itemView.findViewById(R.id.tv_receive_message);
            sendMessage = (TextView) itemView.findViewById(R.id.tv_send_message);
            receiveAvatar = (ImageView) itemView.findViewById(R.id.iv_receive_message);
            sendAvatar = (ImageView) itemView.findViewById(R.id.iv_send_message);
        }
    }
}
