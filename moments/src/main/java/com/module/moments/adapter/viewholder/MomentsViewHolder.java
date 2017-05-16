package com.module.moments.adapter.viewholder;

import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.module.moments.R;
import com.module.moments.widgets.CommentListView;
import com.module.moments.widgets.ExpandTextView;
import com.module.moments.widgets.FavorListView;
import com.module.moments.widgets.PraiseListView;
import com.module.moments.widgets.SnsPopupWindow;
import com.module.moments.widgets.videolist.model.VideoLoadMvpView;
import com.module.moments.widgets.videolist.widget.TextureVideoView;

/**
 * Created by yiw on 2016/8/16.
 */
public abstract class MomentsViewHolder extends RecyclerView.ViewHolder implements VideoLoadMvpView {

    public final static int TYPE_URL = 1;
    public final static int TYPE_IMAGE = 2;
    public final static int TYPE_VIDEO = 3;

    public int viewType;

    public ImageView headIv;
    public TextView nameTv;
    /** 动态的内容 */
    public ExpandTextView contentTv;
    public TextView timeTv;
    public TextView deleteBtn;
    /** 点赞列表*/
    public FavorListView favorListView;


    /** 评论列表 */
    public CommentListView commentList;
    public TextView commentDescription;

    public TextView commentBtn;
    // ===========================
    public SnsPopupWindow snsPopupWindow;

    public MomentsViewHolder(View itemView, int viewType) {
        super(itemView);
        this.viewType = viewType;

        ViewStub viewStub = (ViewStub) itemView.findViewById(R.id.viewStub);

        initSubView(viewType, viewStub);

        headIv = (ImageView) itemView.findViewById(R.id.headIv);
        nameTv = (TextView) itemView.findViewById(R.id.nameTv);

        contentTv = (ExpandTextView) itemView.findViewById(R.id.contentTv);
        timeTv = (TextView) itemView.findViewById(R.id.timeTv);
        deleteBtn = (TextView) itemView.findViewById(R.id.deleteBtn);
        favorListView = (FavorListView) itemView.findViewById(R.id.favorView);

        commentList = (CommentListView)itemView.findViewById(R.id.commentList);
        commentDescription=(TextView)itemView.findViewById(R.id.commentDes);
        commentBtn=(TextView)itemView.findViewById(R.id.commentBtn);

        snsPopupWindow = new SnsPopupWindow(itemView.getContext());

    }

    public abstract void initSubView(int viewType, ViewStub viewStub);

    @Override
    public TextureVideoView getVideoView() {
        return null;
    }

    @Override
    public void videoBeginning() {

    }

    @Override
    public void videoStopped() {

    }

    @Override
    public void videoPrepared(MediaPlayer player) {

    }

    @Override
    public void videoResourceReady(String videoPath) {

    }
}
