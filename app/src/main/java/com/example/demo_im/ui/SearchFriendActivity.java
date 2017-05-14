package com.example.demo_im.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo_im.model.SearchResult;
import com.example.demo_im.utils.HttpManager;
import com.module.zxing.QrMainActivity;
import com.module.zxing.android.CaptureActivity;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendStatus;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.presentation.presenter.FriendshipManagerPresenter;
import com.tencent.qcloud.presentation.viewfeatures.FriendInfoView;
import com.example.demo_im.R;
import com.example.demo_im.adapters.ProfileSummaryAdapter;
import com.example.demo_im.model.FriendProfile;
import com.example.demo_im.model.ProfileSummary;
import com.tencent.qcloud.presentation.viewfeatures.FriendshipManageView;
import com.tencent.qcloud.ui.NotifyDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * 查找添加新朋友
 */
public class SearchFriendActivity extends Activity implements FriendInfoView, AdapterView.OnItemClickListener, View.OnKeyListener{

    private final static String TAG = "SearchFriendActivity";

    private static final int REQUEST_CODE_SCAN = 0x0000;

    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";

    private FriendshipManagerPresenter presenter,presenter2;
    ListView mSearchList;
    EditText mSearchInput;
    LinearLayout searchContainer;
    TextView tvNoResult;
    RelativeLayout searchLayout1;
    RelativeLayout scanLayout;
    RelativeLayout qrcodeLayout;
    LinearLayout searchLayout2;
    EditText search;
    ProfileSummaryAdapter adapter;
    List<ProfileSummary> list = new ArrayList<>();
    private Context context;
    private boolean isSearchByScan=false;    //是否通过扫描的方式添加


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnew);
        context=this;
        searchLayout1=(RelativeLayout)findViewById(R.id.searchLayout1);
        searchLayout2=(LinearLayout)findViewById(R.id.searchLayout2);
        scanLayout=(RelativeLayout)findViewById(R.id.scan);
        qrcodeLayout=(RelativeLayout)findViewById(R.id.QRCode);
        search=(EditText)findViewById(R.id.search);

        scanLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SearchFriendActivity.this, CaptureActivity.class);
                startActivityForResult(intent,REQUEST_CODE_SCAN);
            }
        });

        qrcodeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLayout1.setVisibility(View.VISIBLE);
                searchLayout2.setVisibility(View.GONE);
            }
        });

        mSearchInput = (EditText) findViewById(R.id.inputSearch);
        searchContainer=(LinearLayout)findViewById(R.id.searchContainer);
        mSearchList =(ListView) LayoutInflater.from(context).inflate(R.layout.include_list,null);
        tvNoResult = (TextView) findViewById(R.id.noResult);
        adapter = new ProfileSummaryAdapter(this, R.layout.item_profile_summary, list);
        searchContainer.addView(mSearchList);
        mSearchList.setAdapter(adapter);
        mSearchList.setOnItemClickListener(this);
        presenter = new FriendshipManagerPresenter(this);
        presenter2=new FriendshipManagerPresenter(new FriendshipManageView() {
            @Override
            public void onAddFriend(TIMFriendStatus status) {
                switch (status) {
                    case TIM_ADD_FRIEND_STATUS_PENDING:
                        Toast.makeText(context, getResources().getString(R.string.add_friend_succeed), Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case TIM_FRIEND_STATUS_SUCC:
                        Toast.makeText(context, getResources().getString(R.string.add_friend_added), Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case TIM_ADD_FRIEND_STATUS_FRIEND_SIDE_FORBID_ADD:
                        Toast.makeText(context, getResources().getString(R.string.add_friend_refuse_all), Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case TIM_ADD_FRIEND_STATUS_IN_OTHER_SIDE_BLACK_LIST:
                        Toast.makeText(context, getResources().getString(R.string.add_friend_to_blacklist), Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case TIM_ADD_FRIEND_STATUS_IN_SELF_BLACK_LIST:
                        Toast.makeText(context, getResources().getString(R.string.add_friend_del_black_list), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(context, getResources().getString(R.string.add_friend_error), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onDelFriend(TIMFriendStatus status) {

            }

            @Override
            public void onChangeGroup(TIMFriendStatus status, String groupName) {

            }
        });
        TextView tvCancel = (TextView) findViewById(R.id.cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLayout1.setVisibility(View.GONE);
                searchLayout2.setVisibility(View.VISIBLE);
            }
        });
        mSearchInput.setOnKeyListener(this);


    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        presenter2.addFriend(list.get(i).getIdentify(), "", "", "");

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_UP){   // 忽略其它事件
            return false;
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                list.clear();
                adapter.notifyDataSetChanged();
                String key = mSearchInput.getText().toString();
                if (key.equals("")) return true;
               /* presenter.searchFriendByName(key,true);
                //给手机号加上86-
                if (maybePhone(key)){
                    key = "86-" + key;
                }*/
               final ArrayList ids=new ArrayList();
                HttpManager.getInstance().searchUser(key, new Observer<SearchResult>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d(TAG,"onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull SearchResult searchResult) {
                        if (searchResult==null){
                            presenter.searchFriendById(ids);
                            return;
                        }
                        if ("OK".equalsIgnoreCase(searchResult.getMsg())){
                            if (searchResult.getMobile()!=null&&searchResult.getMobile().length>0){
                                ids.clear();
                                ids.addAll(Arrays.asList(searchResult.getMobile()));
                            }
                        }else {
                            Log.e(TAG,"searchUser error:"+searchResult.getError());
                        }
                        presenter.searchFriendById(ids);

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG,"searchUser onError:"+e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG,"onComplete");
                    }
                });
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    /**
     * 显示好友信息
     *
     * @param users 好友资料列表
     */
    @Override
    public void showUserInfo(List<TIMUserProfile> users) {
        if (users == null) return;
        if (!isSearchByScan) {
            for (TIMUserProfile item : users) {
                if (needAdd(item.getIdentifier()))
                    list.add(new FriendProfile(item));
            }
            adapter.notifyDataSetChanged();
            if (list.size() == 0) {
                tvNoResult.setVisibility(View.VISIBLE);
            } else {
                tvNoResult.setVisibility(View.GONE);
            }
        }else {
            FriendProfile profile=new FriendProfile(users.get(0));
            Intent intent=new Intent(SearchFriendActivity.this,ScanAddFriendActivity.class);
            intent.putExtra("id",profile.getIdentify());
            intent.putExtra("name",profile.getRemark());
            startActivity(intent);
        }
    }

    private boolean needAdd(String id){
        for (ProfileSummary item : list){
            if (item.getIdentify().equals(id)) return false;
        }
        return true;
    }

    private boolean maybePhone(String str){
        if (str.length() != 11) return false;
        for (int i = 0 ; i < str.length() ; ++i){
            if(!Character.isDigit(str.charAt(i))) return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if (requestCode==REQUEST_CODE_SCAN){
                if (data != null) {
                    String content = data.getStringExtra(DECODED_CONTENT_KEY);
                    //Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
                    presenter.searchFriendById(content);
                    isSearchByScan=true;
                }
            }
        }
    }
}
