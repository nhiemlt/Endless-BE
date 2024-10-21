package com.datn.endless.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.datn.endless.entities.Useraddress}
 */
@Value
public class UseraddressDto implements Serializable {
    String addressID;
    String userID;
    String username;
    String provinceName;
    String districtName;
    String wardStreet;
    String detailAddress;
    String addressLevel4;
}