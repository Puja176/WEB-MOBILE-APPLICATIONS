package com.stephanie.whatsapp;

public class InstantMessage {

    private String text;
    private String sender;

    public InstantMessage(String text, String sender) {
        this.message = text;
        this.author = sender;
    }

    public InstantMessage() {
    }

    public String getMessage() {
        return text;
    }

    public String getAuthor() {
        return sender;
    }
}
