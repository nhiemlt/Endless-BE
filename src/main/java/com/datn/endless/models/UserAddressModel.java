package com.datn.endless.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserAddressModel {

    private String userID;

    @NotNull(message = "Province name cannot be null")
    private String provinceID;

    @NotNull(message = "District name cannot be null")
    private String districtID;

    @NotNull(message = "Ward street cannot be null")
    private String wardCode;

    @NotNull(message = "Detail address cannot be null")
    private String detailAddress;

    @NotNull(message = "Province name cannot be null")
    private String provinceName;

    @NotNull(message = "District name cannot be null")
    private String districtName;

    @NotNull(message = "Ward name cannot be null")
    private String wardName;

}