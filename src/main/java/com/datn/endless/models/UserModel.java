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
}