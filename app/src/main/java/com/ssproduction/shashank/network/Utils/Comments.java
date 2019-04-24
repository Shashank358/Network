package com.ssproduction.shashank.network.Utils;

public class Comments {

    private String commenter_pushId;
    private String publisher_id;
    private String comment;
    private String commenter_id;
    private String commenter_image;
    private String publish_pushId;
    private String comment_pushId;

    public Comments(String commenter_pushId, String publisher_id, String comment,
                    String commenter_id, String commenter_image, String publish_pushId, String comment_pushId) {
        this.commenter_pushId = commenter_pushId;
        this.publisher_id = publisher_id;
        this.comment = comment;
        this.commenter_id = commenter_id;
        this.commenter_image = commenter_image;
        this.publish_pushId = publish_pushId;
        this.comment_pushId = comment_pushId;
    }

    public Comments(){

    }

    public String getComment_pushId() {
        return comment_pushId;
    }

    public void setComment_pushId(String comment_pushId) {
        this.comment_pushId = comment_pushId;
    }

    public String getPublish_pushId() {
        return publish_pushId;
    }

    public void setPublish_pushId(String publish_pushId) {
        this.publish_pushId = publish_pushId;
    }

    public String getCommenter_pushId() {
        return commenter_pushId;
    }

    public void setCommenter_pushId(String commenter_pushId) {
        this.commenter_pushId = commenter_pushId;
    }

    public String getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(String publisher_id) {
        this.publisher_id = publisher_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommenter_id() {
        return commenter_id;
    }

    public void setCommenter_id(String commenter_id) {
        this.commenter_id = commenter_id;
    }

    public String getCommenter_image() {
        return commenter_image;
    }

    public void setCommenter_image(String commenter_image) {
        this.commenter_image = commenter_image;
    }
}

