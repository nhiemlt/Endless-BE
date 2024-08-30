package com.datn.endless.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "permissionrole")
public class Permissionrole {
    private PermissionroleId id;

    private Permission permissionID;

    private Role role;

    @EmbeddedId
    public PermissionroleId getId() {
        return id;
    }

    @MapsId("permissionID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PermissionID", nullable = false)
    public Permission getPermissionID() {
        return permissionID;
    }

    @MapsId("roleId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Role_ID", nullable = false)
    public Role getRole() {
        return role;
    }

}