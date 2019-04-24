package com.ssproduction.shashank.network.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.ssproduction.shashank.network.R;
import com.ssproduction.shashank.network.Utils.Publish;

import java.util.List;

public class VideoClipAdapter extends RecyclerView.Adapter<VideoClipAdapter.ViewHolder> {

    private Context mContext;
    private List<Publish> mPublis;

    public VideoClipAdapter(Context mContext, List<Publish> mPublis) {
        this.mContext = mContext;
        this.mPublis = mPublis;
    }

    @NonNull
    @Override
    public VideoClipAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.timeline_single_video_card, viewGroup, false);

        return new VideoClipAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoClipAdapter.ViewHolder viewHolder, int i) {

        Publish publish = mPublis.get(i);

        Uri videoUri = Uri.parse(publish.getPublish());
        viewHolder.videoView.setVideoURI(videoUri);
        viewHolder.videoView.requestFocus();
        viewHolder.videoView.start();

    }

    @Override
    public int getItemCount() {
        return mPublis.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private VideoView videoView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            videoView = (VideoView) itemView.findViewById(R.id.timeline_video_videoview);
        }
    }
}
