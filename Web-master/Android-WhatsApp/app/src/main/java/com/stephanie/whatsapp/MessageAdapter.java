package com.stephanie.whatsapp;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Texts> ListOfTexts;
    private FirebaseAuth fireBaseAuth;
    private DatabaseReference DataBaseRef;

    public MessageAdapter (List<Texts> ListOfTexts) {
        this.ListOfTexts = ListOfTexts;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView sendText, receiveText;
        public CircleImageView userProfileImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            sendText = itemView.findViewById(R.id.sender_message_text);
            receiveText = itemView.findViewById(R.id.receiver_message_text);
            userProfileImage = itemView.findViewById(R.id.message_profile_image);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup view, int i) {
        View view = LayoutInflater.from(view.getContext())
                .inflate(R.layout.custom_Texts_layout, view, false);

        fireBaseAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i) {

        String senderId = fireBaseAuth.getCurrentUser().getUid();
        Texts Texts = ListOfTexts.get(i);

        String fromId = Texts.getFrom();
        String fromMessageCategory = Texts.getType();

        DataBaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromId);

        DataBaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(snapshot snapshot) {
                if (snapshot.hasChild("image")) {
                    String Image = snapshot.child("image").getValue().toString();
                    Picasso.get().load(Image).placeholder(R.drawable.profile_image).into(messageViewHolder.userProfileImage);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        if (fromMessageCategory.equals("text")) {
            messageViewHolder.receiveText.setVisibility(View.INVISIBLE);
            messageViewHolder.userProfileImage.setVisibility(View.INVISIBLE);

            if (fromId.equals(senderId)) {
                messageViewHolder.sendText.setBackgroundResource(R.drawable.sender_Texts_layout);
                messageViewHolder.sendText.setTextColor(Color.BLACK);
                messageViewHolder.sendText.setText(Texts.getMessage());
            } else {
                messageViewHolder.sendText.setVisibility(View.INVISIBLE);

                messageViewHolder.receiveText.setVisibility(View.VISIBLE);
                messageViewHolder.userProfileImage.setVisibility(View.VISIBLE);

                messageViewHolder.receiveText.setBackgroundResource(R.drawable.receiver_Texts_layout);
                messageViewHolder.receiveText.setTextColor(Color.BLACK);
                messageViewHolder.receiveText.setText(Texts.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return ListOfTexts.size();
    }


}
