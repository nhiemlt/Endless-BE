package com.datn.endless.services;

import com.datn.endless.dtos.PermissionDTO;
import com.datn.endless.dtos.RoleDTO;
import com.datn.endless.entities.Role;
import com.datn.endless.entities.User;
import com.datn.endless.entities.Userrole;
import com.datn.endless.repositories.RoleRepository;
import com.datn.endless.repositories.UserRepository;
import com.datn.endless.repositories.UserroleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserRoleService {

    @Autowired
    private UserroleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // Tìm kiếm role theo tên
    public List<RoleDTO> searchRoles(String roleName) {
        List<Role> roles = roleRepository.searchRolesByKeyword(roleName);
        return roles.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<RoleDTO> getRolesByUser(String userID) {
        List<Role> roles = userRoleRepository.findRolesByUserId(userID);
        return roles.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<RoleDTO> getRolesByUsername(String username) {
        List<Role> roles = userRoleRepository.findRolesByUsername(username);
        return roles.stream().map(this::toDto).collect(Collectors.toList());
    }

    public void assignUsersToRole(String roleId, List<String> userIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        for (String userId : userIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Userrole userrole = new Userrole();
            userrole.setUser(user);
            userrole.setRole(role);
            userRoleRepository.save(userrole);
        }
    }

    public void deleteUserRole(String userId, String roleId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        List<Userrole> userroles = userRoleRepository.findByUserAndRole(user, roleId);
        if (!userroles.isEmpty()) {
            userroles.forEach(userRoleRepository::delete);
        } else {
            throw new RuntimeException("Userrole not found");
        }
    }

    public RoleDTO toDto(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setRoleId(role.getRoleId());
        dto.setRoleName(role.getRoleName());
        dto.setEnNamerole(role.getEnNamerole());

        List<PermissionDTO> permissionDTOS = role.getPermissions().stream().map(permission -> {
            PermissionDTO permissionDTO = new PermissionDTO();
            permissionDTO.setPermissionId(permission.getPermissionID());
            permissionDTO.setPermissionName(permission.getPermissionName());
            permissionDTO.setEnPermissionName(permission.getEnPermissionname());
            return permissionDTO;
        }).collect(Collectors.toList());

        dto.setPermissions(permissionDTOS);
        return dto;
    }

}
