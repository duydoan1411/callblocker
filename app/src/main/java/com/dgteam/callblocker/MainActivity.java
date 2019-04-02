package com.dgteam.callblocker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabIndicationInterpolator;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

public class MainActivity extends AppCompatActivity implements BlackList.OnFragmentInteractionListener, BlockLogs.OnFragmentInteractionListener{



    private SmartTabLayout tabLayout;
    private ViewPager viewPager;
    private BlackList blackList;
    private BlockLogs blockLogs;
    private Toolbar toolbar;
    private DrawerLayout drawer;
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);

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

       // tabLayout.setupWithViewPager(viewPager);
//        LinearLayout tabLayout1 = (LinearLayout)((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0);
//        TextView tabTextView = (TextView) tabLayout1.getChildAt(1);
//        tabTextView.setTypeface(tabTextView.getTypeface(), Typeface.BOLD);
//        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                LinearLayout tabLayout1 = (LinearLayout)((ViewGroup) tabLayout.getChildAt(0)).getChildAt(tab.getPosition());
//                TextView tabTextView = (TextView) tabLayout1.getChildAt(1);
//                tabTextView.setTypeface(tabTextView.getTypeface(), Typeface.BOLD);
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                LinearLayout tabLayout1 = (LinearLayout)((ViewGroup) tabLayout.getChildAt(0)).getChildAt(tab.getPosition());
//                TextView tabTextView = (TextView) tabLayout1.getChildAt(1);
//                tabTextView.setTypeface(null, Typeface.NORMAL);
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//        tabLayout.setSelectedTabIndicator(R.drawable.indicator);
//        tabLayout.getTabAt(0).setIcon(R.drawable.locklogs_icon);
//        tabLayout.getTabAt(1).setIcon(R.drawable.blacklist_icon);
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
    }
    public void dataChanged() {
        viewPager.getAdapter().notifyDataSetChanged();
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
