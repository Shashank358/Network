package com.ssproduction.shashank.network.Utils;

public class AllPages {

    private String admin_id;
    private String followers_count;
    private String page_about;
    private String page_cover;
    private String page_tagLine;
    private String push_id;
    private String title;
    private Boolean ask_to_follow;

    public AllPages(String admin_id, String followers_count, String page_about, String page_cover,
                    String page_tagLine, String push_id, String title, Boolean ask_to_follow) {
        this.admin_id = admin_id;
        this.followers_count = followers_count;
        this.page_about = page_about;
        this.page_cover = page_cover;
        this.page_tagLine = page_tagLine;
        this.push_id = push_id;
        this.title = title;
        this.ask_to_follow = ask_to_follow;
    }

    public AllPages(){

    }

    public String getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
    }

    public String getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(String followers_count) {
        this.followers_count = followers_count;
    }

    public String getPage_about() {
        return page_about;
    }

    public void setPage_about(String page_about) {
        this.page_about = page_about;
    }

    public String getPage_cover() {
        return page_cover;
    }

    public void setPage_cover(String page_cover) {
        this.page_cover = page_cover;
    }

    public String getPage_tagLine() {
        return page_tagLine;
    }

    public void setPage_tagLine(String page_tagLine) {
        this.page_tagLine = page_tagLine;
    }

    public String getPush_id() {
        return push_id;
    }

    public void setPush_id(String push_id) {
        this.push_id = push_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getAsk_to_follow() {
        return ask_to_follow;
    }

    public void setAsk_to_follow(Boolean ask_to_follow) {
        this.ask_to_follow = ask_to_follow;
    }
}
