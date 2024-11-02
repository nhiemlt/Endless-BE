package com.datn.endless.dtos;

import lombok.Value;

import java.io.Serializable;

@Value
public class UserroleDTO implements Serializable {
    String userRoleId;
    UserDTO user;
    RoleDTO role;
}