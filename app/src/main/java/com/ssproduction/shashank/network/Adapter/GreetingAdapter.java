package com.ssproduction.shashank.network.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.Greetings;

import java.util.List;

public class GreetingAdapter extends RecyclerView.Adapter {

    private Context context;

    public GreetingAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.greeting_single_layout, viewGroup, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ((ViewHolder) viewHolder).bindView(i);
    }

    @Override
    public int getItemCount() {
        return Greetings.greeting.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        Context context;


        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            this.context = context;
            imageView = (ImageView) itemView.findViewById(R.id.greeting_image);
        }

        public void bindView(int position){
            imageView.setImageResource(Greetings.greeting[position]);
        }
    }
}
