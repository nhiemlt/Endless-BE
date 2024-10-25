package com.datn.endless.repositories;

import com.datn.endless.entities.Role;
import com.datn.endless.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {

    // Tìm người dùng theo tên đăng nhập
    User findByUsername(String username);

    // Tìm người dùng theo email
    User findByEmail(String email);

    User findByPhone(String phone);

    // Tìm người dùng theo từ khóa (tên đăng nhập hoặc email)
    @Query("SELECT u FROM User u WHERE u.username = :keyword OR u.email = :keyword")
    User findByKeyword(@Param("keyword") String keyword);

    // Lấy danh sách các vai trò của người dùng theo ID người dùng
    @Query("SELECT r FROM Role r JOIN Userrole ur ON r.roleId = ur.role.roleId WHERE ur.user.userID = :userId")
    List<Role> findRolesByUserId(@Param("userId") String userId);

    @Query("SELECT u FROM User u WHERE u.fullname LIKE %:keyword%")
    Page<User> searchByFullname(@Param("keyword") String keyword, Pageable pageable);

    // Tìm tất cả người dùng có active là true
    List<User> findByActiveTrue();

    @Query("SELECT u FROM User u WHERE u.fullname LIKE %:keyword% or u.username like %:keyword% or u.email like %:keyword% or u.phone like %:keyword%")
    Page<User> findAllUser(@Param("keyword") String keyword, Pageable pageable);

}
