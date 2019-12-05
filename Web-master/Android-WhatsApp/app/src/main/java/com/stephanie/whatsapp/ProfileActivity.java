package com.stephanie.whatsapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String receiverId, senderID, presentState;

    private CircleImageView UserDP;
    private TextView profileName, ProfileStory;
    private Button sendMessageButton, DeclineMessageButton;

    private DatabaseReference UserDBReference, ChatDBReference, contactsDBReference;
    private FirebaseAuth fireBaseAuth;


    @Override
    protected void onCreate(Bundle State) {
        super.onCreate(State);
        setContentView(R.layout.activity_profile);

        fireBaseAuth = FirebaseAuth.getInstance();
        UserDBReference = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatDBReference = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactsDBReference = FirebaseDatabase.getInstance().getReference().child("Contacts");

        receiverId = getIntent().getExtras().get("visit_user_id").toString();
        senderID = fireBaseAuth.getCurrentUser().getUid();

        UserDP = findViewById(R.id.visit_profile_image);
        profileName = findViewById(R.id.visit_user_name);
        ProfileStory = findViewById(R.id.visit_profile_status);
        sendMessageButton = findViewById(R.id.send_message_request_button);
        DeclineMessageButton = findViewById(R.id.decline_message_request_button);
        presentState = "new";

        RetrieveUserInfo();
    }

    private void RetrieveUserInfo() {
        UserDBReference.child(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("image"))) {
                    String image = snapshot.child("image").getValue().toString();
                    String UserName = snapshot.child("name").getValue().toString();
                    String userStory = snapshot.child("status").getValue().toString();

                    Picasso.get().load(image).placeholder(R.drawable.profile_image).into(UserDP);
                    profileName.setText(UserName);
                    ProfileStory.setText(userStory);

                    ManageChatRequests();

                } else {
                    String UserName = snapshot.child("name").getValue().toString();
                    String userStory = snapshot.child("status").getValue().toString();

                    profileName.setText(UserName);
                    ProfileStory.setText(userStory);

                    ManageChatRequests();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

    private void ManageChatRequests() {

        ChatDBReference.child(senderID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChild(receiverId)) {
                            String request_type = snapshot.child(receiverId).child("request_type").getValue().toString();

                            if (request_type.equals("sent")) {
                                presentState = "request_sent";
                                sendMessageButton.setText("Cancel Chat Request");
                            } else if (request_type.equals("received")){
                                presentState = "request_received";
                                sendMessageButton.setText("Accept Chat Request");

                                DeclineMessageButton.setVisibility(View.VISIBLE);
                                DeclineMessageButton.setEnabled(true);

                                DeclineMessageButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CancelChatRequest();
                                    }
                                });

                            }

                        }
                        else {
                            contactsDBReference.child(senderID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            if (snapshot.hasChild(receiverId)) {
                                                presentState = "friends";
                                                sendMessageButton.setText("Remove this Contact");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });

        if (!senderID.equals(receiverId)) {
            sendMessageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessageButton.setEnabled(false);

                    if (presentState.equals("new")) {
                        SendChatRequests();
                    }
                    if (presentState.equals("request_sent")) {
                        CancelChatRequest();
                    }
                    if (presentState.equals("request_received")) {
                        AcceptChatRequest();
                    }
                    if (presentState.equals("friends")) {
                        RemoveSpecificContact();
                    }
                }
            });

        } else {
            sendMessageButton.setVisibility(View.INVISIBLE);
        }

    }

    private void RemoveSpecificContact() {
        contactsDBReference.child(senderID).child(receiverId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            contactsDBReference.child(receiverId).child(senderID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendMessageButton.setEnabled(true);
                                                presentState = "new";
                                                sendMessageButton.setText("Send Message");

                                                DeclineMessageButton.setVisibility(View.INVISIBLE);
                                                DeclineMessageButton.setEnabled(false);
                                            }

                                        }
                                    });
                        }

                    }
                });

    }

    private void AcceptChatRequest() {
        contactsDBReference.child(senderID).child(receiverId)
                .child("Contacts")
                .setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            contactsDBReference.child(receiverId).child(senderID)
                                    .child("Contacts")
                                    .setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                ChatDBReference.child(senderID).child(receiverId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    ChatDBReference.child(receiverId).child(senderID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    sendMessageButton.setEnabled(true);
                                                                                    presentState = "friends";
                                                                                    sendMessageButton.setText("Remove this Comtact");

                                                                                    DeclineMessageButton.setVisibility(View.INVISIBLE);
                                                                                    DeclineMessageButton.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }

                                        }
                                    });
                        }

                    }
                });

    }

    private void CancelChatRequest() {
        ChatDBReference.child(senderID).child(receiverId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            ChatDBReference.child(receiverId).child(senderID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendMessageButton.setEnabled(true);
                                                presentState = "new";
                                                sendMessageButton.setText("Send Message");

                                                DeclineMessageButton.setVisibility(View.INVISIBLE);
                                                DeclineMessageButton.setEnabled(false);
                                            }

                                        }
                                    });
                        }

                    }
                });
    }

    private void SendChatRequests() {
        ChatDBReference.child(senderID).child(receiverId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ChatDBReference.child(receiverId).child(senderID)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendMessageButton.setEnabled(true);
                                                presentState = "request_sent";
                                                sendMessageButton.setText("Cancel Chat Request");
                                            }
                                        }
                                    });
                        }
                    }
                });

    }
}
