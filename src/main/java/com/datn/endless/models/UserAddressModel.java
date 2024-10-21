package com.datn.endless.models;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserAddressModel {

    @NotBlank(message = "User ID cannot be blank")
    private String userID;

    @NotNull(message = "Province name cannot be blank")
    private String provinceName;

    @NotNull(message = "District name cannot be blank")
    private String districtName;

    @NotNull(message = "Ward street cannot be blank")
    private String wardStreet;

    @NotNull(message = "Detail address cannot be blank")
    private String detailAddress;

    private String addressLevel4;
}
