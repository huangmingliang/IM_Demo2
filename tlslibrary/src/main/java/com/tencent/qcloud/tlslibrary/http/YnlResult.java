package com.tencent.qcloud.tlslibrary.http;

import com.google.gson.annotations.SerializedName;

/**
 * Created by huangmingliang on 2017/4/27.
 */

public class YnlResult {

    @SerializedName("data")
    private YnUserInfo data;
    @SerializedName("msg")
    private String msg;


    public YnlResult(YnUserInfo ynUserInfo, String msg) {
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
