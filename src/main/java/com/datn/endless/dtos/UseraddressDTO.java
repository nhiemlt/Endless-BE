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
    String provinceID;
    String districtName;
    String wardSCode;
    String detailAddress;
}