package com.dgteam.callblocker;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsContactItemLog extends ContactItem implements Serializable{

    private String dateLog, hourLog, header, message;

    public SmsContactItemLog(String id, String name, String number, Bitmap avatar, String message) {
        super(id, name, number, avatar);
        this.message = message;
        Date date = new Date();
        dateLog = new SimpleDateFormat("dd/MM/yyyy").format(date);
        hourLog = new SimpleDateFormat("HH:mm:ss").format(date);
    }
    public SmsContactItemLog(String header){
        super(null,null,null,null);
        this.header = header;
        dateLog = header;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
                ", message='" + message + '\'' +
                '}';
    }
}
