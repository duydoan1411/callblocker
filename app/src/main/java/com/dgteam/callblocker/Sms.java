package com.dgteam.callblocker;

import java.util.ArrayList;

public class Sms {
    private String number;
    private ArrayList<String> messages = new ArrayList<String>();

    public Sms(String number, ArrayList<String> messages) {
        this.number = number;
        this.messages = messages;
    }
    public Sms(String number, String message){
        this.number = number;
        addMessages(message);
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<String> messages) {
        this.messages = messages;
    }

    public void addMessages(String message){
        this.messages.add(message);
    }
}
