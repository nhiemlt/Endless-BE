package com.datn.endless.controllers;

import com.datn.endless.dtos.UserRoleDTO;
import com.datn.endless.dtos.RoleDTO;
import com.datn.endless.services.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/roles")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    @GetMapping
    public ResponseEntity<List<RoleDTO>> getRolesByUser(@PathVariable String userId) {
        return ResponseEntity.ok(userRoleService.getRolesByUser(userId));
    }

    @PostMapping
    public ResponseEntity<Void> assignRolesToUser(@PathVariable String userId, @RequestBody UserRoleDTO userRoleDTO) {
        userRoleService.assignRolesToUser(userId, userRoleDTO.getRoleIds());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteUserRole(@PathVariable("userId") String userId, @PathVariable("roleId") String roleId) {
        try {
            userRoleService.deleteUserRole(userId, roleId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
