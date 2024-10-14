package com.datn.endless.services;

import com.datn.endless.dtos.PermissionDTO;
import com.datn.endless.dtos.RoleDTO;
import com.datn.endless.entities.Permission;
import com.datn.endless.entities.Role;
import com.datn.endless.entities.User;
import com.datn.endless.entities.Userrole;
import com.datn.endless.repositories.PermissionRepository;
import com.datn.endless.repositories.UserRepository;
import com.datn.endless.repositories.UserroleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserRoleService {

    @Autowired
    private UserroleRepository userRoleRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    UserRepository userRepository;

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
        Userrole userrole = new Userrole();
        User user = new User(); // Tạo đối tượng User
        user.setUserID(userID.toString()); // Thiết lập ID người dùng

        Role role = new Role(); // Tạo đối tượng Role
        role.setRoleId(roleId.toString()); // Thiết lập ID vai trò

        userrole.setUser(user); // Thiết lập người dùng
        userrole.setRole(role); // Thiết lập vai trò

        userRoleRepository.save(userrole); // Lưu vào cơ sở dữ liệu
    }


    public void deleteUserRole(String userId, String roleId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        List<Userrole> userroles = userRoleRepository.findByUserAndRole(user, roleId);
        if (!userroles.isEmpty()) {
            // Xóa tất cả các vai trò tìm thấy
            for (Userrole userrole : userroles) {
                userRoleRepository.delete(userrole);
            }
        } else {
            throw new RuntimeException("Userrole not found");
        }
    }

    // Chuyển đổi từ Role entity sang RoleDTO
    public RoleDTO toDto(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setRoleId(role.getRoleId().toString());  // Chuyển đổi UUID sang String
        dto.setRoleName(role.getRoleName());
        dto.setEnNamerole(role.getEnNamerole());
        List<PermissionDTO> permissionDTOS = new ArrayList<>();
        for (Permission permission : role.getPermissions()) {
            PermissionDTO permissionDTO = new PermissionDTO();
            permissionDTO.setPermissionId(permission.getPermissionID());
            permissionDTO.setPermissionName(permission.getPermissionName());
            permissionDTO.setEnPermissionName(permission.getPermissionName());
            permissionDTOS.add(permissionDTO);
        }
        dto.setPermissions(permissionDTOS);
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

