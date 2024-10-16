package com.datn.endless.controllers;

import com.datn.endless.dtos.RoleDTO;
import com.datn.endless.entities.Role;
import com.datn.endless.services.RoleService;
import com.datn.endless.services.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO roleDTO) {
        Role createdRole = roleService.createRole(roleDTO);
        return ResponseEntity.ok(roleService.toDto(createdRole));
    }

    @PutMapping
    public ResponseEntity<RoleDTO> updateRole(@RequestBody RoleDTO roleDTO) {
        try {
            Role updatedRole = roleService.updateRole(roleDTO);
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
            return ResponseEntity.badRequest().build();  // Nếu UUID không hợp lệ, trả về lỗi 400
        }
    }


    @GetMapping("get-all-user-roles")
    public ResponseEntity<List<RoleDTO>> getAllUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getName()+"\n\n\n\n");
        if (authentication == null) {
            return ResponseEntity.noContent().build();
        }
        else{
            List<RoleDTO> roleDtos = userRoleService.getRolesByUsername(authentication.getName());
            for (RoleDTO roleDto : roleDtos) {
                System.out.println(roleDto.getRoleName());
            }
            if (roleDtos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            else{
                return ResponseEntity.ok(roleDtos);
            }
        }
    }
}
