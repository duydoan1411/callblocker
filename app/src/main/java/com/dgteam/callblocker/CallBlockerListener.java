package com.dgteam.callblocker;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.BlockedNumberContract;
import android.support.v4.app.ActivityCompat;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;


public class CallBlockerListener extends BroadcastReceiver {

    private static final String blackList = "black_list.dat";
    private ArrayList<ContactItem> contactList;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) != null)
            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                readContact(context);
                String numberIncoming = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.d("aaa", "onReceive: " + numberIncoming);
                numberIncoming = numberIncoming.length() >= 10 ?
                        numberIncoming.substring(numberIncoming.length() - 9) : numberIncoming;
                for (ContactItem i : contactList) {
                    String numberBL = i.getNumber().length() >= 10 ?
                            i.getNumber().substring(i.getNumber().length() - 9) : i.getNumber();
                    Log.d("aaa", "onReceive: " + numberBL + " " + numberIncoming);
                    if (numberBL.equals(numberIncoming)) {

                        ITelephony telephonyService;
                            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

                            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)){
                                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                                try {
                                    Method m = tm.getClass().getDeclaredMethod("getITelephony");

                                    m.setAccessible(true);
                                    telephonyService = (ITelephony) m.invoke(tm);

                                    if ((number != null)) {
                                        telephonyService.endCall();
                                        Toast.makeText(context, "Ending the call from: " + number, Toast.LENGTH_SHORT).show();
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

                                Toast.makeText(context, "Ring " + number, Toast.LENGTH_SHORT).show();

                            }
                            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                                Toast.makeText(context, "Answered " + number, Toast.LENGTH_SHORT).show();
                            }
                            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)){
                                Toast.makeText(context, "Idle "+ number, Toast.LENGTH_SHORT).show();
                            }

                            break;
                    }
                }
            }
    }

    private void readContact(Context context){
        try {
            FileInputStream fileIn = context.openFileInput(blackList);
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
