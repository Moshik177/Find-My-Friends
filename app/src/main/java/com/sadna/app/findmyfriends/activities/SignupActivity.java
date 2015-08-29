package com.sadna.app.findmyfriends.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sadna.app.findmyfriends.BaseActivity;
import com.sadna.app.findmyfriends.R;
import com.sadna.app.findmyfriends.forms.SignUpForm;
import com.sadna.app.webservice.WebService;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class SignupActivity extends BaseActivity {

    private static final int USER_ALREADY_EXISTS = 1;
    private Calendar mCalendar = Calendar.getInstance();
    private SignUpTask mSignUpTask = null;
    private static int mSignUpActionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        TextView birthdateTextView = (TextView) findViewById(R.id.birthdayText);
        birthdateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                birthDatePicker(v);
            }
        });
    }

    public void attemptSignUp(View view) {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setFirstName(((EditText) findViewById(R.id.firstNameText)).getText().toString());
        signUpForm.setLastName(((EditText) findViewById(R.id.lastNameText)).getText().toString());
        signUpForm.setEmail(((EditText) findViewById(R.id.emailText)).getText().toString());
        signUpForm.setUsername(((EditText) findViewById(R.id.userNameText)).getText().toString());
        signUpForm.setPassword(((EditText) findViewById(R.id.passwordText)).getText().toString());
        signUpForm.setBirthdate(((EditText) findViewById(R.id.birthdayText)).getText().toString());
        signUpForm.setGender(getGenderFromSignUpForm());
        signUpForm.setPhone(getApplicationContext().getSharedPreferences("FindMyFriendsPref", 0).getString("verify_phoneNumber", ""));

        if (!validateForm(signUpForm)) {
            return;
        }

        // Actually try to use WS to add user. Check for exception if username already exists
        // If all good, move to login activity
        mSignUpTask = new SignUpTask(signUpForm);
        mSignUpTask.execute((Void) null);
    }

    private boolean validateForm(SignUpForm signUpForm) {
        boolean valid = true;

        resetErrors();

        if (TextUtils.isEmpty(signUpForm.getFirstName())) {
            ((EditText) findViewById(R.id.firstNameText)).setError(getString(R.string.error_field_required));
            valid = false;
        }

        if (TextUtils.isEmpty(signUpForm.getLastName())) {
            ((EditText) findViewById(R.id.lastNameText)).setError(getString(R.string.error_field_required));
            valid = false;
        }

        if (TextUtils.isEmpty(signUpForm.getEmail())) {
            ((EditText) findViewById(R.id.emailText)).setError(getString(R.string.error_field_required));
            valid = false;
        } else if (!validateEmail(signUpForm.getEmail())) {
            ((EditText) findViewById(R.id.emailText)).setError(getString(R.string.error_invalid_email));
            valid = false;
        }

        if (TextUtils.isEmpty(signUpForm.getUsername())) {
            ((EditText) findViewById(R.id.userNameText)).setError(getString(R.string.error_field_required));
            valid = false;
        }

        if (TextUtils.isEmpty(signUpForm.getPassword())) {
            ((EditText) findViewById(R.id.passwordText)).setError(getString(R.string.error_field_required));
            valid = false;
        }

        if (TextUtils.isEmpty(signUpForm.getBirthdate())) {
            ((EditText) findViewById(R.id.birthdayText)).setError(getString(R.string.error_field_required));
            valid = false;
        }

        if (signUpForm.getGender() == null) {
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

    public void birthDatePicker(View view) {
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

    private String getGenderFromSignUpForm() {

        if (((RadioGroup) findViewById(R.id.genderGroup)).getCheckedRadioButtonId() == -1) {
            return null;
        }

        String male = ((RadioButton) findViewById(R.id.maleButton)).getText().toString();
        String female = ((RadioButton) findViewById(R.id.femaleButton)).getText().toString();

        if (male.equals("") || male == null) {
            return female;
        }

        return male;
    }

    public static boolean signUpUser(String firstname, String lastname, String email, String username, String password, String birthdate, String gender, String phone) {
        WebService wsHttpRequest = new WebService("addUser");
        String result = null;

        try {
            result = wsHttpRequest.execute(firstname, lastname, email, username, password, birthdate, gender, phone);
        } catch (Throwable exception) {
            if (exception.getMessage().contains("User already exists")) {
                mSignUpActionResult = USER_ALREADY_EXISTS;
            }

            exception.printStackTrace();
            return false;
        }

        return true;
    }

    public class SignUpTask extends AsyncTask<Void, Void, Boolean> {

        private SignUpForm mSignUpForm;
        private AlertDialog.Builder mBuilder = null;

        SignUpTask(SignUpForm signUpForm) {
            mSignUpForm = signUpForm;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return signUpUser(mSignUpForm.getFirstName(), mSignUpForm.getLastName(), mSignUpForm.getEmail(),
                    mSignUpForm.getUsername(), mSignUpForm.getPassword(), mSignUpForm.getBirthdate(), mSignUpForm.getGender(), mSignUpForm.getPhone());
        }

        protected void onPreExecute() {
            super.onPreExecute();
            mBuilder = new AlertDialog.Builder(SignupActivity.this);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSignUpForm = null;

            if (success) {
                mBuilder.setTitle("Registration Complete!")
                        .setMessage("The registration process completed. Click OK to return the login screen.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            }
                        });
                AlertDialog alert = mBuilder.create();
                alert.show();
            } else {
                executeResult();
            }
        }

        private void executeResult() {
            switch (mSignUpActionResult) {
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
            mSignUpForm = null;
        }
    }
}
