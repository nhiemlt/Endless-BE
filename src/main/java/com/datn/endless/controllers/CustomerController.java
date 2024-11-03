package com.datn.endless.controllers;

import com.datn.endless.exceptions.AddressNotFoundException;
import com.datn.endless.exceptions.DuplicateResourceException;
import com.datn.endless.exceptions.ResourceNotFoundException;
import com.datn.endless.exceptions.UserNotFoundException;
import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.datn.endless.dtos.CustomerDTO;
import com.datn.endless.dtos.UseraddressDTO;
import com.datn.endless.models.CustomerModel;
import com.datn.endless.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<Page<CustomerDTO>> getCustomers(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        Page<CustomerDTO> customers = customerService.getCustomers(keyword, pageable);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{userId}/addresses")
    public ResponseEntity<?> getAllUserAddresses(@PathVariable String userId) {
        try {
            List<UseraddressDTO> userAddresses = customerService.getAllUserAddresses(userId);
            return ResponseEntity.ok(userAddresses);
        }
        catch (ResourceNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
        catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody CustomerModel customerModel) {
        try {
            CustomerDTO createdCustomer = customerService.createCustomer(customerModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
        } catch (DuplicateResourceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateCustomer(
            @PathVariable String userId,
            @RequestBody CustomerModel customerModel) {
        try {
            CustomerDTO updatedCustomer = customerService.updateCustomer(userId, customerModel);
            return ResponseEntity.ok(updatedCustomer);
        }
        catch (DuplicateResourceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<?> toggleCustomerStatus(@PathVariable String userId) {
        try {
            CustomerDTO updatedCustomer = customerService.toggleCustomerStatus(userId);
            return ResponseEntity.ok(updatedCustomer);
        }
        catch (DuplicateResourceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PostMapping("/{userId}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable String userId) {
        try {
            Map<String, Object> response = customerService.resetPassword(userId);
            return ResponseEntity.ok((String) response.get("message"));
        }
        catch (DuplicateResourceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending email: " + e.getMessage());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
