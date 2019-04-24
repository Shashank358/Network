package com.ssproduction.shashank.network.Utils;

import android.net.Uri;

public class Publish {

    private String publish;
    private String publish_pushId;
    private String publish_thumb;
    private String publisher_id;
    private String publisher_page;
    private String publish_type;
    private String text_info;
    private String firstPoll;
    private String secondPoll;
    private String firstText;
    private String secondText;
    private String question;

    public Publish(String publish, String publish_pushId, String publish_thumb,
                   String publisher_id, String publisher_page, String publish_type,
                   String text_info, String firstPoll, String secondPoll, String firstText,
                   String secondText, String question) {
        this.publish = publish;
        this.publish_pushId = publish_pushId;
        this.publish_thumb = publish_thumb;
        this.publisher_id = publisher_id;
        this.publisher_page = publisher_page;
        this.publish_type = publish_type;
        this.text_info = text_info;
        this.firstPoll = firstPoll;
        this.firstText = firstText;
        this.secondPoll = secondPoll;
        this.secondText = secondText;
        this.question = question;
    }

    public Publish(){

    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getFirstPoll() {
        return firstPoll;
    }

    public void setFirstPoll(String firstPoll) {
        this.firstPoll = firstPoll;
    }

    public String getSecondPoll() {
        return secondPoll;
    }

    public void setSecondPoll(String secondPoll) {
        this.secondPoll = secondPoll;
    }

    public String getFirstText() {
        return firstText;
    }

    public void setFirstText(String firstText) {
        this.firstText = firstText;
    }

    public String getSecondText() {
        return secondText;
    }

    public void setSecondText(String secondText) {
        this.secondText = secondText;
    }

    public String getText_info() {
        return text_info;
    }

    public void setText_info(String text_info) {
        this.text_info = text_info;
    }

    public String getPublish_type() {
        return publish_type;
    }

    public void setPublish_type(String publish_type) {
        this.publish_type = publish_type;
    }

    public String getPublish() {
        return publish;
    }

    public void setPublish(String publish) {
        this.publish = publish;
    }

    public String getPublish_pushId() {
        return publish_pushId;
    }

    public void setPublish_pushId(String publish_pushId) {
        this.publish_pushId = publish_pushId;
    }

    public String getPublish_thumb() {
        return publish_thumb;
    }

    public void setPublish_thumb(String publish_thumb) {
        this.publish_thumb = publish_thumb;
    }

    public String getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(String publisher_id) {
        this.publisher_id = publisher_id;
    }

    public String getPublisher_page() {
        return publisher_page;
    }

    public void setPublisher_page(String publisher_page) {
        this.publisher_page = publisher_page;
    }
}
