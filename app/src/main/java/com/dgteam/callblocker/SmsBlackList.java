package com.dgteam.callblocker;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class SmsBlackList extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int REQUEST_SELECT_CONTACT = 1;
    private static final String BLACK_LIST_SMS = "sms_black_list.dat";

    private RecyclerView recyclerView;
    private FloatingActionButton btAdd, fabContact, fabNumber;
    protected static ArrayList<ContactItem> contactList = new ArrayList<ContactItem>();
    private ContactAdapter contactAdapter;

    private String mParam1;
    private String mParam2;
    private boolean[] kt = {false};

    private OnFragmentInteractionListener mListener;

    public SmsBlackList() {

    }


    public static SmsBlackList newInstance(String param1, String param2) {
        SmsBlackList fragment = new SmsBlackList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        readContact();
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ///Ánh xạ các view
        View view = inflater.inflate(R.layout.fragment_sms_black_list,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewSmsBalckList);
        btAdd = (FloatingActionButton) view.findViewById(R.id.floatingActionButton4Sms);
        fabContact = (FloatingActionButton) view.findViewById(R.id.fabPersonAddSms);
        fabNumber = (FloatingActionButton) view.findViewById(R.id.fabNumberSms);

        // Ẩn hiện 2 floating action button

        btAdd.setImageResource(R.drawable.add);
        btAdd.setOnClickListener(view1 -> {
            if (kt[0]) {
                an();
            }
            else {
                hien();
            }
        });
        fabContact.setOnClickListener(view1 -> selectContact());
        fabNumber.setOnClickListener(view1 -> buttonAddNumber());
        showRecyclerView(container,view);

        return view;

    }

    //Hiện 2 floating action button
    private void hien(){
        kt[0] =!kt[0];
        btAdd.setImageResource(R.drawable.clear);
        fabNumber.show();
        fabContact.show();
    }

    //Ẩn 2 floating action button
    private void an(){
        btAdd.setImageResource(R.drawable.add);
        kt[0] =!kt[0];
        fabContact.hide();
        fabNumber.hide();
    }

    //Tạo 1 dialog để nhập số trực tiếp từ bàn phím
    private void buttonAddNumber(){
        an();
        BlurPopupWindow dialog = new BlurPopupWindow.Builder(getContext())
                .setContentView(R.layout.add_number)
                .setGravity(Gravity.CENTER)
                .setScaleRatio(0.2f)
                .setBlurRadius(15)
                .setTintColor(0x30000000)
                .build();

        EditText etNumber = (EditText) dialog.findViewById(R.id.etNumber);
        Button btAdd = (Button) dialog.findViewById(R.id.btAdd);
        Button btCancel = (Button) dialog.findViewById(R.id.btCancel);

        btAdd.setOnClickListener(view2 -> {
            if (!etNumber.getText().toString().equals("")){
                if (!isExistContact(etNumber.getText().toString())){
                    contactList.add(0,new ContactItem(null,"No Name",etNumber.getText().toString(),
                            BitmapFactory.decodeResource(getResources(),R.drawable.avatar)));

                    writeContact();
                    contactAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }else Toast.makeText(getContext(),"Số điện thoại đã tồn tại", Toast.LENGTH_SHORT).show();
            }else Toast.makeText(getContext(),"Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
        });
        dialog.show();
        btCancel.setOnClickListener(v -> dialog.dismiss());

    }


    //Xử lý RecyclerView
    public void showRecyclerView(ViewGroup container, View view){
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(container.getContext(),
                LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(container.getContext(), 0));
        contactAdapter = new ContactAdapter(contactList,R.layout.contact_adapter,
                container.getContext(),view);

        recyclerView.setAdapter(contactAdapter);
    }

    //Chọn 1 số từ danh bạ
    //Sau đó trả về 1 Intent cho MainActivity
    //Từ phương thức onActivityResult trong MainActivity
    //Gọi trực tiếp phương thức onActivityResult trong fragment này
    public void selectContact() {
        an();
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
                        Snackbar.make(getView(),"Số điện thoại đã tồn tại",Snackbar.LENGTH_SHORT).show();
                    //Toast.makeText(getContext(),"Số điện thoại đã tồn tại",Toast.LENGTH_SHORT).show();
                }else
                    Snackbar.make(getView(),"Đối tượng không có số điện thoại",Snackbar.LENGTH_SHORT).show();
                //Toast.makeText(getContext(),"Đối tượng không có số điện thoại",Toast.LENGTH_SHORT).show();
            }
        }

    }

    public static void writeContact(){
        try {
            FileOutputStream fileOut = (FileOutputStream) MainActivity.getContextOfApplication()
                    .openFileOutput(BLACK_LIST_SMS,Context.MODE_PRIVATE);
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
            FileInputStream fileIn = getContext().openFileInput(BLACK_LIST_SMS);
            ObjectInputStream inputStream = new ObjectInputStream(fileIn);

            ContactItem contact;
            contactList.clear();

            while ((contact = (ContactItem) inputStream.readObject())!= null){
                contactList.add(contact);
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
        if (context instanceof SmsBlackList.OnFragmentInteractionListener) {
            mListener = (SmsBlackList.OnFragmentInteractionListener) context;
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
