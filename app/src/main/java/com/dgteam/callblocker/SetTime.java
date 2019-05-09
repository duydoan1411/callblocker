package com.dgteam.callblocker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

public class SetTime extends AppCompatActivity {

    private Toolbar toolbar;
    private Button btFrom, btTo, btSet;
    private TextView tvFrom, tvTo;
    private Switch aSwitch;
    private Boolean checkTimeBlock;
    private int beginTimeHour=0, beginTimeMinute=0, endTimeHour=0, endTimeMinute=0, dem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_time);
        dem=0;
        toolbar = (Toolbar) findViewById(R.id.toolbarSetting);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        setTitle(getResources().getString(R.string.settime));

        btFrom = (Button) findViewById(R.id.btFrom);
        btTo = (Button)findViewById(R.id.btTo);
        btSet = (Button)findViewById(R.id.btSet);
        tvFrom = (TextView)findViewById(R.id.tvFrom);
        tvTo = (TextView)findViewById(R.id.tvTo);
        aSwitch = (Switch)findViewById(R.id.swOnOff);

        Bundle bundle = getIntent().getExtras();
        beginTimeHour = bundle.getInt("beginTimeHour");
        beginTimeMinute = bundle.getInt("beginTimeMinute");
        endTimeHour = bundle.getInt("endTimeHour");
        endTimeMinute = bundle.getInt("endTimeMinute");
        checkTimeBlock = bundle.getBoolean("OnOff",false);


        tvFrom.setText(beginTimeHour+":"+beginTimeMinute);
        tvTo.setText(endTimeHour+":"+endTimeMinute);
        aSwitch.setChecked(checkTimeBlock);

        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkTimeBlock=isChecked;
        });

        btFrom.setOnClickListener(v -> {
            BlurPopupWindow dialog = new BlurPopupWindow.Builder(this)
                    .setContentView(R.layout.timer_layout)
                    .setGravity(Gravity.CENTER)
                    .setScaleRatio(0.2f)
                    .setBlurRadius(15)
                    .setTintColor(0x30000000)
                    .build();

            Button btSetTime = (Button)dialog.findViewById(R.id.btSetTime);
            TimePicker timePicker = (TimePicker)dialog.findViewById(R.id.timePicker);
            timePicker.setIs24HourView(true);


            btSetTime.setOnClickListener(v1 -> {
                boolean kt = true;
                if (dem>0){
                    if (timePicker.getHour()>endTimeHour) kt = false;
                    else if (timePicker.getHour()==endTimeHour && timePicker.getMinute()>endTimeMinute) kt = false;
                }
                if (kt) {
                    beginTimeHour = timePicker.getHour();
                    beginTimeMinute = timePicker.getMinute();
                    tvFrom.setText(timePicker.getHour() + ":" + timePicker.getMinute());
                    dem++;
                    dialog.dismiss();
                }else Toast.makeText(this, "Vui lòng nhập thời gian bắt đầu bé hơn " +
                        "thời gian kết thúc là "+ endTimeHour+":"+endTimeMinute,Toast.LENGTH_SHORT).show();
            });
            dialog.show();
        });
        btTo.setOnClickListener(v1 -> {
            BlurPopupWindow dialog = new BlurPopupWindow.Builder(this)
                    .setContentView(R.layout.timer_layout)
                    .setGravity(Gravity.CENTER)
                    .setScaleRatio(0.2f)
                    .setBlurRadius(15)
                    .setTintColor(0x30000000)
                    .build();

            Button btSetTime = (Button)dialog.findViewById(R.id.btSetTime);
            TimePicker timePicker = (TimePicker)dialog.findViewById(R.id.timePicker);
            timePicker.setIs24HourView(true);

            btSetTime.setOnClickListener(v2 -> {
                boolean kt = true;
                if (timePicker.getHour()<beginTimeHour) kt=false;
                else if(timePicker.getHour()==beginTimeHour && timePicker.getMinute()<beginTimeMinute)
                    kt=false;
                if (kt) {
                    endTimeHour = timePicker.getHour();
                    endTimeMinute = timePicker.getMinute();
                    tvTo.setText(timePicker.getHour()+":"+timePicker.getMinute());
                    dem++;
                    dialog.dismiss();
                }else Toast.makeText(this, "Vui lòng nhập thời gian kết thức lơn hơn " +
                        "thời gian bắt đầu là "+beginTimeHour+":"+beginTimeMinute,Toast.LENGTH_SHORT).show();
            });
            dialog.show();
        });

        btSet.setOnClickListener(v -> {
            boolean kt = true;
            if (endTimeHour<beginTimeHour) kt=false;
            else if(endTimeHour==beginTimeHour && endTimeMinute<beginTimeMinute)
                kt=false;
            if (kt) {
                Bundle bundleSetTime = new Bundle();
                bundleSetTime.putInt("beginTimeHour", beginTimeHour);
                bundleSetTime.putInt("beginTimeMinute", beginTimeMinute);
                bundleSetTime.putInt("endTimeHour", endTimeHour);
                bundleSetTime.putInt("endTimeMinute", endTimeMinute);
                bundleSetTime.putBoolean("OnOff",checkTimeBlock);
                BlackList.setTime(bundle.getInt("i"), bundleSetTime);
                finish();
            }
            else Toast.makeText(this,"Nhập thời gian bắt đầu nhỏ hơn thời gian kết thúc",Toast.LENGTH_SHORT).show();
        });
    }
}
