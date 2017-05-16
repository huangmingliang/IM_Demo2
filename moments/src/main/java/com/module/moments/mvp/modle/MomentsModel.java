package com.module.moments.mvp.modle;

import android.os.AsyncTask;

import com.module.moments.listener.IDataRequestListener;

/**
 * 
* @ClassName: MomentsModel
* @Description: 因为逻辑简单，这里我就不写model的接口了
* @author yiw
* @date 2015-12-28 下午3:54:55 
 */
public class MomentsModel {
	
	
	public MomentsModel(){
		//
	}

	public void loadData(final IDataRequestListener listener){
		requestServer(listener);
	}
	
	public void deleteMoments( final IDataRequestListener listener) {
		requestServer(listener);
	}

	public void addfavor( final IDataRequestListener listener) {
		requestServer(listener);
	}

	public void deletefavor(final IDataRequestListener listener) {
		requestServer(listener);
	}

	public void addComment( final IDataRequestListener listener) {
		requestServer(listener);
	}

	public void deleteComment( final IDataRequestListener listener) {
		requestServer(listener);
	}
	
	/**
	 * 
	* @Title: requestServer 
	* @Description: 与后台交互, 因为demo是本地数据，不做处理
	* @param  listener    设定文件 
	* @return void    返回类型 
	* @throws
	 */
	private void requestServer(final IDataRequestListener listener) {
		new AsyncTask<Object, Integer, Object>(){
			@Override
			protected Object doInBackground(Object... params) {
				//和后台交互
				return null;
			}
			
			protected void onPostExecute(Object result) {
				listener.loadSuccess(result);
			};
		}.execute();
	}
	
}
