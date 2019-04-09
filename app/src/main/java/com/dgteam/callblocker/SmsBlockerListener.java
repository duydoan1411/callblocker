package com.dgteam.callblocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class SmsBlockerListener extends BroadcastReceiver {

    private static final String SMS_BLOCK_LOG = "sms_block_logs.dat";
    private static final String BLACK_LIST_SMS = "sms_black_list.dat";

    private Bundle bundle;
    private SmsMessage currentSMS;
    private String message;

    private ArrayList<ContactItem> contactList = new ArrayList<ContactItem>();
    private SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        preferences = context.getSharedPreferences("settings",Context.MODE_PRIVATE);
        if (preferences.getBoolean("sms",true)){
            readContact(context);
            bundle = intent.getExtras();
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                if (!contactList.isEmpty()) {
                    if (bundle != null) {
                        Object[] pdu_Objects = (Object[]) bundle.get("pdus");
                        if (pdu_Objects != null) {

                            for (Object aObject : pdu_Objects) {
                                currentSMS = getIncomingMessage(aObject, bundle);
                                String numberIncoming = currentSMS.getDisplayOriginatingAddress();
                                numberIncoming = numberIncoming.length() >= 10 ?
                                        numberIncoming.substring(numberIncoming.length() - 9) : numberIncoming;
                                for (ContactItem i : contactList) {
                                    String numberBL = i.getNumber().length() >= 10 ?
                                            i.getNumber().substring(i.getNumber().length() - 9) : i.getNumber();
                                    if (numberBL.equals(numberIncoming)) {
                                        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2){
                                            this.abortBroadcast();
                                        }else {
                                            Toast.makeText(context,"Không thể chặn tin nhắn với bản Android 4.4 trở lên",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                        message = currentSMS.getDisplayMessageBody();
                                        SmsContactItemLog smsContactItemLog = new SmsContactItemLog(
                                              i.getId(),i.getName(), i.getNumber(),i.getAvatar(),message
                                        );
                                        writeContact(context,smsContactItemLog);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSMS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
        }
        return currentSMS;
    }
    public static void writeContact(Context context, SmsContactItemLog contactItemLog){

        ArrayList<SmsContactItemLog> contactListLog = new ArrayList<SmsContactItemLog>();

        try {
            FileInputStream fileIn = context.openFileInput(SMS_BLOCK_LOG);
            ObjectInputStream inputStream = new ObjectInputStream(fileIn);

            SmsContactItemLog contact;


            while ((contact = (SmsContactItemLog) inputStream.readObject()) != null) {
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
            FileOutputStream fileOut = context.openFileOutput(SMS_BLOCK_LOG,Context.MODE_PRIVATE);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOut);
            contactListLog.add(contactItemLog);
            for (SmsContactItemLog i: contactListLog){
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
            FileInputStream fileIn = context.openFileInput(BLACK_LIST_SMS);
            ObjectInputStream inputStream = new ObjectInputStream(fileIn);

            ContactItem contact;
            contactList.clear();

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
