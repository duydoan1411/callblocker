package com.dgteam.callblocker;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toast;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<ContactItem> contactList;
    private int layout;
    private Context context;

    public ContactAdapter(List<ContactItem> contactList, int layout, Context context) {
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

        viewHolder.imDetele.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setMessage("Bạn có chắc chắn muốn xóa?")
                    .setPositiveButton("Xóa",(dialog, which) -> {
                        contactList.remove(i);
                        notifyDataSetChanged();
                        BlackList.writeContact();
                    })
                    .setNegativeButton("Hủy",null)
                    .setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            positiveButton.setTextColor(Color.parseColor("#FF0000"));
            negativeButton.setTextColor(Color.parseColor("#FF0000"));
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener, View.OnLongClickListener*/{

        private ImageView imAvatar, imDetele;
        private TextView tvName, tvNumber;

        //private ItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvNumber = (TextView)itemView.findViewById(R.id.tvNumber);
            imDetele = (ImageView)itemView.findViewById(R.id.imDelete);

//            itemView.setOnClickListener(this);
//            itemView.setOnLongClickListener(this);
        }

//        public void setItemClickListener(ItemClickListener itemClickListener) {
//            this.itemClickListener = itemClickListener;
//        }
//
//        @Override
//        public void onClick(View view) {
//            itemClickListener.onClick(view,getAdapterPosition(),false);
//        }
//
//        @Override
//        public boolean onLongClick(View view) {
//            itemClickListener.onClick(view,getAdapterPosition(),true);
//            return false;
//        }
    }
}