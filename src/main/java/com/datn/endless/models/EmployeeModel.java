package com.datn.endless.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeModel {
    private String userID;

    @NotEmpty(message = "Username cannot be empty")
    private String username;

    @NotEmpty(message = "Fullname cannot be empty")
    private String fullname;

    @NotEmpty(message = "Phone cannot be empty")
    private String phone;

    @NotEmpty(message = "Email cannot be empty")
    private String email;
    private String avatar;
    private List<String> roleIds; // Danh sách role ID của nhân viên
}
