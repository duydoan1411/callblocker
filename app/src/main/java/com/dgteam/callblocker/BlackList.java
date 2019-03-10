package com.dgteam.callblocker;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class BlackList extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int REQUEST_SELECT_CONTACT = 1;

    private RecyclerView recyclerView;
    private FloatingActionButton btAdd;
    //private TextView textView;

    private ArrayList<ContactItem> contactList;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public BlackList() {
    }



    public static BlackList newInstance(String param1, String param2) {
        BlackList fragment = new BlackList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //textView.setText("DemoNha");

    }

    public static void toastk(Context context, String string){
        Toast.makeText(context,string,Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_black_list,container,false);
        recyclerView = view.findViewById(R.id.recyclerViewBalckList);
        btAdd = view.findViewById(R.id.floatingActionButton4);
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectContact();
            }
        });
        //textView = view.findViewById(R.id.textView);
        showRecyclerView(container);

        return view;
    }

    public void showRecyclerView(ViewGroup container){
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(container.getContext(),
                LinearLayoutManager.VERTICAL,false);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(container.getContext(),layoutManager.getOrientation()));

        contactList = new ArrayList<>();

        contactList.add(new ContactItem("Duy", "013456498",R.drawable.avatar));
        contactList.add(new ContactItem("Trinh", "034564998",R.drawable.avatar));
        contactList.add(new ContactItem("Phuc", "034564998",R.drawable.avatar));
        contactList.add(new ContactItem("Thu", "034564998",R.drawable.avatar));
        contactList.add(new ContactItem("Mom", "034564998",R.drawable.avatar));
        contactList.add(new ContactItem("Duy", "013456498",R.drawable.avatar));
        contactList.add(new ContactItem("Trinh", "034564998",R.drawable.avatar));
        contactList.add(new ContactItem("Phuc", "034564998",R.drawable.avatar));
        contactList.add(new ContactItem("Thu", "034564998",R.drawable.avatar));
        contactList.add(new ContactItem("Mom", "034564998",R.drawable.avatar));
        contactList.add(new ContactItem("Duy", "013456498",R.drawable.avatar));
        contactList.add(new ContactItem("Trinh", "034564998",R.drawable.avatar));
        contactList.add(new ContactItem("Phuc", "034564998",R.drawable.avatar));
        contactList.add(new ContactItem("Thu", "034564998",R.drawable.avatar));
        contactList.add(new ContactItem("Mom", "034564998",R.drawable.avatar));
        contactList.add(new ContactItem("Duy", "013456498",R.drawable.avatar));
        contactList.add(new ContactItem("Trinh", "034564998",R.drawable.avatar));
        contactList.add(new ContactItem("Phuc", "034564998",R.drawable.avatar));
        contactList.add(new ContactItem("Thu", "034564998",R.drawable.avatar));
        contactList.add(new ContactItem("Mom", "034564998",R.drawable.avatar));


        ContactAdapter contactAdapter = new ContactAdapter(contactList,R.layout.contact_adapter,
                container.getContext());
        recyclerView.setAdapter(contactAdapter);
    }

    public void selectContact() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, 2);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Context applicationContext = MainActivity.getContextOfApplication();
        if (requestCode == 2 && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = applicationContext.getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            //textView.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))+"\n"+cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));

        }

    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
