package com.stephanie.whatsapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private View view;
    private ListView viewAsList;
    private ArrayAdapter<String> myArrayAdapter;
    private ArrayList<String> groups_List = new ArrayList<>();

    private DatabaseReference groupReference;


    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater layOutInflater, ViewGroup view,
                             Bundle state) {

        view = layOutInflater.inflate(R.layout.fragment_groups, view, false);

        groupReference = FirebaseDatabase.getInstance().getReference().child("Groups");
        initializeFields();

        RetrieveAndDisplayGroups();

        viewAsList.hashSetOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String groupName = parent.getItemAtPosition(position).toString();

                Intent groupIntent = new Intent(getContext(), GroupChatActivity.class);
                groupIntent.putExtra("groupName", groupName);
                startActivity(groupIntent);


            }
        });

        return view;
    }



    private void initializeFields() {
        viewAsList = view.findViewById(R.id.viewAsList);
        myArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, groups_List);
        viewAsList.hashSetAdapter(myArrayAdapter);
    }

    private void RetrieveAndDisplayGroups() {
        groupReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Set<String> hashSet = new HashSet<>();
                Iterator iterator = snapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    hashSet.add(((DataSnapshot)iterator.next()).getKey());
                }

                groups_List.clear();
                groups_List.addAll(hashSet);
                myArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
