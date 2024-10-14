package com.datn.endless.services;

import com.datn.endless.dtos.PermissionDTO;
import com.datn.endless.dtos.RoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PermissionroleService {

    @Autowired
    private UserRoleService userRoleService;

    public List<PermissionDTO> getPermissionsByUserId(String userId) {
        List<RoleDTO> roles = userRoleService.getRolesByUser(userId);
        List<PermissionDTO> permissionDTOS = new ArrayList<>();

        for (RoleDTO role : roles) {
            permissionDTOS.addAll(role.getPermissions());
        }

        return permissionDTOS;
    }
}

