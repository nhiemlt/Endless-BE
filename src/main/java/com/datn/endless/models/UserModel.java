package com.datn.endless.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {

    private String userID;
    private String username;
    private String fullname;
    private String phone;
    private String email;
    private MultipartFile avatar;
    private String language;

    public void validate() {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        } else if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (phone == null || phone.isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        } else if (!phone.matches("^0\\d{9}$")) {
            throw new IllegalArgumentException("Phone number must be 10 digits and start with 0");
        }
        if (language == null || language.isEmpty()) {
            throw new IllegalArgumentException("Language is required");
        }
    }
}
