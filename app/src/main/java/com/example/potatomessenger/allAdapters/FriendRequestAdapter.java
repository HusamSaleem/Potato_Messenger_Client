package com.example.potatomessenger.allAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potatomessenger.R;
import com.example.potatomessenger.client.Friend;
import com.example.potatomessenger.client.FriendRequest;
import com.example.potatomessenger.interfaces.OnItemClickListener;

import java.util.ArrayList;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<FriendRequest> friendRequests;

    private OnItemClickListener clickListener;

    public FriendRequestAdapter(Context context, ArrayList<FriendRequest> friendRequests) {
        this.context = context;
        this.friendRequests = friendRequests;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView friendName;
        public ImageView acceptBtn;
        public ImageView declineBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            friendName = itemView.findViewById(R.id.friendRequestName);
            acceptBtn = itemView.findViewById(R.id.acceptFriendReqBtn);
            declineBtn = itemView.findViewById(R.id.declineFriendReqBtn);

            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            clickListener.onItemClick(getAdapterPosition());
                        }
                    }
                }
            });

            declineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            clickListener.onDeleteClick(getAdapterPosition());
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
        View view = inflater.inflate(R.layout.friend_request_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.friendName.setText(this.friendRequests.get(position).getRequestingName());
    }

    @Override
    public int getItemCount() {
        return this.friendRequests.size();
    }
}
