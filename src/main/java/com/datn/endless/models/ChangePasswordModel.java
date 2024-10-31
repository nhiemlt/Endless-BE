package com.datn.endless.models;

import lombok.Data;

@Data
public class ChangePasswordModel {
    private String oldPassword;
    private String newPassword;
}
