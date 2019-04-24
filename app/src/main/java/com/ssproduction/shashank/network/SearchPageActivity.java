package com.ssproduction.shashank.network;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ssproduction.shashank.network.Adapter.AllUsersListAdapter;
import com.ssproduction.shashank.network.Adapter.IntrestsAdapter;
import com.ssproduction.shashank.network.Adapter.PagesListAdapterFrag;
import com.ssproduction.shashank.network.Utils.AllPages;
import com.ssproduction.shashank.network.Utils.Users;

import java.util.ArrayList;
import java.util.List;

public class SearchPageActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;
    private EditText searchPages;
    private List<AllPages> mPages;
    private RecyclerView allPagesList;
    private IntrestsAdapter adapter;
    private String currentUser;
    private DatabaseReference pageDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        pageDatabase = FirebaseDatabase.getInstance().getReference().child("AllPages");

        mToolbar = (Toolbar) findViewById(R.id.all_pages_search_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchPages = (EditText) findViewById(R.id.all_pages_search_edittext);
        allPagesList = (RecyclerView) findViewById(R.id.all_pages_searched_users_list);
        LinearLayoutManager manager = new GridLayoutManager(this, 2) ;
        allPagesList.setHasFixedSize(true);
        allPagesList.setLayoutManager(manager);

        searchPages.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                SearchAllPages(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPages = new ArrayList<>();
        readPages();

    }

    private void readPages() {

        pageDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPages.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    AllPages pages = snapshot.getValue(AllPages.class);
                    mPages.add(pages);
                }
                adapter = new IntrestsAdapter(getApplicationContext(), mPages);
                adapter.notifyDataSetChanged();
                allPagesList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SearchAllPages(String s) {

        Query query = pageDatabase.orderByChild("search").startAt(s).endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mPages.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    AllPages pages = snapshot.getValue(AllPages.class);
                    mPages.add(pages);
                }
                adapter = new IntrestsAdapter(getApplicationContext(), mPages);
                adapter.notifyDataSetChanged();
                allPagesList.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
