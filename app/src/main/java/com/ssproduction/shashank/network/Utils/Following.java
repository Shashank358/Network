package com.ssproduction.shashank.network.Utils;

public class Following {

    private String follow_type;
    private String follower_id;
    private String page_id;

    public Following(String follow_type, String follower_id, String page_id) {
        this.follow_type = follow_type;
        this.follower_id = follower_id;
        this.page_id = page_id;
    }

    public Following(){

    }

    public String getFollow_type() {
        return follow_type;
    }

    public void setFollow_type(String follow_type) {
        this.follow_type = follow_type;
    }

    public String getFollower_id() {
        return follower_id;
    }

    public void setFollower_id(String follower_id) {
        this.follower_id = follower_id;
    }

    public String getPage_id() {
        return page_id;
    }

    public void setPage_id(String page_id) {
        this.page_id = page_id;
    }
}
