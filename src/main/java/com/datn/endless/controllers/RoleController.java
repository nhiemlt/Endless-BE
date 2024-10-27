package com.datn.endless.controllers;

import com.datn.endless.dtos.PermissionDTO;
import com.datn.endless.dtos.RoleDTO;
import com.datn.endless.dtos.UserDTO;
import com.datn.endless.entities.Role;
import com.datn.endless.models.RoleModel;
import com.datn.endless.services.PermissionService;
import com.datn.endless.services.RoleService;
import com.datn.endless.services.UserRoleService;
import com.datn.endless.utils.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    UserRoleService userRoleService;

    @Autowired
    private PermissionService permissionService;

    // Phương thức GET để lấy toàn bộ permissions
    @GetMapping("/permissions")
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        List<PermissionDTO> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/{roleId}/count")
    public ResponseEntity<Integer> getUserCountByRole(@PathVariable("roleId") String roleId) {
        int userCount = roleService.countUsersInRole(roleId);
        return ResponseEntity.ok(userCount);
    }

    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<?> getRoleById(@PathVariable("roleId") String roleId) {
        try {
            RoleDTO dto = roleService.getRoleById(roleId);
            return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(List.of(e.getMessage())));
        }
    }

    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@Validated(RoleModel.OnCreate.class) @RequestBody RoleModel roleModel) {
        Role createdRole = roleService.createRole(roleModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.toDto(createdRole));
    }

    @PutMapping
    public ResponseEntity<RoleDTO> updateRole(@Validated(RoleModel.OnUpdate.class) @RequestBody RoleModel roleModel) {
        Role updatedRole = roleService.updateRole(roleModel);
        return ResponseEntity.ok(roleService.toDto(updatedRole));
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<?> deleteRole(@PathVariable("roleId") String roleId) {
        try {
            roleService.deleteRole(roleId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(List.of(e.getMessage())));
        }
    }

    @GetMapping("get-all-user-roles-permission")
    public ResponseEntity<?> getAllUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.noContent().build();
        } else {
            List<RoleDTO> roleDtos = userRoleService.getRolesByUsername(authentication.getName());
            if (roleDtos.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.ok(roleDtos);
            }
        }
    }

    @GetMapping("/get-user-roles-permission/{userId}")
    public ResponseEntity<?> getUserRolesById(@PathVariable("userId") String userId) {
        List<RoleDTO> roleDtos = userRoleService.getRolesByUser(userId);
        if (roleDtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(roleDtos);
        }
    }

    @GetMapping("/{roleId}/permissions")
    public ResponseEntity<?> getRoleWithPermissions(@PathVariable("roleId") String roleId) {
        RoleDTO roleDTO = roleService.getRoleWithPermissions(roleId);
        return roleDTO != null ? ResponseEntity.ok(roleDTO) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{roleId}/users")
    public ResponseEntity<List<UserDTO>> getUsersByRoleId(@PathVariable String roleId) {
        List<UserDTO> users = userRoleService.getUsersByRoleId(roleId);
        return ResponseEntity.ok(users);
    }
}