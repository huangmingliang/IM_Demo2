package com.example.demo_im.model;


import android.util.Log;

import com.tencent.TIMFriendGroup;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMFriendshipProxy;
import com.tencent.TIMManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.presentation.event.FriendshipEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * 好友列表缓存数据结构
 */
public class FriendshipInfo extends Observable implements Observer {

    private final String TAG = "FriendshipInfo";


    private List<String> groups;
    private Map<String, List<FriendProfile>> friends;
    private List<FriendProfile> friendProfiles;

    private static FriendshipInfo instance;

    private FriendshipInfo(){
        groups = new ArrayList<>();
        friends = new HashMap<>();
        friendProfiles=new ArrayList<>();
        FriendshipEvent.getInstance().addObserver(this);
        refresh();
    }

    public synchronized static FriendshipInfo getInstance(){
        if (instance == null){
            instance = new FriendshipInfo();
        }
        return instance;
    }

    /**
     * This method is called if the specified {@code Observable} object's
     * {@code notifyObservers} method is called (because the {@code Observable}
     * object has been updated.
     *
     * @param observable the {@link Observable} object.
     * @param data       the data passed to {@link Observable#notifyObservers(Object)}.
     */
    @Override
    public void update(Observable observable, Object data) {
        TIMManager.getInstance().getEnv();
        if (observable instanceof FriendshipEvent){
            if (data instanceof FriendshipEvent.NotifyCmd){
                FriendshipEvent.NotifyCmd cmd = (FriendshipEvent.NotifyCmd) data;
                Log.d(TAG, "get notify type:" + cmd.type);
                switch (cmd.type){
                    case REFRESH:
                    case DEL:
                    case ADD:
                    case PROFILE_UPDATE:
                    case ADD_REQ:
                    case GROUP_UPDATE:
                        refresh();

                        break;

                }
            }
        }
    }


    private void refresh(){
        getFriendProfiles(null);
        setChanged();
        notifyObservers();
    }

    /**
     * 获取分组列表
     */
    public List<String> getGroups(){
        return groups;
    }

    public String[] getGroupsArray(){
        return groups.toArray(new String[groups.size()]);
    }


    /**
     * 获取好友列表摘要
     */
    public Map<String, List<FriendProfile>> getFriends(){
        return friends;
    }
    /*
    * 获取好友列表不分组
    * */
    public void getFriendProfiles(final OnRefreshFriendProfilesListener listener){
        final List<FriendProfile> newFriends=new ArrayList<>();
        TIMFriendshipManager.getInstance().getFriendList(new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
              friendProfiles.clear();
              for (TIMUserProfile profile:timUserProfiles){
                  friendProfiles.add(new FriendProfile(profile));
              }
                for (FriendProfile profile:friendProfiles){
                    char temp=profile.getSortLetters().charAt(0);
                    if (temp>=65&&temp<=90){       //首字符是字母
                        profile.setSection(temp);
                    }else if (temp>=97&&temp<=122){
                        profile.setSection((char) (temp-32));
                    }else {
                        profile.setSection('#');
                    }
                    newFriends.add(profile);
                }
                PinyinComparator pinyinComparator=new PinyinComparator();
                Collections.sort(newFriends, pinyinComparator);
                if (listener!=null){
                    listener.onRefreshFriendProfiles(newFriends);
                }
            }
        });

    }

    /**
     * 判断是否是好友
     *
     * @param identify 需判断的identify
     */
   /* public boolean isFriend(String identify){
        for (String key : friends.keySet()){
            for (FriendProfile profile : friends.get(key)){
                if (identify.equals(profile.getIdentify())) return true;
            }
        }
        return false;
    }*/

   public  boolean isFriend(String identify){
       for (FriendProfile profile:friendProfiles){
           if (profile.getIdentify().equals(identify)){
               return true;
           }
       }
       return false;
   }




    /**
     * 获取好友资料
     *
     * @param identify 好友id
     */
    /*public FriendProfile getProfile(String identify){
        for (String key : friends.keySet()){
            for (FriendProfile profile : friends.get(key)){
                if (identify.equals(profile.getIdentify())) return profile;
            }
        }
        return null;
    }*/

    public FriendProfile getProfile(String identify){
        for (FriendProfile profile:friendProfiles){
            if (profile.getIdentify().equals(identify)){
                return profile;
            }
        }
        return null;
    }



    /**
     * 清除数据
     */
    public void clear(){
        if (instance == null) return;
        groups.clear();
        friends.clear();
        instance = null;
    }

    /*
    * 好友列表按首字母进行排序
    * */
    class PinyinComparator implements Comparator<FriendProfile> {

        public int compare(FriendProfile o1, FriendProfile o2) {
            //这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
            if (o2.getSection()=='#') {
                return -1;
            } else if (o1.getSection()=='#') {
                return 1;
            } else {
                return o1.getSection()-o2.getSection();
            }
        }
    }

    public interface OnRefreshFriendProfilesListener{
       void onRefreshFriendProfiles(List<FriendProfile> profiles);
    }


}
