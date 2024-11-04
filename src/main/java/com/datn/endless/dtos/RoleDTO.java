package com.datn.endless.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link com.datn.endless.entities.Role}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO implements Serializable {
    String roleId;
    String roleName;
    Integer employees;
    Integer employeeActives;
    Integer employeeInactive;
    Set<PermissionDTO> permissions;

    public RoleDTO(String roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }
}