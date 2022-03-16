package com.example.campusassistance.message.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusassistance.R;
import com.example.campusassistance.message.activity.OneByOneChatAcitvity;
import com.example.campusassistance.message.entity.User;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {

    private ArrayList<User> mUsers;
    private String mSelfUserId;
    private Context mContext;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public MessageListAdapter(ArrayList<User> dataList) {
        this.mUsers = dataList;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mSelfUserId = mContext.getSharedPreferences("Login", Context.MODE_PRIVATE).getString("userId", "");
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_message_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        User user = mUsers.get(position);
        String sendUserId = "", receiveUserId = "";
        String[] ids = user.getBothUserId().split("/");
        if (ids.length == 2) {
            if (mSelfUserId.equals(ids[0])) {
                sendUserId = ids[0];
                receiveUserId = ids[1];
            } else {
                sendUserId = ids[1];
                receiveUserId = ids[0];
            }
        }
        final String tempSendUserId = sendUserId, tempReceiveUserId = receiveUserId;
        holder.otherName.setText(receiveUserId);
        holder.chatRecord.setText(user.getMessage());
        holder.lastTime.setText(df.format(user.getDateTime()));
//        if (!user.isSend()) {
//            holder.avatar.setImageResource(R.drawable.avatar_red);
//        }
        holder.chatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!user.isSend()) {
                holder.avatar.setImageResource(R.drawable.avatar);
                Intent intent = new Intent(mContext, OneByOneChatAcitvity.class);
                intent.putExtra("receiveUserId", tempReceiveUserId);
                intent.putExtra("sendUserId", tempSendUserId);
                mContext.startActivity(intent);
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout chatList;
        public ImageView avatar;
        public TextView otherName;
        public TextView chatRecord;
        public TextView lastTime;

        public ViewHolder(View itemView) {
            super(itemView);
            chatList = (LinearLayout) itemView.findViewById(R.id.ll_chat_list);
            avatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            otherName = (TextView) itemView.findViewById(R.id.tv_other_name);
            chatRecord = (TextView) itemView.findViewById(R.id.tv_chat_record);
            lastTime = (TextView) itemView.findViewById(R.id.tv_last_time);
        }
    }
}
