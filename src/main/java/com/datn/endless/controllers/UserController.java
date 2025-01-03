package com.datn.endless.controllers;

import com.datn.endless.dtos.InforDTO;
import com.datn.endless.dtos.UserDTO;
import com.datn.endless.models.UserModel;
import com.datn.endless.repositories.UserRepository;
import com.datn.endless.services.UserService;
import com.datn.endless.utils.ErrorResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/get-infor")
    public ResponseEntity<Page<InforDTO>> getAllUsersInfor(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InforDTO> users = userService.getUsersInfor(keyword, pageable);
        return ResponseEntity.ok(users);
    }

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
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    // Thêm người dùng mới
    @PostMapping()
    public ResponseEntity<?> createUser(@Valid @ModelAttribute UserModel userModel, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse(errors));
        }

        try {
            UserDTO createdUser = userService.saveUser(userModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(List.of(e.getMessage())));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(List.of("Failed to create user")));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") String id, @Valid @ModelAttribute UserModel userModel, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse(errors));
        }

        userModel.setUserID(id);
        try {
            UserDTO updatedUser = userService.updateCurrentUser(userModel);
            return updatedUser != null ? ResponseEntity.ok(updatedUser) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(List.of(e.getMessage())));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(List.of("Failed to update user")));
        }
    }


    @PutMapping("/current")
    public ResponseEntity<?> updateCurrentUser(@Valid @RequestBody UserModel userModel, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse(errors));
        }

        try {
            // Lấy người dùng hiện tại
            UserDTO currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.notFound().build(); // Người dùng không tìm thấy
            }

            // Giả định avatar là URL từ frontend
            String avatarUrl = userModel.getAvatar();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                userModel.setAvatar(avatarUrl); // Lưu URL ảnh từ Firebase vào userModel
            }

            userModel.setUserID(currentUser.getUserID());
            UserDTO updatedUser = userService.updateCurrentUser(userModel);
            return ResponseEntity.ok(updatedUser); // Trả về người dùng đã cập nhật
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(List.of(e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(List.of("Đã xảy ra lỗi khi cập nhật người dùng")));
        }
    }



    // Xóa người dùng theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") String id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
