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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
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
                        address.getProvinceCode() != null ? address.getProvinceCode().getCode() : null,
                        address.getDistrictCode() != null ? address.getDistrictCode().getCode() : null,
                        address.getWardCode() != null ? address.getWardCode().getCode() : null,
                        address.getHouseNumberStreet(),
                        address.getProvinceCode() != null ? address.getProvinceCode().getName() : null,
                        address.getDistrictCode() != null ? address.getDistrictCode().getName() : null,
                        address.getWardCode() != null ? address.getWardCode().getName() : null
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

        if (userModel.getAvatar() != null && !userModel.getAvatar().isEmpty()) {
            String contentType = userModel.getAvatar().getContentType();

            if (!contentType.startsWith("image/")) {
                throw new IllegalArgumentException("File must be an image");
            }

            try {
                String base64Image = convertToBase64(userModel.getAvatar());
                user.setAvatar(base64Image);
            } catch (IOException e) {
                throw new RuntimeException("Failed to convert image to base64", e);
            }
        }

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

            if (userModel.getAvatar() != null && !userModel.getAvatar().isEmpty()) {
                try {
                    user.setAvatar(convertToBase64(userModel.getAvatar()));
                } catch (IOException e) {
                    throw new RuntimeException("Failed to convert image to base64", e);
                }
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

    // Chuyển đổi MultipartFile thành base64
    private String convertToBase64(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }
}
