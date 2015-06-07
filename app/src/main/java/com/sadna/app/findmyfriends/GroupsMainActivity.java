package com.sadna.app.findmyfriends;

import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

/**
 * A login screen that offers login via username and password.
 */
public class GroupsMainActivity extends ActionBarActivity {

    private Button mAddGroupButton;
    private TableLayout mGroupTable;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_main);

        mAddGroupButton = (Button) findViewById(R.id.add_group_button);
        mGroupTable = (TableLayout) findViewById(R.id.group_table);
    }
    private void addGroup()
    {
        TableRow newGroup = new TableRow(this);
        Button groupButton = new Button(this);
        groupButton.setText("hello from new group");
        newGroup.addView(groupButton);
        mGroupTable.addView(newGroup);
        moveToCreateNewGroupActivity();
    }

    private void moveToCreateNewGroupActivity() {
        startActivity(new Intent(getApplicationContext(), CreateNewGroupActivity.class));
    }
}