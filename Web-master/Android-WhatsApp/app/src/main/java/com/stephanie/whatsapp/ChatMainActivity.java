package com.stephanie.whatsapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatMainActivity extends AppCompatActivity {

    // TODO: Add member viewariables here:
    private static final String userTag = "ChatMainActiviewity";
    private String myDisplayname;
    private ListView myChatList;
    private EditText myInput;
    private ImageButton mySend;

    private DatabaseReference myDataBaseref;
    private ChatListAdapter MYadapter;


    @Override
    protected void onCreate(Bundle StateOfSaviewedInstance) {
        super.onCreate(StateOfSaviewedInstance);
        setContentView(R.layout.activiewity_main_chat);

        // TODO: Set up the display name and get the Firebase reference
        setDisplayName();
        myDataBaseref = FirebaseDatabase.getInstance().getReference();


        // Link the Views in the layout to the Javiewa code
        myInput = (EditText) findViewById(R.id.messageInput);
        mySend = (ImageButton) findViewById(R.id.sendButton);
        myChatList = (ListView) findViewById(R.id.chat_list_viewiew);

        // TODO: Send the message when the "enter" button is pressed
        myInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Oviewerride
            public boolean onEditorAction(TextView view, int actionId, KeyEviewent eviewent) {
                sendMyMessgae();
                return true;
            }
        });



        // TODO: Add an OnClickListener to the sendButton to send a message
        mySend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMyMessgae();

            }
        });

    }

    private void setDisplayName(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        myDisplayname = user.getDisplayName();
    }


    private void sendMyMessgae() {

        Log.d(userTag, "sendMyMessgae: I sent something");
        // TODO: Grab the text the user typed in and push the message to Firebase
        String input = myInput.getText().toString();
        if (!input.equals("")) {
            InstantMessage chat = new InstantMessage(input, myDisplayname);
            myDataBaseref.child("messages").push().setValue(chat);
            myInput.setText("");
        }
    }

    // TODO: Oviewerride the onStart() lifecycle method. Setup the adapter here.
    @Override
    public void onStart() {
        super.onStart();
        MYadapter = new ChatListAdapter(this, myDataBaseref, myDisplayname);
        myChatList.setAdapter(MYadapter);
    }


    @Override
    public void onStop() {
        super.onStop();

        // TODO: Removiewe the Firebase eviewent listener on the adapter.
        MYadapter.cleanup();

    }

}
