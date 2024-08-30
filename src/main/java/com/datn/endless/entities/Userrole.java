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
@Table(name = "userroles")
public class Userrole {
    private String userroleId;

    private User user;

    private Role role;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "Userrole_ID", nullable = false, length = 36)
    public String getUserroleId() {
        return userroleId;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    public User getUser() {
        return user;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_Id", nullable = false)
    public Role getRole() {
        return role;
    }

}