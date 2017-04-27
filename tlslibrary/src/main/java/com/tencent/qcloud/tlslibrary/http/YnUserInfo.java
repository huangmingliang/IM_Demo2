package com.tencent.qcloud.tlslibrary.http;

import com.google.gson.annotations.SerializedName;

/**
 * Created by huangmingliang on 2017/4/27.
 */

public class YnUserInfo {
    @SerializedName("passportId")
    private String passportId;
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("custId")
    private String custId;
    @SerializedName("faceUrl")
    private String faceUrl;
    @SerializedName("imtoken")
    private String imtoken;
    @SerializedName("type")
    private String type;

    public YnUserInfo(String passportId, String mobile, String lastName, String firstName, String custId, String faceUrl, String imtoken, String type) {
        this.passportId = passportId;
        this.mobile = mobile;
        this.lastName = lastName;
        this.firstName = firstName;
        this.custId = custId;
        this.faceUrl = faceUrl;
        this.imtoken = imtoken;
        this.type = type;
    }

    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public String getImtoken() {
        return imtoken;
    }

    public void setImtoken(String imtoken) {
        this.imtoken = imtoken;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
