package com.dgteam.callblocker;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SmsLogItemViewHolder extends RecyclerView.ViewHolder {

    ImageView ivAvatar, imDelete;
    TextView tvName, tvNumber, tvTime, messages;

    public SmsLogItemViewHolder(@NonNull View itemView) {
        super(itemView);
        ivAvatar = (ImageView)itemView.findViewById(R.id.ivAvatarSmsLog);
        imDelete = (ImageView)itemView.findViewById(R.id.imDeleteSmsLog);
        tvName = (TextView)itemView.findViewById(R.id.tvNameSmsLog);
        tvNumber = (TextView)itemView.findViewById(R.id.tvNumberSmsLog);
        tvTime = (TextView)itemView.findViewById(R.id.tvTimeSmsLog);
        messages = (TextView)itemView.findViewById(R.id.messageSmsLog);
    }
}
