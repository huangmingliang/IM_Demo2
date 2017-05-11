package com.example.demo_im.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo_im.R;
import com.example.demo_im.model.FriendshipInfo;
import com.example.demo_im.model.GroupInfo;
import com.example.demo_im.model.UserInfo;
import com.example.demo_im.ui.customview.DialogActivity;
import com.tencent.TIMManager;
import com.tencent.TIMUserStatusListener;
import com.tencent.qcloud.presentation.event.MessageEvent;
import com.tencent.qcloud.tlslibrary.service.TlsBusiness;
import com.tencent.qcloud.ui.NotifyDialog;

public class Home2Activity extends FragmentActivity implements View.OnClickListener {

    private String TAG="Home2Activity";
    private TextView tab1,tab2;
    private RelativeLayout tab1Layout, tab2Layout;
    private ImageView unread1,unread2;
    private FrameLayout frameLayout;
    private ConversationFragment mConversationFragment;
    private ContactFragment2 mContactFragment2;
    private String firstFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity2);
        initView();
        firstFragment=getIntent().getStringExtra("fragment");
        if ("contact".equals(firstFragment)){
            switchToContactFragment();
        }else {
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

    public void logout(){
        TlsBusiness.logout(UserInfo.getInstance().getId());
        UserInfo.getInstance().setId(null);
        MessageEvent.getInstance().clear();
        FriendshipInfo.getInstance().clear();
        GroupInfo.getInstance().clear();
        Intent intent = new Intent(Home2Activity.this,SplashActivity.class);
        finish();
        startActivity(intent);

    }

    private void initView(){
        tab1Layout =(RelativeLayout)findViewById(R.id.tab1);
        tab2Layout =(RelativeLayout)findViewById(R.id.tab2);
        tab1=(TextView)findViewById(R.id.tab1_txt);
        tab2=(TextView)findViewById(R.id.tab2_txt);
        unread1=(ImageView)findViewById(R.id.tabUnread1);
        unread2=(ImageView)findViewById(R.id.tabUnread2);

        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);

    }

    /**
     * 设置未读tab显示
     */
    public void setMsgUnread(Fragment fragment,boolean noUnread){
        if (fragment instanceof ConversationFragment){
            unread1.setVisibility(noUnread? View.GONE:View.VISIBLE);
        }else if (fragment instanceof ContactFragment2){
            unread2.setVisibility(noUnread? View.GONE:View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tab1_txt:
                switchToConversationFragment();
                break;
            case R.id.tab2_txt:
                switchToContactFragment();
                break;
        }
    }

   private void  switchToConversationFragment(){
       tab1Layout.setSelected(true);
       tab2Layout.setSelected(false);
       FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
       if (mConversationFragment==null){
           mConversationFragment=new ConversationFragment();
           ft.add(R.id.fragment, mConversationFragment);
       }
       //隐藏所有fragment
       hideFragment(ft);
       //显示需要显示的fragment
       ft.show(mConversationFragment);
       //提交事务
       ft.commit();

   }

    private void  switchToContactFragment(){
        tab1Layout.setSelected(false);
        tab2Layout.setSelected(true);
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        if (mContactFragment2==null){
            mContactFragment2=new ContactFragment2();
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
    private void hideFragment(FragmentTransaction ft){
        if(mConversationFragment != null){
            ft.hide(mConversationFragment);
        }
        if(mContactFragment2 != null){
            ft.hide(mContactFragment2);
        }
    }
}
