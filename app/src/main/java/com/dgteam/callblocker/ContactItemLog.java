package com.dgteam.callblocker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class ContactItemLog implements Serializable {

    private String number, name, id, dateLog, hourLog;
    private byte[] avatar;

    public ContactItemLog(String id, String name, String number, Bitmap avatar) {
        this.id = id;
        this.number = number;
        this.name = name;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        avatar.compress(Bitmap.CompressFormat.PNG, 100, stream);
        this.avatar = stream.toByteArray();
        Date date = new Date();
        dateLog = new SimpleDateFormat("dd/MM/yyyy").format(date)+"";
        hourLog = new SimpleDateFormat("HH:mm:ss").format(date)+"";
    }

    public Bitmap getAvatar() {
        Bitmap image = BitmapFactory.decodeByteArray(avatar,
                0, avatar.length);
        return image;
    }

    public void setAvatar(Bitmap avatar) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        avatar.compress(Bitmap.CompressFormat.PNG, 100, stream);
        this.avatar = stream.toByteArray();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateLog() {
        return dateLog;
    }

    public void setDateLog(String dateLog) {
        this.dateLog = dateLog;
    }

    public String getHourLog() {
        return hourLog;
    }

    public void setHourLog(String hourLog) {
        this.hourLog = hourLog;
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

    @Override
    public String toString() {
        return "ContactItemLog{" +
                "number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", dateLog='" + dateLog + '\'' +
                ", hourLog='" + hourLog + '\'' +
                ", avatar=" + Arrays.toString(avatar) +
                '}';
    }
}
