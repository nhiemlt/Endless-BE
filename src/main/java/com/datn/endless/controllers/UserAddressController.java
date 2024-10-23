package com.datn.endless.controllers;

import com.datn.endless.dtos.UseraddressDTO;
import com.datn.endless.exceptions.UserNotFoundException;
import com.datn.endless.models.UserAddressModel;
import com.datn.endless.services.UserAddressService;
import com.datn.endless.services.UserLoginInfomation;
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
    @Autowired
    private UserLoginInfomation userLoginInfomation;

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

    @PostMapping("")
    public ResponseEntity<UseraddressDTO> addUserAddress(@RequestBody UserAddressModel userAddressModel) {
        try {
            UseraddressDTO savedAddress = userAddressService.addUserAddress(userAddressModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAddress);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/current")
    public ResponseEntity<?> getUserAddressesForCurrentUser() {
        try {
            // Lấy username của người dùng hiện tại
            String username = userLoginInfomation.getCurrentUsername();
            // Gọi service để lấy danh sách địa chỉ
            List<UseraddressDTO> userAddresses = userAddressService.getUserAddressesForUser(username);
            return ResponseEntity.ok(userAddresses); // Trả về danh sách địa chỉ
        } catch (UserNotFoundException e) {
            // Xử lý lỗi khi không tìm thấy người dùng
            return ResponseEntity.status(404).body("User not found: " + e.getMessage());
        } catch (Exception e) {
            // Xử lý các lỗi khác (bao gồm lỗi giao dịch JPA)
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/add-current")
    public ResponseEntity<?> addUserAddressCurrent(@RequestBody UserAddressModel userAddressModel) {
        try {
            // Kiểm tra dữ liệu đầu vào cơ bản (nếu cần)
            if (userAddressModel == null || userAddressModel.getDetailAddress() == null) {
                return new ResponseEntity<>("Address details cannot be null", HttpStatus.BAD_REQUEST);
            }

            UseraddressDTO userAddressDTO = userAddressService.addUserAddressForUser(userAddressModel);
            return new ResponseEntity<>(userAddressDTO, HttpStatus.CREATED);

        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Ghi log lỗi chi tiết
            System.err.println("Failed to add new address: " + e.getMessage());
            return new ResponseEntity<>("Could not add new address", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{userId}/{addressId}")
    public ResponseEntity<UseraddressDTO> updateUserAddress(@PathVariable("userId") String userId, @PathVariable("addressId") String addressId, @RequestBody UserAddressModel userAddressModel) {
        try {
            userAddressModel.setUserID(userId);
            UseraddressDTO updatedAddress = userAddressService.updateUserAddress(addressId, userAddressModel);
            return ResponseEntity.ok(updatedAddress);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    // Xóa địa chỉ của người dùng hiện tại
    @DeleteMapping("/current/{addressId}")
    public ResponseEntity<?> deleteAddressForCurrentUser(@PathVariable String addressId) {
        try {
            // Gọi service để xóa địa chỉ
            userAddressService.deleteForUser(addressId);
            return ResponseEntity.ok("Địa chỉ đã được xóa thành công");
        } catch (IllegalArgumentException e) {
            // Trả về lỗi 400 nếu địa chỉ không tồn tại hoặc không thuộc về người dùng hiện tại
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Trả về lỗi 500 và thông báo lỗi khi xóa địa chỉ không thành công
            return ResponseEntity.status(500).body("Đã xảy ra lỗi khi xóa địa chỉ" +e.getMessage());
        }
    }



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
