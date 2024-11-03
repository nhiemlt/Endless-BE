package com.datn.endless.services;

import com.datn.endless.dtos.ModuleDTO;
import com.datn.endless.dtos.PermissionDTO;
import com.datn.endless.dtos.RoleDTO;
import com.datn.endless.entities.Permission;
import com.datn.endless.entities.Role;
import com.datn.endless.exceptions.DuplicateResourceException;
import com.datn.endless.exceptions.RemoveRoleException;
import com.datn.endless.exceptions.RoleNotFoundException;
import com.datn.endless.models.RoleModel;
import com.datn.endless.repositories.PermissionRepository;
import com.datn.endless.repositories.RoleRepository;
import com.datn.endless.repositories.UserroleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private UserroleRepository userroleRepository;

    // Lấy tất cả roles với phân trang, tìm kiếm theo keyword và sắp xếp
    public Page<RoleDTO> getAllRoles(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isEmpty()) {
            return roleRepository.findByRoleNameContainingIgnoreCase(keyword, pageable).map(this::toDTO);
        }
        return roleRepository.findAll(pageable).map(this::toDTO);
    }

    // Lấy role theo ID và danh sách permissions
    public Optional<RoleDTO> getRoleById(String id) {
        return roleRepository.findById(id).map(this::toDTO);
    }

    // Tạo mới role và thêm permissions
    public RoleDTO createRole(RoleModel roleModel) {
        if(roleRepository.findByRoleName(roleModel.getRoleName())!=null){
            throw new DuplicateResourceException("Tên vai trò này đã tồn tại");
        }
        Role role = new Role();
        role.setRoleId(UUID.randomUUID().toString());
        role.setRoleName(roleModel.getRoleName());

        Set<Permission> permissions = fetchPermissionsByIds(roleModel.getPermissionIds());
        role.setPermissions(permissions);

        role = roleRepository.save(role);
        return toDTO(role);
    }

    // Cập nhật role và cập nhật lại danh sách permissions
    public Optional<RoleDTO> updateRole(String id, RoleModel roleModel) {
        Optional<Role> roleOpt = roleRepository.findById(id);
        if (roleOpt.isPresent()) {
            Role role = roleOpt.get();
            if(!role.getRoleName().equals(roleModel.getRoleName())){
                if(roleRepository.findByRoleName(roleModel.getRoleName())!=null){
                    throw new DuplicateResourceException("Tên vai trò này đã tồn tại");
                }
            }
            role.setRoleName(roleModel.getRoleName());
            if (role.getRoleName().equals("Nhân viên") || role.getRoleName().equals("SuperAdmin")) {
                throw new RemoveRoleException("Không thể cập nhật vai trò này do đây là vai trò quang trọng");
            }
            role.getPermissions().clear();  // Xóa tất cả các permissions hiện có
            role.getPermissions().addAll(fetchPermissionsByIds(roleModel.getPermissionIds()));  // Gán lại permissions mới

            role = roleRepository.save(role);
            return Optional.of(toDTO(role));
        }
        return Optional.empty();
    }

    // Xóa role và các permissions liên quan
    public void deleteRole(String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role không tồn tại với id: " + id));

        if (role.getRoleName().equals("Nhân viên") || role.getRoleName().equals("SuperAdmin")) {
            throw new RemoveRoleException("Không thể xóa vai trò này do đây là vai trò quang trọng");
        }

        if(!userroleRepository.findByRole(role).isEmpty()){
            throw new RemoveRoleException("Không thể xóa vai trò này do xung đột dữ liệu");
        }

        roleRepository.deleteById(id);
    }


    // Chuyển Role sang DTO
    private RoleDTO toDTO(Role role) {
        Set<PermissionDTO> permissionDTOs = role.getPermissions().stream()
                .map(permission -> new PermissionDTO(
                        permission.getPermissionID(),
                        permission.getModuleID().getModuleName(),
                        permission.getCode(),
                        permission.getPermissionName()))
                .collect(Collectors.toSet());
        int employee = userroleRepository.countUsersByRole(role.getRoleId());
        int employeeActive = userroleRepository.countUsersActiveByRole(role.getRoleId());
        int employeeInactive = userroleRepository.countUsersInactiveByRole(role.getRoleId());

        return new RoleDTO(role.getRoleId(), role.getRoleName(),employee, employeeActive, employeeInactive, permissionDTOs);
    }

    // Lấy danh sách permissions theo ID
    private Set<Permission> fetchPermissionsByIds(List<String> permissionIds) {
        return new HashSet<>(permissionRepository.findAllById(permissionIds));
    }

}
