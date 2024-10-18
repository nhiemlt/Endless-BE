package com.datn.endless.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserAddressModel {

    @NotBlank(message = "User ID cannot be blank")
    private String userID;

    @NotBlank(message = "Ward code cannot be blank")
    private String wardCode;

    @NotBlank(message = "House number and street cannot be blank")
    private String houseNumberStreet;
}
