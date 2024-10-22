package com.datn.endless.controllers;

import com.datn.endless.dtos.UseraddressDTO;
import com.datn.endless.models.UserAddressModel;
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
