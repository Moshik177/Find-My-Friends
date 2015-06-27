package com.sadna.app.findmyfriends.activities;

import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.sadna.app.findmyfriends.BaseActivity;
import com.sadna.app.findmyfriends.R;

/**
 * A login screen that offers login via username and password.
 */
public class GroupsMainActivity extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_main);
    }

    public void addGroup(View view) {
        moveToCreateNewGroupActivity();
    }

    private void moveToCreateNewGroupActivity() {
        startActivity(new Intent(getApplicationContext(), CreateNewGroupActivity.class));
    }
}