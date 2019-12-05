package com.stephanie.whatsapp;


import android.content.Intent;
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
public class ChatsFragment extends Fragment {

    private View privateView;
    private RecyclerView chatList;

    private DatabaseReference chatRef, UserRef;
    private FirebaseAuth myAuth;
    private String currentID;

    private String Image = "default_image";

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup group,
                             Bundle State) {
        // Inflate the layout for this fragment
         privateView = layoutInflater.inflate(R.layout.fragment_chats, group, false);
         myAuth = FirebaseAuth.getInstance();
         currentID = myAuth.getCurrentUser().getUid();

         chatRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentID);
         UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
         chatList = privateView.findViewById(R.id.chats_list);
         chatList.setLayoutManager(new LinearLayoutManager(getContext()));

         return privateView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatRef, Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model) {
                        final String usersIDs = getRef(position).getKey();
                        final String[] Image = {"default_image"};

                        UserRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.hasChild("image")) {
                                        Image[0] = dataSnapshot.child("image").getValue().toString();
                                        Picasso.get().load(Image[0]).into(holder.profile);
                                    }

                                    final String retName = dataSnapshot.child("name").getValue().toString();
                                    final String retStatus = dataSnapshot.child("status").getValue().toString();

                                    holder.myUserName.setText(retName);
                                    holder.userStory.setText("Last Seen: " + "\n" + "Date " + "Time");

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("visit_user_id", usersIDs);
                                            chatIntent.putExtra("visit_user_name", retName);
                                            chatIntent.putExtra("visit_image", Image[0]);

                                            startActivity(chatIntent);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        return new ChatsViewHolder(view);
                    }
                };

        chatList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile;
        TextView userStory, myUserName;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.users_profile_image);
            myUserName = itemView.findViewById(R.id.user_profile_name);
            userStory = itemView.findViewById(R.id.user_status);
        }
    }
}
