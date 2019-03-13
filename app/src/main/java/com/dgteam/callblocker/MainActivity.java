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
    static final int REQUEST_SELECT_CONTACT = 1;
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

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setIcon(R.drawable.warning_amber);
                alert.setTitle("Cảnh báo!");
                alert.setMessage("Ứng dụng sẽ không hoạt động nếu bạn không cấp quyền." +
                        " Việc cấp quyền này là an toàn.");
                alert.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        permisson(Manifest.permission.READ_CONTACTS);
                    }
                });
                alert.setNegativeButton("Từ chối", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogQuit();
                    }
                });
                alert.setCancelable(false);
                alert.show();
            }
        }
    }

    public void dialogQuit(){
        AlertDialog.Builder alert2 = new AlertDialog.Builder(MainActivity.this);
        alert2.setTitle("Đóng ứng dụng");
        alert2.setIcon(R.drawable.warning_amber);
        alert2.setMessage("Ứng dụng sẽ đóng!");
        alert2.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                System.exit(0);
            }
        });
        alert2.setCancelable(false);
        alert2.show();
    }

    public void permisson(String permissonName){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(permissonName) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{permissonName}, 1);

            }
        }
    }

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

    private void setViewPager(){
        adapter = new VPAdapter(getSupportFragmentManager());
        blackList = new BlackList();
        blockLogs = new BlockLogs();
        adapter.add(blackList,"Blacklist");
        adapter.add(blockLogs,"Block Logs");
        viewPager.setAdapter(adapter);

    }



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
