package com.datn.endless.services;

import com.datn.endless.dtos.*;
import com.datn.endless.entities.User;
import com.datn.endless.entities.Useraddress;
import com.datn.endless.exceptions.DuplicateResourceException;
import com.datn.endless.exceptions.ResourceNotFoundException;
import com.datn.endless.exceptions.UserNotFoundException;
import com.datn.endless.models.CustomerModel;
import com.datn.endless.repositories.UserRepository;
import com.datn.endless.repositories.UseraddressRepository;
import com.datn.endless.utils.RandomUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    @Autowired
    UserRepository customerRepository;

    @Autowired
    UserLoginInfomation userLoginInfomation;

    @Autowired
    UseraddressRepository userAddressRepository;

    @Autowired
    MailService mailService;

    public CustomerDTO getCustomerById(String userId) {
        User user = customerRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy khách hàng với ID: " + userId));
        return convertToCustomerDTO(user);
    }

    public Page<CustomerDTO> getCustomers(String keyword, Pageable pageable) {
        Page<User> users = customerRepository.searchCustomers(keyword, pageable);
        return users.map(this::convertToCustomerDTO);
    }


    public List<UseraddressDTO> getAllUserAddresses(String userId) {
        List<Useraddress> userAddresses = userAddressRepository.findAllByUserID(userId);

        if (userAddresses.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy địa chỉ nào cho user với ID: " + userId);
        }

        return userAddresses.stream()
                .map(this::convertToUserAddressDTO)
                .collect(Collectors.toList());
    }

    public CustomerDTO createCustomer(CustomerModel customerModel) {
        if (customerModel.getUsername().isEmpty()) {
            throw new DuplicateResourceException("Username không được bỏ trống");
        }
        if (customerRepository.existsByUsername(customerModel.getUsername())) {
            throw new DuplicateResourceException("Username đã tồn tại");
        }
        if (customerRepository.existsByPhone(customerModel.getPhone())) {
            throw new DuplicateResourceException("Số điện thoại đã tồn tại");
        }
        if (customerRepository.existsByEmail(customerModel.getEmail())) {
            throw new DuplicateResourceException("Email đã tồn tại");
        }

        User user = new User();
        setCreateDetails(user, customerModel);
        user.setUserID(UUID.randomUUID().toString());
        user.setActive(true);
        user.setPassword(createRandomPassword(user.getUsername(), user.getEmail()));

        return convertToCustomerDTO(customerRepository.save(user));
    }

    public CustomerDTO updateCustomer(String userId, CustomerModel customerModel) {
        User user = customerRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy khách hàng"));
        if(!user.getRoles().isEmpty()){
            throw new DuplicateResourceException("Đây là nhân viên chứ không phải khách hàng");
        }
        if (!customerModel.getPhone().equals(user.getPhone()) && customerRepository.existsByPhone(customerModel.getPhone())) {
            throw new DuplicateResourceException("Số điện thoại đã tồn tại");
        }
        if (!customerModel.getEmail().equals(user.getEmail()) && customerRepository.existsByEmail(customerModel.getEmail())) {
            throw new DuplicateResourceException("Email đã tồn tại");
        }
        setUpdateDetails(user, customerModel);

        return convertToCustomerDTO(customerRepository.save(user));
    }

    public CustomerDTO toggleCustomerStatus(String userId) {
        User user = customerRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy khách hàng"));
        if(!user.getRoles().isEmpty()){
            throw new DuplicateResourceException("Đây là nhân viên chứ không phải khách hàng");
        }
        boolean active = user.getActive();
        user.setActive(!active);
        return convertToCustomerDTO(customerRepository.save(user));
    }

    public UseraddressDTO addUserAddress(String userId, UseraddressDTO userAddressDTO) {
        User user = customerRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy khách hàng"));
        if(!user.getRoles().isEmpty()){
            throw new DuplicateResourceException("Đây là nhân viên chứ không phải khách hàng");
        }

        Useraddress userAddress = new Useraddress();
        userAddress.setUserID(user);
        userAddress.setProvinceID(userAddressDTO.getProvinceID());
        userAddress.setProvinceName(userAddressDTO.getProvinceName());
        userAddress.setDistrictID(userAddressDTO.getDistrictID());
        userAddress.setDistrictName(userAddressDTO.getDistrictName());
        userAddress.setWardCode(userAddressDTO.getWardCode());
        userAddress.setWardName(userAddressDTO.getWardName());
        userAddress.setDetailAddress(userAddressDTO.getDetailAddress());

        userAddress = userAddressRepository.save(userAddress);
        return convertToUserAddressDTO(userAddress);
    }

    public void deleteUserAddress(String addressId) {
        Useraddress userAddress = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ với ID: " + addressId));

        userAddressRepository.delete(userAddress);
    }

    public void deleteCustomer(String userId) {
        User user = customerRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy khách hàng với ID: " + userId));

        if (!user.getRoles().isEmpty()) {
            throw new DuplicateResourceException("Đây là nhân viên chứ không phải khách hàng");
        }

        customerRepository.delete(user);
    }

    public String createRandomPassword(String username, String email) {
        String newPassword = RandomUtil.generateComplexRandomString();
        String passwordEndcode = Encode.hashCode(newPassword);
        try {
            mailService.sendTemporaryPasswordMail(username, email, newPassword);
        } catch (MessagingException e) {
            throw new RuntimeException("Đã sảy ra lỗi khi tạo mật khẩu");
        }

        return passwordEndcode;
    }

    public Map<String, Object> resetPassword(String userId) throws MessagingException {
        User user = customerRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy khách hàng"));
        if(!user.getRoles().isEmpty()){
            throw new DuplicateResourceException("Đây là nhân viên chứ không phải khách hàng");
        }

        String newPassword = createRandomPassword(user.getUsername(), user.getEmail());
        user.setPassword(newPassword);
        customerRepository.save(user);

        mailService.sendTemporaryPasswordMail(user.getUsername(), user.getEmail(), newPassword);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Mật khẩu đã được đặt lại thành công và đã được gửi đến email.");
        return response;
    }

    private void setCreateDetails(User user, CustomerModel model) {
        user.setUsername(model.getUsername());
        user.setFullname(model.getFullname());
        user.setPhone(model.getPhone());
        user.setEmail(model.getEmail());
        user.setAvatar(model.getAvatar());
        user.setCreateDate(LocalDateTime.now());
        user.setActive(true);
        user.setForgetPassword(true);
    }

    private void setUpdateDetails(User user, CustomerModel model) {
        user.setFullname(model.getFullname());
        user.setPhone(model.getPhone());
        user.setEmail(model.getEmail());
        user.setAvatar(model.getAvatar());
    }

    private CustomerDTO convertToCustomerDTO(User user) {
        // Lấy danh sách địa chỉ dựa trên userID
        Set<UseraddressDTO> addressDTOs = userAddressRepository.findAllByUserID(user.getUserID()).stream()
                .map(this::convertToUserAddressDTO)
                .collect(Collectors.toSet());

        return new CustomerDTO(
                user.getUserID(),
                user.getUsername(),
                user.getFullname(),
                user.getPhone(),
                user.getEmail(),
                user.getAvatar(),
                user.getActive(),
                addressDTOs
        );
    }

    private UseraddressDTO convertToUserAddressDTO(Useraddress userAddress) {
        return new UseraddressDTO(
                userAddress.getAddressID(),
                userAddress.getUserID().getUserID(),
                userAddress.getUserID().getUsername(),
                userAddress.getProvinceID(),
                userAddress.getProvinceName(),
                userAddress.getDistrictID(),
                userAddress.getDistrictName(),
                userAddress.getWardCode(),
                userAddress.getWardName(),
                userAddress.getDetailAddress()
        );
    }

}
