package com.ssproduction.shashank.network.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class ShowAnswersAdapter extends RecyclerView.Adapter<ShowAnswersAdapter.Adapter> {
    @NonNull
    @Override
    public ShowAnswersAdapter.Adapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ShowAnswersAdapter.Adapter adapter, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class Adapter extends RecyclerView.ViewHolder {



        public Adapter(@NonNull View itemView) {
            super(itemView);
        }
    }
}
