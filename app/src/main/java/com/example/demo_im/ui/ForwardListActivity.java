package com.example.demo_im.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.demo_im.R;
import com.example.demo_im.adapters.ForwardAdapter;
import com.example.demo_im.model.Conversation;
import com.example.demo_im.model.CustomMessage;
import com.example.demo_im.model.Message;
import com.example.demo_im.model.MessageFactory;
import com.example.demo_im.model.NomalConversation;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMMessage;
import com.tencent.qcloud.presentation.presenter.ConversationPresenter;
import com.tencent.qcloud.presentation.viewfeatures.ConversationView;
import com.tencent.qcloud.ui.TemplateTitle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ForwardListActivity extends Activity implements ConversationView {

    private TemplateTitle title;
    private EditText search;
    private TextView createChatting;
    private ListView listView;

    private Message message;
    private ForwardAdapter adapter;
    private List<Conversation> conversations=new ArrayList<>();
    private ConversationPresenter conversationPresenter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward_list);
        message= (Message) getIntent().getSerializableExtra("message");
        title=(TemplateTitle)findViewById(R.id.title);
        search=(EditText)findViewById(R.id.editText);
        createChatting=(TextView)findViewById(R.id.text);
        listView=(ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDialogFragment(message,conversations.get(position));
            }
        });
        conversationPresenter=new ConversationPresenter(this);
        adapter=new ForwardAdapter(this,R.layout.item_forward_list,conversations);
        conversationPresenter.getConversation();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void initView(List<TIMConversation> conversationList) {
        this.conversations.clear();
        for (TIMConversation item:conversationList){
            switch (item.getType()){
                case C2C:
                case Group:
                    this.conversations.add(new NomalConversation(item));
                    break;
            }
        }
    }

    @Override
    public void updateMessage(TIMMessage message) {
        if (message == null){
            adapter.notifyDataSetChanged();
            return;
        }
        if (MessageFactory.getMessage(message) instanceof CustomMessage) return;
        NomalConversation conversation = new NomalConversation(message.getConversation());
        Iterator<Conversation> iterator =conversations.iterator();
        while (iterator.hasNext()){
            Conversation c = iterator.next();
            if (conversation.equals(c)){
                conversation = (NomalConversation) c;
                iterator.remove();
                break;
            }
        }
        conversation.setLastMessage(MessageFactory.getMessage(message));
        conversations.add(conversation);
        Collections.sort(conversations);
        refresh();
    }

    @Override
    public void updateFriendshipMessage() {

    }

    @Override
    public void removeConversation(String identify) {

    }

    @Override
    public void updateGroupInfo(TIMGroupCacheInfo info) {

    }

    @Override
    public void refresh() {
        Collections.sort(conversations);
        adapter.notifyDataSetChanged();
    }

    public void showDialogFragment(Message message,Conversation conversation){
        FragmentTransaction mFragTransaction = getFragmentManager().beginTransaction();
        Fragment fragment =  getFragmentManager().findFragmentByTag("dialogFragment");
        if(fragment!=null){
            //为了不重复显示dialog，在显示对话框之前移除正在显示的对话框
            mFragTransaction.remove(fragment);
        }
        ForwardDialogFragment dialogFragment =newInstance(message,conversation);
        dialogFragment.show(mFragTransaction, "dialogFragment");//显示一个Fragment并且给该Fragment添加一个Tag，可通过findFragmentByTag找到该Fragment
    }

    private ForwardDialogFragment newInstance(Message message,Conversation conversation){
        //创建一个带有参数的Fragment实例
        ForwardDialogFragment fragment = new ForwardDialogFragment();
        Bundle bundle = new Bundle();
        //bundle.putSerializable("message",message);
        bundle.putSerializable("conversations", conversation);
        fragment.setArguments(bundle);//把参数传递给该DialogFragment
        return fragment;
    }
}
