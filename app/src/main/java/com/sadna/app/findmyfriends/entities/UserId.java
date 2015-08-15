package com.sadna.app.findmyfriends.entities;

/**
 * Created by Moshik on 08/08/2015.
 */
public class UserId {

    private String username;
    private String id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String Username) {
        this.username = Username;
    }

    @Override
    public String toString() {
        return getUsername();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
