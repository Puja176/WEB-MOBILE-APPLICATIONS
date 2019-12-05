package com.stephanie.whatsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String ReceiverID, ReceiverName, ReceiverImage, SenderID;

    private TextView name, LastSeen;
    private CircleImageView Image;

    private Toolbar chatBar;

    private FirebaseAuth fireBaseAuth;
    private DatabaseReference dataBaseReference;

    private ImageButton sendMessagebutton;
    private EditText InputMessage;

    private final List<Messages> ListOfMessages = new ArrayList<>();
    private LinearLayoutManager mYLinearLayoutManager;
    private MessageAdapter mYMessageAdapter;
    private RecyclerView userMessages;

    @Override
    protected void onCreate(Bundle instanceState) {
        super.onCreate(instanceState);
        setContentView(R.layout.activity_chat);

        fireBaseAuth = FirebaseAuth.getInstance();
        SenderID = fireBaseAuth.getCurrentUser().getUid();
        dataBaseReference = FirebaseDatabase.getInstance().getReference();

        ReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        ReceiverName = getIntent().getExtras().get("visit_user_name").toString();
        ReceiverImage = getIntent().getExtras().get("visit_image").toString();

        intializeControllers();

        name.setText(ReceiverName);
        Picasso.get().load(ReceiverImage).placeholder(R.drawable.profile_image).into(Image);

        sendMessagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();

            }
        });
    }

    private void intializeControllers() {
        chatBar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater =  (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);

        Image = findViewById(R.id.custom_profile_image);
        name = findViewById(R.id.custom_profile_name);
        LastSeen = findViewById(R.id.custom_user_last_seen);

        sendMessagebutton = findViewById(R.id.send_message_btn);
        InputMessage = findViewById(R.id.input_message);

        mYMessageAdapter = new MessageAdapter(ListOfMessages);
        userMessages = findViewById(R.id.private_messages_list_of_users);

        mYLinearLayoutManager = new LinearLayoutManager(this);
        userMessages.setLayoutManager(mYLinearLayoutManager);
        userMessages.setAdapter(mYMessageAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        dataBaseReference.child("Messages").child(SenderID).child(ReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Messages messages = dataSnapshot.getValue(Messages.class);

                        ListOfMessages.add(messages);

                        mYMessageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void SendMessage() {
        String Message = InputMessage.getText().toString();

        if (TextUtils.isEmpty(Message)) {
            Toast.makeText(this, "Please type a message.", Toast.LENGTH_SHORT).show();
        } else {
            String messageSenderRef = "Messages/" + SenderID + "/" + ReceiverID;
            String messageReceiverRef = "Messages/" + ReceiverID + "/" + SenderID;

            DatabaseReference userMessageKeyRef = dataBaseReference.child("Messages")
                    .child(SenderID).child(ReceiverID).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map MessageBody = new HashMap();
            MessageBody.put("message", Message);
            MessageBody.put("type", "text");
            MessageBody.put("from", SenderID);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, MessageBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, MessageBody);

            dataBaseReference.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChatActivity.this, "Message Sent.", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(ChatActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                    InputMessage.setText("");

                }
            });
        }

    }
}
