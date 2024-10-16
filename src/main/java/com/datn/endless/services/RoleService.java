package com.datn.endless.services;

import com.datn.endless.dtos.PermissionDTO;
import com.datn.endless.dtos.RoleDTO;
import com.datn.endless.entities.Permission;
import com.datn.endless.entities.Role;
import com.datn.endless.repositories.PermissionRepository;
import com.datn.endless.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private PermissionRepository permissionRepository;

    public List<RoleDTO> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        List<RoleDTO> dtos = new ArrayList<RoleDTO>();
        for (Role role : roles) {
            dtos.add(toDto(role));
        }
        return dtos;
    }

    public RoleDTO getRoleById(String roleId) {
        return toDto(roleRepository.findById(roleId).orElse(null));
    }

    public Role createRole(RoleDTO roleDTO) {
        roleDTO.setRoleId(UUID.randomUUID().toString());
        Role role = toEntity(roleDTO);
        return roleRepository.save(role);
    }

    public Role updateRole(RoleDTO roleDTO) {
        Optional<Role> existingRoleOpt = roleRepository.findById(roleDTO.getRoleId());
        if (existingRoleOpt.isPresent()) {
            Role existingRole = existingRoleOpt.get();
            existingRole.setRoleName(roleDTO.getRoleName());;
            existingRole.setEnNamerole(roleDTO.getEnNamerole());
            return roleRepository.save(existingRole);
        }
        return null;
    }

    public void deleteRole(String roleId) {
        roleRepository.deleteById(roleId);
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
