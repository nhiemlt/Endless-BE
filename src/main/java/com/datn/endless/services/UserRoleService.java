package com.datn.endless.services;

import com.datn.endless.dtos.PermissionDTO;
import com.datn.endless.dtos.RoleDTO;
import com.datn.endless.entities.Permission;
import com.datn.endless.entities.Role;
import com.datn.endless.repositories.PermissionRepository;
import com.datn.endless.repositories.UserroleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UserRoleService {

    @Autowired
    private UserroleRepository userRoleRepository;

    @Autowired
    PermissionRepository permissionRepository;

    public List<RoleDTO> getRolesByUser(String userID) {
        List<Role> roles = userRoleRepository.findRolesByUserId(userID);
        List<RoleDTO> roleDTOs = new ArrayList<RoleDTO>();
        for (Role role : roles) {
            roleDTOs.add(toDto(role));
        }
        return roleDTOs;
    }

    public List<RoleDTO> getRolesByUsername(String username) {
        List<Role> roles = userRoleRepository.findRolesByUsername(username);
        List<RoleDTO> roleDTOs = new ArrayList<RoleDTO>();
        for (Role role : roles) {
            roleDTOs.add(toDto(role));
        }
        return roleDTOs;
    }

    public void assignRoleToUser(UUID userID, UUID roleId) {
        userRoleRepository.addRoleToUser(null, userID, roleId);
    }

    public void removeRoleFromUser(UUID userID, UUID roleId) {
        userRoleRepository.removeRoleFromUser(userID, roleId);
    }


    // Chuyển đổi từ Role entity sang RoleDTO
    public RoleDTO toDto(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setRoleId(role.getRoleId().toString());  // Chuyển đổi UUID sang String
        dto.setRoleName(role.getRoleName());
        dto.setEnNamerole(role.getEnNamerole());
        return dto;
    }

    // Chuyển đổi từ RoleDTO sang Role entity
    public Role toEntity(RoleDTO dto) {
        Role role = new Role();
        role.setRoleId(dto.getRoleId());  // Chuyển đổi String sang UUID
        role.setRoleName(dto.getRoleName());
        role.setEnNamerole(dto.getEnNamerole());
        Set<Permission> permissions = null;
        for(PermissionDTO permissionDTO : dto.getPermissions()) {
            Permission permission = new Permission();
            permissionRepository.findById(permissionDTO.getPermissionId()).orElse(null);
            permission.setPermissionName(permissionDTO.getPermissionName());
            permission.setEnPermissionname(permissionDTO.getEnPermissionName());
            permissions.add(permission);
        }
        role.setPermissions(permissions);
        return role;
    }
}

