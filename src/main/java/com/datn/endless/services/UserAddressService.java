package com.datn.endless.services;

import com.datn.endless.dtos.UseraddressDTO;
import com.datn.endless.entities.*;
import com.datn.endless.models.UserAddressModel;
import com.datn.endless.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserAddressService {

    @Autowired
    private UseraddressRepository userAddressRepository;

    @Autowired
    private UserRepository userRepository;

    // Chuyển đổi Useraddress thành UseraddressDTO
    private UseraddressDTO convertToDTO(Useraddress address) {
        return new UseraddressDTO(
                address.getAddressID(),
                address.getUserID().getUserID(),
                address.getUserID().getUsername(),
                address.getProvinceID() != null ? address.getProvinceID() : null,
                address.getDistrictID() != null ? address.getDistrictID() : null,
                address.getWardCode() != null ? address.getWardCode() : null,
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
}