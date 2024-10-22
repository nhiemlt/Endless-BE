package com.datn.endless.services;

import com.datn.endless.dtos.PermissionDTO;
import com.datn.endless.dtos.RoleDTO;
import com.datn.endless.dtos.UserDTO;
import com.datn.endless.dtos.UseraddressDTO;
import com.datn.endless.entities.User;
import com.datn.endless.entities.Useraddress;
import com.datn.endless.models.UserModel;
import com.datn.endless.repositories.UserRepository;
import com.datn.endless.repositories.UseraddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLoginInfomation userLoginInfomation;

    @Autowired
    private UseraddressRepository userAddressRepository;

    // Chuyển đổi User thành UserDTO
    private UserDTO convertToDTO(User user) {
        List<RoleDTO> roles = user.getRoles() != null ? user.getRoles().stream()
                .map(role -> {
                    RoleDTO roleDTO = new RoleDTO();
                    roleDTO.setRoleId(role.getRoleId());
                    roleDTO.setRoleName(role.getRoleName());
                    roleDTO.setPermissions(
                            role.getPermissions().stream()
                                    .map(permission -> new PermissionDTO(permission.getPermissionID(), permission.getPermissionName(), permission.getEnPermissionname()))
                                    .collect(Collectors.toList())
                    );
                    return roleDTO;
                })
                .collect(Collectors.toList()) : null;

        List<Useraddress> addresses = userAddressRepository.findByUser(user);
        List<UseraddressDTO> addressDTOs = addresses != null ? addresses.stream()
                .map(address -> new UseraddressDTO(
                        address.getAddressID(),
                        address.getUserID().getUserID(),
                        address.getUserID().getUsername(),
                        address.getProvinceID() != null ? address.getProvinceID() : null,
                        address.getDistrictID() != null ? address.getDistrictID() : null,
                        address.getWardCode() != null ? address.getWardCode() : null,
                        address.getDetailAddress() != null ? address.getDetailAddress() : null
                ))
                .collect(Collectors.toList()) : null;

        return UserDTO.builder()
                .userID(user.getUserID())
                .username(user.getUsername())
                .fullname(user.getFullname())
                .phone(user.getPhone())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .language(user.getLanguage())
                .roles(roles)
                .addresses(addressDTOs)
                .build();
    }

    // Chuyển đổi danh sách User thành danh sách UserDTO
    private List<UserDTO> convertToDTOList(List<User> users) {
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Lấy người dùng theo ID
    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id).orElse(null);
        return user != null ? convertToDTO(user) : null;
    }

    public UserDTO saveUser(UserModel userModel) {
        User user = new User();
        user.setUserID(userModel.getUserID());
        user.setUsername(userModel.getUsername());
        user.setFullname(userModel.getFullname());
        user.setPhone(userModel.getPhone());
        user.setEmail(userModel.getEmail());
        user.setLanguage(userModel.getLanguage());

        // Lưu chuỗi base64 của avatar từ frontend
        user.setAvatar(userModel.getAvatar());

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO updateCurrentUser(UserModel userModel) {
        User user = userRepository.findById(userModel.getUserID()).orElse(null);
        if (user != null) {
            user.setFullname(userModel.getFullname());
            user.setPhone(userModel.getPhone());
            user.setEmail(userModel.getEmail());
            user.setLanguage(userModel.getLanguage());

            // Cập nhật chuỗi base64 của avatar từ frontend
            if (userModel.getAvatar() != null && !userModel.getAvatar().isEmpty()) {
                user.setAvatar(userModel.getAvatar());
            }

            userRepository.save(user);
            return convertToDTO(user);
        }
        return null;
    }

    public UserDTO updateUserById(UserModel userModel) {
        User user = userRepository.findById(userModel.getUserID()).orElse(null);
        if (user != null) {
            user.setUsername(userModel.getUsername());
            user.setFullname(userModel.getFullname());
            user.setPhone(userModel.getPhone());
            user.setEmail(userModel.getEmail());
            user.setLanguage(userModel.getLanguage());

            if (userModel.getAvatar() != null && !userModel.getAvatar().isEmpty()) {
                user.setAvatar(userModel.getAvatar());
            }

            userRepository.save(user);
            return convertToDTO(user);
        }
        return null;
    }

    // Xóa người dùng theo ID
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    // Lấy tất cả người dùng với phân trang và tìm kiếm theo tên
    public Page<UserDTO> getUsersWithPaginationAndSearch(String keyword, Pageable pageable) {
        Page<User> users;
        if (keyword != null && !keyword.isEmpty()) {
            users = userRepository.searchByFullname(keyword, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }
        return users.map(this::convertToDTO);
    }

    public UserDTO getCurrentUser() {
        String username = userLoginInfomation.getCurrentUsername();
        User user = userRepository.findByUsername(username);
        return convertToDTO(user);
    }
}
