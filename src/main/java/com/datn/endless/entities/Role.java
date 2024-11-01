package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "Role_ID", nullable = false, length = 36)
    private String roleId;

    @Size(max = 255)
    @NotNull
    @Column(name = "RoleName", nullable = false)
    private String roleName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "permissionrole",
            joinColumns = @JoinColumn(name = "RoleID"),
            inverseJoinColumns = @JoinColumn(name = "PermissionID"))
    private Set<Permission> permissions = new LinkedHashSet<>();

}