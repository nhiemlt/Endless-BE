package com.datn.endless.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.datn.endless.entities.Role}
 */
@Value
public class EmployeeRoleDTO implements Serializable {
    @Size(max = 36)
    String roleId;
    @NotNull
    @Size(max = 255)
    String roleName;
}