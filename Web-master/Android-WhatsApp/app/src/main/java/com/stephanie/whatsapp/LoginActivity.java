package com.stephanie.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth fireBaseAuth;
    private ProgressDialog LoadBar;

    private FirebaseUser presentUser;

    private Button Login, phoneLogin;
    private EditText mail, passWord;
    private TextView RequestNewAccount, forgetpass;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_login_alt);

        fireBaseAuth = FirebaseAuth.getInstance();
        presentUser = fireBaseAuth.getCurrentUser();

        initializeFields();

        RequestNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }
        });

        phoneLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginIntent = new Intent(LoginActivity.this, phoneLoginActivity.class);
                startActivity(LoginIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (presentUser != null) {
            SendUserToMainActivity();
        }
    }

    private void AllowUserToLogin() {
        String userEmail = mail.getText().toString();
        String pass = passWord.getText().toString();

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Please enter userEmail...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Please enter pass...", Toast.LENGTH_SHORT).show();
        }
        else {
            LoadBar.setTitle("Sign In");
            LoadBar.setMessage("Please wait...");
            LoadBar.setCanceledOnTouchOutside(true);
            LoadBar.show();

            fireBaseAuth.signInWithEmailAndPassword(userEmail, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                SendUserToMainActivity();
                                Toast.makeText(LoginActivity.this, "Logged in Successful...", Toast.LENGTH_SHORT).show();
                                LoadBar.dismiss();
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                                LoadBar.dismiss();
                            }

                        }
                    });
        }
    }

    private void initializeFields() {
        Login = findViewById(R.id.login_button);
        phoneLogin = findViewById(R.id.phone_login_button);
        mail = findViewById(R.id.login_userEmail);
        passWord = findViewById(R.id.login_pass);
        RequestNewAccount = findViewById(R.id.need_new_account_link);
        forgetpass = findViewById(R.id.forget_pass_link);
        LoadBar = new ProgressDialog(this);
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}
