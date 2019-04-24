package com.ssproduction.shashank.network.Fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssproduction.shashank.network.Adapter.groupAdapter.GroupListAdapter;
import com.ssproduction.shashank.network.AddPeopleActivity;
import com.ssproduction.shashank.network.EditGroupActivity;
import com.ssproduction.shashank.network.GroupChatActivity;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.AllGroups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupChatFrag extends Fragment {

    private RelativeLayout createGroupRel;
    private DatabaseReference mDatabase;
    private String currentUser, group_pushId;

    private RecyclerView groupList;
    private List<AllGroups> mGroups;
    private GroupListAdapter adapter;

    private ProgressDialog dialog;

    public GroupChatFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_chat, container, false);

        dialog = new ProgressDialog(getActivity());
        createGroupRel = (RelativeLayout) view.findViewById(R.id.group_chat_create_relative);

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        final DatabaseReference groupDatabase = FirebaseDatabase.getInstance().getReference().child("AllGroups");

        groupList = (RecyclerView) view.findViewById(R.id.all_chat_groups_list);
        LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        groupList.setHasFixedSize(true);
        groupList.setLayoutManager(layoutManager);


        mGroups = new ArrayList<>();

        groupDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    mGroups.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AllGroups groups = snapshot.getValue(AllGroups.class);
                        mGroups.add(groups);
                    }
                    adapter = new GroupListAdapter(mGroups, getContext());
                    groupList.setAdapter(adapter);
                    adapter.setOnItemClick(new GroupListAdapter.OnItemClick() {
                        @Override
                        public void getPosition(final String admin_id, final String group_id) {
                            final String[] items = {"Edit Group", "Delete Group", "Add Members", "Block", "Mute"};
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                            alertDialog.setTitle("Select Options");
                            alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if ("Edit Group".equals(items[which])){
                                        Toast.makeText(getActivity(), "Edit post is not available yet", Toast.LENGTH_SHORT).show();

                                    }else if ("Delete Group".equals(items[which])){
                                        if (admin_id.equals(currentUser)){
                                            groupDatabase.child(group_id).removeValue();
                                        }else {
                                            Toast.makeText(getActivity(), "only admin can perform this action", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
                            });
                            alertDialog.show();


                        }
                    });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        createGroupRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group_pushId = groupDatabase.push().getKey();

                dialog.setMessage("creating group");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                Map groupMap = new HashMap();
                groupMap.put("group_admin", currentUser);
                groupMap.put("group_title", "default");
                groupMap.put("group_cover", "default");
                groupMap.put("group_tag", "default");
                groupMap.put("group_id", group_pushId);

                groupDatabase.child(group_pushId).updateChildren(groupMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        if (databaseError == null){
                            dialog.dismiss();
                            Intent chatIntent = new Intent(getActivity(), GroupChatActivity.class);
                            chatIntent.putExtra("group_id", group_pushId);
                            startActivity(chatIntent);
                        }
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.group_chat_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.group_chat_add_members_item){
            Intent addIntent = new Intent(getActivity(), AddPeopleActivity.class);
            startActivity(addIntent);
        }

        return true;
    }
}
