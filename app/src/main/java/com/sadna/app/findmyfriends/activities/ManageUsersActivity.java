package com.sadna.app.findmyfriends.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sadna.app.findmyfriends.BaseActivity;
import com.sadna.app.findmyfriends.R;

public class ManageUsersActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);
    }

    public void addUsers(View view) {
        startActivity(new Intent(getApplicationContext(), AddUsersToGroupActivity.class));
    }

    public void removeUsers(View view) {
        startActivity(new Intent(getApplicationContext(), RemoveUsersFromGroupActivity.class));
    }

}