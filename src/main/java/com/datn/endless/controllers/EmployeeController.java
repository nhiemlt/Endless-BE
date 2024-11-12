package com.datn.endless.controllers;

import com.datn.endless.dtos.EmployeeDTO;
import com.datn.endless.dtos.UserroleDTO;
import com.datn.endless.exceptions.DuplicateResourceException;
import com.datn.endless.exceptions.RemoveRoleException;
import com.datn.endless.exceptions.ResourceNotFoundException;
import com.datn.endless.exceptions.UserNotFoundException;
import com.datn.endless.models.EmployeeModel;
import com.datn.endless.services.AuthService;
import com.datn.endless.services.EmployeeService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    // Lấy danh sách nhân viên với từ khóa tìm kiếm và phân trang
    @GetMapping
    public ResponseEntity<Page<EmployeeDTO>> getEmployees(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PageableDefault(size = 10, sort = "createDate", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(employeeService.getEmployees(keyword, pageable));
    }

    // Tạo nhân viên mới
    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody @Valid EmployeeModel employeeModel) {
        try {
            EmployeeDTO createdEmployee = employeeService.createEmployee(employeeModel);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
        } catch (DuplicateResourceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // Cập nhật thông tin nhân viên
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable String userId,
            @RequestBody @Valid EmployeeModel employeeModel) {
        try {
            EmployeeDTO updatedEmployee = employeeService.updateEmployee(userId, employeeModel);
            return ResponseEntity.ok(updatedEmployee);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DuplicateResourceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // Chuyển đổi trạng thái kích hoạt của nhân viên
    @PatchMapping("/{userId}/toggle")
    public ResponseEntity<?> toggleEmployeeStatus(@PathVariable String userId) {
        try {
            EmployeeDTO toggledEmployee = employeeService.toggleEmployeeStatus(userId);
            return ResponseEntity.ok(toggledEmployee);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (DuplicateResourceException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PatchMapping("/{userId}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable String userId) throws MessagingException {
        return ResponseEntity.ok(employeeService.resetPassword(userId));
    }

    // Lấy tất cả vai trò của một người dùng
    @GetMapping("/{userId}/roles")
    public ResponseEntity<?> getAllUserRoles(@PathVariable String userId) {
        try {
            List<UserroleDTO> userRoles = employeeService.getAllUserRoles(userId);
            return ResponseEntity.ok(userRoles);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Cập nhật danh sách vai trò của người dùng
    @PutMapping("/{userId}/roles")
    public ResponseEntity<?> updateUserRoles(
            @PathVariable String userId,
            @RequestBody List<String> roleIds) {
        try {
            employeeService.updateUserrole(userId, roleIds);
            return ResponseEntity.ok("Danh sách vai trò đã được cập nhật thành công.");
        } catch (RemoveRoleException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
