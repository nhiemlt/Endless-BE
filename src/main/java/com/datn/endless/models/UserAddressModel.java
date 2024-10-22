package com.datn.endless.models;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserAddressModel {
    private String userID;

    @NotNull(message = "Province name cannot be blank")
    private String provinceID;

    @NotNull(message = "District name cannot be blank")
    private String districtID;

    @NotNull(message = "Ward street cannot be blank")
    private String wardCode;

    @NotNull(message = "Detail address cannot be blank")
    private String detailAddress;
}