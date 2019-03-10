package com.dgteam.callblocker;

public class ContactItem {

    private String number, name;
    private int avatar;

    public ContactItem(String name, String number, int avatar) {
        this.number = number;
        this.name = name;
        this.avatar = avatar;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
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
