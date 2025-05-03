package com.skillbox.auth.entity;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;

import java.io.Serializable;

@Entity
@Table(name = "users", indexes = @Index(columnList = "email"))
@NoArgsConstructor
public class UserEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String password;

    private String email;

    @ToString.Exclude
    @Type(StringArrayType.class)
    @Column(name = "roles", columnDefinition = "text[]")
    private String[] roles = new String[3];

    public void setId(String id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String[] getRoles() {
        return roles;
    }

    public UserEntity(String id, String firstName, String lastName, String password, String email, String[] roles) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.roles = roles;
    }
}
