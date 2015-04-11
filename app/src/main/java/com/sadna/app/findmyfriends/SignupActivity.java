package com.sadna.app.findmyfriends;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.sadna.app.webservice.WebService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class SignupActivity extends ActionBarActivity {

    private Calendar mCalendar = Calendar.getInstance();
    private UserSignUpTask mSignUpTask = null;

    private int mResult;
    private final int USER_ALREADY_EXISTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
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

    public void attemptSignup(View view) {
        SignupForm signupForm = new SignupForm();
        signupForm.setFirstName(((EditText) findViewById(R.id.firstNameText)).getText().toString());
        signupForm.setLastName(((EditText) findViewById(R.id.lastNameText)).getText().toString());
        signupForm.setEmail(((EditText) findViewById(R.id.emailText)).getText().toString());
        signupForm.setUsername(((EditText) findViewById(R.id.userNameText)).getText().toString());
        signupForm.setPassword(((EditText) findViewById(R.id.passwordText)).getText().toString());
        signupForm.setBirthdate(((EditText) findViewById(R.id.birthdayText)).getText().toString());
        signupForm.setGender(getGenderFromSignupForm());

        if (!validateForm(signupForm)) {
            return;
        }

        // Actually try to use WS to add user. Check for exception if username already exists
        // If all good, move to login activity
        mSignUpTask = new UserSignUpTask(signupForm);
        mSignUpTask.execute((Void) null);
    }

    public class UserSignUpTask extends AsyncTask<Void, Void, Boolean> {

        private SignupForm mSignupForm;
        private AlertDialog.Builder mBuilder;

        UserSignUpTask(SignupForm signupForm) {
            mSignupForm = signupForm;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            WebService wsHttpRequest = new WebService("addUser");
            String result = null;

            try {
                result = wsHttpRequest.execute("arg0", mSignupForm.getFirstName(), "arg1", mSignupForm.getLastName(), "arg2", mSignupForm.getEmail(),
                        "arg3", mSignupForm.getUsername(), "arg4", mSignupForm.getPassword(), "arg5", mSignupForm.getBirthdate(), "arg6", mSignupForm.getGender());
            }
            catch (Throwable exception) {
                if (exception.getMessage().contains("User already exists"))
                {
                    mResult = USER_ALREADY_EXISTS;
                }

                exception.printStackTrace();
                return false;
            }

            return true;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            mBuilder = new AlertDialog.Builder(SignupActivity.this);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSignupForm = null;

            if (success) {
                mBuilder.setTitle("Registration Complete!")
                        .setMessage("The registration process completed. Click OK to return the login screen.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();
                            }
                        });
                AlertDialog alert = mBuilder.create();
                alert.show();
            }
            else {
                executeResult();
            }
        }

        private void executeResult() {
            switch (mResult) {
                case USER_ALREADY_EXISTS:
                    ((EditText) findViewById(R.id.userNameText)).setError(getString(R.string.error_user_already_exists));
                    findViewById(R.id.userNameText).requestFocus();
                    break;
                default:
                    findViewById(R.id.firstNameText).requestFocus();
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            mSignupForm = null;
        }
    }

    private boolean validateForm(SignupForm signupForm) {
        boolean valid = true;

        resetErrors();

        if (TextUtils.isEmpty(signupForm.getFirstName())) {
            ((EditText) findViewById(R.id.firstNameText)).setError(getString(R.string.error_field_required));
            valid = false;
        }

        if (TextUtils.isEmpty(signupForm.getLastName())) {
            ((EditText) findViewById(R.id.lastNameText)).setError(getString(R.string.error_field_required));
            valid = false;
        }

        if (TextUtils.isEmpty(signupForm.getEmail())) {
            ((EditText) findViewById(R.id.emailText)).setError(getString(R.string.error_field_required));
            valid = false;
        }

        else if (!validateEmail(signupForm.getEmail()))
        {
            ((EditText) findViewById(R.id.emailText)).setError(getString(R.string.error_invalid_email));
            valid = false;
        }

        if (TextUtils.isEmpty(signupForm.getUsername())) {
            ((EditText) findViewById(R.id.userNameText)).setError(getString(R.string.error_field_required));
            valid = false;
        }

        if (TextUtils.isEmpty(signupForm.getPassword())) {
            ((EditText) findViewById(R.id.passwordText)).setError(getString(R.string.error_field_required));
            valid = false;
        }

        if (TextUtils.isEmpty(signupForm.getBirthdate())) {
            ((EditText) findViewById(R.id.birthdayText)).setError(getString(R.string.error_field_required));
            valid = false;
        }

        if (signupForm.getGender() == null) {
            ((RadioButton) findViewById(R.id.femaleButton)).setError(getString(R.string.error_field_required));
            valid = false;
        }

        return valid;
    }

    private void resetErrors() {
            ((EditText) findViewById(R.id.firstNameText)).setError(null);
            ((EditText) findViewById(R.id.lastNameText)).setError(null);
            ((EditText) findViewById(R.id.emailText)).setError(null);
            ((EditText) findViewById(R.id.userNameText)).setError(null);
            ((EditText) findViewById(R.id.passwordText)).setError(null);
            ((EditText) findViewById(R.id.birthdayText)).setError(null);
            ((RadioButton) findViewById(R.id.femaleButton)).setError(null);
    }

    public void resetGenderErrors(View view) {
        ((RadioButton) findViewById(R.id.femaleButton)).setError(null);
    }

    public void birthdatePicker(View view) {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateTextBox();
            }
        };

        DatePickerDialog bdDialog = new DatePickerDialog(SignupActivity.this, date, mCalendar
                .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH));

        bdDialog.getDatePicker().setCalendarViewShown(false);
        bdDialog.getDatePicker().setSpinnersShown(true);
        bdDialog.getDatePicker().setMaxDate(new Date().getTime());
        bdDialog.setTitle("When is your birthday?");
        bdDialog.show();
    }

    private void updateTextBox() {
        SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
        ((EditText) findViewById(R.id.birthdayText)).setError(null);
        ((EditText) findViewById(R.id.birthdayText)).setText(formatDate.format(mCalendar.getTime()));
    }

    private boolean validateEmail(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    private String getGenderFromSignupForm() {

        if (((RadioGroup) findViewById(R.id.genderGroup)).getCheckedRadioButtonId() == -1)
        {
            return null;
        }

        String male = ((RadioButton) findViewById(R.id.maleButton)).getText().toString();
        String female = ((RadioButton) findViewById(R.id.femaleButton)).getText().toString();

        if (male.equals("") || male == null)
        {
            return female;
        }

        return male;
    }

    private class SignupForm {

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getBirthdate() {
            return birthdate;
        }

        public void setBirthdate(String birthdate) {
            this.birthdate = birthdate;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        private String firstName;
        private String lastName;
        private String email;
        private String username;
        private String password;
        private String birthdate;
        private String gender;
    }
}
