package com.datn.endless.services;

import com.datn.endless.entities.Permission;
import com.datn.endless.entities.Permissionrole;
import com.datn.endless.entities.PermissionroleId;
import com.datn.endless.repositories.PermissionroleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PermissionroleService {

    @Autowired
    private PermissionroleRepository permissionroleRepository;

    public void addPermissionToRole(UUID roleId, UUID permissionId) {
        PermissionroleId id = new PermissionroleId(permissionId.toString(), roleId.toString());
        Permissionrole permissionrole = new Permissionrole();
        permissionrole.setId(id);
        // Update other properties if necessary
        permissionroleRepository.save(permissionrole);
    }

    public void removePermissionFromRole(UUID roleId, UUID permissionId) {
        PermissionroleId id = new PermissionroleId(permissionId.toString(), roleId.toString());
        permissionroleRepository.deleteById(id);
    }

    public List<Permission> findPermissionsByRoleId(UUID roleId) {
        return permissionroleRepository.findAll().stream()
                .filter(pr -> pr.getId().getRoleId().equals(roleId.toString()))
                .map(Permissionrole::getPermission)
                .collect(Collectors.toList());
    }
}
