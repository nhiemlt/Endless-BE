package com.datn.endless.configs;

import com.datn.endless.services.Constant;
import com.datn.endless.services.CustomOAuth2UserService;
import com.datn.endless.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomUserDetailsService customUserDetailsService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/products/view").hasAuthority("Xem sản phẩm")
//                        .requestMatchers("/products/view-details").hasAuthority("Xem chi tiết sản phẩm")
//                        .requestMatchers("/products/add").hasAuthority("Thêm sản phẩm")
//                        .requestMatchers("/products/edit").hasAuthority("Sửa sản phẩm")
//                        .requestMatchers("/products/delete").hasAuthority("Xóa sản phẩm")
//                        .requestMatchers("/accounts/add").hasAuthority("Thêm tài khoản")
//                        .requestMatchers("/accounts/edit").hasAuthority("Sửa tài khoản")
//                        .requestMatchers("/accounts/delete").hasAuthority("Xóa tài khoản")
//                        .requestMatchers("/promotions/add").hasAuthority("Thêm khuyến mãi")
//                        .requestMatchers("/promotions/edit").hasAuthority("Sửa khuyến mãi")
//                        .requestMatchers("/promotions/delete").hasAuthority("Xóa khuyến mãi")
//                        .requestMatchers("/brands/add").hasAuthority("Thêm thương hiệu")
//                        .requestMatchers("/brands/edit").hasAuthority("Sửa thương hiệu")
//                        .requestMatchers("/brands/delete").hasAuthority("Xóa thương hiệu")
//                        .requestMatchers("/categories/add").hasAuthority("Thêm danh mục")
//                        .requestMatchers("/categories/edit").hasAuthority("Sửa danh mục")
//                        .requestMatchers("/categories/delete").hasAuthority("Xóa danh mục")
//                        .requestMatchers("/roles/add").hasAuthority("Thêm vai trò")
//                        .requestMatchers("/roles/edit").hasAuthority("Sửa vai trò")
//                        .requestMatchers("/roles.delete").hasAuthority("Xóa vai trò")
//                        .requestMatchers("/reports/export").hasAuthority("Xuất báo cáo")
//                        .requestMatchers("/login").permitAll() // Cho phép truy cập vào /login
//                        .requestMatchers("/register").permitAll()
//                        .requestMatchers("/verify").permitAll()
//                        .anyRequest().authenticated()
                        .requestMatchers("/**").permitAll()
                )
                .csrf(csrf -> csrf.disable()) // Vô hiệu hóa CSRF nếu bạn không sử dụng nó cho API
                .logout(logout -> logout.permitAll()) // Cho phép tất cả các yêu cầu logout
                .formLogin(form -> form.disable()) // Tắt form login nếu không sử dụng
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                )
                .addFilterBefore(new JWTAuthenticationFilter(customUserDetailsService),
                        UsernamePasswordAuthenticationFilter.class); // Thêm JWTAuthenticationFilter vào chuỗi bảo mật

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}