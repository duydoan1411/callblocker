package com.dgteam.callblocker;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ContactItemLog extends ContactItem implements Serializable{

    private String dateLog, hourLog, header;

    public ContactItemLog(String id, String name, String number, Bitmap avatar) {
        super(id, name, number, avatar);
        Date date = new Date();
        dateLog = new SimpleDateFormat("dd/MM/yyyy").format(date);
        hourLog = new SimpleDateFormat("HH:mm:ss").format(date);
    }
    public ContactItemLog(String header){
        super(null,null,null,null);
        this.header = header;
        dateLog = header;
    }
    public ContactItemLog(){

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

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    public String toString() {
        return "ContactItemLog{" +
                "number='" + getNumber() + '\'' +
                ", name='" + getName() + '\'' +
                ", id='" + getId() + '\'' +
                ", dateLog='" + dateLog + '\'' +
                ", hourLog='" + hourLog + '\'' +
                '}';
    }
}
