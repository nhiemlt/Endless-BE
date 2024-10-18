package com.datn.endless.controllers;

import com.datn.endless.dtos.UserDTO;
import com.datn.endless.models.UserModel;
import com.datn.endless.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
    @RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Lấy tất cả người dùng
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> users = userService.getUsersWithPaginationAndSearch(keyword, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/current")
    public UserDTO getCurrentUser() {
        return userService.getCurrentUser();
    }

    // Lấy người dùng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") String id) {
        UserDTO user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Thêm người dùng mới
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserModel userModel) {
        UserDTO savedUser = userService.saveUser(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    // Cập nhật người dùng theo ID
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") String id, @RequestBody UserModel userModel) {
        userModel.setUserID(id); // Đảm bảo ID trùng khớp với URL
        UserDTO updatedUser = userService.saveUser(userModel);
        return ResponseEntity.ok(updatedUser);
    }

    // Xóa người dùng theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
