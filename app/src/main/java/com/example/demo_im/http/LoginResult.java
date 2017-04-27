package com.example.demo_im.http;

/**
 * Created by huangmingliang on 2017/4/27.
 */

public class LoginResult {

    private YnUserInfo data;
    private String msg;


    public LoginResult(YnUserInfo ynUserInfo, String msg) {
        this.data = ynUserInfo;
        this.msg = msg;
    }

    public YnUserInfo getData() {
        return data;
    }

    public void setData(YnUserInfo data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
