package com.dgteam.callblocker;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements BlackList.OnFragmentInteractionListener,
        BlockLogs.OnFragmentInteractionListener,
        SmsBlackList.OnFragmentInteractionListener,
        SmsLogs.OnFragmentInteractionListener{



    private SmartTabLayout tabLayout;
    private ViewPager viewPager;
    private BlackList blackList;
    private BlockLogs blockLogs;
    private SmsBlackList smsBlackList;
    private SmsLogs smsLogs;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private VPAdapter adapter;
    private NavigationView navigationView;

    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView)findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);


        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        tabLayout = (SmartTabLayout) findViewById(R.id.tabLayout1);
        viewPager = (ViewPager) findViewById(R.id.viewPager1);

        setViewPager();
        tabLayout.setViewPager(viewPager);

        setTitle("Chặn cuộc gọi");

        tabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0: {
                        setTitle("Chặn Cuộc Gọi");
                        break;
                    }
                    case 1: {
                        setTitle("Chặn Cuộc Gọi");
                        break;
                    }
                    case 2: {
                        setTitle("Chặn Tin Nhắn");
                        break;
                    }
                    case 3: {
                        setTitle("Chặn Tin Nhắn");
                        break;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.callBlock:
                        viewPager.setCurrentItem(0);
                        drawer.closeDrawers();
                        break;

                    case R.id.smsBlock:
                        viewPager.setCurrentItem(2);
                        drawer.closeDrawers();
                        break;

                    case R.id.settings:
                        Intent intentSetting = new Intent(MainActivity.this,Settings.class);
                        startActivity(intentSetting);
                        break;
                    case R.id.about:
                        Intent intentAbout = new Intent(MainActivity.this,About.class);
                        startActivity(intentAbout);
                        break;
                    case R.id.smsActivity:
                        Intent intentSms = new Intent(MainActivity.this,SmsActivity.class);
                        startActivity(intentSms);
                        break;
                }
                return true;
            }
        });


        contextOfApplication = getApplicationContext();
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,


        };


        //Yêu cầu quyền đọc danh bạ
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!hasPermissions(this, PERMISSIONS)) {

                new AlertDialog.Builder(this,R.style.AlertDialogStyle)
                    .setIcon(R.drawable.warning_amber)
                    .setTitle("Cảnh báo!")
                    .setMessage("Ứng dụng sẽ không hoạt động nếu bạn không cấp quyền." +
                            " Việc cấp quyền này là an toàn.")
                    .setPositiveButton("Đồng ý", (dialogInterface, i) ->{
                        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
                    })
                    .setNegativeButton("Từ chối", (d,i) -> dialogQuit())
                    .setCancelable(false)
                    .show();
            }

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if(!Telephony.Sms.getDefaultSmsPackage(getApplicationContext()).equals(getApplicationContext().getPackageName())) {
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                        getApplicationContext().getPackageName());
                startActivity(intent);
            }
        }
    }




    public void dataChanged() {
        viewPager.getAdapter().notifyDataSetChanged();
    }

    // Thoát ứng dụng
    public void dialogQuit(){
        new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogStyle)
        .setTitle("Đóng ứng dụng")
        .setIcon(R.drawable.warning_amber)
        .setMessage("Ứng dụng sẽ đóng!")
        .setPositiveButton("Thoát", (dialogInterface, i) -> {
            finish();
            System.exit(0);
        })
        .setCancelable(false)
        .show();
    }

    //Kiểm tra quyền trên android 6.0+
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    //Kiểm tra xem có chấp nhận quyền không
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0){
                    for (int i: grantResults){
                        if(i == PackageManager.PERMISSION_DENIED){
                            dialogQuit();
                            break;
                        }
                    }
                } else {
                    dialogQuit();
                }

            }
        }
    }

    //Tạo ViewPager
    private void setViewPager(){

        adapter = new VPAdapter(getSupportFragmentManager());
        blackList = new BlackList();
        blockLogs = new BlockLogs();
        smsBlackList = new SmsBlackList();
        smsLogs = new SmsLogs();
        adapter.add(blackList,"Danh sách đen cuộc gọi");
        adapter.add(blockLogs,"Nhật kí chặn cuộc gọi");
        adapter.add(smsBlackList,"Danh sách đen tin nhắn");
        adapter.add(smsLogs,"Nhật kí chặn tin nhắn");
        viewPager.setAdapter(adapter);

    }


    //Gọi phương thức onActivityResult trong fragment BlackList
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        adapter.getItem(0).onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //getMenuInflater().inflate(R.menu.menu_setting, menu);

        return super.onCreateOptionsMenu(menu);
    }

}
