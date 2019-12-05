package com.stephanie.whatsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private ViewPager page;
    private TabLayout tabLayout;
    private TabsAccessorAdapter mYMessageAdapter;

    private FirebaseUser presentuser;
    private FirebaseAuth myAuth;
    private DatabaseReference dataBaseReference;

    @Override
    protected void onCreate(Bundle State) {
        super.onCreate(State);
        setContentView(R.layout.activity_main);

        myAuth = FirebaseAuth.getInstance();
        presentuser = myAuth.getCurrentUser();
        dataBaseReference = FirebaseDatabase.getInstance().getReference();





        myToolBar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setTitle("WhatsApp");

        page = (ViewPager) findViewById(R.id.main_tabs_pager);
        mYMessageAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        page.setAdapter(mYMessageAdapter);

        tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(page);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (presentuser == null) {
            SendUserToLoginActivity();
        } else {
            VerifyUserExist();
        }
    }

    private void VerifyUserExist() {
        String presentuserID = myAuth.getCurrentUser().getUid();
        dataBaseReference.child("Users").child(presentuserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child("name").exists())) {
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                } else {
                    SendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_option) {
            myAuth.signOut();
            SendUserToLoginActivity();
        }

        if (item.getItemId() == R.id.main_create_group_option) {
            RequestNewGroup();

        }

        if (item.getItemId() == R.id.main_chat_option) {
            SendUserToChatActivity();

        }

        if (item.getItemId() == R.id.main_settings_option) {
            SendUserToSettingsActivity();

        }

        if (item.getItemId() == R.id.main_find_friends_option) {
            SendUserToFindFriendsActivity();

        }

        return true;
    }

    private void RequestNewGroup() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        dialogBuilder.setTitle("Enter Group Name :");

        final EditText groupField = new EditText(MainActivity.this);
        groupField.setHint("e.g Coding Cafe");
        dialogBuilder.setView(groupField);

        dialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String group = groupField.getText().toString();
                if (TextUtils.isEmpty(group)) {
                    Toast.makeText(MainActivity.this, "Please write Group Name..", Toast.LENGTH_SHORT).show();
                } else {
                    CreateNewGroup(group);

                }

            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.cancel();

            }
        });

        dialogBuilder.show();
    }

    private void CreateNewGroup(final String group) {
        dataBaseReference.child("Groups").child(group).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, group + " is created successfully.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void SendUserToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void SendUserToSettingsActivity() {
        Intent settingsOfIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsOfIntent);
    }

    private void SendUserToFindFriendsActivity() {
        Intent mYfindFriendsIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(mYfindFriendsIntent);
    }

    private void SendUserToChatActivity() {
        Intent mYfindFriendsIntent = new Intent(MainActivity.this, ChatMainActivity.class);
        startActivity(mYfindFriendsIntent);
    }
}
