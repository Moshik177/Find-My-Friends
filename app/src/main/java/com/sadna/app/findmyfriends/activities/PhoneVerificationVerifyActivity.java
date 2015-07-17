package com.sadna.app.findmyfriends.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.sadna.app.findmyfriends.BaseActivity;
import com.sadna.app.findmyfriends.R;

import org.w3c.dom.Text;

public class PhoneVerificationVerifyActivity extends BaseActivity {

    private SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification_verify);
        findViewById(R.id.textErrorViewVerify).setVisibility(View.INVISIBLE);

        TextView verificationTextView = ((TextView) findViewById(R.id.verify_code_textbox));
        verificationTextView.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                findViewById(R.id.textErrorViewVerify).setVisibility(View.INVISIBLE);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    public void verifyPhone(View view) {
        mSharedPref = getApplicationContext().getSharedPreferences("FindMyFriendsPref", 0); // 0 - for private mode;
        SharedPreferences.Editor editor = mSharedPref.edit();

        String verificationCodeFromSms = mSharedPref.getString("verify_code", "");
        String verificationCodeFromInput = ((TextView) findViewById(R.id.verify_code_textbox)).getText().toString();

        if (verificationCodeFromSms.equals(verificationCodeFromInput)) {
            editor.putBoolean("phone_activated", true);
            editor.commit();

            AlertDialog.Builder successMessage = new AlertDialog.Builder(PhoneVerificationVerifyActivity.this);
            successMessage.setTitle("Phone verification complete")
                    .setMessage("Your phone is now verified!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }
                    });
            AlertDialog alert = successMessage.create();
            alert.show();
        }
        else {
            findViewById(R.id.textErrorViewVerify).setVisibility(View.VISIBLE);
            findViewById(R.id.verify_code_textbox).requestFocus();
        }
    }
}
