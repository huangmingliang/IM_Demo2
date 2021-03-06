package com.example.demo_im.model;

import com.tencent.TIMConversationType;
import com.tencent.TIMMessage;

import java.util.List;

/**
 * Created by Administrator on 2017/5/13.
 */

public class RelativeRecord {
    private String peer;
    private List<TIMMessage> messages;
    private NomalConversation conversation;
    private String keyStr;
    private String Name;
    private int messageCount;


    public RelativeRecord(List<TIMMessage> messages, NomalConversation conversation,String keyStr) {
        this.messages = messages;
        this.conversation = conversation;
        this.keyStr=keyStr;
    }

    public String getKeyStr() {
        return keyStr;
    }

    public int getMessageCount() {
        return messages.size();
    }


    public NomalConversation getNomalConversation() {
        return conversation;
    }

    public void setTimConversation(NomalConversation conversation) {
        this.conversation = conversation;
    }

    public String getPeer() {
        return conversation.getIdentify();
    }

    public void setPeer(String peer) {
        this.peer = peer;
    }

    public List<TIMMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<TIMMessage> messages) {
        this.messages = messages;
    }

    public String getName() {
       return conversation.getName();
    }
    public String getAvatarUrl(){
        return conversation.getAvatarUrl();
    }
}
