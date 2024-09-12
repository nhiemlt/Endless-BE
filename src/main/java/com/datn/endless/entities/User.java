package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @Column(name = "UserID", nullable = false, length = 36)
    private String userID;

    @Size(max = 255)
    @NotNull
    @Column(name = "Username", nullable = false)
    private String username;

    @Size(max = 255)
    @Column(name = "Fullname")
    private String fullname;

    @Size(max = 255)
    @Column(name = "Password")
    private String password;

    @Size(max = 11)
    @Column(name = "Phone", length = 11)
    private String phone;

    @Size(max = 255)
    @Column(name = "Email")
    private String email;

    @Lob
    @Column(name = "Avatar")
    private String avatar;

    @Size(max = 50)
    @Column(name = "Language", length = 50)
    private String language;

    @ColumnDefault("1")
    @Column(name = "active")
    private Boolean active;

    @ColumnDefault("0")
    @Column(name = "forgetPassword")
    private Boolean forgetPassword;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "UserRoles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

}