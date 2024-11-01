package com.datn.endless.controllers;

import com.datn.endless.dtos.PermissionDTO;
import com.datn.endless.services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/permission")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    // Lấy permission theo ID
    @GetMapping("/{id}")
    public ResponseEntity<PermissionDTO> getPermissionById(@PathVariable String id) {
        Optional<PermissionDTO> permission = permissionService.getPermissionById(id);
        return permission.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PermissionDTO>> searchPermissions(
            @RequestParam(value = "keyword", required = false) String keyword) {
        List<PermissionDTO> permissions = permissionService.searchPermissions(keyword);
        return ResponseEntity.ok(permissions);
    }

    // Lọc permissions theo module
    @GetMapping("/filter")
    public ResponseEntity<List<PermissionDTO>> filterPermissionsByModule(
            @RequestParam(value = "moduleID") String moduleID) {
        List<PermissionDTO> permissions = permissionService.filterPermissionsByModule(moduleID);
        return ResponseEntity.ok(permissions);
    }
}
