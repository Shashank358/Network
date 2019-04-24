package com.ssproduction.shashank.network;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.Adapter.SelectPageAdapter;
import com.ssproduction.shashank.network.Utils.AllPages;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectPageActivity extends AppCompatActivity {

    private TextView pageTile, userName;
    private CircleImageView profile;
    private Toolbar mToolbar;

    private List<AllPages> mPages;
    private SelectPageAdapter adapter;

    private RecyclerView selectList;
    private LinearLayoutManager layoutManager;

    private DatabaseReference pageDatabase, mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_page);

        overridePendingTransition(R.anim.slide_left, R.anim.no_animation);

        mPages = new ArrayList<>();
        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mToolbar = (Toolbar) findViewById(R.id.select_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectList = (RecyclerView) findViewById(R.id.all_selection_pages_list);
        userName = (TextView) findViewById(R.id.select_page_user_name);
        profile = (CircleImageView) findViewById(R.id.select_page_user_profile);

        layoutManager = new LinearLayoutManager(this);
        selectList.setLayoutManager(layoutManager);
        selectList.setHasFixedSize(true);

        pageDatabase = FirebaseDatabase.getInstance().getReference().child("AllPages");

        pageDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mPages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    AllPages pages = snapshot.getValue(AllPages.class);
                    if (pages.getAdmin_id().equals(currentUser)){
                        mPages.add(pages);
                    }

                    SelectPageAdapter adapter = new SelectPageAdapter(getApplicationContext(), mPages);
                    selectList.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("user_id").getValue().toString();
                String image = dataSnapshot.child("user_thumbImage").getValue().toString();

                Picasso picasso = Picasso.get();
                picasso.setIndicatorsEnabled(false);
                picasso.load(image).placeholder(R.drawable.avatar).into(profile);
                userName.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
