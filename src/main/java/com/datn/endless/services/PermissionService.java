package com.datn.endless.services;

import com.datn.endless.dtos.PermissionDTO;
import com.datn.endless.entities.Permission;
import com.datn.endless.repositories.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public List<PermissionDTO> getAllPermissions() {
        return permissionRepository.findAll().stream().map(this::toDTO).toList();
    }

    public Optional<PermissionDTO> getPermissionById(String id) {
        return permissionRepository.findById(id).map(this::toDTO);
    }

    public List<PermissionDTO> searchPermissions(String keyword) {
        return permissionRepository.findAllByKeyword(keyword).stream()
                .map(this::toDTO).toList();
    }

    public List<PermissionDTO> filterPermissionsByModule(String moduleID) {
        return permissionRepository.findByModuleID_ModuleID(moduleID).stream()
                .map(this::toDTO).toList();
    }

    private PermissionDTO toDTO(Permission permission) {
        return new PermissionDTO(
                permission.getPermissionID(),
                permission.getModuleID().getModuleName(),
                permission.getCode(),
                permission.getPermissionName()
        );
    }

//    public List<ModuleWithPermissionsDTO> getAllPermissions() {
//        List<Permission> permissions = permissionRepository.findAll();
//
//        // Nhóm permissions theo module
//        Map<String, ModuleWithPermissionsDTO> moduleMap = new HashMap<>();
//
//        for (Permission permission : permissions) {
//            String moduleId = permission.getModuleID().getModuleID();
//            String moduleName = permission.getModuleID().getModuleName();
//
//            if (!moduleMap.containsKey(moduleId)) {
//                moduleMap.put(moduleId, new ModuleWithPermissionsDTO(moduleId, moduleName, new ArrayList<>()));
//            }
//
//            // Thêm permission vào module tương ứng
//            PermissionDTO2 permissionDTO = new PermissionDTO2(permission.getPermissionID(), permission.getPermissionName());
//            moduleMap.get(moduleId).getPermissions().add(permissionDTO);
//        }
//
//        return new ArrayList<>(moduleMap.values());
//    }

}
