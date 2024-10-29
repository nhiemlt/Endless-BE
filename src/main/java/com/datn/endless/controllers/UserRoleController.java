package com.datn.endless.controllers;

import com.datn.endless.dtos.UserDTO;
import com.datn.endless.dtos.UserRoleDTO;
import com.datn.endless.dtos.RoleDTO;
import com.datn.endless.services.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles/manage")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    @GetMapping("/users/{userId}/roles")
    public ResponseEntity<List<RoleDTO>> getRolesByUser(@PathVariable String userId) {
        return ResponseEntity.ok(userRoleService.getRolesByUser(userId));
    }

    @GetMapping("/roles/{roleId}/users")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String roleId) {
        List<UserDTO> users = userRoleService.getUsersByRoleId(roleId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RoleDTO>> searchRoles(@RequestParam String keyword) {
        List<RoleDTO> roles = userRoleService.searchRoles(keyword);
        return ResponseEntity.ok(roles);
    }

    @PostMapping
    public ResponseEntity<Void> assignUsersToRole(@PathVariable String roleId, @RequestBody UserRoleDTO userRoleDTO) {
        userRoleService.assignUsersToRole(roleId, userRoleDTO.getUserIds());
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
