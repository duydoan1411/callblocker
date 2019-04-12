package com.dgteam.callblocker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class ContactItem implements Serializable {

    private String number, name, id;
    private byte[] avatar;

    public ContactItem(String id,String name, String number, Bitmap avatar) {
        this.id = id;
        this.number = number;
        this.name = name;
        if(avatar!=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            avatar.compress(Bitmap.CompressFormat.PNG, 100, stream);
            this.avatar = stream.toByteArray();
        }

    }
    public ContactItem(){

    }

    public Bitmap getAvatar() {
        if (this.avatar!=null) {
            Bitmap image = BitmapFactory.decodeByteArray(avatar,
                    0, avatar.length);

            return image;
        }
        return null;
    }

    public void setAvatar(Bitmap avatar) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        avatar.compress(Bitmap.CompressFormat.PNG, 100, stream);
        this.avatar = stream.toByteArray();
    }

    public String getNumber() {
        return number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ContactItem{" +
                "number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
