package com.dgteam.callblocker;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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

public class SmsLogContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<SmsContactItemLog> smsContactItemLogList;
    private Context context;
    private View view;


    public SmsLogContactAdapter(List<SmsContactItemLog> smsContactItemLogList, Context context, View view) {
        this.smsContactItemLogList = smsContactItemLogList;
        this.context = context;
        this.view = view;
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
                    R.layout.log_sms_item,viewGroup,false);
            return new SmsLogItemViewHolder(view);
        }
        throw new RuntimeException(i+"");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof LogHeaderViewHolder){
            SmsContactItemLog itemLog = smsContactItemLogList.get(i);
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
        } else if (viewHolder instanceof SmsLogItemViewHolder){
            SmsContactItemLog itemLog = smsContactItemLogList.get(i);
            class ImgAsyncTask extends AsyncTask<Void, SmsContactItemLog, Void>{

                @Override
                protected Void doInBackground(Void... voids) {

                    SmsContactItemLog itemLog1 = smsContactItemLogList.get(i);
                    publishProgress(itemLog1);

                    return null;
                }

                @Override
                protected void onProgressUpdate(SmsContactItemLog... values) {
                    super.onProgressUpdate(values);
                    ((SmsLogItemViewHolder)viewHolder).ivAvatar.setImageBitmap(values[0].getAvatar());
                }
            }
            new ImgAsyncTask().execute();
            ((SmsLogItemViewHolder)viewHolder).tvName.setText(itemLog.getName());
            ((SmsLogItemViewHolder)viewHolder).tvNumber.setText(itemLog.getNumber());
            ((SmsLogItemViewHolder)viewHolder).tvTime.setText(itemLog.getHourLog());
            ((SmsLogItemViewHolder)viewHolder).messages.setText(itemLog.getMessage());
            ((SmsLogItemViewHolder)viewHolder).imDelete.setOnClickListener(v -> {
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
                    SmsContactItemLog backup = smsContactItemLogList.get(i);
                    smsContactItemLogList.remove(i);
                    SmsLogs.checkLogs();
                    notifyDataSetChanged();
                    SmsLogs.writeLogs();
                    dialog.dismiss();
                    Snackbar snackbar = Snackbar.make(view,"Bạn đã xóa thành công",Snackbar.LENGTH_LONG)
                            .setAction("Hoàn tác",v2 -> {
                                if (smsContactItemLogList.isEmpty())
                                {
                                    SmsLogs.addList(backup);
                                }else if(i>=smsContactItemLogList.size()) {
                                    smsContactItemLogList.add(backup);
                                }else
                                    smsContactItemLogList.add(i,backup);
                                SmsLogs.checkLogs();
                                notifyDataSetChanged();
                                SmsLogs.writeLogs();
                            });
                    View snackBarView = snackbar.getView();
                    TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar.show();
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
        return smsContactItemLogList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        if (smsContactItemLogList.get(position).getName()==null){
            return true;
        }else return false;
    }

}
