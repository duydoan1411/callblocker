package com.dgteam.callblocker;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SmsActivity extends AppCompatActivity {

    //private List<String> lstSms = new ArrayList<String>();
    private List<String> lstAdd = new ArrayList<String>();
    private List<ContactItem> contactItemList = new ArrayList<ContactItem>();
    private List<ContactItem> contactList = new ArrayList<ContactItem>();


    private RecyclerView recyclerView;
    private SmsContactAdapter contactAdapter;
    private Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver;

    class ReadContactTask extends AsyncTask<Void, Void, Void>{
        private ProgressDialog dialog;
        public ReadContactTask(SmsActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Đang tải tin nhắn! Vui lòng đợi!");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getContactList();
            getAllSmsFromProvider();
            for (int i=0; i<lstAdd.size();i++) {
                String numberAdd = lstAdd.get(i);
                numberAdd = numberAdd.length() >= 10 ? numberAdd.substring(numberAdd.length() - 9) : numberAdd;
                boolean kt = true;
                if (contactItemList!=null) {
                    for (ContactItem j : contactItemList) {
                        String numberItem = j.getNumber();
                        numberItem = numberItem.length() >= 10 ? numberItem.substring(numberItem.length() - 9) : numberItem;
                        if(numberAdd.equalsIgnoreCase(numberItem)){
                            kt = false;
                            break;
                        }
                    }
                    if (kt){
                        contactItemList.add(new ContactItem(null,lstAdd.get(i),lstAdd.get(i),BitmapFactory.decodeResource(getResources(),R.drawable.avatar)));
                    }
                }
            } //Lấy được những số từ tin nhắn, không bị trùng (chỉ có số)
            for (ContactItem i: contactItemList){
                String numberItem = i.getNumber();
                numberItem = numberItem.length() >= 10 ? numberItem.substring(numberItem.length() - 9) : numberItem;
                for (ContactItem j: contactList){
                    String numberAdd = j.getNumber();
                    numberAdd = numberAdd.length() >= 10 ? numberAdd.substring(numberAdd.length() - 9) : numberAdd;
                    if (numberAdd.equalsIgnoreCase(numberItem)){
                        i.setName(j.getName());
                        i.setId(j.getId());
                        i.setAvatar(j.getAvatar());
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            contactAdapter.notifyDataSetChanged();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
    class ReloadSmsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            getAllSmsFromProvider();
            for (int i=0; i<lstAdd.size();i++) {
                String numberAdd = lstAdd.get(i);
                numberAdd = numberAdd.length() >= 10 ? numberAdd.substring(numberAdd.length() - 9) : numberAdd;
                boolean kt = true;
                if (contactItemList!=null) {
                    for (ContactItem j : contactItemList) {
                        String numberItem = j.getNumber();
                        numberItem = numberItem.length() >= 10 ? numberItem.substring(numberItem.length() - 9) : numberItem;
                        if(numberAdd.equalsIgnoreCase(numberItem)){
                            kt = false;
                            break;
                        }
                    }
                    if (kt){
                        contactItemList.add(new ContactItem(null,lstAdd.get(i),lstAdd.get(i),BitmapFactory.decodeResource(getResources(),R.drawable.avatar)));
                    }
                }
            }
            for (ContactItem i : contactItemList) {
                String numberItem = i.getNumber();
                numberItem = numberItem.length() >= 10 ? numberItem.substring(numberItem.length() - 9) : numberItem;
                for (ContactItem j : contactList) {
                    String numberAdd = j.getNumber();
                    numberAdd = numberAdd.length() >= 10 ? numberAdd.substring(numberAdd.length() - 9) : numberAdd;
                    if (numberAdd.equalsIgnoreCase(numberItem)) {
                        i.setName(j.getName());
                        i.setId(j.getId());
                        i.setAvatar(j.getAvatar());
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            contactAdapter.notifyDataSetChanged();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        toolbar = (Toolbar) findViewById(R.id.toolbarSmsActivity);
        getAllSmsFromProvider();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Tin nhắn");
        toolbar.setNavigationOnClickListener(v -> finish());
        recyclerView = (RecyclerView)findViewById(R.id.recyclerSmsActivity);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        new ReadContactTask(this).execute();
        recyclerView.setLayoutManager(layoutManager);

        contactAdapter = new SmsContactAdapter(contactItemList,R.layout.contact_adapter,getApplicationContext());
        recyclerView.setAdapter(contactAdapter);
        contactAdapter.notifyDataSetChanged();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                    if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                        new ReloadSmsTask().execute();
                    }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    }

    public void getAllSmsFromProvider() {

        ContentResolver cr = getContentResolver();

        Cursor number = cr.query(Telephony.Sms.Inbox.CONTENT_URI, // Official CONTENT_URI from docs
                new String[] { Telephony.Sms.Inbox.ADDRESS }, // Select body text
                null,
                null,
                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER); // Default sort order

        int totalAdd = number.getCount();
        lstAdd.clear();

        if (number.moveToFirst()) {
            for (int i = 0; i < totalAdd; i++) {
                lstAdd.add(number.getString(0));
                number.moveToNext();
            }
        }
        number.close();

//        Cursor sms = cr.query(Telephony.Sms.Inbox.CONTENT_URI,
//                new String[] { Telephony.Sms.Inbox.BODY },
//                null,
//                null,
//                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);
//
//        int totalSMS = sms.getCount();
//        lstSms.clear();
//
//        if (sms.moveToFirst()) {
//            for (int i = 0; i < totalSMS; i++) {
//                lstSms.add(sms.getString(0));
//                sms.moveToNext();
//            }
//        }
//        sms.close();
    }
    private void getContactList() {
        contactList.clear();
        ContentResolver cr = getContentResolver();
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
                        InputStream inputPhoto =openPhoto(Long.parseLong(id));
                        Bitmap photo;
                        photo = inputPhoto != null ? getCroppedBitmap(BitmapFactory.decodeStream(inputPhoto)) :
                                BitmapFactory.decodeResource(getResources(),R.drawable.avatar);
                        contactList.add(new ContactItem(id,name,phoneNo,photo));
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
    }
    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
    private InputStream openPhoto(long contactId) {
        Context applicationContext = MainActivity.getContextOfApplication();
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = applicationContext.getContentResolver().query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
