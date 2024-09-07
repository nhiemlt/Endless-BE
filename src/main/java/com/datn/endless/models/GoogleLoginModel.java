package com.datn.endless.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GoogleLoginModel {
    private String googleId;
    private String email;
    private String fullName;
    private String avatar;
}
