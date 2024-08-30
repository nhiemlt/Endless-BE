package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    private String userID;

    private String username;

    private String fullname;

    private String password;

    private String phone;

    private String email;

    private String avatar;

    private String language;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "UserID", nullable = false, length = 36)
    public String getUserID() {
        return userID;
    }

    @Size(max = 255)
    @NotNull
    @Column(name = "Username", nullable = false)
    public String getUsername() {
        return username;
    }

    @Size(max = 255)
    @Column(name = "Fullname")
    public String getFullname() {
        return fullname;
    }

    @Size(max = 255)
    @Column(name = "Password")
    public String getPassword() {
        return password;
    }

    @Size(max = 11)
    @Column(name = "Phone", length = 11)
    public String getPhone() {
        return phone;
    }

    @Size(max = 255)
    @Column(name = "Email")
    public String getEmail() {
        return email;
    }

    @Lob
    @Column(name = "Avatar")
    public String getAvatar() {
        return avatar;
    }

    @Size(max = 50)
    @Column(name = "Language", length = 50)
    public String getLanguage() {
        return language;
    }

}