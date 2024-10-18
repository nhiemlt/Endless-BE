package com.datn.endless.controllers;

import com.datn.endless.dtos.PermissionDTO;
import com.datn.endless.services.PermissionroleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionroleController {

    @Autowired
    private PermissionroleService permissionRoleService;

    @GetMapping("/get-user-permissions/{userId}")
    public ResponseEntity<List<PermissionDTO>> getUserPermissionsById(@PathVariable("userId") String userId) {
        List<PermissionDTO> permissionDtos = permissionRoleService.getPermissionsByUserId(userId);
        if (permissionDtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(permissionDtos);
        }
    }
}

