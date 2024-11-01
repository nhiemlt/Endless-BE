package com.datn.endless.controllers;

import com.datn.endless.dtos.EmployeeDTO;
import com.datn.endless.models.EmployeeModel;
import com.datn.endless.services.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<Page<EmployeeDTO>> getEmployees(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PageableDefault(size = 10, sort = "fullname") Pageable pageable) {
        return ResponseEntity.ok(employeeService.getEmployees(keyword, pageable));
    }

    // Create employee
    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody @Valid EmployeeModel employeeModel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(employeeModel));
    }

    // Update employee
    @PutMapping("/{userId}")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable String userId,
            @RequestBody @Valid EmployeeModel employeeModel) {
        return ResponseEntity.ok(employeeService.updateEmployee(userId, employeeModel));
    }

    // Toggle employee active status
    @PatchMapping("/{userId}/toggle")
    public ResponseEntity<EmployeeDTO> toggleEmployeeStatus(
            @PathVariable String userId,
            @RequestParam Boolean active) {
        return ResponseEntity.ok(employeeService.toggleEmployeeStatus(userId, active));
    }
}
