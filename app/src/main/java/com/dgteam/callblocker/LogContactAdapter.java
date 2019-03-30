package com.dgteam.callblocker;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LogContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<ContactItemLog> contactItemLogList;
    private Context context;


    public LogContactAdapter(List<ContactItemLog> contactItemLogList, Context context) {
        this.contactItemLogList = contactItemLogList;
        this.context = context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == TYPE_HEADER){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.header_log_block,viewGroup,false);
            return new LogHeaderViewHolder(view);
        } else if (i == TYPE_ITEM){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.log_contact_adapter,viewGroup,false);
            return new LogItemViewHolder(view);
        }
        throw new RuntimeException(i+"");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof LogHeaderViewHolder){
            ContactItemLog itemLog = contactItemLogList.get(i);
            Date date = new Date();
            String today = new SimpleDateFormat("dd/MM/yyyy").format(date);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE,-1);
            String yesterday = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
            if (today.equalsIgnoreCase(itemLog.getDateLog())){
                ((LogHeaderViewHolder)viewHolder).header.setText("HÔM NAY");
            }else if (yesterday.equalsIgnoreCase(itemLog.getDateLog())){
                ((LogHeaderViewHolder)viewHolder).header.setText("HÔM QUA");
            }else {
                ((LogHeaderViewHolder)viewHolder).header.setText(itemLog.getDateLog());
            }
        } else if (viewHolder instanceof LogItemViewHolder){
            ContactItemLog itemLog = contactItemLogList.get(i);
            ((LogItemViewHolder)viewHolder).ivAvatar.setImageBitmap(itemLog.getAvatar());
            ((LogItemViewHolder)viewHolder).tvName.setText(itemLog.getName());
            ((LogItemViewHolder)viewHolder).tvNumber.setText(itemLog.getNumber());
            ((LogItemViewHolder)viewHolder).tvTime.setText(itemLog.getHourLog());
            ((LogItemViewHolder)viewHolder).imDelete.setOnClickListener(v -> {
                BlurPopupWindow dialog = new BlurPopupWindow.Builder(context)
                        .setContentView(R.layout.dialog)
                        .setGravity(Gravity.CENTER)
                        .setScaleRatio(0.2f)
                        .setBlurRadius(15)
                        .setTintColor(0x30000000)
                        .setAnimationDuration(300)
                        .setDismissOnClickBack(false)
                        .setDismissOnTouchBackground(false)
                        .build();
                TextView message = (TextView) dialog.findViewById(R.id.tvMessage);
                Button agree = (Button) dialog.findViewById(R.id.btAgree);
                Button degree = (Button) dialog.findViewById(R.id.btDegree);

                message.setText("Bạn có chắc chắn muốn xóa?");
                agree.setText("Xóa");
                agree.setTextColor(Color.parseColor("#FF0000"));
                agree.setOnClickListener(v1 -> {
                    contactItemLogList.remove(i);
                    BlockLogs.checkLogs();
                    notifyDataSetChanged();
                    BlockLogs.writeLogs();
                    dialog.dismiss();
                });
                degree.setTextColor(Color.parseColor("#FF0000"));
                degree.setText("Hủy");
                degree.setOnClickListener(v1 -> dialog.dismiss());
                dialog.show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return contactItemLogList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        if (contactItemLogList.get(position).getName()==null){
            return true;
        }else return false;
    }
}
