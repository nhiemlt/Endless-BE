package com.datn.endless.repositories;

import com.datn.endless.entities.Role;
import com.datn.endless.entities.User;
import com.datn.endless.entities.Userrole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserroleRepository extends JpaRepository<Userrole, String> {

    @Query("SELECT ur.role FROM Userrole ur WHERE ur.user.userID = :userID")
    List<Role> findRolesByUserId(String userID);

    @Query("SELECT ur.role FROM Userrole ur WHERE ur.user.username = :username")
    List<Role> findRolesByUsername(String username);

    @Query("SELECT ur FROM Userrole ur WHERE ur.user = :user AND ur.role.roleId = :roleId")
    List<Userrole> findByUserAndRole(User user, String roleId);

    @Query("SELECT COUNT(ur) FROM Userrole ur WHERE ur.role.roleId = :roleId")
    int countUsersByRole(@Param("roleId") String roleId);

    List<Userrole> findByRole(Role role);

    Optional<Userrole> findByUserRoleId(String userRoleId);

    List<Userrole> findAllByUser_UserID(String userID);

}

