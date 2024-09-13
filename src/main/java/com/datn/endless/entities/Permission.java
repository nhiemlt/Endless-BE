package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "permissions")
public class Permission {
    @Id
    @Size(max = 36)
    @Column(name = "PermissionID", nullable = false, length = 36)
    private String permissionID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ModuleID", nullable = false)
    private Module moduleID;

    @Size(max = 255)
    @NotNull
    @Column(name = "PermissionName", nullable = false)
    private String permissionName;

    @Size(max = 255)
    @Column(name = "EN_PermissionName")
    private String enPermissionname;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Role> roles;

    public Permission(UUID permissionId) {
        this.permissionID = permissionId.toString();
    }
}
