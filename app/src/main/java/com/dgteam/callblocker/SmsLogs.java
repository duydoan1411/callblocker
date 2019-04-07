package com.dgteam.callblocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


public class SmsLogs extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String SMS_BLOCK_LOG = "sms_block_logs.dat";

    private RecyclerView recyclerView;
    private FloatingActionButton fabClearAll;

    private SmsLogContactAdapter smsLogContactAdapter;
    protected static List<SmsContactItemLog> smsContactItemLogList = new ArrayList<SmsContactItemLog>();
    private Context context;

    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;
    private BroadcastReceiver broadcastReceiver;

    public SmsLogs() {

    }

    public static BlockLogs newInstance(String param1, String param2) {
        BlockLogs fragment = new BlockLogs();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        readLogs();
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_block_logs, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.rvLog);
        fabClearAll = (FloatingActionButton)view.findViewById(R.id.fabClearAll);

        fabClearAll.setOnClickListener(v -> {
            if (!smsContactItemLogList.isEmpty()) {
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

                message.setText("Bạn có chắc muốn xóa tất cả nhật kí chặn cuộc gọi?");
                agree.setText("Xóa");
                agree.setTextColor(Color.parseColor("#FF0000"));
                agree.setOnClickListener(v1 -> {
                    List<SmsContactItemLog> backupList = new ArrayList<SmsContactItemLog>();
                    backupList.clear();
                    for (SmsContactItemLog i: smsContactItemLogList){
                        backupList.add(i);
                    }
                    smsContactItemLogList.clear();
                    smsLogContactAdapter.notifyDataSetChanged();
                    writeLogs();
                    //Toast.makeText(container.getContext(),"Đã xóa nhật kí chặn cuộc gọi",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    Snackbar.make(view, "Đã xóa nhật kí chặn cuộc gọi", Snackbar.LENGTH_LONG)
                            .setAction("Hoàn tác", v2 -> {
                                for (SmsContactItemLog i: backupList){
                                    smsContactItemLogList.add(i);
                                }
                                smsLogContactAdapter.notifyDataSetChanged();
                                writeLogs();
                            }).show();
                });
                degree.setTextColor(Color.parseColor("#FF0000"));
                degree.setText("Hủy");
                degree.setOnClickListener(v1 -> dialog.dismiss());
                dialog.show();
            }else {
                //Toast.makeText(container.getContext(),"Nhật kí chặn cuộc gọi rỗng",Toast.LENGTH_SHORT).show();
                Snackbar.make(view, "Nhật kí chặn cuộc gọi rỗng", Snackbar.LENGTH_SHORT).show();
            }

        });

        showRecyclerView(container, view);

//        broadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).
//                        equals(TelephonyManager.EXTRA_STATE_IDLE)){
//                    readLogs();
//                    Log.d("aaa", "onReceive: "+smsContactItemLogList.size());
//                    smsLogContactAdapter.notifyDataSetChanged();
//                }
//            }
//        };
//        context.registerReceiver(broadcastReceiver, new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED));

        return view;
    }

    public static void addList(SmsContactItemLog smsContactItemLog){
        if (smsContactItemLogList.isEmpty()) {
            smsContactItemLogList.add(0,smsContactItemLog);
            smsContactItemLogList.add(0,new SmsContactItemLog(smsContactItemLog.getDateLog()));

        }else {
            SmsContactItemLog smsContactItemLog1 = smsContactItemLogList.get(0);
            if (smsContactItemLog1.getDateLog().equalsIgnoreCase(smsContactItemLog.getDateLog())){
                smsContactItemLogList.add(1,smsContactItemLog);
            }else {
                smsContactItemLogList.add(0,smsContactItemLog);
                smsContactItemLogList.add(0,new SmsContactItemLog(smsContactItemLog.getDateLog()));
            }
        }
    }

    public static void checkLogs(){
        if (smsContactItemLogList.size()<=1){
            smsContactItemLogList.clear();
        }else {
            if (smsContactItemLogList.get(smsContactItemLogList.size()-1).getHeader()!=null){
                Log.d("aaa", "checkLogs: Xoa cuoi");
                smsContactItemLogList.remove(smsContactItemLogList.size()-1);
            }
            for (int i=0;i<smsContactItemLogList.size()-1; i++ ){
                if (smsContactItemLogList.get(i).getHeader()!=null
                        && !smsContactItemLogList.get(i+1).getDateLog()
                        .equalsIgnoreCase(smsContactItemLogList.get(i).getDateLog())){
                    smsContactItemLogList.remove(i);
                }
            }
        }
        if (!smsContactItemLogList.isEmpty() && smsContactItemLogList.get(0).getHeader()==null){
            smsContactItemLogList.add(0,new SmsContactItemLog(smsContactItemLogList.get(0).getDateLog()));
        };
        int i=0;
        while (i<smsContactItemLogList.size()-1){
            if (!smsContactItemLogList.get(i).getDateLog()
                    .equalsIgnoreCase(smsContactItemLogList.get(i+1).getDateLog())
                    && smsContactItemLogList.get(i).getHeader()==null &&
                    smsContactItemLogList.get(i+1).getHeader()==null){
                smsContactItemLogList.add(i+1,new SmsContactItemLog(
                        smsContactItemLogList.get(i+1).getDateLog()));
            }
            if(smsContactItemLogList.get(i).getHeader()!=null && smsContactItemLogList.get(i+1)
                    .getHeader()!=null){
                smsContactItemLogList.remove(i);
            }
            i++;
        }
    }

    public void showRecyclerView(ViewGroup container, View view){
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(container.getContext(),
                LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(container.getContext(), 0));
        smsLogContactAdapter = new SmsLogContactAdapter(smsContactItemLogList, container.getContext(), view);

        recyclerView.setAdapter(smsLogContactAdapter);
        smsLogContactAdapter.notifyDataSetChanged();
    }
    public static void writeLogs(){
            try {
                FileOutputStream fileOut = (FileOutputStream) MainActivity.getContextOfApplication()
                        .openFileOutput(SMS_BLOCK_LOG,Context.MODE_PRIVATE);
                ObjectOutputStream outputStream = new ObjectOutputStream(fileOut);
                for (int i=smsContactItemLogList.size()-1;i>=0;i--){
                    outputStream.writeObject(smsContactItemLogList.get(i));
                }
                outputStream.close();
                fileOut.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private void readLogs(){

                try {
                    FileInputStream fileIn = getContext().openFileInput(SMS_BLOCK_LOG);
                    ObjectInputStream inputStream = new ObjectInputStream(fileIn);

                    SmsContactItemLog itemLog;
                    smsContactItemLogList.clear();

                    while ((itemLog = (SmsContactItemLog) inputStream.readObject())!= null){
                        addList(itemLog);
                        checkLogs();
                    }

                    fileIn.close();
                    inputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    checkLogs();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        this.context=context;
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        //context.unregisterReceiver(broadcastReceiver);
        super.onDestroyView();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
