package com.example.potatomessenger.allAdapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potatomessenger.R;
import com.example.potatomessenger.client.ClientManager;
import com.example.potatomessenger.client.Friend;
import com.example.potatomessenger.interfaces.OnItemClickListener;

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.MyViewHolder> {

    private ArrayList<Friend> friendList;
    Context context;

    private OnItemClickListener clickListener;

    public FriendAdapter(Context ct, ArrayList<Friend> friendList) {
        this.context = ct;
        this.friendList = friendList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView friendNameTxt;
        TextView activeStatusTxt;
        ImageView deleteImg;
        ImageView newNotification;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            friendNameTxt = itemView.findViewById(R.id.friendName);
            activeStatusTxt = itemView.findViewById(R.id.friendActiveTxt);
            deleteImg = itemView.findViewById(R.id.imgDelete);
            newNotification = itemView.findViewById(R.id.notificationImg2);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            clickListener.onItemClick(pos);
                        }
                    }
                }
            });

            deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            clickListener.onDeleteClick(pos);
                        }
                    }
                }
            });
        }

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.friend_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.friendNameTxt.setText(friendList.get(position).getName());

        if (ClientManager.friendNotifications.containsKey(position) && ClientManager.friendNotifications.get(position))
            holder.newNotification.setVisibility(View.VISIBLE);
        else
            holder.newNotification.setVisibility(View.INVISIBLE);

        if (friendList.get(position).isActive()) {
            holder.activeStatusTxt.setTextColor(Color.parseColor("#00FF00"));
            holder.activeStatusTxt.setText("Online");
        }
        else {
            holder.activeStatusTxt.setTextColor(Color.parseColor("#FF0000"));
            holder.activeStatusTxt.setText("Offline");
        }
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }
}
