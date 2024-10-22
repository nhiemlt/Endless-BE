package com.datn.endless.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CurrentUserAddressModel {
    @NotNull(message = "Ward code cannot be empty")
    private String wardCode;

    @NotNull(message = "House number cannot be null")
    private String houseNumberStreet;
}
