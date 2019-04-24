package com.ssproduction.shashank.network.Utils;

public class multiPublish {

    private String publish_thumb;
    private String thumb_key;
    private String publishThumbId;
    private String publishId;

    public multiPublish(String publish_thumb, String thumb_key, String publishThumbId, String publishId) {
        this.publish_thumb = publish_thumb;
        this.thumb_key = thumb_key;
        this.publishThumbId = publishThumbId;
        this.publishId = publishId;

    }

    public multiPublish(){

    }

    public String getPublish_thumb() {
        return publish_thumb;
    }

    public void setPublish_thumb(String publish_thumb) {
        this.publish_thumb = publish_thumb;
    }

    public String getThumb_key() {
        return thumb_key;
    }

    public void setThumb_key(String thumb_key) {
        this.thumb_key = thumb_key;
    }

    public String getPublishThumbId() {
        return publishThumbId;
    }

    public void setPublishThumbId(String publishThumbId) {
        this.publishThumbId = publishThumbId;
    }

    public String getPublishId() {
        return publishId;
    }

    public void setPublishId(String publishId) {
        this.publishId = publishId;
    }
}
