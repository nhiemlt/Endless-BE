package com.datn.endless.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {
    private String roleId;

    private String roleName;

    private String enNamerole;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "Role_ID", nullable = false, length = 36)
    public String getRoleId() {
        return roleId;
    }

    @Size(max = 255)
    @NotNull
    @Column(name = "RoleName", nullable = false)
    public String getRoleName() {
        return roleName;
    }

    @Size(max = 255)
    @Column(name = "EN_nameRole")
    public String getEnNamerole() {
        return enNamerole;
    }

}