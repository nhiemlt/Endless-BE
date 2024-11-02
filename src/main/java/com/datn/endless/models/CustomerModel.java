package com.datn.endless.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerModel {
    @Pattern(regexp = "^[^\\s]+$", message = "Tên đăng nhập không được chứa khoảng trắng")
    private String username;

    @NotEmpty(message = "Tên đầy đủ không được để trống")
    private String fullname;

    @NotEmpty(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Số điện thoại phải là một số hợp lệ (10-15 chữ số)")
    private String phone;

    @NotEmpty(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    private String avatar;
}
