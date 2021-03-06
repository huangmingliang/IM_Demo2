package com.example.demo_im.model;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.example.demo_im.ui.ChatActivity;
import com.example.demo_im.utils.PinyinUtils;
import com.tencent.TIMConversationType;
import com.tencent.TIMUserProfile;
import com.example.demo_im.MyApplication;
import com.example.demo_im.R;
import com.example.demo_im.ui.AddFriendActivity;
import com.example.demo_im.ui.ProfileActivity;

/**
 * 好友资料
 */
public class FriendProfile implements ProfileSummary {


    private TIMUserProfile profile;
    private boolean isSelected;
    private char section='#';

    public FriendProfile(TIMUserProfile profile){
        this.profile = profile;
    }


    /**
     * 获取头像资源
     */
    @Override
    public int getAvatarRes() {
        return R.drawable.head_man;
    }

    /**
     * 获取头像地址
     */
    @Override
    public String getAvatarUrl() {
        return TextUtils.isEmpty(profile.getFaceUrl())?null:profile.getFaceUrl();
    }

    /**
     * 获取名字
     */
    @Override
    public String getName() {
        if (!profile.getRemark().equals("")){
            return profile.getRemark();
        }else if (!profile.getNickName().equals("")){
            return profile.getNickName();
        }
        return profile.getIdentifier();
    }

    /**
     * 获取描述信息
     */
    @Override
    public String getDescription() {
        return null;
    }

    /**
     * 显示详情
     *
     * @param context 上下文
     */
    @Override
    public void onClick(Context context) {
        if (FriendshipInfo.getInstance().isFriend(profile.getIdentifier())){
            //ProfileActivity.navToProfile(context, profile.getIdentifier());
            ChatActivity.navToChat(context,getIdentify(),TIMConversationType.C2C);
        }else{
            Intent person = new Intent(context,AddFriendActivity.class);
            person.putExtra("id",profile.getIdentifier());
            person.putExtra("name",getName());
            context.startActivity(person);
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    /**
     * 获取用户ID
     */
    @Override
    public String getIdentify(){
        return profile.getIdentifier();
    }


    /**
     * 获取用户备注名
     */
    public String getRemark(){
        if (TextUtils.isEmpty(profile.getRemark())){
            return getName();
        }else {
            return profile.getRemark();
        }
    }


    /**
     * 获取好友分组
     */
    public String getGroupName(){
        if (profile.getFriendGroups().size() == 0){
            return MyApplication.getContext().getString(R.string.default_group_name);
        }else{
            return profile.getFriendGroups().get(0);
        }
    }

    /*
    * 获取好友备注名字首字母
    * */
    public String getSortLetters(){
        return PinyinUtils.getLetters(getRemark());
    }

    public char getSection(){
        return section;
    }

    public void setSection(char section){
        this.section=section;
    }



}
