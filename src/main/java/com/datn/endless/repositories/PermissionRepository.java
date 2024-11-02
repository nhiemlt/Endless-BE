package com.datn.endless.repositories;

import com.datn.endless.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, String> {
    @Query("SELECT p FROM Permission p WHERE :keyword IS NULL OR " +
            "(p.moduleID.moduleName LIKE %:keyword% OR p.permissionName LIKE %:keyword%)")
    List<Permission> findAllByKeyword(@Param("keyword") String keyword);

    List<Permission> findByModuleID_ModuleID(String moduleID);
}