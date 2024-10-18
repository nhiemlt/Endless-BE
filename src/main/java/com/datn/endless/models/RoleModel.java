package com.datn.endless.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RoleModel {
    @NotNull(message = "Role ID cannot be null")
    private String roleId;

    @NotEmpty(message = "Role name cannot be empty")
    private String roleName;

    @NotEmpty(message = "Role English name cannot be empty")
    private String enNamerole;

    private List<String> permissionIds;
}
