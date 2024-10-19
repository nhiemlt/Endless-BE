package com.datn.endless.services;

import com.datn.endless.dtos.PermissionDTO;
import com.datn.endless.entities.Permission;
import com.datn.endless.repositories.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public List<PermissionDTO> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAll();
        return permissions.stream()
                .map(permission -> new PermissionDTO(
                        permission.getPermissionID(),
                        permission.getPermissionName(),
                        permission.getEnPermissionname()))
                .collect(Collectors.toList());
    }
}

