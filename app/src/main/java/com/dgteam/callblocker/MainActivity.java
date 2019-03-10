package com.dgteam.callblocker;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.util.Log;
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
    }


    private void setViewPager(){
        adapter = new VPAdapter(getSupportFragmentManager());
        blackList = new BlackList();
        blockLogs = new BlockLogs();
        adapter.add(blackList,"Blacklist");
        adapter.add(blockLogs,"Block Logs");
        viewPager.setAdapter(adapter);

    }




//    public void selectContact() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(intent, REQUEST_SELECT_CONTACT);
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        adapter.getItem(0).onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 2 && resultCode == RESULT_OK) {
//            Uri contactUri = data.getData();
//            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
//            cursor.moveToFirst();
//            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//
//            Log.d("phone number", cursor.getString(column));
//        }
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
