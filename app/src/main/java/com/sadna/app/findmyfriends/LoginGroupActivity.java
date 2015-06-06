package com.sadna.app.findmyfriends;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.sadna.app.webservice.WebService;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * A login screen that offers login via username and password.
 */
public class LoginGroupActivity extends ActionBarActivity {

    private Button mAddGroupButton;
    private TableLayout mGroupTable;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_group);

        mAddGroupButton = (Button) findViewById(R.id.add_group_button);
        mGroupTable = (TableLayout) findViewById(R.id.group_table);

        mAddGroupButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroup();
            }
        });
    }
    private void addGroup()
    {
        TableRow newGroup = new TableRow(this);
        Button groupButton = new Button(this);
        groupButton.setText("hello from new group");
        newGroup.addView(groupButton);
        mGroupTable.addView(newGroup);
        moveToLoginGroupActivity();
    }

    private void moveToLoginGroupActivity() {
        startActivity(new Intent(getApplicationContext(), CreateNewGroupActivity.class));
    }
}