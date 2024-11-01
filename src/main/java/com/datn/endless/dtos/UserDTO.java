package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String userID;
    private String username;
    private String fullname;
    private String phone;
    private String email;
    private String avatar;
    private String role;
    private List<UseraddressDTO> addresses;
}
