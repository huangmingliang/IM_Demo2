package com.example.demo_im.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by huangmingliang on 2017/5/9.
 */

public class SearchResult {
    @SerializedName("mobile")
    private String[] mobile;
    @SerializedName("mobile")
    private String msg;
    @SerializedName("error")
    private String error;

    public String[] getMobile() {
        return mobile;
    }

    public void setMobile(String[] mobile) {
        this.mobile = mobile;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
