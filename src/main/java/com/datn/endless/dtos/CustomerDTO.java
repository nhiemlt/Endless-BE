package com.datn.endless.dtos;

import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link com.datn.endless.entities.User}
 */
@Value
public class CustomerDTO implements Serializable {
    String userID;
    @Size(max = 255)
    String username;
    @Size(max = 255)
    String fullname;
    @Size(max = 11)
    String phone;
    @Size(max = 255)
    String email;
    String avatar;
    Boolean active;
    Set<UseraddressDTO> userAddressDTOS;
}