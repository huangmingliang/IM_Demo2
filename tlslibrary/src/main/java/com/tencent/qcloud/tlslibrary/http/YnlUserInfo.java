package com.tencent.qcloud.tlslibrary.http;

/**
 * 用户数据
 */
public class YnlUserInfo {

    private String id;
    private String userSig;
    private static YnlUserInfo ourInstance = new YnlUserInfo();

    public static YnlUserInfo getInstance() {
        return ourInstance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserSig() {
        return userSig;
    }

    public void setUserSig(String userSig) {
        this.userSig = userSig;
    }

}