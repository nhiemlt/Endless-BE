package com.datn.endless.models;

import com.datn.endless.entities.Permission;
import com.datn.endless.entities.Role;
import com.datn.endless.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CustomUserDetails implements UserDetails {
    private User user;
    private Set<GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.user = user;
        this.authorities = new HashSet<>();

        // Chuyển đổi Roles và Permissions thành GrantedAuthority
        for (Role role : user.getRoles()) {
            // Thêm Role như một GrantedAuthority
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName().toUpperCase()));

            // Thêm từng Permission của Role như một GrantedAuthority
            for (Permission permission : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission.getPermissionName()));
            }
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // Các phương thức kiểm tra trạng thái tài khoản
    @Override
    public boolean isAccountNonExpired() {
        return true; // Bạn có thể thay đổi logic này nếu cần
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Bạn có thể thay đổi logic này nếu cần
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Bạn có thể thay đổi logic này nếu cần
    }

    @Override
    public boolean isEnabled() {
        return true; // Bạn có thể thay đổi logic này nếu cần
    }
}
