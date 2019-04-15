package com.dgteam.callblocker;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SmsForNumberActivity extends AppCompatActivity {

    private List<String> lstAdd = new ArrayList<String>();
    private List<String> lstSms = new ArrayList<String>();
    private List<String> lstDate = new ArrayList<String>();
    private List<String> messagesList = new ArrayList<String>();
    private Toolbar toolbar;
    private String number;
    private SmsForNumberAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_for_number);
        toolbar = (Toolbar)findViewById(R.id.toolbarSmsForNumber);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        recyclerView = (RecyclerView)findViewById(R.id.recyclerSmsForNumber);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        getAllSmsFromProvider();

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        setTitle(bundle.getString("name"));
        number = bundle.getString("number");
        Log.d("aaa", "onCreate: "+intent.getStringExtra("123"));
        number = number.length() >= 10 ? number.substring(number.length() - 9) : number;
        Log.d("aaa", "onCreate: "+intent.getStringExtra("name")+" "+intent.getStringExtra("number"));

        if(lstAdd!=null) {
            messagesList.clear();
            for (int i = 0; i < lstAdd.size(); i++) {
                String number1 = lstAdd.get(i).length() >= 10 ?
                        lstAdd.get(i).substring(lstAdd.get(i).length() - 9) : lstAdd.get(i);
                if (number1.equalsIgnoreCase(number)) {
                    messagesList.add(lstSms.get(i));
                }
            }
        }
        adapter = new SmsForNumberAdapter(messagesList);
        recyclerView.setAdapter(adapter);

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

        Cursor sms = cr.query(Telephony.Sms.Inbox.CONTENT_URI,
                new String[] { Telephony.Sms.Inbox.BODY },
                null,
                null,
                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);

        int totalSMS = sms.getCount();
        lstSms.clear();

        if (sms.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                lstSms.add(sms.getString(0));
                sms.moveToNext();
            }
        }
        sms.close();

        Cursor date = cr.query(Telephony.Sms.Inbox.CONTENT_URI, // Official CONTENT_URI from docs
                new String[] { Telephony.Sms.Inbox.DATE }, // Select body text
                null,
                null,
                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER); // Default sort order

        int totalDate = date.getCount();
        lstDate.clear();

        if (date.moveToFirst()) {
            for (int i = 0; i < totalDate; i++) {
                lstDate.add(date.getString(0));
                date.moveToNext();
            }
        }
        date.close();
    }
}
