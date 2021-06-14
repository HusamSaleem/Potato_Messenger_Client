package com.example.potatomessenger.client;

import java.util.Date;

public class Message {
    private String user;
    private String date;
    private String msg;

    public Message(String user, String msg, String date) {
        this.user = user;
        this.date = date;
        this.msg = msg;
    }

    public String getName() {
        return this.user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
