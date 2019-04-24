package com.ssproduction.shashank.network.Utils;

public class Universities {

    private String admin;
    private String people_num;
    private String university_image;
    private String university_name;

    public Universities(String admin, String people_num, String university_image, String university_name) {
        this.admin = admin;
        this.people_num = people_num;
        this.university_image = university_image;
        this.university_name = university_name;
    }

    public Universities(){

    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getPeople_num() {
        return people_num;
    }

    public void setPeople_num(String people_num) {
        this.people_num = people_num;
    }

    public String getUniversity_image() {
        return university_image;
    }

    public void setUniversity_image(String university_image) {
        this.university_image = university_image;
    }

    public String getUniversity_name() {
        return university_name;
    }

    public void setUniversity_name(String university_name) {
        this.university_name = university_name;
    }
}
