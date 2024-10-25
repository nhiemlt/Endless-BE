package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InforDTO implements Serializable {
    String userID;
    String username;
    String fullName;
    List <String> roles;
    String phone;
    String email;
    String avatar;
    Boolean active;
}