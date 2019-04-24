package com.ssproduction.shashank.network.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.Publish;
import com.ssproduction.shashank.network.Utils.multiPublish;

import java.util.List;

public class PublishShowAdapter extends RecyclerView.Adapter<PublishShowAdapter.ViewHolder> {

    private List<multiPublish> mMultiPublish;
    private Context mContext;

    public PublishShowAdapter(List<multiPublish> mMultiPublish, Context mContext) {
        this.mMultiPublish = mMultiPublish;
        this.mContext = mContext;
    }



    @NonNull
    @Override
    public PublishShowAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.publish_image_single_preview_layout, viewGroup, false);

        return new PublishShowAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublishShowAdapter.ViewHolder viewHolder, int i) {

        final multiPublish publish = mMultiPublish.get(i);
        Picasso picasso = Picasso.get();
        picasso.load(publish.getPublish_thumb()).into(viewHolder.postImage);

    }

    @Override
    public int getItemCount() {
        return mMultiPublish.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView postImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = (ImageView) itemView.findViewById(R.id.publish_single_image_preview);
        }
    }
}
