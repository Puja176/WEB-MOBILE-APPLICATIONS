package com.stephanie.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    
    private Button accountUpdate;
    private EditText cn, userStory;
    private CircleImageView profilePhoto;

    private String presentUserId;
    private FirebaseAuth fireBaseAuth;
    private DatabaseReference dbRootRef;
    private static final int GalleryPick = 1;

    private StorageReference DBRef;
    private ProgressDialog LoadBar;

    private Toolbar Settings;

    @Override
    protected void onCreate(Bundle State) {
        super.onCreate(State);
        setContentView(R.layout.activity_settings);

        fireBaseAuth = FirebaseAuth.getInstance();
        presentUserId = fireBaseAuth.getCurrentUser().getUid();
        dbRootRef = FirebaseDatabase.getInstance().getReference();
        DBRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        initializeFields();

        cn.setVisibility(View.INVISIBLE);


        accountUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });

        RetrieveUserInformation();

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, GalleryPick);
            }
        });
    }




    private void initializeFields() {

        accountUpdate = findViewById(R.id.update_settings_button);
        cn = findViewById(R.id.set_user_name);
        userStory = findViewById(R.id.set_profile_status);
        profilePhoto = findViewById(R.id.set_profile_image);
        LoadBar = new ProgressDialog(this);
        Settings = findViewById(R.id.settings_toolbar);
        setSupportActionBar(Settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");
    }

    @Override
    protected void onActivityResult(int request, int result, @Nullable Intent data) {
        super.onActivityResult(request, result, data);

        if (request == GalleryPick && result == RESULT_OK && data!=null) {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (request == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (result == RESULT_OK) {

                LoadBar.setTitle("Set Profile Image");
                LoadBar.setMessage("Please wait, uploading you profile image.");
                LoadBar.setCanceledOnTouchOutside(false);
                LoadBar.show();

                Uri resultOfUri = result.getUri();

                StorageReference path = DBRef.child(presentUserId + ".jpg");

                path.putFile(resultOfUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this, "Profile Image uploaded successfully.", Toast.LENGTH_SHORT).show();

                            final String downloadLink = task.getResult().getDownloadUrl().toString();
                            dbRootRef.child("Users").child(presentUserId).child("image")
                                    .setValue(downloadLink)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SettingsActivity.this, "Image saved in Database, Successfully.", Toast.LENGTH_SHORT).show();
                                                LoadBar.dismiss();
                                            } else {
                                                String text = task.getException().toString();
                                                Toast.makeText(SettingsActivity.this, "Error: " + text, Toast.LENGTH_SHORT).show();
                                                LoadBar.dismiss();
                                            }
                                        }
                                    });
                        } else {
                            String text = task.getException().toString();
                            Toast.makeText(SettingsActivity.this, "Error: " + text, Toast.LENGTH_SHORT).show();
                            LoadBar.dismiss();
                        }
                    }
                });
            }

        }
    }

    private void UpdateSettings() {
        String setCn = cn.getText().toString();
        String setStory = userStory.getText().toString();

        if (TextUtils.isEmpty(setCn)) {
            Toast.makeText(this, "Please write your username..", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(setStory)) {
            Toast.makeText(this, "Please write your status..", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid", presentUserId);
            profileMap.put("name", setCn);
            profileMap.put("status", setStory);
            dbRootRef.child("Users").child(presentUserId).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                SendUserToMainActivity();
                                Toast.makeText(SettingsActivity.this, "Profile Updated Successfully..", Toast.LENGTH_SHORT).show();
                            } else {
                                String text = task.getException().toString();
                                Toast.makeText(SettingsActivity.this, "Error: " +text, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void RetrieveUserInformation() {
        dbRootRef.child("Users").child(presentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if ((snapshot.exists()) && (snapshot.hasChild("name")  && (snapshot.hasChild("image")))) {
                            String getCn = snapshot.child("name").getValue().toString();
                            String retrieveStory = snapshot.child("status").getValue().toString();
                            String getDp = snapshot.child("image").getValue().toString();

                            cn.setText(getCn);
                            userStory.setText(retrieveStory);
                            Picasso.get().load(getDp).into(profilePhoto);


                        } else if ((snapshot.exists()) && (snapshot.hasChild("name"))) {
                            String getCn = snapshot.child("name").getValue().toString();
                            String retrieveStory = snapshot.child("status").getValue().toString();

                            cn.setText(getCn);
                            userStory.setText(retrieveStory);
                        } else {
                            cn.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingsActivity.this, "Please set & update your profile information", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void SendUserToMainActivity() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
