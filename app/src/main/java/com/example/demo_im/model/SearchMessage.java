package com.example.demo_im.model;

/**
 * Created by huangmingliang on 2017/5/12.
 */

public class SearchMessage {
    private String titleContent;
    private String avatarUrl;
    private String message;
    private String messageSrc;
    private String identify;
    private boolean isShowTitle;
    private int tab;

    public String getTitleContent() {
        return titleContent;
    }

    public void setTitleContent(String titleContent) {
        this.titleContent = titleContent;
    }

    public boolean isShowTitle() {
        return isShowTitle;
    }

    public void setShowTitle(boolean showTitle) {
        isShowTitle = showTitle;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageSrc() {
        return messageSrc;
    }

    public void setMessageSrc(String messageSrc) {
        this.messageSrc = messageSrc;
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public int getTab() {
        return tab;
    }

    public void setTab(int tab) {
        this.tab = tab;
    }
}
