package com.stephanie.whatsapp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private View contacts;
    private RecyclerView myContacts;

    private DatabaseReference myContactsReference, UsersReference;
    private FirebaseAuth fireBaseAuth;
    private String currentID;


    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater layOutInflater, ViewGroup view,
                             Bundle State) {
        // Inflate the layout for this fragment
        contacts = layOutInflater.inflate(R.layout.fragment_contacts, view, false);

        myContacts = contacts.findViewById(R.id.contacts_list);
        myContacts.setLayoutManager(new LinearLayoutManager(getContext()));

        fireBaseAuth = FirebaseAuth.getInstance();
        currentID = fireBaseAuth.getCurrentUser().getUid();

        myContactsReference = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentID);
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");


        return contacts;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions recyclerOptions =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(myContactsReference, Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, contactsHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, contactsHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final contactsHolder myHolder, int pos, @NonNull Contacts model) {
                String usersIDs = getRef(pos).getKey();

                UsersReference.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChild("image")) {
                            String UserPhoto = snapshot.child("image").getValue().toString();
                            String UserName = snapshot.child("name").getValue().toString();
                            String UserStory = snapshot.child("status").getValue().toString();

                            myHolder.userName.setText(UserName);
                            myHolder.userStatus.setText(UserStory);
                            Picasso.get().load(UserPhoto).placemyHolder(R.drawable.profile_image).into(myHolder.profileImage);
                        } else {
                            String UserName = snapshot.child("name").getValue().toString();
                            String UserStory = snapshot.child("status").getValue().toString();

                            myHolder.userName.setText(UserName);
                            myHolder.userStatus.setText(UserStory);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public contactsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                contactsHolder viewmyHolder = new contactsHolder(view);
                return viewmyHolder;
            }
        };

        myContacts.setAdapter(adapter);
        adapter.startListening();
    }


    public static class contactsHolder extends RecyclerView.ViewHolder {

        TextView userName, userStatus;
        CircleImageView profileImage;

        public contactsHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}
