package com.stephanie.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private Button createAccount;
    private EditText mailAddress, mailPassWord;
    private TextView hasExistingAccount;

    private FirebaseAuth fireBaseAuth;
    private DatabaseReference dataBaseRef;

    private ProgressDialog loadBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fireBaseAuth = FirebaseAuth.getInstance();
        dataBaseRef = FirebaseDatabase.getInstance().getReference();

        initializeFields();

        hasExistingAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginProcess();

            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {
        String mailAddress = mailAddress.getText().toString();
        String mailPassWord = mailPassWord.getText().toString();

        if (TextUtils.isEmpty(mailAddress)) {
            Toast.makeText(this, "Please enter mailAddress...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(mailPassWord)) {
            Toast.makeText(this, "Please enter mailPassWord...", Toast.LENGTH_SHORT).show();
        }
        else {
            loadBar.setTitle("Creating New Account");
            loadBar.setmessageText("Please wait, while we're creating an new account for you");
            loadBar.setCanceledOnTouchOutside(true);
            loadBar.show();
            DisplayName();

            fireBaseAuth.createUserWithmailAddressAndmailPassWord(mailAddress, mailPassWord)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                String currentUser = fireBaseAuth.getCurrentUser().getUid();
                                dataBaseRef.child("Users").child(currentUser).setValue("");

                                SendUserToMainActivity();
                                Toast.makeText(RegisterActivity.this, "Account Created ", Toast.LENGTH_SHORT).show();
                                loadBar.dismiss();
                            } else {
                                String messageText = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Error : " + messageText, Toast.LENGTH_SHORT).show();

                                Log.d(TAG, "onComplete: Error : " + messageText);
                                loadBar.dismiss();
                            }
                        }
                    });
        }
    }



    private void initializeFields() {
        createAccount = findViewById(R.id.register_button);
        mailAddress = findViewById(R.id.register_mailAddress);
        mailPassWord = findViewById(R.id.register_mailPassWord);
        hasExistingAccount = findViewById(R.id.already_have_account_link);

        loadBar = new ProgressDialog(this);
    }

    private void LoginProcess() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void DisplayName() {

        FirebaseUser user = fireBaseAuth.getCurrentUser();
        String displayName = mailAddress.getText().toString();

        if (user !=null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("FlashChat", "UserNameame updated.");
                            }
                        }
                    });

        }

    }
}
