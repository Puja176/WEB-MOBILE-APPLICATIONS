package com.stephanie.whatsapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {

    private Activity myActivity;
    private DatabaseReference myDataBasereferenceerence;
    private String myDisplayName;
    private ArrayList<DataSnapshot> mySnapshotList;

    private ChildEventListener mListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot snapshot, String s) {
            mySnapshotList.add(snapshot);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot snapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot snapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError error) {

        }
    };

    public ChatListAdapter(Activity myActivity, DatabaseReference reference, String myName) {
        myActivity = myActivity;
        myDisplayName = myName;
        myDataBasereferenceerence = reference.child("messages");
        myDataBasereferenceerence.addChildEventListener(mListener);

        mySnapshotList = new ArrayList<>();
    }

    static class ViewHolder {
        TextView authorName;
        TextView body;
        LinearLayout.LayoutParams params;
    }

    @Override
    public int getCount() {
        return mySnapshotList.size();
    }

    @Override
    public InstantMessage getItem(int position) {

        DataSnapshot snapshot = mySnapshotList.get(position);
        return snapshot.getValue(InstantMessage.class);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) myActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            assert inflater != null;
            convertView = inflater.inflate(R.layout.chat_msg_row, parent, false);

            final ViewHolder viewholder = new ViewHolder();
            viewholder.authorName = (TextView) convertView.findViewById(R.id.author);
            viewholder.body = (TextView) convertView.findViewById(R.id.message);
            viewholder.params = (LinearLayout.LayoutParams) viewholder.authorName.getLayoutParams();
            convertView.setTag(viewholder);
        }

        final InstantMessage message = getItem(position);
        final ViewHolder viewholder = (ViewHolder) convertView.getTag();

        boolean isMe = message.getAuthor().equals(myDisplayName);
        setChatRowAppearance(isMe, viewholder);

        String author = message.getAuthor();
        viewholder.authorName.setText(author);

        String msg = message.getMessage();
        viewholder.body.setText(msg);

        return convertView;
    }

    private void setChatRowAppearance(boolean isItMe, ViewHolder viewholder) {
        if(isItMe) {
            viewholder.params.gravity = Gravity.END;
            viewholder.authorName.setTextColor(Color.GREEN);
            viewholder.body.setBackgroundResource(R.drawable.bubble2);
        } else {
            viewholder.params.gravity = Gravity.START;
            viewholder.authorName.setTextColor(Color.BLUE);
            viewholder.body.setBackgroundResource(R.drawable.bubble1);


        }

        viewholder.authorName.setLayoutParams(viewholder.params);
        viewholder.body.setLayoutParams(viewholder.params);

    }

    public void cleanup() {
        myDataBasereferenceerence.removeEventListener(mListener);
    }


}