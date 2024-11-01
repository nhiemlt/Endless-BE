package com.datn.endless.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import java.util.List;

@Data
public class RoleModel {
    private String roleId;

    @NotEmpty(message = "Role name cannot be empty")
    private String roleName;

    @NotEmpty(message = "Permissions cannot be empty")
    private List<String> permissionIds;
}