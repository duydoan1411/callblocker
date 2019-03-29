package com.dgteam.callblocker;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class LogHeaderViewHolder extends RecyclerView.ViewHolder {

    private TextView header;

    public LogHeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        header = itemView.findViewById(R.id.tvHeader);
    }
}
