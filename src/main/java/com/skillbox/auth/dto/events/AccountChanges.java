package com.skillbox.auth.dto.events;

import java.time.LocalDateTime;

public class AccountChanges {
    private String id;
    private String firstName;
    private String lastName;
    private String phone;
    private String photo;
    private String about;
    private String city;
    private String country;
    private LocalDateTime birthDate;
    private String emojiStatus;

    public AccountChanges(String id, String firstName, String lastName, String phone, String photo, String about, String city, String country, LocalDateTime birthDate, String emojiStatus) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.photo = photo;
        this.about = about;
        this.city = city;
        this.country = country;
        this.birthDate = birthDate;
        this.emojiStatus = emojiStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public LocalDateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDateTime birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmojiStatus() {
        return emojiStatus;
    }

    public void setEmojiStatus(String emojiStatus) {
        this.emojiStatus = emojiStatus;
    }
}
