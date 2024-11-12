package com.datn.endless.dtos;

import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.util.Set;


@Value
public class EmployeeDTO implements Serializable {
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
    Set<EmployeeRoleDTO> roles;
}