package com.datn.endless.controllers;

import com.datn.endless.dtos.UserDTO;
import com.datn.endless.entities.User;
import com.datn.endless.models.UserModel;
import com.datn.endless.repositories.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

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
    public ResponseEntity<UserDTO> getCurrentUser() {
        UserDTO user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
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
    public ResponseEntity<UserDTO> createUser(@ModelAttribute UserModel userModel) {
        try {
            UserDTO createdUser = userService.saveUser(userModel);
            return ResponseEntity.ok(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") String id, @ModelAttribute UserModel userModel) {
        userModel.setUserID(id);
        try {
            UserDTO updatedUser = userService.saveUser(userModel);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/current")
    public ResponseEntity<UserDTO> updateCurrentUser(@RequestBody UserModel userModel) {
        try {
            UserDTO currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.notFound().build();
            }

            userModel.setUserID(currentUser.getUserID());
            UserDTO updatedUser = userService.updateCurrentUser(userModel);

            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Xóa người dùng theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") String id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
