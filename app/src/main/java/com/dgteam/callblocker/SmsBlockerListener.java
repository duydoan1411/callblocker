package com.dgteam.callblocker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SmsBlockerListener extends BroadcastReceiver {

    private static final String SMS_BLOCK_LOG = "sms_block_logs.dat";
    private static final String BLACK_LIST_SMS = "sms_black_list.dat";
    private static final String SMS = "sms.dat";

    private Bundle bundle;
    private SmsMessage currentSMS;
    private String message;
    private Context context;

    private ArrayList<ContactItem> contactBlackList = new ArrayList<ContactItem>();
    private SharedPreferences preferences;

    private ArrayList<ContactItem> ContactList = new ArrayList<ContactItem>();

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        createNotificationChannel();
        preferences = context.getSharedPreferences("settings",Context.MODE_PRIVATE);
        //readSmsList();
        if (preferences.getBoolean("sms",true)){
            readBlackContact();
            bundle = intent.getExtras();
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                if (bundle != null) {
                    boolean kt = true;
                    String number = "";
                    Object[] pdus = (Object[])bundle.get("pdus");
                    final SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < pdus.length; i++) {
                        String format = bundle.getString("format");
                        messages[i] = SmsMessage.createFromPdu((byte[])pdus[i],format);
                        currentSMS = messages[i];
                    }
                    StringBuffer content = new StringBuffer();
                    if (messages.length > 0) {
                        for (int i = 0; i < messages.length; i++) {
                            content.append(messages[i].getMessageBody());
                        }
                    }
                    message = content.toString();
                        String numberIncoming = currentSMS.getDisplayOriginatingAddress();
                        number = numberIncoming;
                        numberIncoming = numberIncoming.length() >= 10 ?
                                numberIncoming.substring(numberIncoming.length() - 9) : numberIncoming;
                        if (!contactBlackList.isEmpty()) {
                            for (ContactItem i : contactBlackList) {
                                String numberBL = i.getNumber().length() >= 10 ?
                                        i.getNumber().substring(i.getNumber().length() - 9) : i.getNumber();
                                if (numberBL.equals(numberIncoming)) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                                        if (Telephony.Sms.getDefaultSmsPackage(context).equals(context.getPackageName()))
                                            this.abortBroadcast();
                                        else {
                                            Toast.makeText(context, "Bản phải chọn ứng dụng mặc định để chặn tin nhắn",
                                                    Toast.LENGTH_LONG).show();
                                        }

                                    SmsContactItemLog smsContactItemLog = new SmsContactItemLog(
                                            i.getId(), i.getName(), i.getNumber(), i.getAvatar(), message
                                    );
                                    writeContact(smsContactItemLog);
                                    kt = false;
                                    break;
                                }
                            }
                        }
                    if(kt){
                        class TaskReadContact extends AsyncTask<String, List<ContactItem>, String>{

                            @Override
                            protected String doInBackground(String... voids) {
                                getContactList();

                                return voids[0];
                            }

                            @Override
                            protected void onPostExecute(String aVoid) {
                                super.onPostExecute(aVoid);
                                ContactItem item = existContact(aVoid);
                                if (item!=null){
                                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
                                    managerCompat.notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE),
                                            notifyApp(item.getNumber(),item.getName(),message).build());
                                }else {
                                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
                                    managerCompat.notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE),
                                            notifyApp(aVoid,aVoid,message).build());
                                }
                            }
                        }
                        new TaskReadContact().execute(number);

                        ContentValues values = new ContentValues();
                        values.put(Telephony.Sms.ADDRESS, number);
                        values.put(Telephony.Sms.BODY, message);
                        context.getApplicationContext().getContentResolver().insert(Telephony.Sms.Inbox.CONTENT_URI, values);
                    }
                }
            }
        }
    }
    private ContactItem existContact(String number){
        number = number.length() >= 10 ? number.substring(number.length() - 9) : number;
        String numberI;
        for (ContactItem i: ContactList){
            numberI = i.getNumber();
            numberI = numberI.length() >= 10 ? numberI.substring(numberI.length() - 9) : numberI;
            if (number.equalsIgnoreCase(numberI)){
                return i;
            }
        }
        return null;
    }
    private void getContactList() {
        ContactList.clear();
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME)).replaceAll("\n"," ");;

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\D+","");
                        ContactList.add(new ContactItem(id,name,phoneNo,null));
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Callblocker";
            String description = "thong bao";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("123456", name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public NotificationCompat.Builder notifyApp(String number, String title, String body){
        Intent intent = new Intent(context,SmsForNumberActivity.class);
        Bundle bundle = new Bundle();
        bundle.clear();
        intent.removeExtra("123");
        bundle.putString("name", title);
        bundle.putString("number", number);

        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(context, "123456")
                .setSmallIcon(R.drawable.icon_sms)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setWhen(Calendar.getInstance().getTimeInMillis())
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setVibrate(new long[0])
                .setPriority(NotificationCompat.PRIORITY_HIGH);
    }

    private void writeContact(SmsContactItemLog contactItemLog){

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
//    private void  readSmsList(){
//        try {
//            FileInputStream fileIn = context.openFileInput(BLACK_LIST_SMS);
//            ObjectInputStream inputStream = new ObjectInputStream(fileIn);
//
//            Sms smsItem;
//            smsList.clear();
//
//            while ((smsItem = (Sms) inputStream.readObject())!= null){
//                smsList.add(smsItem);
//            }
//
//            fileIn.close();
//            inputStream.close();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
    private void readBlackContact(){
        try {
            FileInputStream fileIn = context.openFileInput(BLACK_LIST_SMS);
            ObjectInputStream inputStream = new ObjectInputStream(fileIn);

            ContactItem contact;
            contactBlackList.clear();

            while ((contact = (ContactItem) inputStream.readObject())!= null){
                contactBlackList.add(contact);
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
