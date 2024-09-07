package com.datn.endless.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "permissionrole")
public class Permissionrole {
    @EmbeddedId
    private PermissionroleId id;

    @MapsId("permissionID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PermissionID", nullable = false)
    private Permission permissionID;

    @MapsId("roleId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Role_ID", nullable = false)
    private Role role;

}