package com.dgteam.callblocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class CallBlockerListener extends BroadcastReceiver {

    private static final String BLACK_LIST = "black_list.dat";
    private static final String BLOCK_LOG = "block_logs.dat";
    private SharedPreferences preferences;
    private ArrayList<ContactItem> contactList = new ArrayList<ContactItem>();
    ArrayList<ContactItemLog> contactListLog = new ArrayList<ContactItemLog>();


    @Override
    public void onReceive(Context context, Intent intent) {

        preferences = context.getSharedPreferences("settings",Context.MODE_PRIVATE);
        if (preferences.getBoolean("call",true))
        if (intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) != null)
            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                readContact(context);
                readLogs(context);
                if (!contactList.isEmpty()) {
                    String numberIncoming = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    numberIncoming = numberIncoming.length() >= 10 ?
                            numberIncoming.substring(numberIncoming.length() - 9) : numberIncoming;
                    for (ContactItem i : contactList) {
                        String numberBL = i.getNumber().length() >= 10 ?
                                i.getNumber().substring(i.getNumber().length() - 9) : i.getNumber();
                        if (numberBL.equals(numberIncoming)) {

                            ITelephony telephonyService;
                            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

                            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
                                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                                try {
                                    Method m = tm.getClass().getDeclaredMethod("getITelephony");

                                    m.setAccessible(true);
                                    telephonyService = (ITelephony) m.invoke(tm);

                                    if (checkTime(i)) {
                                        if (checkCount(context, i)) {
                                            ContactItemLog contactItemLog = new ContactItemLog(i.getId(), i.getName()
                                                    , i.getNumber(), i.getAvatar());
                                            writeContact(context, contactItemLog);
                                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
                                                telephonyService.endCall();
                                                Toast.makeText(context, "Đã chặn cuộc gọi từ: " + number, Toast.LENGTH_LONG).show();
                                            } else
                                                Toast.makeText(context, "Không thể chặn cuộc gọi với phiên bản Android lớn hơn 8.0", Toast.LENGTH_LONG)
                                                        .show();
                                        }
                                    }
                                } catch (NoSuchMethodException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        }
                    }
                }
            }
    }
    private void readLogs(Context context){
        try {
            FileInputStream fileIn = context.openFileInput(BLOCK_LOG);
            ObjectInputStream inputStream = new ObjectInputStream(fileIn);

            ContactItemLog contact;


            while ((contact = (ContactItemLog) inputStream.readObject()) != null) {
                contactListLog.add(contact);
            }
            fileIn.close();
            inputStream.close();
        }catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    private boolean checkTime(ContactItem item){

        if (!item.isCheckTimeBlock()) return true;

        Date date = new Date();
        int beginTimeHour = item.getBeginTimeHour();
        int beginTimeMinute = item.getBeginTimeMinute();
        int endTimeHour = item.getEndTimeHour();
        int endTimeMinute = item.getEndTimeMinute();
        int curHour = Integer.parseInt(new SimpleDateFormat("HH").format(date));
        int curMinute = Integer.parseInt(new SimpleDateFormat("mm").format(date));
        if (curHour*60+curMinute>=beginTimeHour*60+beginTimeMinute
                && curHour*60+curMinute<endTimeHour*60+endTimeMinute) return true;
        return false;
    }

    private boolean checkCount(Context context, ContactItem item){
        if (item.getCount()==0) return true;
        Date hourLog;
        int count = item.getCount();
        try {
            int dem=0;
            for (ContactItemLog i: contactListLog){
                String numberI = i.getNumber();
                numberI = numberI.length() >= 10 ?
                        numberI.substring(numberI.length() - 9) : numberI;
                String numberItem = i.getNumber();
                numberItem = numberItem.length() >= 10 ?
                        numberItem.substring(numberItem.length() - 9) : numberItem;
                if (numberI.equals(numberItem)) {
                    Date date = new Date();
                    hourLog = new SimpleDateFormat("HH:mm:ss").parse(i.getHourLog());
                    long diff = date.getTime() - hourLog.getTime();
                    long diffSeconds = diff / 1000 % 60;
                    if (diffSeconds <= 3600) {
                        dem++;
                    }
                }
            }
            if (dem<count) return true;
            else return false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void writeContact(Context context, ContactItemLog contactItemLog){

        ArrayList<ContactItemLog> contactListLog = new ArrayList<ContactItemLog>();

        try {
            FileInputStream fileIn = context.openFileInput(BLOCK_LOG);
            ObjectInputStream inputStream = new ObjectInputStream(fileIn);

            ContactItemLog contact;


            while ((contact = (ContactItemLog) inputStream.readObject()) != null) {
                contactListLog.add(contact);
            }


            fileIn.close();
            inputStream.close();
        }catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }

        try{
        FileOutputStream fileOut = context.openFileOutput(BLOCK_LOG,Context.MODE_PRIVATE);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOut);
            contactListLog.add(contactItemLog);
            for (ContactItemLog i: contactListLog){
                outputStream.writeObject(i);
            }

            outputStream.close();
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readContact(Context context){
        try {
            FileInputStream fileIn = context.openFileInput(BLACK_LIST);
            ObjectInputStream inputStream = new ObjectInputStream(fileIn);

            ContactItem contact;
            contactList = new ArrayList<ContactItem>();

            while ((contact = (ContactItem) inputStream.readObject())!= null){
                contactList.add(contact);
            }

            fileIn.close();
            inputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
