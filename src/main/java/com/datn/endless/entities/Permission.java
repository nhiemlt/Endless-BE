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
@Table(name = "permissions")
public class Permission {
    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "PermissionID", nullable = false, length = 36)
    private String permissionID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ModuleID", nullable = false)
    private Module moduleID;

    @Size(max = 255)
    @NotNull
    @Column(name = "Code", nullable = false)
    private String code;

    @Size(max = 255)
    @NotNull
    @Column(name = "PermissionName", nullable = false)
    private String permissionName;

}