package com.datn.endless.services;

import com.datn.endless.dtos.PermissionDTO;
import com.datn.endless.dtos.RoleDTO;
import com.datn.endless.dtos.UserDTO;
import com.datn.endless.dtos.UseraddressDTO;
import com.datn.endless.entities.User;
import com.datn.endless.entities.Useraddress;
import com.datn.endless.models.UserModel;
import com.datn.endless.entities.Role;
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
        List<RoleDTO> roles = user.getRoles().stream()
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
                .collect(Collectors.toList());

        List<Useraddress> addresses = userAddressRepository.findByUser(user);
        List<UseraddressDTO> addressDTOs = addresses.stream()
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
                .collect(Collectors.toList());

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

    // Lấy tất cả người dùng
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return convertToDTOList(users);
    }

    // Lấy người dùng theo ID
    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id).orElse(null);
        return user != null ? convertToDTO(user) : null;
    }

    // Convert MultipartFile to base64
    private String convertToBase64(MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            return Base64.getEncoder().encodeToString(file.getBytes());
        }
        return null; // Handle no file case if needed
    }

    public UserDTO saveUser(UserModel userModel) {
        User user = new User();
        user.setUserID(userModel.getUserID());
        user.setUsername(userModel.getUsername());
        user.setFullname(userModel.getFullname());
        user.setPhone(userModel.getPhone());
        user.setEmail(userModel.getEmail());
        user.setLanguage(userModel.getLanguage());

        if (userModel.getAvatar() != null) {
            try {
                byte[] avatarBytes = userModel.getAvatar().getBytes();
                String base64Avatar = Base64.getEncoder().encodeToString(avatarBytes);
                user.setAvatar(base64Avatar);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
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
        // Lấy tên người dùng hiện tại từ UserLoginInfomation
        String username = userLoginInfomation.getCurrentUsername();

        // Lấy thông tin người dùng từ repository bằng tên người dùng
        User user = userRepository.findByUsername(username);

        // Chuyển đổi sang UserDTO và trả về
        return convertToDTO(user);
    }
}