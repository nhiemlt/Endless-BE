package com.datn.endless.dtos;

import lombok.Data;

import java.util.List;

@Data
public class RoleDTO {
    private String roleId;
    private String roleName;
    private String enNamerole;
    private List<PermissionDTO> permissions;
}
