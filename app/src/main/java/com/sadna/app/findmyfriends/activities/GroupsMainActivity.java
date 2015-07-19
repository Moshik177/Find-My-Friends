package com.sadna.app.findmyfriends.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sadna.app.findmyfriends.BaseActivity;
import com.sadna.app.findmyfriends.MyApplication;
import com.sadna.app.findmyfriends.R;
import com.sadna.app.findmyfriends.entities.Group;
import com.sadna.app.webservice.WebService;

import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via username and password.
 */
public class GroupsMainActivity extends BaseActivity {

    private List<Group> userGroups = new ArrayList<>();
    private Gson gson = new Gson();
    private ListView userGroupsListView;
    private ArrayAdapter<Group> listViewAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_main);
        getUserGroups();

        // Find the ListView resource.
        userGroupsListView = (ListView) findViewById(R.id.groupsListView);

        // On item clicked set selected group name and id in myapp
        userGroupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group currentSelectedGroup = (Group) userGroupsListView.getItemAtPosition(position);
                ((MyApplication)getApplication()).setSelectedGroupId(currentSelectedGroup.getId());
                ((MyApplication)getApplication()).setSelectedGroupName(currentSelectedGroup.getName());
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });

        // Create ArrayAdapter using the planet list.
        listViewAdapter = new ArrayAdapter<>(this, R.layout.group_row, userGroups);

        // Set the ArrayAdapter as the ListView's adapter.
        userGroupsListView.setAdapter(listViewAdapter);

    }

    private void getUserGroups() {
        Thread getUserGroupsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> userGroupsStrings;
                    WebService wsHttpRequest = new WebService("getUserGroups");
                    String result = null;

                    try {
                        result = wsHttpRequest.execute(((MyApplication) getApplication()).getUserId());
                    } catch (Throwable exception) {
                        Log.e("GroupsMainActivity" ,exception.getMessage());
                    }

                    userGroupsStrings = gson.fromJson(result, new TypeToken<ArrayList<String>>() {
                    }.getType());
                    for (String group : userGroupsStrings) {
                        userGroups.add(gson.fromJson(group, Group.class));
                    }
                } catch (Exception e) {
                    Log.e("GroupsMainActivity", e.getMessage());
                }
            }
        });

        getUserGroupsThread.start();
        try {
            getUserGroupsThread.join();
        } catch (InterruptedException exception) {
            Log.e("GroupsMainActivity", exception.getMessage());
        }
    }

    public void addGroup(View view) {
        moveToCreateNewGroupActivity();
    }

    private void moveToCreateNewGroupActivity() {
        startActivity(new Intent(getApplicationContext(), CreateNewGroupActivity.class));
    }
}