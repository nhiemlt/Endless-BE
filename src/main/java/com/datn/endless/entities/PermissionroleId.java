package com.datn.endless.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    public PermissionroleId(UUID permissionId, UUID roleId) {
        this.permissionID = permissionId.toString();
        this.roleId = roleId.toString();
    }
}
