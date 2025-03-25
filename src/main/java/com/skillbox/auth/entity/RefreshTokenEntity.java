package com.skillbox.auth.entity;

import jakarta.persistence.*;

import java.time.Instant;
@Entity(name = "refreshtoken")
public class RefreshTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "expiry_date" , nullable = false)
    private Instant expiryDate;

    public String getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }
}
