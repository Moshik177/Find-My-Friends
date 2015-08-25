package com.sadna.app.findmyfriends;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.sadna.app.findmyfriends.activities.LoginActivity;
import com.sadna.app.findmyfriends.activities.WelcomeScreenActivity;
import com.sadna.app.gpstracker.LocationServiceManager;


/**
 * Created by avihoo on 26/06/2015.
 */
public class BaseActivity extends AppCompatActivity {

    private final String kUSERID = "fmf_login_userid";
    private final String kUSERNAME = "fmf_login_username";

    private SharedPreferences mSharedPref;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mSharedPref = getApplicationContext().getSharedPreferences("FindMyFriendsPref", 0); // 0 - for private mode;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                logout();
                navigateToLoginScreen();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void navigateToLoginScreen() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void logout() {
        // Stops gps update service
        Intent gpsUpdatesIntent = new Intent(getApplicationContext(), LocationServiceManager.class);
        getApplicationContext().stopService(gpsUpdatesIntent);

        // This cleans the logged in user details aka logout
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(kUSERID);
        editor.remove(kUSERNAME);
        editor.commit();

        // logging out from facebook as well
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
    }
}
