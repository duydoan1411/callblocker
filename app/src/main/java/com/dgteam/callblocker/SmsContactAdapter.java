package com.dgteam.callblocker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import java.util.List;

public class SmsContactAdapter extends RecyclerView.Adapter<SmsContactAdapter.ViewHolder> {

    private List<ContactItem> contactList;
    private int layout;
    private Context context;


    public SmsContactAdapter(List<ContactItem> contactList, int layout, Context context) {
        this.contactList = contactList;
        this.layout = layout;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(layout,viewGroup,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.tvName.setText(contactList.get(i).getName());
        viewHolder.tvNumber.setText(contactList.get(i).getNumber());
        viewHolder.imAvatar.setImageBitmap(contactList.get(i).getAvatar());
        viewHolder.imDetele.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private ImageView imAvatar, imDetele;
        private TextView tvName, tvNumber;

        private ItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvNumber = (TextView)itemView.findViewById(R.id.tvNumber);
            imDetele = (ImageView)itemView.findViewById(R.id.imDelete);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            Context context = itemView.getContext();
            setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    Intent intent = new Intent(context,SmsForNumberActivity.class);
                    intent.putExtra("name",contactList.get(position).getName());
                    intent.putExtra("number",contactList.get(position).getNumber());
                    context.startActivity(intent);
                }
            });
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),false);
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),true);
            return false;
        }
    }
}