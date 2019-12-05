package com.stephanie.whatsapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;

    private DatabaseReference chatDBRef, UserDBRef, ContactsDBRef;

    private FirebaseAuth fireBaseAuth;
    private String presentUserId;


    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater layOut, ViewGroup ViewGroupContainer,
                             Bundle State) {
        // Inflate the layout for this fragment
        view = layOut.inflate(R.layout.fragment_request, ViewGroupContainer, false);

        fireBaseAuth = FirebaseAuth.getInstance();
        presentUserId = fireBaseAuth.getCurrentUser().getUid();

        chatDBRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        UserDBRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ContactsDBRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        recyclerView = view.findViewById(R.id.chat_requests_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> choices =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatDBRef.child(presentUserId), Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts, RequestViewHolder> fireBaseAdapter =
                new FirebaseRecyclerAdapter<Contacts, RequestViewHolder>(choices) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestViewHolder myHolder, int pos, @NonNull Contacts model) {
                        myHolder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
                        myHolder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.VISIBLE);

                        final String list_user_id = getRef(pos).getKey(); // Gets Chatrequest / userID / first item.

                        final DatabaseReference getTypeRef = getRef(pos).child("request_type").getRef();
                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String type = snapshot.getValue().toString();

                                    if (type.equals("received")) {
                                        UserDBRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot snapshot) {
                                                if (snapshot.hasChild("image")) {

                                                    final String requestProfileImage = snapshot.child("image").getValue().toString();
                                                    Picasso.get().load(requestProfileImage).into(myHolder.profilePhoto);

                                                }

                                                final String requestUserName = snapshot.child("name").getValue().toString();
                                                final String requestUserStatus = snapshot.child("status").getValue().toString();

                                                myHolder.cn.setText(requestUserName);
                                                myHolder.userStory.setText("wants to connect with you.");

                                                myHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        CharSequence choices[] = new CharSequence[] {
                                                                "Accept",
                                                                "Cancel"
                                                        };

                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                        builder.setTitle(requestUserName + " Chat Request");
                                                        builder.setItems(choices, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                if (which == 0) {
                                                                    ContactsDBRef.child(presentUserId).child(list_user_id).child("Contact")
                                                                            .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                ContactsDBRef.child(list_user_id).child(presentUserId).child("Contact")
                                                                                        .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            chatDBRef.child(presentUserId).child(list_user_id)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                chatDBRef.child(list_user_id).child(presentUserId)
                                                                                                                        .removeValue()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                if (task.isSuccessful()) {
                                                                                                                                    Toast.makeText(getContext(), "New Contact Saved", Toast.LENGTH_SHORT).show();
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
                                                                        }
                                                                    });

                                                                }

                                                                if (which == 1) {
                                                                    chatDBRef.child(presentUserId).child(list_user_id)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        chatDBRef.child(list_user_id).child(presentUserId)
                                                                                                .removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        if (task.isSuccessful()) {
                                                                                                            Toast.makeText(getContext(), "Contact Deleted", Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                }
                                                                            });


                                                                }

                                                            }
                                                        });
                                                        builder.show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError error) {

                                            }
                                        });

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        RequestViewHolder myHolder = new RequestViewHolder(view);
                        return myHolder;
                    }
                };

        recyclerView.setAdapter(fireBaseAdapter);
        fireBaseAdapter.startListening();


    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        TextView cn, userStory;
        CircleImageView profilePhoto;
        Button accept, cancel;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            cn = itemView.findViewById(R.id.user_profile_name);
            userStory = itemView.findViewById(R.id.user_status);
            profilePhoto = itemView.findViewById(R.id.users_profile_image);
            accept = itemView.findViewById(R.id.request_accept_btn);
            cancel = itemView.findViewById(R.id.request_cancel_btn);
        }
    }
}


























