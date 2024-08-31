package com.datn.endless.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class PermissionroleId implements Serializable {
    private static final long serialVersionUID = -1058579061975791561L;
    @Size(max = 36)
    @NotNull
    @Column(name = "PermissionID", nullable = false, length = 36)
    private String permissionID;

    @Size(max = 36)
    @NotNull
    @Column(name = "Role_ID", nullable = false, length = 36)
    private String roleId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PermissionroleId entity = (PermissionroleId) o;
        return Objects.equals(this.permissionID, entity.permissionID) &&
                Objects.equals(this.roleId, entity.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissionID, roleId);
    }

}