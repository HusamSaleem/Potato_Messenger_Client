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
import com.example.potatomessenger.client.ClientManager;
import com.example.potatomessenger.interfaces.OnItemClickListener;

import java.util.ArrayList;

public class RoomIDAdapter extends RecyclerView.Adapter<RoomIDAdapter.MyViewHolder> {

    ArrayList<String> ids;
    Context context;

    private OnItemClickListener clickListener;

    public RoomIDAdapter(Context ct, ArrayList<String> ids) {
        this.context = ct;
        this.ids = ids;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView roomId;
        public ImageView deleteImg;
        public ImageView newNotification;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            roomId = itemView.findViewById(R.id.roomID);
            deleteImg = itemView.findViewById(R.id.imgDelete);
            newNotification = itemView.findViewById(R.id.notificationImg);

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
        View view = inflater.inflate(R.layout.room_id_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.roomId.setText("ID: " + ids.get(position));

        if (ClientManager.roomIdNotifications.containsKey(position) && ClientManager.roomIdNotifications.get(position))
            holder.newNotification.setVisibility(View.VISIBLE);
        else
            holder.newNotification.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return ids.size();
    }
}