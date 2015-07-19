package com.sadna.app.findmyfriends;

import android.app.Application;

/**
 * Created by avihoo on 16/05/2015.
 */
public class MyApplication extends Application {

    private String mUsername;
    private String mUserId;
    private String mSelectedGroupName;
    private String mSelectedGroupId;

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public String getSelectedGroupName() {
        return mSelectedGroupName;
    }

    public void setSelectedGroupName(String selectedGroupName) {
        this.mSelectedGroupName = selectedGroupName;
    }

    public String getSelectedGroupId() {
        return mSelectedGroupId;
    }

    public void setSelectedGroupId(String selectedGroupId) {
        this.mSelectedGroupId = selectedGroupId;
    }
}