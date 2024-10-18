package com.datn.endless.controllers;

import com.datn.endless.dtos.RoleDTO;
import com.datn.endless.entities.Role;
import com.datn.endless.models.RoleModel;
import com.datn.endless.services.RoleService;
import com.datn.endless.services.UserRoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    UserRoleService userRoleService;

    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable("roleId") String roleId) {
        try {
            RoleDTO dto = roleService.getRoleById(roleId);
            return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@Validated(RoleModel.OnCreate.class) @RequestBody RoleModel roleModel) {
        Role createdRole = roleService.createRole(roleModel);
        return ResponseEntity.ok(roleService.toDto(createdRole));
    }

    @PutMapping
    public ResponseEntity<RoleDTO> updateRole(@Validated(RoleModel.OnUpdate.class) @RequestBody RoleModel roleModel) {
        try {
            Role updatedRole = roleService.updateRole(roleModel);
            return updatedRole != null ? ResponseEntity.ok(roleService.toDto(updatedRole)) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable("roleId") String roleId) {
        try {
            roleService.deleteRole(roleId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("get-all-user-roles-permission")
    public ResponseEntity<List<RoleDTO>> getAllUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.noContent().build();
        }
        else{
            List<RoleDTO> roleDtos = userRoleService.getRolesByUsername(authentication.getName());
            if (roleDtos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            else{
                return ResponseEntity.ok(roleDtos);
            }
        }
    }

    @GetMapping("/get-user-roles-permission/{userId}")
    public ResponseEntity<List<RoleDTO>> getUserRolesById(@PathVariable("userId") String userId) {
        List<RoleDTO> roleDtos = userRoleService.getRolesByUser(userId);
        if (roleDtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(roleDtos);
        }
    }

    @GetMapping("/{roleId}/permissions")
    public ResponseEntity<RoleDTO> getRoleWithPermissions(@PathVariable("roleId") String roleId) {
        RoleDTO roleDTO = roleService.getRoleWithPermissions(roleId);
        return roleDTO != null ? ResponseEntity.ok(roleDTO) : ResponseEntity.notFound().build();
    }

}
