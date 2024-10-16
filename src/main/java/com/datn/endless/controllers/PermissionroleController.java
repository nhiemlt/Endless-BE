package com.datn.endless.controllers;

import com.datn.endless.entities.Permission;
import com.datn.endless.services.PermissionroleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/permissions-roles")
public class PermissionroleController {

    @Autowired
    private PermissionroleService permissionroleService;

    @PostMapping("/add/{roleId}/{permissionId}")
    public void addPermissionToRole(@PathVariable UUID roleId, @PathVariable UUID permissionId) {
        permissionroleService.addPermissionToRole(roleId, permissionId);
    }

    @DeleteMapping("/remove/{roleId}/{permissionId}")
    public void removePermissionFromRole(@PathVariable UUID roleId, @PathVariable UUID permissionId) {
        permissionroleService.removePermissionFromRole(roleId, permissionId);
    }

    @GetMapping("/permissions/{roleId}")
    public List<Permission> findPermissionsByRoleId(@PathVariable UUID roleId) {
        return permissionroleService.findPermissionsByRoleId(roleId);
    }
}
