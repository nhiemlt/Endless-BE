package com.datn.endless.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class UserLoginInfomation {
    // Lấy thông tin người dùng hiện tại
    public UserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        }

        throw new RuntimeException("No authenticated user found");
    }

    // Lấy tên người dùng hiện tại
    public String getCurrentUsername() {
        UserDetails userDetails = getCurrentUser();
        return userDetails.getUsername();
    }

    // Lấy các quyền (authorities) của người dùng hiện tại
    public Collection<? extends GrantedAuthority> getCurrentUserAuthorities() {
        UserDetails userDetails = getCurrentUser();
        return userDetails.getAuthorities();
    }
}
