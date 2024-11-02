package com.datn.endless.repositories;

import com.datn.endless.entities.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    @Query("SELECT r FROM Role r WHERE LOWER(r.roleName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Role> searchRolesByKeyword(@Param("keyword") String keyword);

    Page<Role> findByRoleNameContainingIgnoreCase(String roleName, Pageable pageable);

    Role findByRoleName(String roleName);
}
