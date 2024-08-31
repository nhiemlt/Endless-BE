package com.datn.endless.controllers;

import com.datn.endless.entities.Role;
import com.datn.endless.entities.User;
import com.datn.endless.models.CustomUserDetails;
import com.datn.endless.models.LoginModel;
import com.datn.endless.repositories.UserRepository;
import com.datn.endless.services.Constant;
import com.datn.endless.services.Encode;
import com.datn.endless.services.JWT;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginModel loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        boolean remember = loginRequest.isRemember();

        Map<String, Object> response = new HashMap<>();

        // Kiểm tra thông tin đầu vào
        if (username == null || username.isEmpty()) {
            response.put("error", "Username is required.");
            return ResponseEntity.badRequest().body(response);
        }

        if (password == null || password.isEmpty()) {
            response.put("error", "Password is required.");
            return ResponseEntity.badRequest().body(response);
        }

        // Tìm kiếm người dùng dựa trên username
        Optional<User> userOpt = Optional.ofNullable(userRepository.findByUsername(username));
        if (userOpt.isEmpty()) {
            response.put("error", "User not found.");
            return ResponseEntity.badRequest().body(response);
        }

        User user = userOpt.get();

        // Tạo JWT
        String secret = new Constant().getAUTH_KEY();
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        long expirationTimeMillis = remember ? 7 * 24 * 60 * 60 * 1000L : 30 * 60 * 1000L; // 7 ngày nếu "remember", 30 phút nếu không
        JWT jwt = new JWT(secretKey, expirationTimeMillis);

        // Kiểm tra mật khẩu
        if (Encode.checkCode(password, user.getPassword())) {
            String token = jwt.generateToken(user.getUsername());

            // Lấy thông tin vai trò từ cơ sở dữ liệu
            List<Role> roles = userRepository.findRolesByUserId(user.getUserID());
            String roleName = roles.isEmpty() ? "user" : roles.get(0).getRoleName();

            // Tạo đối tượng CustomUserDetails
            CustomUserDetails userDetails = new CustomUserDetails(user);

            // Tạo phản hồi
            response.put("role", roleName); // Sử dụng RoleName
            response.put("permissions", userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).collect(Collectors.toList())); // Thêm thông tin về quyền của người dùng
            response.put("success", true);

            // Trả về phản hồi với token trong header
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + token) // Thêm token vào header
                    .body(response);
        } else {
            response.put("error", "Invalid password.");
            return ResponseEntity.badRequest().body(response);
        }
    }

}
