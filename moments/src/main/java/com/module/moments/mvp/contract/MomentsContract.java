package com.module.moments.mvp.contract;

import com.module.moments.bean.MomentsItem;
import com.module.moments.bean.CommentConfig;
import com.module.moments.bean.CommentItem;
import com.module.moments.bean.FavortItem;

import java.util.List;

/**
 * Created by suneee on 2016/7/15.
 */
public interface MomentsContract {

    interface View extends BaseView{
        void update2DeleteMoments(String MomentsId);
        void update2AddFavorite(int MomentsPosition, FavortItem addItem);
        void update2DeleteFavort(int MomentsPosition, String favortId);
        void update2AddComment(int MomentsPosition, CommentItem addItem);
        void update2DeleteComment(int MomentsPosition, String commentId);
        void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig);
        void update2loadData(int loadType, List<MomentsItem> datas);
    }

    interface Presenter extends BasePresenter{
        void loadData(int loadType);
        void deleteMoments(final String MomentsId);
        void addFavort(final int MomentsPosition);
        void deleteFavort(final int MomentsPosition, final String favortId);
        void deleteComment(final int MomentsPosition, final String commentId);

    }
}
