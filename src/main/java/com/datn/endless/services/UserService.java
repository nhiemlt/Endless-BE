package com.datn.endless.services;

import com.datn.endless.entities.User;
import com.datn.endless.dtos.UserDTO;
import com.datn.endless.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Chuyển đổi User thành UserDTO
    private UserDTO convertToDTO(User user) {
        return new UserDTO(user); // Sử dụng constructor của UserDTO
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

    // Lưu người dùng mới hoặc cập nhật người dùng hiện tại
    public UserDTO saveUser(UserDTO userDTO) {
        User user = User.builder()
                .userID(userDTO.getUserID())
                .username(userDTO.getUsername())
                .fullname(userDTO.getFullname())
                .phone(userDTO.getPhone())
                .email(userDTO.getEmail())
                .avatar(userDTO.getAvatar())
                .language(userDTO.getLanguage())
                .build();
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    // Xóa người dùng theo ID
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
