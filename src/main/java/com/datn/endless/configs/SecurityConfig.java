package com.datn.endless.configs;

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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JWTService jwtService;

    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          JWTService jwtService) {
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
                                .requestMatchers("/verify-auth-token").permitAll()  // Sử dụng quyền verify
                                .requestMatchers("/verify-reset-email").permitAll()  // Sử dụng quyền verify
                                .requestMatchers("/login/google").permitAll()  // Sử dụng quyền login/google
                                .requestMatchers("/forgot-password").permitAll()  // Sử dụng quyền forgot-password
                                .requestMatchers("/reset-password").permitAll()  // Sử dụng quyền reset-password
                                .requestMatchers("/token/validate").permitAll()  // Sử dụng quyền token/validate
                                .requestMatchers("/products").permitAll()  // Sử dụng quyền view_all_products
                                .requestMatchers("/products/{id}").permitAll()  // Sử dụng quyền view_product_details
                                .requestMatchers("/api/product-versions/get-user").permitAll()
                                .requestMatchers("/api/product-versions/filter-product-versions").permitAll()
                                .requestMatchers("/api/product-versions/top5ByCategory/**").permitAll()
                                .requestMatchers("/api/product-versions/filter").permitAll()
                                .requestMatchers(HttpMethod.GET,"/api/product-versions/{id}").permitAll()
                                .requestMatchers("/api/product-versions/top-selling").permitAll()
                                .requestMatchers("/api/product-versions/top-selling/all-time").permitAll()
                                .requestMatchers("/api/user-vouchers").permitAll()
                                .requestMatchers("/api/vouchers").authenticated()
                                .requestMatchers("/api/roles/**").permitAll()
                                .requestMatchers("/api/permission/**").permitAll()
                                .requestMatchers("/api/payment/**").permitAll()
                                .requestMatchers("/product-info/**").permitAll()

                                // Các API yêu cầu đăng nhập
                                .requestMatchers("/logout").authenticated()  // Sử dụng quyền logout
                                .requestMatchers("/change-password").authenticated()  // Sử dụng quyền change-password
                                .requestMatchers("/notifications/user/{userId}").authenticated()  // Sử dụng quyền view_all_notifications
                                .requestMatchers("/notifications/user").authenticated()
                                .requestMatchers("/notifications/unread-count").authenticated()// Sử dụng quyền view_all_notifications
                                .requestMatchers("/favorites").authenticated()  // Sử dụng quyền view_favorites
                                .requestMatchers("/carts/**").authenticated()  // Sử dụng quyền view_carts
                                .requestMatchers(HttpMethod.POST, "/orders/create").authenticated()  // Sử dụng quyền orders/create
                                .requestMatchers(HttpMethod.POST, "/ratings/add").authenticated()  // Sử dụng quyền ratings/add
                                .requestMatchers("/api/users/current").authenticated()  // Sử dụng quyền logout
                                .requestMatchers(HttpMethod.POST, "/orders/mark-as-pending").authenticated() //Chờ xác nhận đơn hàng
                                .requestMatchers(HttpMethod.GET, "/orders/user").authenticated() //xem danh sách đơn hàng
                                .requestMatchers(HttpMethod.GET, "/orders/{id}/details").authenticated()
                                .requestMatchers(HttpMethod.GET, "/orders/{id}/status").authenticated()
                                .requestMatchers(HttpMethod.POST, "/orders/mark-as-delivered").authenticated()
                                .requestMatchers("/api/useraddresses/add-current").authenticated()
                                .requestMatchers("/api/roles/current").authenticated()

                                // Các API yêu cầu quyền (dựa theo permission code)
                                .requestMatchers(HttpMethod.POST, "/notifications/send").hasAuthority("send_notifications")
                                .requestMatchers(HttpMethod.POST, "/notifications/send-all").hasAuthority("send_notifications")
                                .requestMatchers(HttpMethod.POST, "/notifications/markAsRead").authenticated()
                                .requestMatchers(HttpMethod.POST, "/notifications/markAllAsRead").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/notifications/delete").hasAuthority("notifications/delete")
                                .requestMatchers(HttpMethod.DELETE, "/notifications/delete/{notificationRecipientID}").authenticated()

                                .requestMatchers(HttpMethod.POST, "/orders").authenticated()
                                .requestMatchers(HttpMethod.POST, "/orders/create-order-online").authenticated()
                                .requestMatchers(HttpMethod.GET, "/orders/{id}").authenticated()
                                .requestMatchers(HttpMethod.GET, "/orders").hasAuthority("view_order")
                                .requestMatchers(HttpMethod.GET, "/orders/{id}/details").hasAuthority("orders/{id}/details")
                                .requestMatchers(HttpMethod.POST, "/orders/mark-as-delivered").permitAll()
                                .requestMatchers(HttpMethod.POST, "/orders/cancel").permitAll()
                                .requestMatchers(HttpMethod.POST, "/orders/cancel-paid").permitAll()
                                .requestMatchers(HttpMethod.POST, "/orders/mark-as-paid").hasAuthority("view_order")
                                .requestMatchers(HttpMethod.POST, "/orders/mark-as-shipping").hasAuthority("mark-as-shipping_order")
                                .requestMatchers(HttpMethod.POST, "/orders/mark-as-confirmed").hasAuthority("mark-as-confirmed_order")

                                .requestMatchers(HttpMethod.GET, "/purchase-orders").hasAuthority("view_all_purchase_orders")
                                .requestMatchers(HttpMethod.GET, "/purchase-orders/{id}").hasAuthority("view_purchase_order")
                                .requestMatchers(HttpMethod.GET, "/purchase-orders").hasAuthority("view_all_purchase_orders")
                                .requestMatchers(HttpMethod.GET, "/purchase-orders/{id}/details").hasAuthority("view_purchase_order_details")

                                .requestMatchers(HttpMethod.POST, "/api/attributes").hasAuthority("add_attribute")
                                .requestMatchers(HttpMethod.GET, "/api/attributes/{id}").hasAuthority("view_attribute")
                                .requestMatchers(HttpMethod.PUT, "/api/attributes/{id}").hasAuthority("update_attribute")
                                .requestMatchers(HttpMethod.DELETE, "/api/attributes/{id}").hasAuthority("delete_attribute")
                                .requestMatchers(HttpMethod.POST, "/api/attributes/{attributeId}/values").hasAuthority("add_attribute_value")
                                .requestMatchers(HttpMethod.PUT, "/api/attributes/values/{valueId}").hasAuthority("update_attribute_value")
                                .requestMatchers(HttpMethod.DELETE, "/api/attributes/values/{valueId}").hasAuthority("delete_attribute_value")

                                .requestMatchers(HttpMethod.POST, "/api/brands").hasAuthority("add_brand")
                                .requestMatchers(HttpMethod.GET, "/api/brands/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/brands").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/api/brands/{id}").hasAuthority("update_brand")
                                .requestMatchers(HttpMethod.DELETE, "/api/brands/{id}").hasAuthority("delete_brand")

                                .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/categories").hasAuthority("add_category")
                                .requestMatchers(HttpMethod.GET, "/api/categories/{id}").hasAuthority("view_category")
                                .requestMatchers(HttpMethod.PUT, "/api/categories/{id}").hasAuthority("update_category")
                                .requestMatchers(HttpMethod.DELETE, "/api/categories/{id}").hasAuthority("delete_category")

                                .requestMatchers(HttpMethod.POST, "/api/products").hasAuthority("add_product")
                                .requestMatchers(HttpMethod.GET, "/api/products/{id}").hasAuthority("view_product")
                                .requestMatchers(HttpMethod.PUT, "/api/products/{id}").hasAuthority("edit_product")
                                .requestMatchers(HttpMethod.DELETE, "/api/products/{id}").hasAuthority("delete_product")

                                .requestMatchers(HttpMethod.POST, "/api/product-versions").hasAuthority("add_product_version")
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

                                .requestMatchers(HttpMethod.POST, "/api/promotion-products").hasAuthority("add_promotion_detail")
                                .requestMatchers(HttpMethod.GET, "/api/promotion-products/{id}").hasAuthority("view_promotion_detail")
                                .requestMatchers(HttpMethod.PUT, "/api/promotion-products/{id}").hasAuthority("update_promotion_detail")
                                .requestMatchers(HttpMethod.DELETE, "/api/promotion-products/{id}").hasAuthority("delete_promotion_detail")

                                .requestMatchers(HttpMethod.GET, "/entry-orders/**").hasAuthority("view_entry")
                                .requestMatchers(HttpMethod.POST, "/entry-orders").hasAuthority("add_new_entry")

//                        .requestMatchers(HttpMethod.POST, "/api/roles").hasAuthority("add_new_user_role")

                                .requestMatchers(HttpMethod.GET, "/api/ratings/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/ratings").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/ratings/{id}").hasAuthority("delete_review")

                                .requestMatchers(HttpMethod.PUT, "/user-roles/{id}").hasAuthority("update_user_role")
                                .requestMatchers(HttpMethod.DELETE, "/user-roles/{id}").hasAuthority("delete_user_role")
                                .requestMatchers(HttpMethod.GET, "/user-roles").hasAuthority("view_role")

                                .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("view_all_users")
                                .requestMatchers(HttpMethod.GET, "/api/users/get-infor").hasAuthority("view_all_users")
                                .requestMatchers(HttpMethod.GET, "/api/users/{id}").hasAuthority("view_all_users")
                                .requestMatchers(HttpMethod.PUT, "/api/users/{id}").hasAuthority("update_user")
                                .requestMatchers(HttpMethod.POST, "/api/users/get-infor").hasAuthority("add_new_user")

                                .requestMatchers(HttpMethod.POST,"/api/employees").hasAuthority("add_employee")
                                .requestMatchers(HttpMethod.GET, "/api/employees/**").hasAuthority("view_employee")
                                .requestMatchers(HttpMethod.PUT, "/api/employees/{userId}").hasAuthority("update_employee")
                                .requestMatchers(HttpMethod.PATCH, "/api/employees/**").hasAuthority("update_employee")

                                .requestMatchers(HttpMethod.POST,"/api/customers/**").hasAuthority("add_customer")
                                .requestMatchers(HttpMethod.GET, "/api/customers/**").hasAuthority("view_customer")
                                .requestMatchers(HttpMethod.PUT, "/api/customers/{userId}").hasAuthority("update_customer")
                                .requestMatchers(HttpMethod.PATCH, "/api/customers/{userId}/status").hasAuthority("update_customer")
                                .requestMatchers(HttpMethod.POST,"/api/customers/**").hasAuthority("delete_customer")
                                .anyRequest().authenticated()
                )

                .csrf(AbstractHttpConfigurer::disable) // Vô hiệu hóa CSRF nếu bạn không sử dụng nó cho API
                .logout(LogoutConfigurer::permitAll) // Cho phép tất cả các yêu cầu logout
                .formLogin(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .addHeaderWriter((request, response) -> {
                            response.setHeader("Cross-Origin-Opener-Policy", "same-origin");
                            response.setHeader("Cross-Origin-Embedder-Policy", "require-corp");
                        })
                )
                .addFilterBefore(new JWTAuthenticationFilter(customUserDetailsService, jwtService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
