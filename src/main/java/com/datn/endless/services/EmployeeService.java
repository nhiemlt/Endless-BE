package com.datn.endless.services;

import com.datn.endless.dtos.EmployeeDTO;
import com.datn.endless.dtos.EmployeeRoleDTO;
import com.datn.endless.dtos.UserDTO;
import com.datn.endless.entities.Role;
import com.datn.endless.entities.User;
import com.datn.endless.models.EmployeeModel;
import com.datn.endless.repositories.RoleRepository;
import com.datn.endless.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository employeeRepository;

    public Page<EmployeeDTO> getEmployees(String keyword, Pageable pageable) {
        Page<User> users = employeeRepository.searchEmployees(keyword, pageable);
        return users.map(this::convertToDTO);
    }

    // Tạo nhân viên
    public EmployeeDTO createEmployee(EmployeeModel employeeModel) {
        User user = new User();
        setUserDetails(user, employeeModel);
        user.setUserID(UUID.randomUUID().toString());
        user.setActive(true);
        Set<Role> roles = getRolesFromIds(employeeModel.getRoleIds());
        roles.add(roleRepository.findByRoleName("Nhân viên"));
        user.setRoles(roles);

        return convertToDTO(employeeRepository.save(user));
    }

    // Cập nhật nhân viên
    public EmployeeDTO updateEmployee(String userId, EmployeeModel employeeModel) {
        User user = employeeRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        setUserDetails(user, employeeModel);

        user.getRoles().clear();
        user.getRoles().addAll(getRolesFromIds(employeeModel.getRoleIds()));

        return convertToDTO(employeeRepository.save(user));
    }

    // Active/Inactive nhân viên
    public EmployeeDTO toggleEmployeeStatus(String userId, Boolean active) {
        User user = employeeRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        user.setActive(active);
        return convertToDTO(employeeRepository.save(user));
    }

    // Helper methods
    private void setUserDetails(User user, EmployeeModel model) {
        user.setUsername(model.getUsername());
        user.setFullname(model.getFullname());
        user.setPhone(model.getPhone());
        user.setEmail(model.getEmail());
        user.setAvatar(model.getAvatar());
    }

    private Set<Role> getRolesFromIds(List<String> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(roleRepository.findAllById(roleIds));
    }

    private EmployeeDTO convertToDTO(User user) {
        Set<EmployeeRoleDTO> roleDTOs = user.getRoles().stream()
                .map(role -> new EmployeeRoleDTO(
                        role.getRoleId(),
                        role.getRoleName()
                ))
                .collect(Collectors.toSet());

        return new EmployeeDTO(
                user.getUserID(),
                user.getUsername(),
                user.getFullname(),
                user.getPhone(),
                user.getEmail(),
                user.getAvatar(),
                user.getActive(),
                roleDTOs
        );
    }

}
