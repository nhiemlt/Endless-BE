package com.datn.endless.models;

import lombok.Data;

@Data
public class UpdateProfileModel {
    private String username;
    private String email;
    private String fullName;
    private String avatar;
}
