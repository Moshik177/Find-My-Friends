package com.sadna.app.findmyfriends;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.sadna.app.findmyfriends.activities.LoginActivity;


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
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    private void logout() {
        // This cleans the logged in user details aka logout
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove(kUSERID);
        editor.remove(kUSERNAME);
        editor.commit();

        // logging out from facebook as well
        LoginManager.getInstance().logOut();
    }
}
