package com.sadna.app.findmyfriends.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

import com.sadna.app.findmyfriends.BaseActivity;
import com.sadna.app.findmyfriends.R;


public class WelcomeScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** set time to splash out */
        final int welcomeScreenDisplay = 3000;
        /** create a thread to show splash up to splash time */
        Thread welcomeThread = new Thread() {

            int wait = 0;

            @Override
            public void run() {
                try {
                    super.run();
                    /**
                        * use while to get the splash time. Use sleep() to increase
                        * the wait variable for every 100L.
                    */
                    while (wait < welcomeScreenDisplay) {
                        sleep(100);
                        wait += 100;
                    }
                } catch (Exception e) {
                    System.out.println("Exception thrown:" + e);
                } finally {
                    /**
                        * Called after splash times up. Do some action after splash
                        * times up. Here we moved to another main activity class
                    */
                    startActivity(new Intent(getApplicationContext(),
                            LoginActivity.class));
                    finish();
                }
            }
        };
        welcomeThread.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
