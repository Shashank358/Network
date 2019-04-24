package com.ssproduction.shashank.network.Utils;

public class AllGroups {

    private String group_admin;
    private String group_title;
    private String group_cover;
    private String group_tag;
    private String group_id;

    public AllGroups(String group_admin, String group_title, String group_cover,
                     String group_tag, String group_id) {
        this.group_admin = group_admin;
        this.group_title = group_title;
        this.group_cover = group_cover;
        this.group_tag = group_tag;
        this.group_id = group_id;
    }

    public AllGroups(){

    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_admin() {
        return group_admin;
    }

    public void setGroup_admin(String group_admin) {
        this.group_admin = group_admin;
    }

    public String getGroup_title() {
        return group_title;
    }

    public void setGroup_title(String group_title) {
        this.group_title = group_title;
    }

    public String getGroup_cover() {
        return group_cover;
    }

    public void setGroup_cover(String group_cover) {
        this.group_cover = group_cover;
    }

    public String getGroup_tag() {
        return group_tag;
    }

    public void setGroup_tag(String group_tag) {
        this.group_tag = group_tag;
    }
}
