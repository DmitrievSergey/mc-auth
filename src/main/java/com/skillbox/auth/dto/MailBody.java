package com.skillbox.auth.dto;

public class MailBody {
    private String to;
    private String subject;
    private String text;

    public MailBody(String to, String subject, String text) {
        this.to = to;
        this.subject = subject;
        this.text = text;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }
}
