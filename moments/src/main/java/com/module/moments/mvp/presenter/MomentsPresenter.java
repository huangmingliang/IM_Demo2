package com.module.moments.mvp.presenter;

import android.view.View;

import com.module.moments.bean.MomentsItem;
import com.module.moments.bean.CommentConfig;
import com.module.moments.bean.CommentItem;
import com.module.moments.bean.FavortItem;
import com.module.moments.mvp.contract.MomentsContract;
import com.module.moments.mvp.modle.MomentsModel;
import com.module.moments.listener.IDataRequestListener;
import com.module.moments.utils.DatasUtil;

import java.util.List;

/**
 * 
* @ClassName: MomentsPresenter
* @Description: 通知model请求服务器和通知view更新
* @author yiw
* @date 2015-12-28 下午4:06:03 
*
 */
public class MomentsPresenter implements MomentsContract.Presenter{
	private MomentsModel MomentsModel;
	private MomentsContract.View view;

	public MomentsPresenter(MomentsContract.View view){
		MomentsModel = new MomentsModel();
		this.view = view;
	}

	public void loadData(int loadType){

        List<MomentsItem> datas = DatasUtil.createMomentsDatas();
        if(view!=null){
            view.update2loadData(loadType, datas);
        }
	}


	/**
	 * 
	* @Title: deleteMoments
	* @Description: 删除动态 
	* @param  MomentsId
	* @return void    返回类型 
	* @throws
	 */
	public void deleteMoments(final String MomentsId){
		MomentsModel.deleteMoments(new IDataRequestListener() {

			@Override
			public void loadSuccess(Object object) {
                if(view!=null){
                    view.update2DeleteMoments(MomentsId);
                }
			}
		});
	}
	/**
	 * 
	* @Title: addFavort 
	* @Description: 点赞
	* @param  MomentsPosition
	* @return void    返回类型 
	* @throws
	 */
	public void addFavort(final int MomentsPosition){
		MomentsModel.addFavort(new IDataRequestListener() {

			@Override
			public void loadSuccess(Object object) {
				FavortItem item = DatasUtil.createCurUserFavortItem();
                if(view !=null ){
                    view.update2AddFavorite(MomentsPosition, item);
                }

			}
		});
	}
	/**
	 * 
	* @Title: deleteFavort 
	* @Description: 取消点赞 
	* @param @param MomentsPosition
	* @param @param favortId     
	* @return void    返回类型 
	* @throws
	 */
	public void deleteFavort(final int MomentsPosition, final String favortId){
		MomentsModel.deleteFavort(new IDataRequestListener() {

			@Override
			public void loadSuccess(Object object) {
                if(view !=null ){
                    view.update2DeleteFavort(MomentsPosition, favortId);
                }
			}
		});
	}
	
	/**
	 * 
	* @Title: addComment 
	* @Description: 增加评论
	* @param  content
	* @param  config  CommentConfig
	* @return void    返回类型 
	* @throws
	 */
	public void addComment(final String content, final CommentConfig config){
		if(config == null){
			return;
		}
		MomentsModel.addComment(new IDataRequestListener() {

			@Override
			public void loadSuccess(Object object) {
				CommentItem newItem = null;
				if (config.commentType == CommentConfig.Type.PUBLIC) {
					newItem = DatasUtil.createPublicComment(content);
				} else if (config.commentType == CommentConfig.Type.REPLY) {
					newItem = DatasUtil.createReplyComment(config.replyUser, content);
				}
                if(view!=null){
                    view.update2AddComment(config.MomentsPosition, newItem);
                }
			}

		});
	}
	
	/**
	 * 
	* @Title: deleteComment 
	* @Description: 删除评论 
	* @param @param MomentsPosition
	* @param @param commentId     
	* @return void    返回类型 
	* @throws
	 */
	public void deleteComment(final int MomentsPosition, final String commentId){
		MomentsModel.deleteComment(new IDataRequestListener(){

			@Override
			public void loadSuccess(Object object) {
                if(view!=null){
                    view.update2DeleteComment(MomentsPosition, commentId);
                }
			}
			
		});
	}

	/**
	 *
	 * @param commentConfig
	 */
	public void showEditTextBody(CommentConfig commentConfig){
        if(view != null){
            view.updateEditTextBodyVisible(View.VISIBLE, commentConfig);
        }
	}


    /**
     * 清除对外部对象的引用，反正内存泄露。
     */
    public void recycle(){
        this.view = null;
    }
}
