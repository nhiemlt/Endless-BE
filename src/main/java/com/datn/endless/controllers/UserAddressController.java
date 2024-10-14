package com.datn.endless.controllers;

import com.datn.endless.dtos.UseraddressDTO;
import com.datn.endless.services.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/useraddresses")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    // Lấy tất cả địa chỉ của người dùng
    @GetMapping("/{userId}")
    public ResponseEntity<List<UseraddressDTO>> getAllUserAddresses(@PathVariable("userId") String userId) {
        try {
            List<UseraddressDTO> userAddresses = userAddressService.getUserAddressesByUserId(userId);
            return ResponseEntity.ok(userAddresses);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Thêm địa chỉ mới cho người dùng
    @PostMapping("")
    public ResponseEntity<UseraddressDTO> addUserAddress(@RequestBody UseraddressDTO userAddressDTO) {
        try {
            UseraddressDTO savedAddress = userAddressService.saveUserAddress(userAddressDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAddress);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Cập nhật địa chỉ của người dùng
    @PutMapping("/{userId}/{addressId}")
    public ResponseEntity<UseraddressDTO> updateUserAddress(@PathVariable("userId") String userId, @PathVariable("addressId") String addressId, @RequestBody UseraddressDTO userAddressDTO) {
        try {
            // Đảm bảo rằng addressId không phải là null
            if (addressId == null || addressId.isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }
            userAddressDTO.setAddressID(addressId);
            userAddressDTO.setUserID(userId); // Đặt userId cho địa chỉ
            UseraddressDTO updatedAddress = userAddressService.saveUserAddress(userAddressDTO);
            return ResponseEntity.ok(updatedAddress);
        } catch (IllegalArgumentException e) {
            // Xử lý lỗi khi UUID không hợp lệ
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Xóa địa chỉ của người dùng
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteUserAddress(@PathVariable("addressId") String addressId) {
        try {
            userAddressService.deleteUserAddress(addressId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
