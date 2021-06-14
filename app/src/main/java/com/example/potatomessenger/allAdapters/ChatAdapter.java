package com.example.potatomessenger.allAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potatomessenger.R;
import com.example.potatomessenger.client.ClientManager;
import com.example.potatomessenger.client.Message;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Message> msgList;

    public ChatAdapter(Context context, ArrayList<Message> msgList) {
        this.context = context;
        this.msgList = msgList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView userMsg;
        public TextView userDate;

        // Other users
        public TextView otherUserName;
        public TextView otherUserMsg;
        public TextView otherUserDate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            this.userMsg = itemView.findViewById(R.id.userMsg);
            this.userDate = itemView.findViewById(R.id.userDate);

            this.otherUserName = itemView.findViewById(R.id.otherUserName);
            this.otherUserDate = itemView.findViewById(R.id.otherUserDate);
            this.otherUserMsg = itemView.findViewById(R.id.otherUserMsg);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (this.msgList.get(position).getName().equals(ClientManager.username)) {
            return R.layout.user_chat_bubble;
        }
        return R.layout.other_user_chat_bubble;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (this.msgList.get(position).getName().equals(ClientManager.username)) {
            holder.userDate.setText(this.msgList.get(position).getDate().toString());
            holder.userMsg.setText(this.msgList.get(position).getMsg());
        } else {
            holder.otherUserName.setText(this.msgList.get(position).getName());
            holder.otherUserDate.setText(this.msgList.get(position).getDate().toString());
            holder.otherUserMsg.setText(this.msgList.get(position).getMsg());
        }
    }

    @Override
    public int getItemCount() {
        return this.msgList.size();
    }
}
