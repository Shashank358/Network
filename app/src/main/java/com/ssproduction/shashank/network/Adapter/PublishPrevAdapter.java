package com.ssproduction.shashank.network.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.ssproduction.shashank.network.PublishPrevActivity;
import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.PublishPrev;

import java.util.List;

public class PublishPrevAdapter extends RecyclerView.Adapter<PublishPrevAdapter.ViewHolder> {

    private List<PublishPrev> mPublish;
    private Context mContext;

    public PublishPrevAdapter(List<PublishPrev> mPublish, Context mContext) {
        this.mPublish = mPublish;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public PublishPrevAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.publish_image_single_preview_layout, viewGroup, false);

        return new PublishPrevAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublishPrevAdapter.ViewHolder viewHolder, int i) {

        final PublishPrev publishPrev = mPublish.get(i);

        Picasso picasso = Picasso.get();
        picasso.load(publishPrev.getUri()).placeholder(R.drawable.publish_prev_menu_back)
                .into(viewHolder.publishImage);
    }

    @Override
    public int getItemCount() {
        return mPublish.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView publishImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            publishImage = (ImageView) itemView.findViewById(R.id.publish_single_image_preview);
        }
    }
}
