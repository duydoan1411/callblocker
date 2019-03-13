package com.dgteam.callblocker;

import android.graphics.Bitmap;

public class ContactItem {

    private String number, name, id;
    private Bitmap avatar;

    public ContactItem(String id,String name, String number, Bitmap avatar) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.avatar = avatar;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public String getNumber() {
        return number;
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
}
