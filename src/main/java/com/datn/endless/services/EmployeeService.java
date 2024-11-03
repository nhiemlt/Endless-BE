package com.datn.endless.services;

import com.datn.endless.dtos.*;
import com.datn.endless.entities.Role;
import com.datn.endless.entities.User;
import com.datn.endless.entities.Userrole;
import com.datn.endless.exceptions.DuplicateResourceException;
import com.datn.endless.exceptions.RemoveRoleException;
import com.datn.endless.exceptions.ResourceNotFoundException;
import com.datn.endless.exceptions.UserNotFoundException;
import com.datn.endless.models.EmployeeModel;
import com.datn.endless.repositories.RoleRepository;
import com.datn.endless.repositories.UserRepository;
import com.datn.endless.repositories.UserroleRepository;
import com.datn.endless.utils.RandomUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository employeeRepository;

    @Autowired
    UserroleRepository employeeRoleRepository;

    @Autowired
    UserLoginInfomation userLoginInfomation;

    @Autowired
    MailService mailService;

    public Page<EmployeeDTO> getEmployees(String keyword, Pageable pageable) {
        Page<User> users = employeeRepository.searchEmployees(keyword, pageable);
        return users.map(this::convertToDTO);
    }

    public List<UserroleDTO> getAllUserRoles(String userId) {
        // Lấy danh sách vai trò của người dùng từ repository
        List<Userrole> userRoles = employeeRoleRepository.findAllByUser_UserID(userId);

        // Nếu không tìm thấy vai trò nào, ném ngoại lệ hoặc trả về danh sách rỗng
        if (userRoles.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy vai trò nào cho user với ID: " + userId);
        }

        // Chuyển đổi danh sách Userrole sang danh sách UserroleDTO
        return userRoles.stream()
                .map(this::convertToUserRoleDTO)
                .collect(Collectors.toList());
    }

    // Tạo nhân viên
    public EmployeeDTO createEmployee(EmployeeModel employeeModel) {
        // Kiểm tra sự tồn tại của username, phone và email
        if(employeeModel.getUsername().isEmpty()){
            throw new DuplicateResourceException("Username không được bỏ trống");
        }
        if (employeeRepository.existsByUsername(employeeModel.getUsername())) {
            throw new DuplicateResourceException("Username đã tồn tại");
        }
        if (employeeRepository.existsByPhone(employeeModel.getPhone())) {
            throw new DuplicateResourceException("Số điện thoại đã tồn tại");
        }
        if (employeeRepository.existsByEmail(employeeModel.getEmail())) {
            throw new DuplicateResourceException("Email đã tồn tại");
        }

        // Tạo đối tượng User mới và thiết lập các thông tin từ EmployeeModel
        User user = new User();
        setCreateDetails(user, employeeModel);
        user.setUserID(UUID.randomUUID().toString());
        user.setActive(true);
        user.setPassword(createRandomPassword(user.getUsername(), user.getEmail()));

        // Thiết lập vai trò cho nhân viên
        Set<Role> roles = getRolesFromIds(employeeModel.getRoleIds());
        Role nv = roleRepository.findByRoleName("Nhân viên");
        roles.removeIf(role -> role.equals(nv));
        roles.add(nv);
        user.setRoles(roles);

        // Lưu User và trả về EmployeeDTO
        return convertToDTO(employeeRepository.save(user));
    }

    // Cập nhật nhân viên
    public EmployeeDTO updateEmployee(String userId, EmployeeModel employeeModel) {
        User user = employeeRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy nhân viên"));
        if(user.getRoles().isEmpty()){
            throw new DuplicateResourceException("Đây là tài khoản khách hàng");
        }
        if (!employeeModel.getPhone().equals(user.getPhone()) && employeeRepository.existsByPhone(employeeModel.getPhone())) {
            throw new DuplicateResourceException("Số điện thoại đã tồn tại");
        }
        if (!employeeModel.getEmail().equals(user.getEmail()) && employeeRepository.existsByEmail(employeeModel.getEmail())) {
            throw new DuplicateResourceException("Email đã tồn tại");
        }
        setUpdateDetails(user, employeeModel);

        return convertToDTO(employeeRepository.save(user));
    }

    // Active/Inactive nhân viên
    public EmployeeDTO toggleEmployeeStatus(String userId) {
        User user = employeeRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy nhân viên"));
        if(user.getRoles().isEmpty()){
            throw new DuplicateResourceException("Đây là tài khoản khách hàng");
        }
        if(user.getUsername().equals(userLoginInfomation.getCurrentUsername())) {
            throw new DuplicateResourceException("Bạn không thể thay đổi trạng thái tài khoản của bản thân !");
        }
        for (Role role: user.getRoles()){
            if(role.getRoleName().equals("SuperAdmin")){
                throw new DuplicateResourceException("Bạn không thể thay đổi trạng thái kích hoạt của tài khoản này");
            }
        }
        boolean active = user.getActive();
        user.setActive(!active);
        return convertToDTO(employeeRepository.save(user));
    }

    public Map<String, Object> resetPassword(String userId) throws MessagingException {
        User user = employeeRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy nhân viên"));

        // Tạo mật khẩu mới
        String newPassword = createRandomPassword(user.getUsername(), user.getEmail());
        user.setPassword(newPassword);
        employeeRepository.save(user);

        // Gửi mật khẩu tạm thời qua email
        mailService.sendTemporaryPasswordMail(user.getUsername(), user.getEmail(), newPassword);

        // Chuẩn bị phản hồi
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Mật khẩu đã được đặt lại thành công và đã được gửi thông qua email.");
        return response;
    }

    public String createRandomPassword(String username, String email) {
        String newPassword = RandomUtil.generateComplexRandomString();
        String passwordEndcode = Encode.hashCode(newPassword);
        try{
            mailService.sendTemporaryPasswordMail(username, email, newPassword);
        } catch (MessagingException e) {
            throw new RuntimeException("Đã sảy ra lỗi khi tạo mật khẩu");
        }

        return passwordEndcode;
    }


    public List<Userrole> updateUserrole(String userId, List<String> roleIds) {
        // Lấy thông tin người dùng hiện tại
        String currentUsername = userLoginInfomation.getCurrentUsername();

        // Kiểm tra xem user có tồn tại hay không
        User user = employeeRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy nhân viên với ID: " + userId));

        // Không cho phép người dùng xóa vai trò của chính họ
        if (user.getUsername().equals(currentUsername)) {
            throw new RemoveRoleException("Bạn không thể xóa vai trò của chính mình!");
        }

        // Lấy danh sách Userrole hiện tại của user và xóa chúng nếu không thuộc vai trò quan trọng
        List<Userrole> currentRoles = employeeRoleRepository.findAllByUser_UserID(userId);
        List<Userrole> rolesToRemove = new ArrayList<>();
        for (Userrole ur : currentRoles) {
            String roleName = ur.getRole().getRoleName();
            // Không cho phép xóa vai trò "SuperAdmin" và "Nhân viên"
            if (!roleName.equals("SuperAdmin") && !roleName.equals("Nhân viên")) {
                rolesToRemove.add(ur);
            }
        }
        employeeRoleRepository.deleteAll(rolesToRemove);

        List<Userrole> updatedRoles = new ArrayList<>();

        // Thêm các vai trò mới cho user
        List<String> rolesToKeep = new ArrayList<>(roleIds); // Tạo một danh sách mới từ roleIds
        for (String roleId : rolesToKeep) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò với ID: " + roleId));
            if(role.getRoleName().equals("SuperAdmin") || role.getRoleName().equals("Nhân viên")) {
                continue; // Bỏ qua vai trò này
            }

            Userrole userrole = new Userrole();
            userrole.setUser(user);
            userrole.setRole(role);
            updatedRoles.add(userrole);
        }

        // Lưu danh sách Userrole mới và trả về kết quả
        return employeeRoleRepository.saveAll(updatedRoles);
    }

    // Helper methods
    private void setCreateDetails(User user, EmployeeModel model) {
        user.setUsername(model.getUsername());
        user.setFullname(model.getFullname());
        user.setPhone(model.getPhone());
        user.setEmail(model.getEmail());
        user.setAvatar(model.getAvatar());
        user.setCreateDate(LocalDateTime.now());
        user.setActive(true);
        user.setForgetPassword(true);
    }

    private void setUpdateDetails(User user, EmployeeModel model) {
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

    private UserroleDTO convertToUserRoleDTO(Userrole userrole) {
        RoleDTO roleDTO = new RoleDTO(
                userrole.getRole().getRoleId(),
                userrole.getRole().getRoleName());

        return new UserroleDTO(
                userrole.getUserRoleId(),
                userrole.getUser().getUserID(),
                roleDTO
        );
    }
}
