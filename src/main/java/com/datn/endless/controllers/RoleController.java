package com.datn.endless.controllers;


import com.datn.endless.dtos.RoleDTO;
import com.datn.endless.exceptions.DuplicateResourceException;
import com.datn.endless.exceptions.RemoveRoleException;
import com.datn.endless.exceptions.RoleNotFoundException;
import com.datn.endless.models.RoleModel;
import com.datn.endless.services.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    // Lấy tất cả roles với phân trang, lọc, tìm kiếm theo keyword, và sắp xếp
    @GetMapping
    public ResponseEntity<Page<RoleDTO>> getAllRoles(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sortBy", defaultValue = "roleName") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RoleDTO> roles = roleService.getAllRoles(keyword, pageable);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/current")
    public ResponseEntity<Set<RoleDTO>> getCurrentUserRoles() {
        Set<RoleDTO> currentUserRoles = roleService.getCurrentUserRoles();
        return ResponseEntity.ok(currentUserRoles);
    }

    // Lấy role theo ID và kèm danh sách permissions
    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable String id) {
        Optional<RoleDTO> role = roleService.getRoleById(id);
        return role.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    // Tạo mới role và gán permissions cho role
    @PostMapping
    public ResponseEntity<?> createRole(@Valid @RequestBody RoleModel roleModel) {
        try {
            RoleDTO createdRole = roleService.createRole(roleModel);
            return ResponseEntity.ok(createdRole);
        } catch (DuplicateResourceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // Cập nhật role và cập nhật lại danh sách permissions cho role
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable String id, @Valid @RequestBody RoleModel roleModel) {
        try{
            Optional<RoleDTO> updatedRole = roleService.updateRole(id, roleModel);
            return updatedRole.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (RoleNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RemoveRoleException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }catch (DuplicateResourceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // Xóa role và tất cả các permissions liên quan đến role
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable String id) {
        try {
            roleService.deleteRole(id);
            return ResponseEntity.status(HttpStatus.OK).body("Xóa thành công");
        } catch (RoleNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RemoveRoleException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }catch (DuplicateResourceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

}