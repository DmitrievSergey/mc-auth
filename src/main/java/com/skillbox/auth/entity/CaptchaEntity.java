package com.skillbox.auth.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "captcha")
public class CaptchaEntity {
    @Id
    //@GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "code")
    String captchaCode;

    public CaptchaEntity(String id, String captchaCode) {
        this.id = id;
        this.captchaCode = captchaCode;
    }

    public CaptchaEntity() {

    }

    public String getId() {
        return id;
    }

    public String getCaptchaCode() {
        return captchaCode;
    }
}
