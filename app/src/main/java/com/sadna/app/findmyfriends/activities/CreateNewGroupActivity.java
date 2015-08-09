package com.sadna.app.findmyfriends.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sadna.app.findmyfriends.BaseActivity;
import com.sadna.app.findmyfriends.MyApplication;
import com.sadna.app.findmyfriends.R;
import com.sadna.app.findmyfriends.entities.Group;
import com.sadna.app.findmyfriends.forms.GroupCreationForm;
import com.sadna.app.webservice.WebService;

/**
 * A login screen that offers login via username and password.
 */


public class CreateNewGroupActivity extends BaseActivity {

    GroupCreationTask mGroupCreationTask = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);
    }

    public void attemptGroupCreation(View view) {
        GroupCreationForm groupCreationForm = new GroupCreationForm();
        groupCreationForm.setGroupName(((EditText) findViewById(R.id.newGroupNameTextBox)).getText().toString());

        if (!validateForm(groupCreationForm)) {
            return;
        }

        mGroupCreationTask = new GroupCreationTask(groupCreationForm);
        mGroupCreationTask.execute((Void) null);
    }


    private void resetErrors() {
        ((EditText) findViewById(R.id.newGroupNameTextBox)).setError(null);
    }

    private boolean validateForm(GroupCreationForm groupCreationForm) {
        boolean valid = true;

        resetErrors();

        if (groupCreationForm.getGroupName().isEmpty()) {
            ((EditText) findViewById(R.id.newGroupNameTextBox)).setError(getString(R.string.error_field_not_empty));
            valid = false;
        }

        return valid;
    }

    public boolean signUpGroup(String groupName, String userId) {

        WebService wsHttpRequest = new WebService("addGroup");
        String result;
        Group CreateGroupByUser;
        Gson gson = new Gson();
        try {
            result = wsHttpRequest.execute(groupName, userId);
        } catch (Throwable exception) {
            if (exception.getMessage().contains("group error")) {
            }
            exception.printStackTrace();
            return false;
        }
        CreateGroupByUser = gson.fromJson(result, new TypeToken<Group>() {
        }.getType());
        ((MyApplication) getApplication()).setSelectedGroupId(CreateGroupByUser.getId());
        return true;
    }

    public class GroupCreationTask extends AsyncTask<Void, Void, Boolean> {

        private GroupCreationForm mGroupCreationForm;


        GroupCreationTask(GroupCreationForm groupCreationForm) {
            mGroupCreationForm = groupCreationForm;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return signUpGroup(mGroupCreationForm.getGroupName(), ((MyApplication) getApplication()).getUserId());
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                startActivity(new Intent(getApplicationContext(), GettingAllGroupActivity.class));
                finish();
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
