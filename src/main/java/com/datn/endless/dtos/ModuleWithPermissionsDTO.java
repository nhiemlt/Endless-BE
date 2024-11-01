package com.datn.endless.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleWithPermissionsDTO {
    private String moduleId;
    private String moduleName;
    private List<PermissionDTO> permissions;
}
