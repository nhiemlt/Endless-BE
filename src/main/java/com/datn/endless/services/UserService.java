package com.datn.endless.services;

import com.datn.endless.dtos.*;
import com.datn.endless.entities.Role;
import com.datn.endless.entities.User;
import com.datn.endless.entities.Useraddress;
import com.datn.endless.exceptions.EmailAlreadyExistsException;
import com.datn.endless.exceptions.PhoneAlreadyExistsException;
import com.datn.endless.exceptions.UserNotFoundException;
import com.datn.endless.models.UserModel;
import com.datn.endless.repositories.UserRepository;
import com.datn.endless.repositories.UseraddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLoginInfomation userLoginInfomation;

    @Autowired
    private UseraddressRepository userAddressRepository;

    @Autowired
    AuthService authService;

    // Chuyển đổi User thành UserDTO
    private UserDTO convertToDTO(User user) {

        List<Useraddress> addresses = userAddressRepository.findByUser(user);
        List<UseraddressDTO> addressDTOs = addresses != null ? addresses.stream()
                .map(address -> new UseraddressDTO(
                        address.getAddressID(),
                        address.getUserID().getUserID(),
                        address.getUserID().getUsername(),
                        address.getProvinceID() != null ? address.getProvinceID() : null,
                        address.getProvinceName() != null ? address.getProvinceName() : null,
                        address.getDistrictID() != null ? address.getDistrictID() : null,
                        address.getDistrictName() != null ? address.getDistrictName() : null,
                        address.getWardCode() != null ? address.getWardCode() : null,
                        address.getWardName() != null ? address.getWardName() : null,
                        address.getDetailAddress() != null ? address.getDetailAddress() : null
                ))
                .collect(Collectors.toList()) : null;
        UserDTO userDTO = new UserDTO();
        userDTO.setUserID(user.getUserID());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        String role = user.getRoles().isEmpty() ? "customer" : "admin";
        userDTO.setRole(role);
        userDTO.setAddresses(addressDTOs);
        userDTO.setPhone(user.getPhone());
        userDTO.setAvatar(user.getAvatar());
        userDTO.setFullname(user.getFullname());
        return userDTO;
    }

    private InforDTO convertToInfor(User user) {
        InforDTO inforDto = new InforDTO();
        inforDto.setUserID(user.getUserID());
        inforDto.setUsername(user.getUsername());
        inforDto.setFullName(user.getFullname());
        inforDto.setEmail(user.getEmail());
        inforDto.setPhone(user.getPhone());
        inforDto.setAvatar(user.getAvatar());
        inforDto.setActive(user.getActive());
        Set<Role> roles = user.getRoles();
        List<String> roleNames = new ArrayList<>();
        for(Role role: roles){
            roleNames.add(role.getRoleName());
        }
        inforDto.setRoles(roleNames);
        return inforDto;
    }

    public Page<InforDTO> getUsersInfor(String keyword, Pageable pageable) {
        Page<User> users;
        users = userRepository.findAllUser(keyword == null ? "" : keyword , pageable);
        return users.map(this::convertToInfor);
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
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO updateCurrentUser(UserModel userModel) {
        // Tìm người dùng theo ID
        User user = userRepository.findById(userModel.getUserID()).orElse(null);
        if (user == null) {
            throw new UserNotFoundException("Người dùng không tồn tại."); // Sử dụng ngoại lệ tùy chỉnh
        }

        // Kiểm tra xem số điện thoại có thay đổi và đã tồn tại không
        if (user.getPhone() !=null && !user.getPhone().equals(userModel.getPhone())) {
            if (userRepository.findByPhone(userModel.getPhone()) != null) {
                throw new PhoneAlreadyExistsException("Số điện thoại đã tồn tại."); // Sử dụng ngoại lệ tùy chỉnh
            }
        }

        // Cập nhật thông tin người dùng
        user.setFullname(userModel.getFullname());
        user.setPhone(userModel.getPhone());

        // Cập nhật chuỗi base64 của avatar từ frontend
        if (userModel.getAvatar() != null && !userModel.getAvatar().isEmpty()) {
            user.setAvatar(userModel.getAvatar());
        }

        // Lưu thay đổi vào cơ sở dữ liệu
        userRepository.save(user);
        if (!user.getPassword().isEmpty() && user.getEmail() !=null && !user.getEmail().equals(userModel.getEmail())) {
            if (userRepository.findByEmail(userModel.getEmail()) != null) {
                throw new EmailAlreadyExistsException("Email đã tồn tại."); // Sử dụng ngoại lệ tùy chỉnh
            }
            else{
                authService.updateEmail(user.getUsername(), userModel.getEmail());
            }
        }
        return convertToDTO(user); // Trả về DTO của người dùng đã cập nhật
    }


    public UserDTO updateUserById(UserModel userModel) {
        User user = userRepository.findById(userModel.getUserID()).orElse(null);
        if (user != null) {
            user.setUsername(userModel.getUsername());
            user.setFullname(userModel.getFullname());
            user.setPhone(userModel.getPhone());
            user.setEmail(userModel.getEmail());

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
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> {
                    UserDTO userDTO = new UserDTO();
                    userDTO.setUserID(user.getUserID());
                    userDTO.setUsername(user.getUsername());
                    userDTO.setFullname(user.getFullname());
                    userDTO.setEmail(user.getEmail());
                    userDTO.setPhone(user.getPhone());
                    userDTO.setAvatar(user.getAvatar());
                    return userDTO;
                })
                .collect(Collectors.toList());
    }

    public UserDTO updateEmailByAdmin(String userId, String newEmail) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Người dùng không tồn tại."));

        // Kiểm tra xem email mới có đã tồn tại trong hệ thống không
        if (userRepository.findByEmail(newEmail) != null) {
            throw new EmailAlreadyExistsException("Email đã tồn tại.");
        }

        // Cập nhật email
        user.setEmail(newEmail);
        userRepository.save(user);

        return convertToDTO(user);
    }




}

