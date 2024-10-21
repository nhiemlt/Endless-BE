package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserDTO {
    private String userID;
    private String username;
    private String fullname;
    private String phone;
    private String email;
    private String avatar;
    private String language;
    private List<RoleDTO> roles;
    private List<UseraddressDto> addresses;
}
