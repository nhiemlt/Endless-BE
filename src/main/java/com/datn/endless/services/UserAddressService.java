package com.datn.endless.services;

import com.datn.endless.dtos.UseraddressDTO;
import com.datn.endless.entities.*;
import com.datn.endless.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

    // Chuyển đổi Useraddress thành UseraddressDTO
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
            System.out.println("User not found \n\n\n\n");
        }
        List<Useraddress> userAddresses = userAddressRepository.findByUserID(userId);
        for (Useraddress userAddress : userAddresses) {
            System.out.println("UserAddress : " + userAddress.getWardCode().getCode());
        }
        System.out.println("\n\n\n\n");
        return convertToDTOList(userAddresses);
    }

    // Lưu địa chỉ người dùng mới hoặc cập nhật địa chỉ người dùng hiện tại
    public UseraddressDTO saveUserAddress(UseraddressDTO userAddressDTO) {
        User user = userRepository.findById(userAddressDTO.getUserID()).orElse(null);
        Ward ward = wardRepository.findById(userAddressDTO.getWardCode()).orElse(null);
        Useraddress userAddress = new Useraddress();
            userAddress.setAddressID(UUID.randomUUID().toString());
            userAddress.setUserID(user);
            userAddress.setProvinceCode(ward.getDistrictCode().getProvinceCode());
            userAddress.setDistrictCode(ward.getDistrictCode());
            userAddress.setWardCode(ward);
            userAddress.setHouseNumberStreet(userAddressDTO.getHouseNumberStreet());
        Useraddress savedUserAddress = userAddressRepository.save(userAddress);
        return convertToDTO(savedUserAddress);
    }

    // Xóa địa chỉ người dùng theo ID
    public void deleteUserAddress(String addressId) {
        userAddressRepository.deleteById(addressId);
    }
}
