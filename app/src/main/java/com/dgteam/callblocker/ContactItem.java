package com.dgteam.callblocker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class ContactItem implements Serializable {

    private String number, name, id;
    private boolean checkTimeBlock = false;
    private int count,beginTimeHour=0, beginTimeMinute=0, endTimeHour=0, endTimeMinute=0;
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
        this.count = 0;

    }

    public boolean isCheckTimeBlock() {
        return checkTimeBlock;
    }

    public void setCheckTimeBlock(boolean checkTimeBlock) {
        this.checkTimeBlock = checkTimeBlock;
    }

    public int getBeginTimeHour() {
        return beginTimeHour;
    }

    public void setBeginTimeHour(int beginTimeHour) {
        this.beginTimeHour = beginTimeHour;
    }

    public int getBeginTimeMinute() {
        return beginTimeMinute;
    }

    public void setBeginTimeMinute(int beginTimeMinute) {
        this.beginTimeMinute = beginTimeMinute;
    }

    public int getEndTimeHour() {
        return endTimeHour;
    }

    public void setEndTimeHour(int endTimeHour) {
        this.endTimeHour = endTimeHour;
    }

    public int getEndTimeMinute() {
        return endTimeMinute;
    }

    public void setEndTimeMinute(int endTimeMinute) {
        this.endTimeMinute = endTimeMinute;
    }

    public ContactItem(){
        this.count = 0;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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
