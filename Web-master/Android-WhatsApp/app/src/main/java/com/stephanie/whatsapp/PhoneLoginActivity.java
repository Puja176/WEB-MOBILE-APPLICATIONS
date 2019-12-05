package com.stephanie.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity
{
    private EditText inputPhoneNumber, inputValidationCode;
    private Button sendcodeVerification, verify;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks myCallBacks;
    private FirebaseAuth fireBaseAuth;

    private ProgressDialog LoadBar;

    private String mYVerificationId;
    private PhoneAuthProvider.ForceResendingToken mYResendToken;



    @Override
    protected void onCreate(Bundle State)
    {
        super.onCreate(State);
        setContentView(R.layout.activity_phone_login);


        fireBaseAuth = FirebaseAuth.getInstance();


        inputPhoneNumber = (EditText) findViewById(R.id.phone_number_input);
        inputValidationCode = (EditText) findViewById(R.id.verification_code_input);
        sendcodeVerification = (Button) findViewById(R.id.send_ver_code_button);
        verify = (Button) findViewById(R.id.verify_button);
        LoadBar = new ProgressDialog(this);


        sendcodeVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String phNumber = inputPhoneNumber.getText().toString();

                if (TextUtils.isEmpty(phNumber))
                {
                    Toast.makeText(PhoneLoginActivity.this, "Please enter your phone number first...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    LoadBar.setTitle("Phone Verification");
                    LoadBar.setMessage("Please wait, while we are authenticating using your phone...");
                    LoadBar.setCanceledOnTouchOutside(false);
                    LoadBar.show();


                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phNumber, 60, TimeUnit.SECONDS, PhoneLoginActivity.this, myCallBacks);
                }
            }
        });



        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                inputPhoneNumber.setVisibility(View.INVISIBLE);
                sendcodeVerification.setVisibility(View.INVISIBLE);


                String verificationCode = inputValidationCode.getText().toString();

                if (TextUtils.isEmpty(verificationCode))
                {
                    Toast.makeText(PhoneLoginActivity.this, "Please write verification code first...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    LoadBar.setTitle("Verification Code");
                    LoadBar.setMessage("Please wait, while we are verifying verification code...");
                    LoadBar.setCanceledOnTouchOutside(false);
                    LoadBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mYVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });


        myCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {
                String message = e.getMessage().toString();
                Toast.makeText(PhoneLoginActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                Log.d("PhoneLogin", "onVerificationFailed: " + message);

                Toast.makeText(PhoneLoginActivity.this, "Invalid Phone Number, Please enter correct phone number with your country code...", Toast.LENGTH_LONG).show();
                LoadBar.dismiss();

                inputPhoneNumber.setVisibility(View.VISIBLE);
                sendcodeVerification.setVisibility(View.VISIBLE);

                inputValidationCode.setVisibility(View.INVISIBLE);
                verify.setVisibility(View.INVISIBLE);
            }

            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token)
            {
                // Save verification ID and resending token so we can use them later
                mYVerificationId = verificationId;
                mYResendToken = token;


                Toast.makeText(PhoneLoginActivity.this, "Code has been sent, please check and verify...", Toast.LENGTH_SHORT).show();
                LoadBar.dismiss();

                inputPhoneNumber.setVisibility(View.INVISIBLE);
                sendcodeVerification.setVisibility(View.INVISIBLE);

                inputValidationCode.setVisibility(View.VISIBLE);
                verify.setVisibility(View.VISIBLE);
            }
        };
    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        fireBaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            LoadBar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "Congratulations, you're logged in Successfully.", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();
                        }
                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            LoadBar.dismiss();
                        }
                    }
                });
    }




    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(PhoneLoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}