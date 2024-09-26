package com.datn.endless.configs;

import com.datn.endless.services.Constant;
import com.datn.endless.services.CustomOAuth2UserService;
import com.datn.endless.services.CustomUserDetailsService;
import com.datn.endless.services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    private final JWTService jwtService;

    @Autowired
    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                          CustomUserDetailsService customUserDetailsService,
                          JWTService jwtService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Các API công khai
                        .requestMatchers("/login").permitAll()  // Sử dụng quyền login
                        .requestMatchers("/register").permitAll()  // Sử dụng quyền register
                        .requestMatchers("/verify").permitAll()  // Sử dụng quyền verify
                        .requestMatchers("/login/google").permitAll()  // Sử dụng quyền login/google
                        .requestMatchers("/forgot-password").permitAll()  // Sử dụng quyền forgot-password
                        .requestMatchers("/reset-password").permitAll()  // Sử dụng quyền reset-password
                        .requestMatchers("/token/validate").permitAll()  // Sử dụng quyền token/validate
                        .requestMatchers("/products").permitAll()  // Sử dụng quyền view_all_products
                        .requestMatchers("/products/{id}").permitAll()  // Sử dụng quyền view_product_details
                        .requestMatchers("/ratings").permitAll()  // Sử dụng quyền view_all_ratings
                        .requestMatchers("/ratings/{id}").permitAll()  // Sử dụng quyền view_rating_details

                        // Các API yêu cầu đăng nhập
                        .requestMatchers("/logout").authenticated()  // Sử dụng quyền logout
                        .requestMatchers("/change-password").authenticated()  // Sử dụng quyền change-password
                        .requestMatchers("/notifications/user/{userId}").authenticated()  // Sử dụng quyền view_all_notifications
                        .requestMatchers("/notifications/user").authenticated()  // Sử dụng quyền view_all_notifications
                        .requestMatchers("/favorites").authenticated()  // Sử dụng quyền view_favorites
                        .requestMatchers("/carts").authenticated()  // Sử dụng quyền view_carts
                        .requestMatchers(HttpMethod.POST, "/orders/create").authenticated()  // Sử dụng quyền orders/create

                        // Các API yêu cầu quyền (dựa theo permission code)
                        .requestMatchers(HttpMethod.POST, "/notifications/send").hasAuthority("send_notifications")
                        .requestMatchers(HttpMethod.POST, "/notifications/markAsRead").hasAuthority("notifications/markAsRead")
                        .requestMatchers(HttpMethod.POST, "/notifications/markAllAsRead").hasAuthority("notifications/markAllAsRead")
                        .requestMatchers(HttpMethod.DELETE, "/notifications/delete").hasAuthority("notifications/delete")

                        .requestMatchers(HttpMethod.POST, "/orders").hasAuthority("orders/create")
                        .requestMatchers(HttpMethod.GET, "/orders/{id}").hasAuthority("view_all_orders")
                        .requestMatchers(HttpMethod.GET, "/orders").hasAuthority("view_all_orders")
                        .requestMatchers(HttpMethod.GET, "/orders/user").hasAuthority("view_user_orders")
                        .requestMatchers(HttpMethod.GET, "/orders/{id}/details").hasAuthority("orders/{id}/details")
                        .requestMatchers(HttpMethod.GET, "/orders/{id}/status").hasAuthority("orders/{id}/status")
                        .requestMatchers(HttpMethod.POST, "/orders/cancel").hasAuthority("orders/cancel")
                        .requestMatchers(HttpMethod.POST, "/orders/mark-as-paid").hasAuthority("orders/mark-as-paid")
                        .requestMatchers(HttpMethod.POST, "/orders/mark-as-shipping").hasAuthority("orders/mark-as-shipping")
                        .requestMatchers(HttpMethod.POST, "/orders/mark-as-delivered").hasAuthority("orders/mark-as-delivered")
                        .requestMatchers(HttpMethod.POST, "/orders/mark-as-confirmed").hasAuthority("orders/mark-as-confirmed")
                        .requestMatchers(HttpMethod.POST, "/orders/mark-as-pending").hasAuthority("orders/mark-as-pending")

                        .requestMatchers(HttpMethod.GET, "/purchase-orders").hasAuthority("view_all_purchase_orders")
                        .requestMatchers(HttpMethod.GET, "/purchase-orders/{id}").hasAuthority("view_purchase_order")
                        .requestMatchers(HttpMethod.GET, "/purchase-orders").hasAuthority("view_all_purchase_orders")
                        .requestMatchers(HttpMethod.GET, "/purchase-orders/{id}/details").hasAuthority("view_purchase_order_details")

                        .requestMatchers(HttpMethod.POST, "/api/attributes").hasAuthority("add_new_attribute")
                        .requestMatchers(HttpMethod.GET, "/api/attributes/{id}").hasAuthority("view_attribute")
                        .requestMatchers(HttpMethod.PUT, "/api/attributes/{id}").hasAuthority("update_attribute")
                        .requestMatchers(HttpMethod.DELETE, "/api/attributes/{id}").hasAuthority("delete_attribute")
                        .requestMatchers(HttpMethod.POST, "/api/attributes/{attributeId}/values").hasAuthority("add_attribute_value")
                        .requestMatchers(HttpMethod.PUT, "/api/attributes/values/{valueId}").hasAuthority("update_attribute_value")
                        .requestMatchers(HttpMethod.DELETE, "/api/attributes/values/{valueId}").hasAuthority("delete_attribute_value")

                        .requestMatchers(HttpMethod.POST, "/api/brands").hasAuthority("add_new_brand")
                        .requestMatchers(HttpMethod.GET, "/api/brands/{id}").hasAuthority("view_brand")
                        .requestMatchers(HttpMethod.PUT, "/api/brands/{id}").hasAuthority("update_brand")
                        .requestMatchers(HttpMethod.DELETE, "/api/brands/{id}").hasAuthority("delete_brand")

                        .requestMatchers(HttpMethod.POST, "/api/categories").hasAuthority("add_new_category")
                        .requestMatchers(HttpMethod.GET, "/api/categories/{id}").hasAuthority("view_category")
                        .requestMatchers(HttpMethod.PUT, "/api/categories/{id}").hasAuthority("update_category")
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/{id}").hasAuthority("delete_category")

                        .requestMatchers(HttpMethod.POST, "/api/products").hasAuthority("add_new_product")
                        .requestMatchers(HttpMethod.GET, "/api/products/{id}").hasAuthority("view_product")
                        .requestMatchers(HttpMethod.PUT, "/api/products/{id}").hasAuthority("update_product")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/{id}").hasAuthority("delete_product")

                        .requestMatchers(HttpMethod.POST, "/api/product-versions").hasAuthority("add_new_product_version")
                        .requestMatchers(HttpMethod.PUT, "/api/product-versions/{id}").hasAuthority("update_product_version")
                        .requestMatchers(HttpMethod.DELETE, "/api/product-versions/{id}").hasAuthority("delete_product_version")

                        .requestMatchers(HttpMethod.POST, "/api/promotions").hasAuthority("add_new_promotion")
                        .requestMatchers(HttpMethod.GET, "/api/promotions/{id}").hasAuthority("view_promotion")
                        .requestMatchers(HttpMethod.PUT, "/api/promotions/{id}").hasAuthority("update_promotion")
                        .requestMatchers(HttpMethod.DELETE, "/api/promotions/{id}").hasAuthority("delete_promotion")
                        .requestMatchers(HttpMethod.GET, "/api/promotions/search").hasAuthority("search_promotions")

                        .requestMatchers(HttpMethod.POST, "/api/promotion-details").hasAuthority("add_new_promotion_detail")
                        .requestMatchers(HttpMethod.GET, "/api/promotion-details/{id}").hasAuthority("view_promotion_detail")
                        .requestMatchers(HttpMethod.PUT, "/api/promotion-details/{id}").hasAuthority("update_promotion_detail")
                        .requestMatchers(HttpMethod.DELETE, "/api/promotion-details/{id}").hasAuthority("delete_promotion_detail")

                        .requestMatchers(HttpMethod.POST, "/api/promotion-products").hasAuthority("add_new_promotion_product")
                        .requestMatchers(HttpMethod.GET, "/api/promotion-products/{id}").hasAuthority("view_promotion_product")
                        .requestMatchers(HttpMethod.PUT, "/api/promotion-products/{id}").hasAuthority("update_promotion_product")
                        .requestMatchers(HttpMethod.DELETE, "/api/promotion-products/{id}").hasAuthority("delete_promotion_product")

                        .requestMatchers(HttpMethod.POST, "/user-roles").hasAuthority("add_new_user_role")
                        .requestMatchers(HttpMethod.GET, "/user-roles/{id}").hasAuthority("view_user_role")
                        .requestMatchers(HttpMethod.PUT, "/user-roles/{id}").hasAuthority("update_user_role")
                        .requestMatchers(HttpMethod.DELETE, "/user-roles/{id}").hasAuthority("delete_user_role")
                        .anyRequest().authenticated()
                )

                .csrf(csrf -> csrf.disable()) // Vô hiệu hóa CSRF nếu bạn không sử dụng nó cho API
                .logout(logout -> logout.permitAll()) // Cho phép tất cả các yêu cầu logout
                .formLogin(form -> form.disable()) // Tắt form login nếu không sử dụng
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                )
                .addFilterBefore(new JWTAuthenticationFilter(customUserDetailsService, jwtService),
                        UsernamePasswordAuthenticationFilter.class); // Thêm JWTAuthenticationFilter vào chuỗi bảo mật

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
