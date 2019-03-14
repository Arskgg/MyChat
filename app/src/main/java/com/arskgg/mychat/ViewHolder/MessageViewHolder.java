package com.arskgg.mychat.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.arskgg.mychat.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public TextView userName, message, time;
    public CardView messageLayout;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);

        userName = itemView.findViewById(R.id.userName);
        message = itemView.findViewById(R.id.userMessage);
        time = itemView.findViewById(R.id.timeMessage);
        messageLayout = itemView.findViewById(R.id.messageLayout);

    }

}
