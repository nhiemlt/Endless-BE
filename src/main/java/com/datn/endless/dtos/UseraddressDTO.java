package com.datn.endless.dtos;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.datn.endless.entities.Useraddress}
 */
@Value
public class UseraddressDTO implements Serializable {
    String addressID;
    String userID;
    String username;
    Integer provinceID;
    String provinceName;
    Integer districtID;
    String districtName;
    Integer wardCode;
    String wardName;
    String detailAddress;
}