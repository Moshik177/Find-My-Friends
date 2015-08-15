package com.sadna.app.findmyfriends.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A login screen that offers login via username and password.
 */
public class GroupsMainActivity extends BaseActivity {

    private List<Group> userGroups = new ArrayList<>();
    private Gson gson = new Gson();
    private ListView userGroupsListView;
    private ArrayAdapter<Group> listViewAdapter;

    private final String[] menuItems = {"Leave Group", "Delete Group", "Manage Users"};
    private Map<String, List<Object>> menuItemsPropertiesMap = new HashMap<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuItemsPropertiesMap.put(menuItems[0], Arrays.asList(true, (Object) "Are you sure you want to leave the group"));
        menuItemsPropertiesMap.put(menuItems[1], Arrays.asList(true, (Object) "Are you sure you want to delete the group"));
        menuItemsPropertiesMap.put(menuItems[2], Arrays.asList(false, (Object) null));
        setContentView(R.layout.activity_group_main);
        getUserGroups();

        // Find the ListView resource.
        userGroupsListView = (ListView) findViewById(R.id.groupsListView);

        // On item clicked set selected group name and id in myapp
        userGroupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group currentSelectedGroup = (Group) userGroupsListView.getItemAtPosition(position);
                ((MyApplication) getApplication()).setSelectedGroupId(currentSelectedGroup.getId());
                ((MyApplication) getApplication()).setSelectedGroupName(currentSelectedGroup.getName());
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });

        // Create ArrayAdapter using the planet list.
        listViewAdapter = new ArrayAdapter<>(this, R.layout.group_row, userGroups);

        // Set the ArrayAdapter as the ListView's adapter.
        userGroupsListView.setAdapter(listViewAdapter);
        registerForContextMenu(userGroupsListView);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.groupsListView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(userGroupsListView.getItemAtPosition(info.position).toString());
            menu.add(Menu.NONE, 0, 0, menuItems[0]);
            if (((Group) userGroupsListView.getItemAtPosition(info.position)).getOwnerId() == Integer.parseInt(((MyApplication) getApplication()).getUserId())) {
                for (int i = 1; i < menuItems.length; i++) {
                    menu.add(Menu.NONE, i, i, menuItems[i]);
                }
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        final String menuItemName = menuItems[menuItemIndex];
        String listItemName = userGroupsListView.getItemAtPosition(info.position).toString();

        if ((boolean) menuItemsPropertiesMap.get(menuItemName).get(0)) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(GroupsMainActivity.this);
            mBuilder.setMessage(menuItemsPropertiesMap.get(menuItemName).get(1) + " \"" + listItemName + "\"?")
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    executeContextMenuAction(menuItemName, ((Group) userGroupsListView.getItemAtPosition(info.position)).getId());
                                }
                            }
                    );
            AlertDialog alert = mBuilder.create();
            alert.show();
        } else {
            executeContextMenuAction(menuItemName, null);
        }

        return true;
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
                        Log.e("GroupsMainActivity", exception.getMessage());
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

    private void executeContextMenuAction(String actionName, String groupId) {
        if (actionName.equals(menuItems[0])) {
            leaveGroup(groupId);
            refreshActivity();
        } else if (actionName.equals(menuItems[1])) {
            // TODO: Delete group
            refreshActivity();
        } else if (actionName.equals(menuItems[2])) {
            // TODO: Manage users
            System.out.println(actionName);
        }
    }

    private void refreshActivity() {
        finish();
        startActivity(getIntent());
    }

    private void leaveGroup(final String groupId) {
        Thread leaveGroupThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WebService wsHttpRequest = new WebService("leaveGroup");

                    try {
                        wsHttpRequest.execute(((MyApplication) getApplication()).getUserId(), groupId);
                    } catch (Throwable exception) {
                        Log.e("GroupsMainActivity", exception.getMessage());
                    }

                } catch (Exception e) {
                    Log.e("GroupsMainActivity", e.getMessage());
                }
            }
        });

        leaveGroupThread.start();
        try {
            leaveGroupThread.join();
        } catch (InterruptedException exception) {
            Log.e("GroupsMainActivity", exception.getMessage());
        }
    }

}