package com.dgteam.callblocker;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

//Fragment
public class BlackList extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int REQUEST_SELECT_CONTACT = 1;
    private static final String blackList = "black_list.dat";

    private RecyclerView recyclerView;
    private FloatingActionButton btAdd, fabContact, fabNumber;
    private ArrayList<ContactItem> contactList = new ArrayList<>();
    private ContactAdapter contactAdapter;

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
        readContact();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ///Ánh xạ các view
        View view = inflater.inflate(R.layout.fragment_black_list,container,false);
        recyclerView = view.findViewById(R.id.recyclerViewBalckList);
        btAdd = view.findViewById(R.id.floatingActionButton4);
        fabContact = view.findViewById(R.id.fabPersonAdd);
        fabNumber = view.findViewById(R.id.fabNumber);

        // Ẩn hiện 2 floating action button
        boolean[] kt = {true};
        btAdd.setImageResource(R.drawable.add);
        btAdd.setOnClickListener(view1 -> {
            kt[0] =!kt[0];
            if (kt[0]) {
                btAdd.setImageResource(R.drawable.add);
                an();
            }
            else {
                btAdd.setImageResource(R.drawable.clear);
                hien();
            }
        });
        fabContact.setOnClickListener(view1 -> selectContact());
        fabNumber.setOnClickListener(view1 -> buttonAddNumber());
        showRecyclerView(container);
        if (contactList.size()==7) Toast.makeText(getContext(),contactList.get(0).getName(),Toast.LENGTH_SHORT).show();
        return view;

    }

    //Hiện 2 floating action button
    private void hien(){
        fabNumber.show();
        fabContact.show();
    }

    //Ẩn 2 floating action button
    private void an(){
        fabContact.hide();
        fabNumber.hide();
    }

    //Tạo 1 dialog để nhập số trực tiếp từ bàn phím
    private void buttonAddNumber(){
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_number);

        EditText etNumber = (EditText) dialog.findViewById(R.id.etNumber);
        Button btAdd = (Button) dialog.findViewById(R.id.btAdd);



        dialog.getWindow().setLayout(1300,850);


        btAdd.setOnClickListener(view2 -> {
            if (!etNumber.getText().toString().equals("")){
                if (!isExistContact(etNumber.getText().toString())){
                    contactList.add(new ContactItem(null,"No Name",etNumber.getText().toString(),
                            BitmapFactory.decodeResource(getResources(),R.drawable.avatar)));
                    contactAdapter.notifyDataSetChanged();
                    writeContact();
                    dialog.dismiss();
                }else Toast.makeText(getContext(),"Số điện thoại đã tồn tại", Toast.LENGTH_SHORT).show();
            }else Toast.makeText(getContext(),"Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
        });
        dialog.show();
    }

    //Xử lý RecyclerView
    public void showRecyclerView(ViewGroup container){
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(container.getContext(),
                LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(container.getContext(),layoutManager.getOrientation()));
        contactAdapter = new ContactAdapter(contactList,R.layout.contact_adapter,
                container.getContext());
        recyclerView.setAdapter(contactAdapter);
    }

    //Chọn 1 số từ danh bạ
    //Sau đó trả về 1 Intent cho MainActivity
    //Từ phương thức onActivityResult trong MainActivity
    //Gọi trực tiếp phương thức onActivityResult trong fragment này
    public void selectContact() {

        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_CONTACT);
        }
    }


    //Được gọi từ onActivityResult trong MainActivity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Context applicationContext = MainActivity.getContextOfApplication();

        //Lấy ID, tên và số điện thoại từ danh bạ
        if (requestCode == REQUEST_SELECT_CONTACT && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = applicationContext.getContentResolver().query(contactUri, null, null,
                    null, null);
            if (cursor.moveToFirst()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));


                if (hasPhone.equalsIgnoreCase("1")) {
                    Cursor phone = applicationContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                            null, null);

                    phone.moveToFirst();
                    String number = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA))
                            .replaceAll("\\D+","");
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                            .replaceAll("\n"," ");

                    InputStream inputPhoto =openPhoto(cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
                    Bitmap photo;
                    photo = inputPhoto != null ? getCroppedBitmap(BitmapFactory.decodeStream(inputPhoto)) :
                             BitmapFactory.decodeResource(getResources(),R.drawable.avatar);


                    if (!isExistContact(number)) {
                     contactList.add(0,new ContactItem(id, name, number, photo));
                     contactAdapter.notifyDataSetChanged();
                     writeContact();

                    }else
                        Toast.makeText(getContext(),"Số điện thoại đã tồn tại",Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(getContext(),"Đối tượng không có số điện thoại",Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void writeContact(){
        try {
            FileOutputStream fileOut = (FileOutputStream) MainActivity.getContextOfApplication()
                    .openFileOutput(blackList,Context.MODE_PRIVATE);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOut);
            for(ContactItem i: contactList)
                outputStream.writeObject(i);
            outputStream.close();
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readContact(){
        try {
            FileInputStream fileIn = MainActivity.getContextOfApplication()
                    .openFileInput(blackList);
            ObjectInputStream inputStream = new ObjectInputStream(fileIn);

            ContactItem contact;
            contactList = new ArrayList<ContactItem>();

            while ((contact = (ContactItem) inputStream.readObject())!= null){
                contactList.add(contact);
                Log.d("aaa", "readContact: "+contact.toString());
            }

            fileIn.close();
            inputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Kiểm tra số đã tồn tại trong blacklist hay chưa
    private boolean isExistContact(String number){

        boolean check = false;
        for (ContactItem item: contactList)
            if (item.getNumber().equals(number)){
                check = true;
                break;
            }
        return check;
    }

    //Cắt hình từ vuông thành tròn
    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    //Lấy hình từ danh bạ thông qua ID
    private InputStream openPhoto(long contactId) {
        Context applicationContext = MainActivity.getContextOfApplication();
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = applicationContext.getContentResolver().query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
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
