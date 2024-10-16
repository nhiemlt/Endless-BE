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

    @Autowired
    private WardRepository wardRepository;

    private UseraddressDTO convertToDTO(Useraddress userAddress) {
        Ward ward = userAddress.getWardCode();
        District district = ward.getDistrictCode();
        Province province = district.getProvinceCode();
        return UseraddressDTO.builder()
                .addressID(userAddress.getAddressID())
                .userID(userAddress.getUserID().getUserID())
                .provinceCode(province.getCode())
                .districtCode(district.getCode())
                .wardCode(ward.getCode())
                .houseNumberStreet(userAddress.getHouseNumberStreet())
                .build();
    }

    // Chuyển đổi danh sách Useraddress thành danh sách UseraddressDTO
    private List<UseraddressDTO> convertToDTOList(List<Useraddress> userAddresses) {
        return userAddresses.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Lấy tất cả địa chỉ của người dùng theo ID người dùng
    public List<UseraddressDTO> getUserAddressesByUserId(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        List<Useraddress> userAddresses = userAddressRepository.findByUserID(userId);
        return convertToDTOList(userAddresses);
    }

    // Lưu địa chỉ người dùng mới hoặc cập nhật địa chỉ người dùng hiện tại (using UserAddressModel)
    public UseraddressDTO addUserAddress(UserAddressModel userAddressModel) {
        User user = userRepository.findById(userAddressModel.getUserID())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Ward ward = wardRepository.findById(userAddressModel.getWardCode())
                .orElseThrow(() -> new IllegalArgumentException("Ward not found"));

        Useraddress userAddress = new Useraddress();
        userAddress.setAddressID(UUID.randomUUID().toString());
        userAddress.setUserID(user);
        userAddress.setProvinceCode(ward.getDistrictCode().getProvinceCode());
        userAddress.setDistrictCode(ward.getDistrictCode());
        userAddress.setWardCode(ward);
        userAddress.setHouseNumberStreet(userAddressModel.getHouseNumberStreet());

        Useraddress savedUserAddress = userAddressRepository.save(userAddress);
        return convertToDTO(savedUserAddress);
    }

    // Cập nhật địa chỉ người dùng hiện tại
    public UseraddressDTO updateUserAddress(String addressId, UserAddressModel userAddressModel) {
        Useraddress existingAddress = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        User user = userRepository.findById(userAddressModel.getUserID())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Ward ward = wardRepository.findById(userAddressModel.getWardCode())
                .orElseThrow(() -> new IllegalArgumentException("Ward not found"));

        existingAddress.setUserID(user);
        existingAddress.setProvinceCode(ward.getDistrictCode().getProvinceCode());
        existingAddress.setDistrictCode(ward.getDistrictCode());
        existingAddress.setWardCode(ward);
        existingAddress.setHouseNumberStreet(userAddressModel.getHouseNumberStreet());

        Useraddress updatedUserAddress = userAddressRepository.save(existingAddress);
        return convertToDTO(updatedUserAddress);
    }

    // Xóa địa chỉ người dùng theo ID
    public void deleteUserAddress(String addressId) {
        if (!userAddressRepository.existsById(addressId)) {
            throw new IllegalArgumentException("Address not found");
        }
        userAddressRepository.deleteById(addressId);
    }
}
