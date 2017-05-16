package com.module.moments.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.module.moments.R;
import com.module.moments.adapter.MomentsAdapter;
import com.module.moments.bean.MomentsItem;
import com.module.moments.bean.CommentConfig;
import com.module.moments.bean.CommentItem;
import com.module.moments.bean.FavorItem;
import com.module.moments.mvp.contract.MomentsContract;
import com.module.moments.mvp.presenter.MomentsPresenter;
import com.module.moments.utils.CommonUtils;
import com.module.moments.widgets.CommentListView;
import com.module.moments.widgets.DivItemDecoration;
import com.module.moments.widgets.TitleBar;
import com.module.moments.widgets.dialog.UpLoadDialog;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by huangmingliang on 2017/4/20.
 */

public class MomentsFragment extends Fragment implements MomentsContract.View, EasyPermissions.PermissionCallbacks{

    private final String TAG = "MomentsFragment";

    Activity activity;
    private View view;

    private MomentsAdapter momentsAdapter;
    private LinearLayout edittextbody;
    private EditText editText;
    private ImageView sendIv;

    private int screenHeight;
    private int editTextBodyHeight;
    private int currentKeyboardH;
    private int selectMomentsItemH;
    private int selectCommentItemOffset;

    private MomentsPresenter presenter;
    private CommentConfig commentConfig;
    private SuperRecyclerView recyclerView;
    private RelativeLayout bodyLayout;
    private LinearLayoutManager layoutManager;
    private TitleBar titleBar;

    private final static int TYPE_PULLREFRESH = 1;
    private final static int TYPE_UPLOADREFRESH = 2;
    private UpLoadDialog uploadDialog;
    private SwipeRefreshLayout.OnRefreshListener refreshListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity=this.getActivity();
        if (view==null){
            view = inflater.inflate(R.layout.fragment_moments, container, false);
            presenter = new MomentsPresenter(this);
            initView();
            initPermission();
            //实现自动下拉刷新功能
            recyclerView.getSwipeToRefresh().post(new Runnable(){
                @Override
                public void run() {
                    recyclerView.setRefreshing(true);//执行下拉刷新的动画
                    refreshListener.onRefresh();//执行数据加载操作
                }
            });

        }
        return view;
    }

    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(String errorMsg) {

    }

    @Override
    public void update2DeleteMoments(String MomentsId) {
        List<MomentsItem> MomentsItems = momentsAdapter.getDatas();
        for(int i=0; i<MomentsItems.size(); i++){
            if(MomentsId.equals(MomentsItems.get(i).getId())){
                MomentsItems.remove(i);
                momentsAdapter.notifyDataSetChanged();
                //momentsAdapter.notifyItemRemoved(i+1);
                return;
            }
        }

    }

    @Override
    public void update2AddFavorite(int MomentsPosition, FavorItem addItem) {
        if(addItem != null){
            MomentsItem item = (MomentsItem) momentsAdapter.getDatas().get(MomentsPosition);
            item.getfavorers().add(addItem);
            momentsAdapter.notifyDataSetChanged();
            //momentsAdapter.notifyItemChanged(MomentsPosition+1);
        }
    }

    @Override
    public void update2Deletefavor(int MomentsPosition, String favorId) {
        MomentsItem item = (MomentsItem) momentsAdapter.getDatas().get(MomentsPosition);
        List<FavorItem> items = item.getfavorers();
        for(int i=0; i<items.size(); i++){
            if(favorId.equals(items.get(i).getId())){
                items.remove(i);
                momentsAdapter.notifyDataSetChanged();
                //momentsAdapter.notifyItemChanged(MomentsPosition+1);
                return;
            }
        }
    }

    @Override
    public void update2AddComment(int MomentsPosition, CommentItem addItem) {
        if(addItem != null){
            MomentsItem item = (MomentsItem) momentsAdapter.getDatas().get(MomentsPosition);
            item.getComments().add(addItem);
            momentsAdapter.notifyDataSetChanged();
            //momentsAdapter.notifyItemChanged(MomentsPosition+1);
        }
        //清空评论文本
        editText.setText("");
    }

    @Override
    public void update2DeleteComment(int MomentsPosition, String commentId) {
        MomentsItem item = (MomentsItem) momentsAdapter.getDatas().get(MomentsPosition);
        List<CommentItem> items = item.getComments();
        for(int i=0; i<items.size(); i++){
            if(commentId.equals(items.get(i).getId())){
                items.remove(i);
                momentsAdapter.notifyDataSetChanged();
                //momentsAdapter.notifyItemChanged(MomentsPosition+1);
                return;
            }
        }
    }

    @Override
    public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {
        this.commentConfig = commentConfig;
        edittextbody.setVisibility(visibility);

        measureMomentsItemHighAndCommentItemOffset(commentConfig);

        if(View.VISIBLE==visibility){
            editText.requestFocus();
            //弹出键盘
            CommonUtils.showSoftInput( editText.getContext(),  editText);

        }else if(View.GONE==visibility){
            //隐藏键盘
            CommonUtils.hideSoftInput( editText.getContext(),  editText);
        }
    }

    @Override
    public void update2loadData(int loadType, List<MomentsItem> datas) {
        Log.e(TAG,"HML update2loadData loadType="+loadType);
        if (loadType == TYPE_PULLREFRESH){
            recyclerView.setRefreshing(false);
            momentsAdapter.setDatas(datas);
        }else if(loadType == TYPE_UPLOADREFRESH){
            momentsAdapter.getDatas().addAll(datas);
        }
        momentsAdapter.notifyDataSetChanged();

        if(momentsAdapter.getDatas().size()<45 + MomentsAdapter.HEADVIEW_SIZE){
            recyclerView.setupMoreListener(new OnMoreListener() {
                @Override
                public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            presenter.loadData(TYPE_UPLOADREFRESH);
                        }
                    }, 2000);

                }
            }, 1);
        }else{
            recyclerView.removeMoreListener();
            recyclerView.hideMoreProgress();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(activity, "您拒绝了相关权限，可能会导致相关功能不可用" , Toast.LENGTH_LONG).show();
    }

    private void initView() {

        initTitle();
        initUploadDialog();

        recyclerView = (SuperRecyclerView) view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        int divideHeight=(int)getResources().getDimension(R.dimen.divide_height);
        int color= ContextCompat.getColor(getActivity(),R.color.gray_line);
        recyclerView.addItemDecoration(new DivItemDecoration(getActivity(), LinearLayoutManager.HORIZONTAL,divideHeight,color));
        recyclerView.getMoreProgressView().getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (edittextbody.getVisibility() == View.VISIBLE) {
                    updateEditTextBodyVisible(View.GONE, null);
                    return true;
                }
                return false;

            }
        });

        refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        presenter.loadData(TYPE_PULLREFRESH);
                    }
                }, 2000);
            }
        };
        recyclerView.setRefreshListener(refreshListener);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    Glide.with(activity).resumeRequests();
                }else{
                    Glide.with(activity).pauseRequests();
                }

            }
        });

        momentsAdapter = new MomentsAdapter(this.getContext());
        momentsAdapter.setMomentsPresenter(presenter);
        recyclerView.setAdapter(momentsAdapter);

        edittextbody = (LinearLayout) view.findViewById(R.id.editTextBodyLl);
        editText = (EditText) view.findViewById(R.id.momentsEt);
        sendIv = (ImageView) view.findViewById(R.id.sendIv);
        sendIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presenter != null) {
                    //发布评论
                    String content =  editText.getText().toString().trim();
                    if(TextUtils.isEmpty(content)){
                        Toast.makeText(activity, "评论内容不能为空...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    presenter.addComment(content, commentConfig);
                }
                updateEditTextBodyVisible(View.GONE, null);
            }
        });

        setViewTreeObserver();

    }

    private void initPermission() {
        String[] perms = {Manifest.permission.CALL_PHONE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(this.getActivity(), perms)) {
            // Already have permission, do the thing
            // ...
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "因为功能需要，需要使用相关权限，请允许",
                    100, perms);
        }
    }

    private void initTitle() {

        titleBar = (TitleBar) view.findViewById(R.id.main_title_bar);
        titleBar.setTitle("朋友圈");
        titleBar.setTitleColor(getResources().getColor(R.color.white));
        titleBar.setBackgroundColor(getResources().getColor(R.color.title_bg));

        TextView textView = (TextView) titleBar.addAction(new TitleBar.TextAction("发布视频") {
            @Override
            public void performAction(View view) {
                Toast.makeText(activity, "敬请期待...", Toast.LENGTH_SHORT).show();
            }
        });
        textView.setTextColor(getResources().getColor(R.color.white));
    }

    private void initUploadDialog() {
        uploadDialog = new UpLoadDialog(this.getContext());
    }

    private void setViewTreeObserver() {
        bodyLayout = (RelativeLayout) view.findViewById(R.id.bodyLayout);
        final ViewTreeObserver swipeRefreshLayoutVTO = bodyLayout.getViewTreeObserver();
        swipeRefreshLayoutVTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                bodyLayout.getWindowVisibleDisplayFrame(r);
                int statusBarH = getStatusBarHeight();//状态栏高度
                int screenH = bodyLayout.getRootView().getHeight();
                if(r.top != statusBarH ){
                    //在这个demo中r.top代表的是状态栏高度，在沉浸式状态栏时r.top＝0，通过getStatusBarHeight获取状态栏高度
                    r.top = statusBarH;
                }
                int keyboardH = screenH - (r.bottom - r.top);
                Log.d(TAG, "screenH＝ "+ screenH +" &keyboardH = " + keyboardH + " &r.bottom=" + r.bottom + " &top=" + r.top + " &statusBarH=" + statusBarH);

                if(keyboardH == currentKeyboardH){//有变化时才处理，否则会陷入死循环
                    return;
                }

                currentKeyboardH = keyboardH;
                screenHeight = screenH;//应用屏幕的高度
                editTextBodyHeight = edittextbody.getHeight();

                if(keyboardH<150){//说明是隐藏键盘的情况
                    updateEditTextBodyVisible(View.GONE, null);
                    return;
                }
                //偏移listview
                if(layoutManager!=null && commentConfig != null){
                    layoutManager.scrollToPositionWithOffset(commentConfig.MomentsPosition + MomentsAdapter.HEADVIEW_SIZE, getListviewOffset(commentConfig));
                }
            }
        });
    }

    /**
     * 获取状态栏高度
     * @return
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 测量偏移量
     * @param commentConfig
     * @return
     */
    private int getListviewOffset(CommentConfig commentConfig) {
        if(commentConfig == null)
            return 0;
        //这里如果你的listview上面还有其它占高度的控件，则需要减去该控件高度，listview的headview除外。
        //int listviewOffset = mScreenHeight - mSelectMomentsItemH - mCurrentKeyboardH - mEditTextBodyHeight;
        int listviewOffset = screenHeight - selectMomentsItemH - currentKeyboardH - editTextBodyHeight - titleBar.getHeight();
        if(commentConfig.commentType == CommentConfig.Type.REPLY){
            //回复评论的情况
            listviewOffset = listviewOffset + selectCommentItemOffset;
        }
        Log.i(TAG, "listviewOffset : " + listviewOffset);
        return listviewOffset;
    }

    private void measureMomentsItemHighAndCommentItemOffset(CommentConfig commentConfig){
        if(commentConfig == null)
            return;

        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        //只能返回当前可见区域（列表可滚动）的子项
        View selectMomentsItem = layoutManager.getChildAt(commentConfig.MomentsPosition + MomentsAdapter.HEADVIEW_SIZE - firstPosition);

        if(selectMomentsItem != null){
            selectMomentsItemH = selectMomentsItem.getHeight();
        }

        if(commentConfig.commentType == CommentConfig.Type.REPLY){
            //回复评论的情况
            CommentListView commentLv = (CommentListView) selectMomentsItem.findViewById(R.id.commentList);
            if(commentLv!=null){
                //找到要回复的评论view,计算出该view距离所属动态底部的距离
                View selectCommentItem = commentLv.getChildAt(commentConfig.commentPosition);
                if(selectCommentItem != null){
                    //选择的commentItem距选择的MomentsItem底部的距离
                    selectCommentItemOffset = 0;
                    View parentView = selectCommentItem;
                    do {
                        int subItemBottom = parentView.getBottom();
                        parentView = (View) parentView.getParent();
                        if(parentView != null){
                            selectCommentItemOffset += (parentView.getHeight() - subItemBottom);
                        }
                    } while (parentView != null && parentView != selectMomentsItem);
                }
            }
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


    }


    @Override
    public void onDestroy() {
        if(presenter !=null){
            presenter.recycle();
        }
        super.onDestroy();
        super.onDestroy();
    }
}
