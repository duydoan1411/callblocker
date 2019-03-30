package com.dgteam.callblocker;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


public class BlockLogs extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String BLOCK_LOG = "block_logs.dat";

    private RecyclerView recyclerView;
    private LogContactAdapter logContactAdapter;
    protected static List<ContactItemLog> contactItemLogList = new ArrayList<ContactItemLog>();
    private Context context;

    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;
    private BroadcastReceiver broadcastReceiver;

    public BlockLogs() {

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

        showRecyclerView(container);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).
                        equals(TelephonyManager.EXTRA_STATE_IDLE)){
                    readLogs();
                    Log.d("aaa", "onReceive: "+contactItemLogList.size());
                    logContactAdapter.notifyDataSetChanged();
                }
            }
        };
        context.registerReceiver(broadcastReceiver, new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED));

        return view;
    }

    public static void addList(ContactItemLog contactItemLog){
        if (contactItemLogList.isEmpty()) {
            contactItemLogList.add(0,contactItemLog);
            contactItemLogList.add(0,new ContactItemLog(contactItemLog.getDateLog()));

        }else {
            ContactItemLog contactItemLog1 = contactItemLogList.get(0);
            if (contactItemLog1.getDateLog().equalsIgnoreCase(contactItemLog.getDateLog())){
                contactItemLogList.add(1,contactItemLog);
            }else {
                contactItemLogList.add(0,contactItemLog);
                contactItemLogList.add(0,new ContactItemLog(contactItemLog.getDateLog()));
            }
        }
    }

    public static void checkLogs(){
        if (contactItemLogList.size()<=1){
            contactItemLogList.clear();
        }else {
            if (contactItemLogList.get(contactItemLogList.size()-1).getHeader()!=null){
                Log.d("aaa", "checkLogs: Xoa cuoi");
                contactItemLogList.remove(contactItemLogList.size()-1);
            }
            for (int i=0;i<contactItemLogList.size(); i++ ){
                if (contactItemLogList.get(i).getHeader()!=null
                        && !contactItemLogList.get(i+1).getDateLog()
                        .equalsIgnoreCase(contactItemLogList.get(i).getDateLog())){
                    contactItemLogList.remove(i);
                }
            }
        }
    }

    public void showRecyclerView(ViewGroup container){
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(container.getContext(),
                LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(container.getContext(), 0));
        logContactAdapter = new LogContactAdapter(contactItemLogList, container.getContext());

        recyclerView.setAdapter(logContactAdapter);
        logContactAdapter.notifyDataSetChanged();
    }
    public static void writeLogs(){
        try {
            FileOutputStream fileOut = (FileOutputStream) MainActivity.getContextOfApplication()
                    .openFileOutput(BLOCK_LOG,Context.MODE_PRIVATE);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOut);
            for(ContactItemLog i: contactItemLogList)
                outputStream.writeObject(i);
            outputStream.close();
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readLogs(){
        try {
            FileInputStream fileIn = MainActivity.getContextOfApplication()
                    .openFileInput(BLOCK_LOG);
            ObjectInputStream inputStream = new ObjectInputStream(fileIn);

            ContactItemLog itemLog;
            contactItemLogList.clear();

            while ((itemLog = (ContactItemLog) inputStream.readObject())!= null){
                addList(itemLog);
            }

            fileIn.close();
            inputStream.close();
            checkLogs();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
        context.unregisterReceiver(broadcastReceiver);
        super.onDestroyView();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
