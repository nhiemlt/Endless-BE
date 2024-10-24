package com.datn.endless.services;

import com.datn.endless.dtos.UseraddressDTO;
import com.datn.endless.entities.*;
import com.datn.endless.exceptions.UserNotFoundException;
import com.datn.endless.models.UserAddressModel;
import com.datn.endless.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserAddressService {

    @Autowired
    private UseraddressRepository userAddressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLoginInfomation userLoginInfomation;

    // Chuyển đổi Useraddress thành UseraddressDTO
    private UseraddressDTO convertToDTO(Useraddress address) {
        return new UseraddressDTO(
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
        );
    }

    private List<UseraddressDTO> convertToDTOList(List<Useraddress> userAddresses) {
        return userAddresses.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<UseraddressDTO> getUserAddressesByUserId(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        List<Useraddress> userAddresses = userAddressRepository.findByUser(user);
        return convertToDTOList(userAddresses);
    }

    // Lưu địa chỉ người dùng mới hoặc cập nhật địa chỉ người dùng hiện tại
    public UseraddressDTO addUserAddress(UserAddressModel userAddressModel) {
        User user = userRepository.findById(userAddressModel.getUserID())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Useraddress userAddress = new Useraddress();
        userAddress.setAddressID(UUID.randomUUID().toString());
        userAddress.setUserID(user);
        userAddress.setProvinceID(userAddressModel.getProvinceID());
        userAddress.setDistrictID(userAddressModel.getDistrictID());
        userAddress.setWardCode(userAddressModel.getWardCode());
        userAddress.setDetailAddress(userAddressModel.getDetailAddress());

        Useraddress savedUserAddress = userAddressRepository.save(userAddress);
        return convertToDTO(savedUserAddress);
    }

    public List<UseraddressDTO> getUserAddressesForUser(String username) {
        // Tìm kiếm user trong cơ sở dữ liệu theo username
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        // Lấy danh sách địa chỉ của user
        List<Useraddress> userAddresses = userAddressRepository.findByUser(user);
        // Chuyển đổi sang DTO và trả về
        return convertToDTOList(userAddresses);
    }

    @Transactional // Đảm bảo phương thức này được thực hiện trong một transaction
    public UseraddressDTO addUserAddressForUser(UserAddressModel userAddressModel) {
        try {
            // Lấy thông tin người dùng hiện tại
            String currentUsername = userLoginInfomation.getCurrentUsername();
            User user = userRepository.findByUsername(currentUsername);
            if (user == null) {
                throw new UserNotFoundException("User not found");
            }
            // Tạo đối tượng Useraddress mới để lưu địa chỉ
            Useraddress userAddress = new Useraddress();
            userAddress.setAddressID(UUID.randomUUID().toString());
            userAddress.setUserID(user); // Gán người dùng hiện tại
            userAddress.setProvinceID(userAddressModel.getProvinceID());
            userAddress.setProvinceName(userAddressModel.getProvinceName());
            userAddress.setDistrictID(userAddressModel.getDistrictID());
            userAddress.setDistrictName(userAddressModel.getDistrictName());
            userAddress.setWardCode(userAddressModel.getWardCode());
            userAddress.setWardName(userAddressModel.getWardName());
            userAddress.setDetailAddress(userAddressModel.getDetailAddress());
            // Lưu địa chỉ người dùng vào cơ sở dữ liệu
            Useraddress savedUserAddress = userAddressRepository.save(userAddress);
            // Chuyển đổi đối tượng Useraddress thành UseraddressDTO để trả về
            return convertToDTO(savedUserAddress);
        } catch (Exception e) {
            // Ghi log để theo dõi chi tiết lỗi
            throw new RuntimeException("Could not save user address. Please try again later.");
        }
    }


    // Cập nhật địa chỉ người dùng hiện tại
    public UseraddressDTO updateUserAddress(String addressId, UserAddressModel userAddressModel) {
        Useraddress userAddress = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        User user = userRepository.findById(userAddressModel.getUserID())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        userAddress.setProvinceID(userAddressModel.getProvinceID());
        userAddress.setDistrictID(userAddressModel.getDistrictID());
        userAddress.setWardCode(userAddressModel.getWardCode());
        userAddress.setDetailAddress(userAddressModel.getDetailAddress());

        Useraddress updatedUserAddress = userAddressRepository.save(userAddress);
        return convertToDTO(updatedUserAddress);
    }

    public void deleteUserAddress(String addressId) {
        if (!userAddressRepository.existsById(addressId)) {
            throw new IllegalArgumentException("Address not found");
        }
        userAddressRepository.deleteById(addressId);
    }

    // Xóa địa chỉ của người dùng hiện tại
    public void deleteForUser(String addressId) {
        // Lấy tên người dùng hiện tại
        String currentUsername = userLoginInfomation.getCurrentUsername();

        // Tìm kiếm địa chỉ dựa trên addressId và username
        Useraddress userAddress = userAddressRepository.findByIdAndUsername(addressId, currentUsername);

        // Kiểm tra xem địa chỉ có tồn tại không
        if (userAddress == null) {
            throw new IllegalArgumentException("Địa chỉ không tồn tại hoặc không thuộc về người dùng hiện tại");
        }

        // Xóa địa chỉ
        userAddressRepository.deleteById(addressId);
    }

}