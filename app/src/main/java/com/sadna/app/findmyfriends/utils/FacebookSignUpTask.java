package com.sadna.app.findmyfriends.utils;
import android.os.AsyncTask;

import com.sadna.app.findmyfriends.forms.SignUpForm;

import static com.sadna.app.findmyfriends.activities.SignupActivity.signUpUser;

/**
 * Created by avihoo on 18/04/2015.
 */
public class FacebookSignUpTask extends AsyncTask<Void, Void, Boolean> {

    private SignUpForm mSignUpForm;

    public FacebookSignUpTask(SignUpForm signUpForm) {
        mSignUpForm = signUpForm;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return signUpUser(mSignUpForm.getFirstName(), mSignUpForm.getLastName(), mSignUpForm.getEmail(),
                mSignUpForm.getUsername(), mSignUpForm.getPassword(), mSignUpForm.getBirthdate(), mSignUpForm.getGender(), mSignUpForm.getPhone());
    }

    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        mSignUpForm = null;

        if (success) {
            // User registered successfully
        } else {
            // User already exists or we got exception
        }
    }

    @Override
    protected void onCancelled() {
        mSignUpForm = null;
    }
}
