package com.ssproduction.shashank.network.Utils;

public class Users {

    private String user_name;
    private String user_image;
    private String user_id;
    private String user_thumbImage;
    private boolean online;
    private String page_name;
    private String userKey_id;
    private String search;

    public Users(String user_name, String user_image, String user_id,
                 String user_thumbImage, boolean online, String page_name, String userKey_id,
                 String search) {
        this.user_name = user_name;
        this.user_image = user_image;
        this.user_id = user_id;
        this.user_thumbImage = user_thumbImage;
        this.online = online;
        this.page_name = page_name;
        this.userKey_id = userKey_id;
        this.search = search;
    }

    public Users() {
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getUserKey_id() {
        return userKey_id;
    }

    public void setUserKey_id(String userKey_id) {
        this.userKey_id = userKey_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_thumbImage() {
        return user_thumbImage;
    }

    public void setUser_thumbImage(String user_thumbImage) {
        this.user_thumbImage = user_thumbImage;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getPage_name() {
        return page_name;
    }

    public void setPage_name(String page_name) {
        this.page_name = page_name;
    }
}
