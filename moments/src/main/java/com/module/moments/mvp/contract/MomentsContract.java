package com.module.moments.mvp.contract;

import com.module.moments.bean.MomentsItem;
import com.module.moments.bean.CommentConfig;
import com.module.moments.bean.CommentItem;
import com.module.moments.bean.FavorItem;

import java.util.List;

/**
 * Created by suneee on 2016/7/15.
 */
public interface MomentsContract {

    interface View extends BaseView{
        void update2DeleteMoments(String MomentsId);
        void update2AddFavorite(int MomentsPosition, FavorItem addItem);
        void update2Deletefavor(int MomentsPosition, String favorId);
        void update2AddComment(int MomentsPosition, CommentItem addItem);
        void update2DeleteComment(int MomentsPosition, String commentId);
        void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig);
        void update2loadData(int loadType, List<MomentsItem> datas);
    }

    interface Presenter extends BasePresenter{
        void loadData(int loadType);
        void deleteMoments(final String MomentsId);
        void addfavor(final int MomentsPosition);
        void deletefavor(final int MomentsPosition, final String favorId);
        void deleteComment(final int MomentsPosition, final String commentId);

    }
}
