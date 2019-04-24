package com.ssproduction.shashank.network.Utils;

public class ReplyComment {

    private String reply;
    private String replier_id;
    private String replier_image;
    private String comment_id;

    public ReplyComment(String reply, String replier_id, String replier_image, String comment_id) {
        this.reply = reply;
        this.replier_id = replier_id;
        this.replier_image = replier_image;
        this.comment_id = comment_id;
    }

    public ReplyComment(){

    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getReplier_id() {
        return replier_id;
    }

    public void setReplier_id(String replier_id) {
        this.replier_id = replier_id;
    }

    public String getReplier_image() {
        return replier_image;
    }

    public void setReplier_image(String replier_image) {
        this.replier_image = replier_image;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }
}
