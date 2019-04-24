package com.ssproduction.shashank.network.Utils;

public class GroupChats {

    private String message;
    private String receiver_id;
    private String sender_id;
    private String time;
    private String isRead;
    private String type;
    private String sender_profile;
    private String sender_userId;

    public GroupChats(String message, String receiver_id, String sender_id, String time,
                      String isRead, String type, String sender_profile, String sender_userId) {
        this.message = message;
        this.receiver_id = receiver_id;
        this.sender_id = sender_id;
        this.time = time;
        this.isRead = isRead;
        this.type = type;
        this.sender_profile = sender_profile;
        this.sender_userId = sender_userId;
    }

    public GroupChats(){

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender_profile() {
        return sender_profile;
    }

    public void setSender_profile(String sender_profile) {
        this.sender_profile = sender_profile;
    }

    public String getSender_userId() {
        return sender_userId;
    }

    public void setSender_userId(String sender_userId) {
        this.sender_userId = sender_userId;
    }
}
