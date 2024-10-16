package com.datn.endless.services;

import com.datn.endless.entities.User;
import com.datn.endless.models.CustomUserDetails;
import com.datn.endless.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository; // Repository để truy xuất Users

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm người dùng theo username
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Người dùng không tồn tại với username: " + username);
        }
        return new CustomUserDetails(user);
    }
}
