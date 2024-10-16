package com.datn.endless.controllers;

import com.datn.endless.configs.SecurityConfig;
import com.datn.endless.dtos.RoleDTO;
import com.datn.endless.entities.Role;
import com.datn.endless.services.RoleService;
import com.datn.endless.services.UserRoleService;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/roles")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleDTO>> getRolesByUser(@PathVariable String userId) {
        return ResponseEntity.ok(userRoleService.getRolesByUser(userId));
    }

    @PostMapping("/{roleId}")
    public ResponseEntity<Void> assignRoleToUser(@PathVariable UUID userId, @PathVariable UUID roleId) {
        userRoleService.assignRoleToUser(userId, roleId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> removeRoleFromUser(@PathVariable UUID userId, @PathVariable UUID roleId) {
        userRoleService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.noContent().build();
    }

}

