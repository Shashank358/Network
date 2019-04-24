package com.ssproduction.shashank.network.Utils;

public class ChatList {

    private String image;
    private String name;
    private String message;
    private String type;
    private String key;

    public ChatList(String image, String name, String message, String type, String key) {
        this.image = image;
        this.name = name;
        this.message = message;
        this.type = type;
        this.key = key;
    }

    public ChatList(){

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
