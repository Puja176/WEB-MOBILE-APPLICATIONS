package com.stephanie.whatsapp;

public class Messages {
    private String sender, text, category;

    public Messages() {
    }

    public Messages(String sender, String text, String category) {
        this.sender = sender;
        this.text = text;
        this.category = category;
    }

    public String getFrom() {
        return sender;
    }

    public void setFrom(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return text;
    }

    public void setMessage(String text) {
        this.text = text;
    }

    public String getType() {
        return category;
    }

    public void setType(String category) {
        this.type = category;
    }
}
