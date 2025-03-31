package com.skillbox.auth.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Date;


@Entity(name = "reset_password")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String token;

    @Column(name = "expiration")
    private Instant expirationTime;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    public PasswordResetToken() {
    }

    public PasswordResetToken(String token, Instant expirationTime, UserEntity user) {
        this.token = token;
        this.expirationTime = expirationTime;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpirationTime(Instant expirationTime) {
        this.expirationTime = expirationTime;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }


    public Instant getExpirationTime() {
        return expirationTime;
    }

    public UserEntity getUser() {
        return user;
    }
}
