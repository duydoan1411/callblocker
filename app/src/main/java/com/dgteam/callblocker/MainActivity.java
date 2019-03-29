package com.dgteam.callblocker;

import android.Manifest;
import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BlackList.OnFragmentInteractionListener, BlockLogs.OnFragmentInteractionListener{



    private TabLayout tabLayout;
    private ViewPager viewPager;
    private BlackList blackList;
    private BlockLogs blockLogs;
    private VPAdapter adapter;


    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout1);
        viewPager = (ViewPager) findViewById(R.id.viewPager1);

        setViewPager();
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.locklogs_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.blacklist_icon);
        setTitle("Chặn cuộc gọi");




        contextOfApplication = getApplicationContext();
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.CALL_PHONE
        };


        //Yêu cầu quyền đọc danh bạ
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!hasPermissions(this, PERMISSIONS)) {

                new AlertDialog.Builder(this)
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
    }


    // Thoát ứng dụng
    public void dialogQuit(){
        new AlertDialog.Builder(MainActivity.this)
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
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0){
                    for (int i: grantResults){
                        if(i == PackageManager.PERMISSION_DENIED){
                            dialogQuit();
                            break;
                        }
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
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
        adapter.add(blackList,"Danh sách đen");
        adapter.add(blockLogs,"Nhật kí chặn");
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
