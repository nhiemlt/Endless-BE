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
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

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

    public RoleDTO getRoleById(String roleId) {
        return toDto(roleRepository.findById(roleId).orElse(null));
    }

    public Role createRole(RoleDTO roleDTO) {
        // Tạo vai trò mới với ID ngẫu nhiên
        Role role = new Role();
        role.setRoleId(UUID.randomUUID().toString());
        role.setRoleName(roleDTO.getRoleName());
        role.setEnNamerole(roleDTO.getEnNamerole());

        // Lấy danh sách các quyền từ cơ sở dữ liệu dựa trên permissionId mà người dùng đã chọn
        Set<Permission> permissions = new HashSet<>(
                permissionRepository.findAllById(
                        roleDTO.getPermissions().stream()
                                .map(PermissionDTO::getPermissionId)
                                .collect(Collectors.toList())
                )
        );

        // Gán các quyền cho vai trò
        role.setPermissions(permissions);

        // Lưu vai trò mới cùng với các quyền vào cơ sở dữ liệu
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
