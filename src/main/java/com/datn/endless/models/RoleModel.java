package com.datn.endless.models;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class RoleModel {
    @NotEmpty(message = "Role name cannot be empty")
    private String roleName;

    private List<String> permissionIds;
}