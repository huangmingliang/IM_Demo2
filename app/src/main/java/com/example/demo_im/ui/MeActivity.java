package com.example.demo_im.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.demo_im.R;
import com.example.demo_im.model.Conversation;
import com.example.demo_im.model.NomalConversation;
import com.tencent.TIMConversation;
import com.tencent.TIMFriendFutureItem;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMMessage;
import com.tencent.qcloud.presentation.presenter.ConversationPresenter;
import com.tencent.qcloud.presentation.presenter.FriendshipManagerPresenter;
import com.tencent.qcloud.presentation.presenter.GroupManagerPresenter;
import com.tencent.qcloud.presentation.viewfeatures.ConversationView;
import com.tencent.qcloud.presentation.viewfeatures.FriendshipMessageView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MeActivity extends Activity implements ConversationView,FriendshipMessageView {

    private List<Conversation> conversationList = new LinkedList<>();
    private ConversationPresenter presenter;
    private FriendshipManagerPresenter friendshipManagerPresenter;
    private ImageView imageView1,imageView2,imageView3;
    private TextView textView1,textView2;
    private long unRead1,unRead2;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        context=this;
        imageView1=(ImageView)findViewById(R.id.meImg1);
        imageView2=(ImageView)findViewById(R.id.meImg2);
        imageView3=(ImageView)findViewById(R.id.meImg3);
        textView1=(TextView)findViewById(R.id.msgCount1);
        textView2=(TextView)findViewById(R.id.msgCount2);
        friendshipManagerPresenter = new FriendshipManagerPresenter(this);
        presenter = new ConversationPresenter(this);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MeActivity.this,Home2Activity.class);
                intent.putExtra("fragment","conversation");
                startActivity(intent);
                //finish();

            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MeActivity.this,Home2Activity.class);
                intent.putExtra("fragment","contact");
                startActivity(intent);
                //finish();
            }
        });

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MeActivity.this,MomentActivity.class);
                intent.putExtra("fragment","contact");
                startActivity(intent);
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getConversation();
        friendshipManagerPresenter.getFriendshipMessage();
    }

    @Override
    public void onGetFriendshipLastMessage(TIMFriendFutureItem message, long unreadCount) {
        unRead2=unreadCount;
        refresh();
    }

    @Override
    public void onGetFriendshipMessage(List<TIMFriendFutureItem> message) {
        refresh();
    }

    @Override
    public void initView(List<TIMConversation> conversationList) {
        this.conversationList.clear();
        for (TIMConversation item:conversationList){
            switch (item.getType()){
                case C2C:
                case Group:
                    this.conversationList.add(new NomalConversation(item));
                    break;
            }
        }
        refresh();
    }

    @Override
    public void updateMessage(TIMMessage message) {
        refresh();
    }

    @Override
    public void updateFriendshipMessage() {
        refresh();
    }

    @Override
    public void removeConversation(String identify) {
        refresh();
    }

    @Override
    public void updateGroupInfo(TIMGroupCacheInfo info) {
        refresh();
    }

    @Override
    public void refresh() {
        unRead1=getTotalUnreadNum();
        setUnread(textView1,unRead1);
        setUnread(textView2,unRead2);

    }

    private long getTotalUnreadNum(){
        long num = 0;
        for (Conversation conversation : conversationList){
            num += conversation.getUnreadNum();
        }
        return num;
    }

    private void setUnread(TextView textView,long unReadCount){
        if (unReadCount <= 0){
            textView.setVisibility(View.INVISIBLE);
        }else{
            textView.setVisibility(View.VISIBLE);
            String unReadStr = String.valueOf(unReadCount);
            if (unReadCount < 10){
                textView.setBackground(context.getResources().getDrawable(R.drawable.point1));
            }else{
                textView.setBackground(context.getResources().getDrawable(R.drawable.point2));
                if (unReadCount > 99){
                    unReadStr = context.getResources().getString(R.string.time_more);
                }
            }
            textView.setText(unReadStr);
        }
    }
}
