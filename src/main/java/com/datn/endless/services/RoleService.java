package com.datn.endless.services;

import com.datn.endless.dtos.PermissionDTO;
import com.datn.endless.dtos.RoleDTO;
import com.datn.endless.entities.Permission;
import com.datn.endless.entities.Role;
import com.datn.endless.models.RoleModel;
import com.datn.endless.repositories.PermissionRepository;
import com.datn.endless.repositories.RoleRepository;
import com.datn.endless.repositories.UserroleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserroleRepository userroleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    public List<RoleDTO> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        List<RoleDTO> dtos = new ArrayList<>();
        for (Role role : roles) {
            dtos.add(toDto(role));
        }
        return dtos;
    }

    public int countUsersInRole(String roleId) {
        return userroleRepository.countUsersByRole(roleId);
    }


    public RoleDTO getRoleById(String roleId) {
        return toDto(roleRepository.findById(roleId).orElse(null));
    }

    public Role createRole(RoleModel roleModel) {
        Role role = new Role();

        // Generate UUID if roleId is null
        if (roleModel.getRoleId() == null) {
            role.setRoleId(UUID.randomUUID().toString());
        } else {
            role.setRoleId(roleModel.getRoleId());
        }

        role.setRoleName(roleModel.getRoleName());
        role.setEnNamerole(roleModel.getEnNamerole());

        // Handle permissions
        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(roleModel.getPermissionIds()));
        role.setPermissions(permissions);

        return roleRepository.save(role);
    }

    public Role updateRole(RoleModel roleModel) {
        Optional<Role> existingRoleOpt = roleRepository.findById(roleModel.getRoleId());
        if (existingRoleOpt.isPresent()) {
            Role existingRole = existingRoleOpt.get();
            existingRole.setRoleName(roleModel.getRoleName());
            existingRole.setEnNamerole(roleModel.getEnNamerole());

            // Cập nhật lại permissions
            Set<Permission> permissions = new HashSet<>(
                    permissionRepository.findAllById(roleModel.getPermissionIds())
            );
            existingRole.setPermissions(permissions);

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
        dto.setRoleId(role.getRoleId().toString());
        dto.setRoleName(role.getRoleName());
        dto.setEnNamerole(role.getEnNamerole());

        // Chuyển đổi permissions
        List<PermissionDTO> permissionDTOS = new ArrayList<>();
        for (Permission permission : role.getPermissions()) {
            PermissionDTO permissionDTO = new PermissionDTO();
            permissionDTO.setPermissionId(permission.getPermissionID());
            permissionDTO.setPermissionName(permission.getPermissionName());
            permissionDTO.setEnPermissionName(permission.getEnPermissionname());
            permissionDTOS.add(permissionDTO);
        }
        dto.setPermissions(permissionDTOS); // Đặt danh sách quyền vào DTO

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

    public RoleDTO getRoleWithPermissions(String roleId) {
        Role role = roleRepository.findById(roleId).orElse(null);
        return role != null ? toDto(role) : null;
    }

}
