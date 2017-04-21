package com.module.moments.adapter.viewholder;

import android.view.View;
import android.view.ViewStub;

import com.module.moments.R;
import com.module.moments.widgets.MomentsVideoView;

/**
 * Created by suneee on 2016/8/16.
 */
public class VideoViewHolder extends MomentsViewHolder {

    public MomentsVideoView videoView;

    public VideoViewHolder(View itemView){
        super(itemView, TYPE_VIDEO);
    }

    @Override
    public void initSubView(int viewType, ViewStub viewStub) {
        if(viewStub == null){
            throw new IllegalArgumentException("viewStub is null...");
        }
        
        viewStub.setLayoutResource(R.layout.viewstub_videobody);
        View subView = viewStub.inflate();

        MomentsVideoView videoBody = (MomentsVideoView) subView.findViewById(R.id.videoView);
        if(videoBody!=null){
            this.videoView = videoBody;
        }
    }
}
