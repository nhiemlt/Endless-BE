package com.datn.endless.repositories;

import com.datn.endless.entities.Permissionrole;
import com.datn.endless.entities.PermissionroleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionroleRepository extends JpaRepository<Permissionrole, PermissionroleId> {
    void deleteById(PermissionroleId id);
}
