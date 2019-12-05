package com.stephanie.whatsapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private ImageButton SendButton;
    private EditText userInput;
    private ScrollView myScrollView;
    private TextView showMessages;

    private FirebaseAuth fireBaseAuth;
    private DatabaseReference UserReference, GroupReference, MessageRef;

    private String CurrentGroup, CurrentUser, presentuserName, presentDate, presentTime;

    @Override
    protected void onCreate(Bundle State) {
        super.onCreate(State);
        setContentView(R.layout.activity_group_chat);

        CurrentGroup = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(GroupChatActivity.this, CurrentGroup, Toast.LENGTH_SHORT).show();

        fireBaseAuth = FirebaseAuth.getInstance();
        CurrentUser = fireBaseAuth.getCurrentUser().getUid();
        UserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(CurrentGroup);

        initializeFields();
        GetUserInfo();

        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveMessageInfoToDatabase();
                userInput.setText("");
                myScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        GroupReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String s) {
                if (snapshot.exists()) {
                    DisplayMessages(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String s) {
                if (snapshot.exists()) {
                    DisplayMessages(snapshot);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeFields() {
        myToolBar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setTitle(CurrentGroup);

        SendButton = findViewById(R.id.send_text_button);
        userInput = findViewById(R.id.input_group_text);
        showMessages = findViewById(R.id.group_chat_text_display);
        myScrollView = findViewById(R.id.my_scroll_view);
    }

    private void GetUserInfo() {
        UserReference.child(CurrentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot .exists()) {
                    presentuserName = snapshot.child("name").getValue().toString();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void SaveMessageInfoToDatabase() {
        String text = userInput.getText().toString();
        String textKey = GroupReference.push().getKey();

        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "Please write a text", Toast.LENGTH_SHORT).show();
        } else {
            Calendar date = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            presentDate = currentDateFormat.format(date.getTime());

            Calendar timeNow = Calendar.getInstance();
            SimpleDateFormat presentTimeFormat = new SimpleDateFormat("hh:mm a");
            presentTime = presentTimeFormat.format(timeNow.getTime());

            HashMap<String, Object> groupKey = new HashMap<>();
            GroupReference.updateChildren(groupKey);

            MessageRef = GroupReference.child(textKey);

            HashMap<String, Object> textInfoMap = new HashMap<>();
                textInfoMap.put("name", presentuserName);
                textInfoMap.put("text", text);
                textInfoMap.put("date", presentDate);
                textInfoMap.put("time", presentTime);

                MessageRef.updateChildren(textInfoMap);
        }
    }

    private void DisplayMessages(DataSnapshot snapshot) {
        Iterator iterator = snapshot.getChildren().iterator();

        while(iterator.hasNext()) {
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();

            showMessages.append(chatName + " :\n"+ chatMessage + "\n" + chatTime + "   " + chatDate + "\n\n\n");
            myScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }
}
