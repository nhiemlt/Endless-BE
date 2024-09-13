package com.datn.endless.repositories;

import com.datn.endless.entities.Role;
import com.datn.endless.entities.Userrole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserroleRepository extends JpaRepository<Userrole, String> {

    @Query("SELECT ur.role FROM Userrole ur WHERE ur.user.userID = :userID")
    List<Role> findRolesByUserId(String userID);

    @Query("SELECT ur.role FROM Userrole ur WHERE ur.user.username = :usename")
    List<Role> findRolesByUsername(String usename);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO userroles (UserRole_ID, user_id, role_id) VALUES (:userRoleId, :userID, :roleId)", nativeQuery = true)
    void addRoleToUser(String userRoleId, UUID userID, UUID roleId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM userroles WHERE user_id = :userID AND role_id = :roleId", nativeQuery = true)
    void removeRoleFromUser(UUID userID, UUID roleId);
}

