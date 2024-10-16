package com.datn.endless.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterModel {
    @NotBlank(message = "Username cannot be blank")
    @Pattern(
            regexp = "^[A-Za-z\\d]{5,}$",
            message = "Username must be at least 5 characters long and can only contain letters and numbers"
    )
    private String username;

    @NotEmpty(message = "Please enter email here!")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters long, contain at least one lowercase letter, one uppercase letter, one digit, and one special character"
    )
    private String password;

}