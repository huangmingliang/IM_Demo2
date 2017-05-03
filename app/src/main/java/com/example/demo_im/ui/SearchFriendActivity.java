package com.example.demo_im.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.module.zxing.QrMainActivity;
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
import java.util.Collections;
import java.util.List;

/**
 * 查找添加新朋友
 */
public class SearchFriendActivity extends Activity implements FriendInfoView, AdapterView.OnItemClickListener, View.OnKeyListener{

    private final static String TAG = "SearchFriendActivity";

    private FriendshipManagerPresenter presenter,presenter2;
    ListView mSearchList;
    EditText mSearchInput;
    TextView tvNoResult;
    RelativeLayout searchLayout1;
    RelativeLayout scanLayout;
    RelativeLayout qrcodeLayout;
    LinearLayout searchLayout2;
    EditText search;
    ProfileSummaryAdapter adapter;
    List<ProfileSummary> list = new ArrayList<>();
    private Context context;


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
                QrMainActivity.navToQrCodeScan(context);
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
        mSearchList =(ListView) findViewById(R.id.list);
        tvNoResult = (TextView) findViewById(R.id.noResult);
        adapter = new ProfileSummaryAdapter(this, R.layout.item_profile_summary, list);
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
                presenter.searchFriendByName(key,true);
                //给手机号加上86-
                if (maybePhone(key)){
                    key = "86-" + key;
                }
                presenter.searchFriendById(key);
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
        for (TIMUserProfile item : users){
            if (needAdd(item.getIdentifier()))
                list.add(new FriendProfile(item));
        }
        adapter.notifyDataSetChanged();
        if (list.size() == 0){
            tvNoResult.setVisibility(View.VISIBLE);
        }else{
            tvNoResult.setVisibility(View.GONE);
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

}
