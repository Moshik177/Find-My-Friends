package com.sadna.app.findmyfriends;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
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
import android.widget.RadioButton;
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


public class CreateNewGroupActivity extends ActionBarActivity
{
    /*Data Members*/
    private static final int GROUP_ERROR= 1;
    GroupCreationTask mGroupCreationTask=null;
    private static int mSignUpActionResult;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);
    }


    private void moveToLoginGroupActivity() {
        startActivity(new Intent(getApplicationContext(), LoginGroupActivity.class));
    }

    public void attemptGroupCreation(View view) {
       //
        GroupCreationForm groupCreationForm= new GroupCreationForm();
        groupCreationForm.setGroupName(((EditText)findViewById(R.id.newGroupNameTextBox)).getText().toString());

        if (!validateForm(groupCreationForm)) {
            return;
        }

        mGroupCreationTask = new GroupCreationTask(groupCreationForm);
        mGroupCreationTask.execute((Void) null);
    }

    private boolean validateForm(GroupCreationForm groupCreationForm) {
        boolean valid = true;
        return valid;
    }

    public static boolean signUpGroup(String groupName,String userId) {

        WebService wsHttpRequest = new WebService("addGroup");
        String result = null;

        try {
            result = wsHttpRequest.execute(groupName, userId);
        } catch (Throwable exception) {
            if (exception.getMessage().contains("group error")) {
            }
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    public class GroupCreationTask extends AsyncTask<Void, Void, Boolean> {

        private GroupCreationForm mGroupCreationForm;
        private AlertDialog.Builder mBuilder = null;


        GroupCreationTask(GroupCreationForm groupCreationForm) {
            mGroupCreationForm = groupCreationForm;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
           return signUpGroup(mGroupCreationForm.getGroupName(),((MyApplication) getApplication()).getUserId());
        }

        protected void onPreExecute() {
            super.onPreExecute();
            mBuilder = new AlertDialog.Builder(CreateNewGroupActivity.this);
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                mBuilder.setTitle("Group creation Complete!")
                        .setMessage("The group creation completed. Click OK to return the groups screen.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(getApplicationContext(), LoginGroupActivity.class));
                                finish();
                            }
                        });
                AlertDialog alert = mBuilder.create();
                alert.show();
            } else {
                //
            }
        }

        @Override
        protected void onCancelled() {
            mGroupCreationForm = null;
        }
    }
}
