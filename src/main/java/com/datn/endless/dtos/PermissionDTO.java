package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.datn.endless.entities.Permission}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO implements Serializable {
    String permissionID;
    String module;
    String code;
    String permissionName;
}