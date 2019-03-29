package com.dgteam.callblocker;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LogItemViewHolder extends RecyclerView.ViewHolder {

    private ImageView ivAvatar, imDelete;
    private TextView tvName, tvNumber, tvTime;

    public LogItemViewHolder(@NonNull View itemView) {
        super(itemView);
        ivAvatar = (ImageView)itemView.findViewById(R.id.ivAvatarLog);
        imDelete = (ImageView)itemView.findViewById(R.id.imDeleteLog);
        tvName = (TextView)itemView.findViewById(R.id.tvNameLog);
        tvNumber = (TextView)itemView.findViewById(R.id.tvNumberLog);
        tvTime = (TextView)itemView.findViewById(R.id.tvTimeLog);

    }
}
