package com.dgteam.callblocker;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Switch;

public class Settings extends AppCompatActivity {

    private Toolbar toolbar;
    private Switch swCall, swSms;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar) findViewById(R.id.toolbarSetting);
        swCall = (Switch) findViewById(R.id.switchCall);
        swSms = (Switch) findViewById(R.id.switchSms);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        setTitle(getResources().getString(R.string.setting));

        preferences = getSharedPreferences("settings",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        loadPre();
        swCall.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                editor.putBoolean("call",true);
                editor.commit();
            }else {
                editor.putBoolean("call",false);
                editor.commit();
            }
        });
        swSms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                editor.putBoolean("sms",true);
                editor.commit();
            }else {
                editor.putBoolean("sms",false);
                editor.commit();
            }
        });
    }
    private void loadPre(){
        if (preferences.getBoolean("call",true)){
            swCall.setChecked(true);
        }else {
            swCall.setChecked(false);
        }
        if (preferences.getBoolean("sms",true)){
            swSms.setChecked(true);
        }else {
            swSms.setChecked(false);
        }

    }
}
