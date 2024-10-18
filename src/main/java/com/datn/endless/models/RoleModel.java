package com.datn.endless.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.util.List;

@Data
public class RoleModel {

    @Null(groups = OnCreate.class, message = "Role ID must be null when creating a new role")
    @NotNull(groups = OnUpdate.class, message = "Role ID cannot be null when updating a role")
    private String roleId;

    @NotEmpty(message = "Role name cannot be empty")
    private String roleName;

    @NotEmpty(message = "Role English name cannot be empty")
    private String enNamerole;

    private List<String> permissionIds;

    public interface OnCreate {}
    public interface OnUpdate {}
}

