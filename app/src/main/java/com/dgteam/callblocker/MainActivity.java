package com.dgteam.callblocker;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements BlackList.OnFragmentInteractionListener, BlockLogs.OnFragmentInteractionListener{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    //static final int REQUEST_SELECT_CONTACT = 1;
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

        contextOfApplication = getApplicationContext();


        //Yêu cầu quyền đọc danh bạ
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(this)
                .setIcon(R.drawable.warning_amber)
                .setTitle("Cảnh báo!")
                .setMessage("Ứng dụng sẽ không hoạt động nếu bạn không cấp quyền." +
                        " Việc cấp quyền này là an toàn.")
                .setPositiveButton("Đồng ý", (dialogInterface, i) ->
                        permisson(Manifest.permission.READ_CONTACTS))
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

    //Yêu cầu quyền trên android 6.0+
    public void permisson(String permissonName){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(permissonName) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{permissonName}, 1);

            }
        }
    }

    //Kiểm tra xem có chấp nhận quyền không
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                dialogQuit();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //Tạo ViewPager
    private void setViewPager(){
        adapter = new VPAdapter(getSupportFragmentManager());
        blackList = new BlackList();
        blockLogs = new BlockLogs();
        adapter.add(blackList,"Blacklist");
        adapter.add(blockLogs,"Block Logs");
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

        getMenuInflater().inflate(R.menu.menu_setting, menu);

        return super.onCreateOptionsMenu(menu);
    }
}
