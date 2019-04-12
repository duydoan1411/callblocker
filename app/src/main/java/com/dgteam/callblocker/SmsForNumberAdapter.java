package com.dgteam.callblocker;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SmsForNumberAdapter extends RecyclerView.Adapter<SmsForNumberAdapter.ViewHolder>{

    private List<String> listMessages;

    public SmsForNumberAdapter(List<String> listMessages) {
        this.listMessages = listMessages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.messages_item,viewGroup,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.textView.setText(listMessages.get(i));
    }

    @Override
    public int getItemCount() {
        return listMessages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.tvMessageItem);
        }
    }
}
