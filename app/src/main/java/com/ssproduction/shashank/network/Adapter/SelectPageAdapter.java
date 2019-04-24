package com.ssproduction.shashank.network.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.ssproduction.shashank.network.PublishPrevActivity;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.AllPages;

import java.util.List;

public class SelectPageAdapter extends RecyclerView.Adapter<SelectPageAdapter.ViewHolder> {

    private Context mContext;
    private List<AllPages> mPages;

    public SelectPageAdapter(Context mContext, List<AllPages> mPages) {
        this.mContext = mContext;
        this.mPages = mPages;
    }

    @NonNull
    @Override
    public SelectPageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.page_single_selection_layout, viewGroup, false);

        return new SelectPageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SelectPageAdapter.ViewHolder viewHolder, int i) {

        final DatabaseReference pageDatabase = FirebaseDatabase.getInstance().getReference().child("AllPages");
        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final AllPages pages = mPages.get(i);

        viewHolder.pageTitle.setText(pages.getTitle());

        viewHolder.pageTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.pageTitle.getText().toString().equals(pages.getTitle())){
                    Intent pageIntent = new Intent(mContext, PublishPrevActivity.class);
                    pageIntent.putExtra("page_id", pages.getPush_id());
                    pageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(pageIntent);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mPages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView pageTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pageTitle = (TextView) itemView.findViewById(R.id.page_selection_title);
        }
    }
}
