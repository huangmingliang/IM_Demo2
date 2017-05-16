package com.module.moments.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.module.moments.R;
import com.module.moments.activity.ImagePagerActivity;
import com.module.moments.adapter.viewholder.MomentsViewHolder;
import com.module.moments.adapter.viewholder.ImageViewHolder;
import com.module.moments.adapter.viewholder.URLViewHolder;
import com.module.moments.adapter.viewholder.VideoViewHolder;
import com.module.moments.bean.ActionItem;
import com.module.moments.bean.MomentsItem;
import com.module.moments.bean.CommentConfig;
import com.module.moments.bean.CommentItem;
import com.module.moments.bean.FavorItem;
import com.module.moments.bean.PhotoInfo;
import com.module.moments.mvp.presenter.MomentsPresenter;
import com.module.moments.utils.DatasUtil;
import com.module.moments.utils.GlideMomentsTransform;
import com.module.moments.utils.UrlUtils;
import com.module.moments.widgets.FavorListView;
import com.module.moments.widgets.MomentsVideoView;
import com.module.moments.widgets.CommentListView;
import com.module.moments.widgets.ExpandTextView;
import com.module.moments.widgets.MultiImageView;
import com.module.moments.widgets.SnsPopupWindow;
import com.module.moments.widgets.dialog.CommentDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yiwei on 16/5/17.
 */
public class MomentsAdapter extends BaseRecycleViewAdapter {

    private String TAG = "MomentsAdapter";
    public final static int TYPE_HEAD = 0;

    private static final int STATE_IDLE = 0;
    private static final int STATE_ACTIVED = 1;
    private static final int STATE_DEACTIVED = 2;
    private int videoState = STATE_IDLE;
    public static final int HEADVIEW_SIZE = 0;

    int curPlayIndex = -1;

    private MomentsPresenter presenter;
    private Context context;

    public void setMomentsPresenter(MomentsPresenter presenter) {
        this.presenter = presenter;
    }

    public MomentsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        int itemType = 0;
        MomentsItem item = (MomentsItem) datas.get(position);
        if (MomentsItem.TYPE_URL.equals(item.getType())) {
            itemType = MomentsViewHolder.TYPE_URL;
        } else if (MomentsItem.TYPE_IMG.equals(item.getType())) {
            itemType = MomentsViewHolder.TYPE_IMAGE;
        } else if (MomentsItem.TYPE_VIDEO.equals(item.getType())) {
            itemType = MomentsViewHolder.TYPE_VIDEO;
        }
        return itemType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == TYPE_HEAD) {
            View headView = LayoutInflater.from(parent.getContext()).inflate(R.layout.head_moments, parent, false);
            viewHolder = new HeaderViewHolder(headView);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_moment, parent, false);

            if (viewType == MomentsViewHolder.TYPE_URL) {
                viewHolder = new URLViewHolder(view);
            } else if (viewType == MomentsViewHolder.TYPE_IMAGE) {
                viewHolder = new ImageViewHolder(view);
            } else if (viewType == MomentsViewHolder.TYPE_VIDEO) {
                viewHolder = new VideoViewHolder(view);
            }
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        if (getItemViewType(position) == TYPE_HEAD) {
            //HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
        } else {

            final int MomentsPosition = position;      //有头部时此减一
            final MomentsViewHolder holder = (MomentsViewHolder) viewHolder;
            final MomentsItem momentsItem = (MomentsItem) datas.get(MomentsPosition);
            final String MomentsId = momentsItem.getId();
            String name = momentsItem.getUser().getName();
            String headImg = momentsItem.getUser().getHeadUrl();
            final String content = momentsItem.getContent();
            String createTime = momentsItem.getCreateTime();
            final List<FavorItem> favorDatas = momentsItem.getfavorers();
            final List<CommentItem> commentsDatas = momentsItem.getComments();
            boolean hasFavor = momentsItem.hasfavor();
            boolean hasComment = momentsItem.hasComment();

            Glide.with(context).load(headImg).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.color.bg_no_photo).transform(new GlideMomentsTransform(context)).into(holder.headIv);

            holder.nameTv.setText(name);
            holder.timeTv.setText(createTime);

            if (!TextUtils.isEmpty(content)) {
                holder.contentTv.setExpand(momentsItem.isExpand());
                holder.contentTv.setExpandStatusListener(new ExpandTextView.ExpandStatusListener() {
                    @Override
                    public void statusChange(boolean isExpand) {
                        momentsItem.setExpand(isExpand);
                    }
                });

                holder.contentTv.setText(UrlUtils.formatUrlString(content));
            }
            holder.contentTv.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);

            if (DatasUtil.curUser.getId().equals(momentsItem.getUser().getId())) {
                holder.deleteBtn.setVisibility(View.VISIBLE);
            } else {
                holder.deleteBtn.setVisibility(View.GONE);
            }
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //删除
                    if (presenter != null) {
                        presenter.deleteMoments(MomentsId);
                    }
                }
            });
            if (hasFavor) {//处理点赞列表
                holder.favorListView.setOnFavorClickListener(new FavorListView.OnFavorClickListener() {
                    @Override
                    public void onClick(int position) {
                        String userName = favorDatas.get(position).getUser().getName();
                        String userId = favorDatas.get(position).getUser().getId();
                        Toast.makeText(context, userName + " &id = " + userId, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFavor(boolean isFavor) {

                    }
                });
                holder.favorListView.setDatas(favorDatas);
                holder.favorListView.setVisibility(View.VISIBLE);
            } else {
                holder.favorListView.setVisibility(View.GONE);
            }

            if (hasComment) {//处理评论列表
                holder.commentList.setOnItemClickListener(new CommentListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(int commentPosition) {
                        CommentItem commentItem = commentsDatas.get(commentPosition);
                        if (DatasUtil.curUser.getId().equals(commentItem.getUser().getId())) {//复制或者删除自己的评论

                            CommentDialog dialog = new CommentDialog(context, presenter, commentItem, MomentsPosition);
                            dialog.show();
                        } else {//回复别人的评论
                            if (presenter != null) {
                                CommentConfig config = new CommentConfig();
                                config.MomentsPosition = MomentsPosition;
                                config.commentPosition = commentPosition;
                                config.commentType = CommentConfig.Type.REPLY;
                                config.replyUser = commentItem.getUser();
                                presenter.showEditTextBody(config);
                            }
                        }
                    }
                });
                holder.commentList.setOnItemLongClickListener(new CommentListView.OnItemLongClickListener() {
                    @Override
                    public void onItemLongClick(int commentPosition) {
                        //长按进行复制或者删除
                        CommentItem commentItem = commentsDatas.get(commentPosition);
                        CommentDialog dialog = new CommentDialog(context, presenter, commentItem, MomentsPosition);
                        dialog.show();
                    }
                });
                holder.commentList.setDatas(commentsDatas);
                String strFormat=context.getString(R.string.comment_description);
                holder.commentDescription.setText(String.format(strFormat,commentsDatas.size()));
                holder.commentDescription.setVisibility(View.VISIBLE);
                holder.commentList.setVisibility(View.VISIBLE);

            } else {
                holder.commentDescription.setVisibility(View.GONE);
                holder.commentList.setVisibility(View.GONE);
            }

            holder.commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (presenter != null) {
                        CommentConfig config = new CommentConfig();
                        config.MomentsPosition = MomentsPosition;
                        config.commentType = CommentConfig.Type.PUBLIC;
                        presenter.showEditTextBody(config);
                    }
                }
            });


           /* final SnsPopupWindow snsPopupWindow = holder.snsPopupWindow;
            //判断是否已点赞
            String curUserfavorId = MomentsItem.getCurUserfavorId(DatasUtil.curUser.getId());
            if(!TextUtils.isEmpty(curUserfavorId)){
                snsPopupWindow.getmActionItems().get(0).mTitle = "取消";
            }else{
                snsPopupWindow.getmActionItems().get(0).mTitle = "赞";
            }
            snsPopupWindow.update();
            snsPopupWindow.setmItemClickListener(new PopupItemClickListener(MomentsPosition, MomentsItem, curUserfavorId));
            holder.snsBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    //弹出popupwindow
                    snsPopupWindow.showPopupWindow(view);
                }
            });*/

            switch (holder.viewType) {
                case MomentsViewHolder.TYPE_URL:// 处理链接动态的链接内容和和图片
                    if (holder instanceof URLViewHolder) {
                        String linkImg = momentsItem.getLinkImg();
                        String linkTitle = momentsItem.getLinkTitle();
                        Glide.with(context).load(linkImg).into(((URLViewHolder) holder).urlImageIv);
                        ((URLViewHolder) holder).urlContentTv.setText(linkTitle);
                        ((URLViewHolder) holder).urlBody.setVisibility(View.VISIBLE);
                    }

                    break;
                case MomentsViewHolder.TYPE_IMAGE:// 处理图片
                    if (holder instanceof ImageViewHolder) {
                        final List<PhotoInfo> photos = momentsItem.getPhotos();
                        if (photos != null && photos.size() > 0) {
                            ((ImageViewHolder) holder).multiImageView.setVisibility(View.VISIBLE);
                            ((ImageViewHolder) holder).multiImageView.setList(photos);
                            ((ImageViewHolder) holder).multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    //imagesize是作为loading时的图片size
                                    ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());

                                    List<String> photoUrls = new ArrayList<String>();
                                    for (PhotoInfo photoInfo : photos) {
                                        photoUrls.add(photoInfo.url);
                                    }
                                    ImagePagerActivity.navToImagePagerActivity(context, photoUrls, position, imageSize);


                                }
                            });
                        } else {
                            ((ImageViewHolder) holder).multiImageView.setVisibility(View.GONE);
                        }
                    }

                    break;
                case MomentsViewHolder.TYPE_VIDEO:
                    if (holder instanceof VideoViewHolder) {
                        ((VideoViewHolder) holder).videoView.setVideoUrl(momentsItem.getVideoUrl());
                        ((VideoViewHolder) holder).videoView.setVideoImgUrl(momentsItem.getVideoImgUrl());//视频封面图片
                        ((VideoViewHolder) holder).videoView.setPostion(position);
                        ((VideoViewHolder) holder).videoView.setOnPlayClickListener(new MomentsVideoView.OnPlayClickListener() {
                            @Override
                            public void onPlayClick(int pos) {
                                curPlayIndex = pos;
                            }
                        });
                    }

                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();//有head需要加1
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class PopupItemClickListener implements SnsPopupWindow.OnItemClickListener {
        private String mFavorId;
        //动态在列表中的位置
        private int mMomentsPosition;
        private long mLasttime = 0;
        private MomentsItem mMomentsItem;

        public PopupItemClickListener(int MomentsPosition, MomentsItem MomentsItem, String favorId) {
            this.mFavorId = favorId;
            this.mMomentsPosition = MomentsPosition;
            this.mMomentsItem = MomentsItem;
        }



        @Override
        public void onItemClick(ActionItem actionitem, int position) {
            switch (position) {
                case 0://点赞、取消点赞
                    if (System.currentTimeMillis() - mLasttime < 700)//防止快速点击操作
                        return;
                    mLasttime = System.currentTimeMillis();
                    if (presenter != null) {
                        if ("赞".equals(actionitem.mTitle.toString())) {
                            presenter.addfavor(mMomentsPosition);
                        } else {//取消点赞
                            presenter.deletefavor(mMomentsPosition, mFavorId);
                        }
                    }
                    break;
                case 1://发布评论
                    if (presenter != null) {
                        CommentConfig config = new CommentConfig();
                        config.MomentsPosition = mMomentsPosition;
                        config.commentType = CommentConfig.Type.PUBLIC;
                        presenter.showEditTextBody(config);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
