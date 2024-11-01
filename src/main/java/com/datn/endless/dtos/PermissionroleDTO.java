package com.datn.endless.dtos;

import com.datn.endless.entities.PermissionroleId;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.datn.endless.entities.Permissionrole}
 */
@Value
public class PermissionroleDTO implements Serializable {
    PermissionroleId id;
    PermissionDTO permission;
    RoleDTO role;
}