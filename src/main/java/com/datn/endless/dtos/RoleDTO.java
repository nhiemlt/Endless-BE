package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {
    private String roleId;
    private String roleName;
    private String enNamerole;
    private List<PermissionDTO> permissions;

    public RoleDTO(String roleId, String roleName, int userCount) {
    }
}
