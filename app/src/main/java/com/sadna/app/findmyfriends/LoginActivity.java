package com.sadna.app.findmyfriends;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
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
import android.widget.TextView;

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
import java.security.MessageDigest;


/**
 * A login screen that offers login via username and password.
 */
public class LoginActivity extends ActionBarActivity {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    // Facebook login references
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTokenTracker;
    private LoginButton mLoginButton;
    private FbUserName fbUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        fbUserName = new FbUserName();
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
            }
        };
        updateWithToken(AccessToken.getCurrentAccessToken());
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        SignUpWithFacebookAsyncTask signUpWithFacebookAsyncTask = new SignUpWithFacebookAsyncTask(loginResult.getAccessToken());
                        signUpWithFacebookAsyncTask.execute((Void) null);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.v("LoginActivity", "cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.e("LoginActivity", exception.getCause().toString());
                    }
                });

        setContentView(R.layout.activity_login);
        mLoginButton = (LoginButton) findViewById(R.id.login_button);
        mLoginButton.setReadPermissions(Arrays.asList("public_profile, email"));

        // Disable auto popup of the keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void userGraphRequest(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        Log.v("LoginActivity", response.toString());
                        try {
                            String[] fullName = object.getString("name").split(" ");
                            fbUserName.setFirstName(fullName[0]);
                            fbUserName.setLastName(fullName[1]);
                            fbUserName.setEmail(object.getString("email"));
                            fbUserName.setPassword(hashString(fbUserName.getEmail()));
                            fbUserName.setGender(object.getString("gender").substring(0,1).toUpperCase() + object.getString("gender").substring(1));
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, email, gender");
        request.setParameters(parameters);
        request.executeAndWait();
    }

    private String hashString(String stringToEncrypt) throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.reset();
        m.update(stringToEncrypt.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String hashText = bigInt.toString(16);
        // Now we need to zero pad it if you actually want the full 32 chars.
        while (hashText.length() < 32) {
            hashText = "0" + hashText;
        }
        return hashText;
    }

    private boolean signUpWithFacebook(String firstname, String lastname, String email, String username, String password, String birthdate, String gender) throws ExecutionException, InterruptedException {
        SignUpForm signUpForm = new SignUpForm();

        signUpForm.setFirstName(firstname);
        signUpForm.setLastName(lastname);
        signUpForm.setEmail(email);
        signUpForm.setUsername(username);
        signUpForm.setPassword(password);
        signUpForm.setBirthdate(birthdate);
        signUpForm.setGender(gender);

        FacebookSignUpTask signUpTask = new FacebookSignUpTask(signUpForm);
        return signUpTask.execute((Void) null).get();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void updateWithToken(final AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    UserLoginWithFacebookAsyncTask userLoginWithFacebookAsyncTask = new UserLoginWithFacebookAsyncTask(currentAccessToken);
                    userLoginWithFacebookAsyncTask.execute((Void) null);
                }
            }, 1);
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                }
            }, 1);
        }
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("This field is required");
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void moveToSignUpActivity(View view) {
        startActivity(new Intent(getApplicationContext(), SignupActivity.class));
    }

    private void successLogin() {
        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
    }

    private void failedLogin() {
        findViewById(R.id.textErrorView).setVisibility(View.VISIBLE);
    }

    protected boolean loginThroughWebService(String username, String password) {
        WebService wsHttpRequest = new WebService("checkUserDetails");
        String result = null;

        try {
            result = wsHttpRequest.execute(username, password);
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e("LoginActivity", e.getCause().toString());
            return false;
        }

        return Boolean.valueOf(result);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            findViewById(R.id.textErrorView).setVisibility(View.INVISIBLE);
            return loginThroughWebService(mUsername, mPassword);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
                successLogin();
            } else {
                failedLogin();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class SignUpWithFacebookAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private final AccessToken mAccessToken;

        SignUpWithFacebookAsyncTask(AccessToken accessToken)
        {
            mAccessToken = accessToken;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            userGraphRequest(mAccessToken);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            try {
                signUpWithFacebook(fbUserName.getFirstName(), fbUserName.getLastName(), fbUserName.getEmail(), fbUserName.getEmail(), fbUserName.getPassword(), "", fbUserName.getGender());
            } catch (Throwable e) {
                e.printStackTrace();
                Log.e("LoginActivity", e.getCause().toString());
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    public class UserLoginWithFacebookAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private final AccessToken mAccessToken;

        UserLoginWithFacebookAsyncTask(AccessToken accessToken)
        {
            mAccessToken = accessToken;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            userGraphRequest(mAccessToken);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            UserLoginTask authTask = new UserLoginTask(fbUserName.getEmail(), fbUserName.getPassword());
            authTask.execute((Void) null);
        }

        @Override
        protected void onCancelled() {
        }
    }
}