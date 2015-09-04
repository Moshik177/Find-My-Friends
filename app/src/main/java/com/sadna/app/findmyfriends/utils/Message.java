package com.sadna.app.findmyfriends.utils;

import com.google.gson.Gson;

/**
 * Created by avihoo on 22/08/2015.
 */

public class Message {
    private String fromName;
    private String message;
    private boolean isSelf;

    public Message() {
    }

    public Message(String fromName, String message, boolean isSelf) {
        this.fromName = fromName;
        this.message = message;
        this.isSelf = isSelf;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    public String toJsonString()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}