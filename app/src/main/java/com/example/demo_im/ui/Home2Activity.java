package com.example.demo_im.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo_im.R;
import com.example.demo_im.adapters.FriendSearchAdapter;
import com.example.demo_im.adapters.MessageSearchAdapter;
import com.example.demo_im.model.FriendProfile;
import com.example.demo_im.model.FriendshipInfo;
import com.example.demo_im.model.GroupInfo;
import com.example.demo_im.model.NomalConversation;
import com.example.demo_im.model.RelativeRecord;
import com.example.demo_im.model.UserInfo;
import com.example.demo_im.ui.customview.DialogActivity;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;
import com.tencent.TIMUserStatusListener;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.presentation.event.MessageEvent;
import com.tencent.qcloud.tlslibrary.service.TlsBusiness;
import com.tencent.qcloud.ui.NotifyDialog;

import java.util.ArrayList;
import java.util.List;

public class Home2Activity extends FragmentActivity implements View.OnClickListener, View.OnKeyListener {

    private String TAG = "Home2Activity";
    private Context context;
    private TextView tab1, tab2;
    private RelativeLayout tab1Layout, tab2Layout;
    private ImageView unread1, unread2;
    private FrameLayout frameLayout;
    private ConversationFragment mConversationFragment;
    private ContactFragment2 mContactFragment2;
    private String firstFragment;
    private LinearLayout homeFragment;
    private RelativeLayout homeSearch;
    private TextView searchCancel;
    private MessageSearchAdapter messageSearchAdapter;
    private FriendSearchAdapter friendSearchAdapter;
    private List<TIMConversation> conversationList;
    private List<RelativeRecord> relativeRecords = new ArrayList<>();
    private List<FriendProfile> friendProfiles=new ArrayList<>();
    private SearchHandler handler= new SearchHandler();
    private ListView recordListView,friendListView;
    private LinearLayout searchContainer;
    private EditText inputEditText;
    private TextView noResultTv;
    private int count = 100;
    private int REFRESH_RELATIVE_RECORD = 200;
    private int REFRESH_FRIEND_PROFILE=201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity2);
        context=this;
        initView();

        messageSearchAdapter = new MessageSearchAdapter(this, R.layout.item_message_search, relativeRecords);
        friendSearchAdapter =new FriendSearchAdapter(this,R.layout.item_friend_search,friendProfiles);
        recordListView.setAdapter(messageSearchAdapter);
        friendListView.setAdapter(friendSearchAdapter);

        firstFragment = getIntent().getStringExtra("fragment");
        if ("contact".equals(firstFragment)) {
            switchToContactFragment();
        } else {
            switchToConversationFragment();
        }
        //互踢下线逻辑
        TIMManager.getInstance().setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                Log.d(TAG, "receive force offline message");
                Intent intent = new Intent(Home2Activity.this, DialogActivity.class);
                startActivity(intent);
            }

            @Override
            public void onUserSigExpired() {
                //票据过期，需要重新登录
                new NotifyDialog().show(getString(R.string.tls_expire), getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });
            }
        });
        Toast.makeText(this, getString(TIMManager.getInstance().getEnv() == 0 ? R.string.env_normal : R.string.env_test), Toast.LENGTH_SHORT).show();

    }

    public void logout() {
        TlsBusiness.logout(UserInfo.getInstance().getId());
        UserInfo.getInstance().setId(null);
        MessageEvent.getInstance().clear();
        FriendshipInfo.getInstance().clear();
        GroupInfo.getInstance().clear();
        Intent intent = new Intent(Home2Activity.this, SplashActivity.class);
        finish();
        startActivity(intent);

    }

    private void initView() {
        homeFragment = (LinearLayout) findViewById(R.id.home);
        homeSearch = (RelativeLayout) findViewById(R.id.homeSearch);
        searchContainer=(LinearLayout)findViewById(R.id.searchContainer);
        inputEditText = (EditText) findViewById(R.id.inputSearch);
        inputEditText.setOnKeyListener(this);
        noResultTv = (TextView) findViewById(R.id.noResult);
        searchCancel = (TextView) findViewById(R.id.cancel);
        tab1Layout = (RelativeLayout) findViewById(R.id.tab1);
        tab2Layout = (RelativeLayout) findViewById(R.id.tab2);
        tab1 = (TextView) findViewById(R.id.tab1_txt);
        tab2 = (TextView) findViewById(R.id.tab2_txt);
        unread1 = (ImageView) findViewById(R.id.tabUnread1);
        unread2 = (ImageView) findViewById(R.id.tabUnread2);

        LayoutInflater inflater=LayoutInflater.from(this);
        recordListView=(ListView)inflater.inflate(R.layout.include_list,null);
        friendListView=(ListView)inflater.inflate(R.layout.include_list,null);

        searchContainer.addView(recordListView);
        searchContainer.addView(friendListView);

        recordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RelativeRecord relativeRecord=relativeRecords.get(position);
                if (relativeRecord.getMessageCount()>1){
                    SearchRecordActivity.navToSearchRecord(context,relativeRecord);
                }else {
                    ChatActivity.navToChat(context,relativeRecord.getPeer(),TIMConversationType.C2C,relativeRecord.getMessages().get(0));
                }

            }
        });

        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatActivity.navToChat(context,friendProfiles.get(position).getIdentify(),TIMConversationType.C2C);
            }
        });

        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        searchCancel.setOnClickListener(this);

    }

    /**
     * 设置未读tab显示
     */
    public void setMsgUnread(Fragment fragment, boolean noUnread) {
        if (fragment instanceof ConversationFragment) {
            unread1.setVisibility(noUnread ? View.GONE : View.VISIBLE);
        } else if (fragment instanceof ContactFragment2) {
            unread2.setVisibility(noUnread ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab1_txt:
                switchToConversationFragment();
                break;
            case R.id.tab2_txt:
                switchToContactFragment();
                break;
            case R.id.cancel:
                toFragment();
                break;
        }
    }

    private void switchToConversationFragment() {
        tab1Layout.setSelected(true);
        tab2Layout.setSelected(false);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mConversationFragment == null) {
            mConversationFragment = new ConversationFragment();
            ft.add(R.id.fragment, mConversationFragment);
        }
        //隐藏所有fragment
        hideFragment(ft);
        //显示需要显示的fragment
        ft.show(mConversationFragment);
        //提交事务
        ft.commit();

    }

    private void switchToContactFragment() {
        tab1Layout.setSelected(false);
        tab2Layout.setSelected(true);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mContactFragment2 == null) {
            mContactFragment2 = new ContactFragment2();
            ft.add(R.id.fragment, mContactFragment2);
        }
        //隐藏所有fragment
        hideFragment(ft);
        //显示需要显示的fragment
        ft.show(mContactFragment2);
        //提交事务
        ft.commit();

    }

    //隐藏所有的fragment
    private void hideFragment(FragmentTransaction ft) {
        if (mConversationFragment != null) {
            ft.hide(mConversationFragment);
        }
        if (mContactFragment2 != null) {
            ft.hide(mContactFragment2);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.e(TAG, "hml keyCode=" + keyCode);
        if (event.getAction() != KeyEvent.ACTION_UP) {   // 忽略其它事件
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            String str = inputEditText.getText().toString();
            if (TextUtils.isEmpty(str)) {
                Toast.makeText(this, "输入为空", Toast.LENGTH_SHORT).show();
                return super.onKeyDown(keyCode, event);
            }
            searchRecords(str);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (homeSearch.getVisibility() == View.VISIBLE) {
                toFragment();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void toFragment() {
        findViewById(R.id.home).setVisibility(View.VISIBLE);
        findViewById(R.id.homeSearch).setVisibility(View.GONE);
    }

    private void searchRecords(final String str) {
        relativeRecords.clear();
        friendProfiles.clear();
        conversationList = TIMManager.getInstance().getConversionList();
        for (final TIMConversation conversation : conversationList) {
            if (conversation.getType() == TIMConversationType.C2C) {
                conversation.getLocalMessage(100, null, new TIMValueCallBack<List<TIMMessage>>() {
                    @Override
                    public void onError(int i, String s) {
                    }

                    @Override
                    public void onSuccess(List<TIMMessage> timMessages) {
                        List<TIMMessage> messages = new ArrayList<>();
                        for (TIMMessage message : timMessages) {
                            for (int i = 0; i < message.getElementCount(); i++) {
                                TIMElem timElem = message.getElement(i);
                                if (timElem.getType() == TIMElemType.Text) {
                                    String txtMessage = ((TIMTextElem) timElem).getText().toString();
                                    if (txtMessage.contains(str)) {
                                        messages.add(message);
                                        break;
                                    }
                                }
                            }
                        }
                        if (messages.size() > 0) {
                            relativeRecords.add(new RelativeRecord(messages, new NomalConversation(conversation),str));
                        }
                        handler.sendEmptyMessage(REFRESH_RELATIVE_RECORD);
                    }

                });
            }
        }

        FriendshipInfo.getInstance().getFriendProfiles(new FriendshipInfo.OnRefreshFriendProfilesListener() {
            @Override
            public void onRefreshFriendProfiles(List<FriendProfile> profiles) {
                for (FriendProfile profile:profiles){
                    if (profile.getRemark().contains(str)){
                        friendProfiles.add(profile);
                    }
                }
                handler.sendEmptyMessage(REFRESH_FRIEND_PROFILE);
            }
        });
    }

    class SearchHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == REFRESH_RELATIVE_RECORD) {
                messageSearchAdapter.notifyDataSetChanged();
            }else if (msg.what==REFRESH_FRIEND_PROFILE){
                friendSearchAdapter.notifyDataSetChanged();
            }
            if (relativeRecords.size() == 0&&friendProfiles.size()==0) {
                noResultTv.setVisibility(View.VISIBLE);
            } else {
                noResultTv.setVisibility(View.GONE);
            }
        }
    }
}
