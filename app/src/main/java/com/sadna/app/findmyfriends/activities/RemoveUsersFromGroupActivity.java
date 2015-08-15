package com.sadna.app.findmyfriends.activities;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.sadna.app.findmyfriends.entities.UserId;
import com.sadna.app.webservice.WebService;

import java.util.ArrayList;
import java.util.List;

public class RemoveUsersFromGroupActivity extends BaseActivity {

    private Gson gson = new Gson();
    private List<UserId> groupMembers = new ArrayList<>();
    private ListView groupMembersListView;
    private ArrayAdapter<UserId> listViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_users_from_group);

        getGroupMembers();

        // Find the ListView resource.
        groupMembersListView = (ListView) findViewById(R.id.ListOfGroupMembers);

        // On item clicked set selected group name and id in myapp
        groupMembersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView Clicked item value
                final UserId selectedUser = (UserId) groupMembersListView.getItemAtPosition(position);
                if (!selectedUser.getId().equals(((MyApplication) getApplication()).getUserId())) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(RemoveUsersFromGroupActivity.this);
                    mBuilder.setMessage("Are you sure you want to remove this person from the group?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            listViewAdapter.remove(selectedUser);
                                            String userId = selectedUser.getId();
                                            removeUserFromGroup(userId);
                                        }
                                    }
                            )
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = mBuilder.create();
                    alert.show();
                }
            }
        });

        // Create ArrayAdapter using the planet list.
        listViewAdapter = new ArrayAdapter<>(this, R.layout.group_row, groupMembers);

        // Set the ArrayAdapter as the ListView's adapter.
        groupMembersListView.setAdapter(listViewAdapter);
    }


    public void moveToGroupsActivity(View view) {
        startActivity(new Intent(getApplicationContext(), GroupsMainActivity.class));
    }


    private void removeUserFromGroup(final String userId) {
        Thread removeUserFromGroupThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WebService wsHttpRequest = new WebService("leaveGroup");

                    try {
                        wsHttpRequest.execute(userId, ((MyApplication) getApplication()).getSelectedGroupId());
                    } catch (Throwable exception) {
                        Log.e("RemoveUsersGroupActvt", exception.getMessage());
                    }
                } catch (Exception e) {
                    Log.e("RemoveUsersGroupActvt", e.getMessage());
                }
            }
        });

        removeUserFromGroupThread.start();
        try {
            removeUserFromGroupThread.join();
        } catch (InterruptedException exception) {
            Log.e("RemoveUsersGroupActvt", exception.getMessage());
        }
    }

    private void getGroupMembers() {
        Thread getGroupMembersThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WebService wsHttpRequest = new WebService("getGroupMembers");
                    String result = null;

                    try {
                        result = wsHttpRequest.execute(((MyApplication) getApplication()).getSelectedGroupId());
                    } catch (Throwable exception) {
                        Log.e("RemoveUsersGroupActvt", exception.getMessage());
                    }

                    groupMembers = gson.fromJson(result, new TypeToken<ArrayList<UserId>>() {
                    }.getType());
                } catch (Exception e) {
                    Log.e("RemoveUsersGroupActvt", e.getMessage());
                }
            }
        });

        getGroupMembersThread.start();
        try {
            getGroupMembersThread.join();
        } catch (InterruptedException exception) {
            Log.e("RemoveUsersGroupActvt", exception.getMessage());
        }
    }


}