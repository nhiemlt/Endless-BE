package com.datn.endless.services;

import com.datn.endless.dtos.ModuleWithPermissionsDTO;
import com.datn.endless.dtos.PermissionDTO;
import com.datn.endless.entities.Permission;
import com.datn.endless.entities.Module;
import com.datn.endless.repositories.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public List<ModuleWithPermissionsDTO> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAll();

        // Nhóm permissions theo module
        Map<String, ModuleWithPermissionsDTO> moduleMap = new HashMap<>();

        for (Permission permission : permissions) {
            String moduleId = permission.getModuleID().getModuleID();
            String moduleName = permission.getModuleID().getModuleName();

            if (!moduleMap.containsKey(moduleId)) {
                moduleMap.put(moduleId, new ModuleWithPermissionsDTO(moduleId, moduleName, new ArrayList<>()));
            }

            // Thêm permission vào module tương ứng
            PermissionDTO permissionDTO = new PermissionDTO(permission.getPermissionID(), permission.getPermissionName());
            moduleMap.get(moduleId).getPermissions().add(permissionDTO);
        }

        return new ArrayList<>(moduleMap.values());
    }


}
