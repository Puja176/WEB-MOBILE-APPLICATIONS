package com.stephanie.whatsapp;

public class Contacts {

    public String user, story, picture;

    public Contacts() {
    }

    public Contacts(String user, String story, String picture) {
        this.name = user;
        this.status = story;
        this.image = picture;
    }

    public String getName() {
        return user;
    }

    public void setName(String user) {
        this.name = user;
    }

    public String getStatus() {
        return story;
    }

    public void setStatus(String story) {
        this.status = story;
    }

    public String getImage() {
        return picture;
    }

    public void setImage(String picture) {
        this.image = picture;
    }
}
