package com.datn.endless.services;

import com.datn.endless.dtos.UserDTO;
import com.datn.endless.entities.User;
import com.datn.endless.models.UserModel;
import com.datn.endless.repositories.UserRepository;
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

    // Chuyển đổi User thành UserDTO
    private UserDTO convertToDTO(User user) {
        return new UserDTO(user); // Sử dụng constructor của UserDTO
    }

    // Chuyển đổi danh sách User thành danh sách UserDTO
    private List<UserDTO> convertToDTOList(List<User> users) {
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public UserDTO getCurrentUser() {
        // Lấy tên người dùng hiện tại từ UserLoginInfomation
        String username = userLoginInfomation.getCurrentUsername();

        // Lấy thông tin người dùng từ repository bằng tên người dùng
        User user = userRepository.findByUsername(username);

        // Chuyển đổi sang UserDTO và trả về
        return convertToDTO(user);
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

    public UserDTO saveUser(UserModel userModel) {
        User user = new User();
        user.setUserID(userModel.getUserID());
        user.setUsername(userModel.getUsername());
        user.setFullname(userModel.getFullname());
        user.setPhone(userModel.getPhone());
        user.setEmail(userModel.getEmail());
        user.setAvatar(userModel.getAvatar());
        user.setLanguage(userModel.getLanguage());

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
}
