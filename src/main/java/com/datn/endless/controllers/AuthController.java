package com.datn.endless.controllers;

import com.datn.endless.entities.Role;
import com.datn.endless.entities.User;
import com.datn.endless.models.CustomUserDetails;
import com.datn.endless.models.GoogleLoginModel;
import com.datn.endless.models.LoginModel;
import com.datn.endless.models.RegisterModel;
import com.datn.endless.repositories.UserRepository;
import com.datn.endless.services.Constant;
import com.datn.endless.services.Encode;
import com.datn.endless.services.JWT;
import com.datn.endless.services.MailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
    HttpServletRequest request;

    @Autowired
    private MailService mailService;

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

        // Kiểm tra mật khẩu
        if (Encode.checkCode(password, user.getPassword())) {
            // Tạo JWT
            String secret = new Constant().getAUTH_KEY();
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
            long expirationTimeMillis = remember ? 7 * 24 * 60 * 60 * 1000L : 30 * 60 * 1000L; // 7 ngày nếu "remember", 30 phút nếu không
            JWT jwt = new JWT(secretKey, expirationTimeMillis);
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

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterModel registerModel, BindingResult error) {
        if (error.hasErrors()) {
            return ResponseEntity.badRequest().body("Please check all fields.");
        }
        if(userRepository.findByUsername(registerModel.getUsername()) != null){
            return ResponseEntity.badRequest().body("Username is already in use, please choose another one.");
        }

        if (userRepository.findByEmail(registerModel.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email is already in use, please choose another one.");
        }

        if (!registerModel.getPassword().equals(registerModel.getRepeatPassword())) {
            return ResponseEntity.badRequest().body("Passwords do not match.");
        }

        try {
            String secret = new Constant().getAUTH_KEY();
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
            long expirationTimeMillis = 24 * 60 * 60 * 1000L; // Token có hiệu lực trong 24 giờ
            JWT jwt = new JWT(secretKey, expirationTimeMillis);

            // Tạo claims chứa toàn bộ thông tin đăng ký
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", registerModel.getUsername());
            claims.put("email", registerModel.getEmail());
            claims.put("password", registerModel.getPassword()); // Lưu ý: mật khẩu cần được mã hóa trước khi lưu trữ

            String verificationToken = jwt.generateToken(registerModel.getEmail(), claims);
            String verificationLink = "http://localhost:8080/verify?token=" + verificationToken;

            mailService.sendVerificationMail(registerModel.getUsername(), registerModel.getEmail(), verificationLink);

            return ResponseEntity.ok("Registration successful. Please check your email for verification.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        try {
            String secret = new Constant().getAUTH_KEY();
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
            JWT jwt = new JWT(secretKey, 24 * 60 * 60 * 1000L);

            if (!jwt.isTokenValid(token)) {
                return ResponseEntity.badRequest().body("Invalid or expired token.");
            }

            // Lấy thông tin từ token
            Claims claims = jwt.getClaims(token);
            String username = (String) claims.get("username");
            String email = (String) claims.get("email");
            String password = (String) claims.get("password");

            // Mã hóa mật khẩu trước khi lưu trữ
            String encryptedPassword = Encode.hashCode(password);

            // Tạo và lưu user
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(encryptedPassword);
            userRepository.save(user);

            return ResponseEntity.ok("Email successfully verified. You can now log in.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @PostMapping("/login/google")
    public ResponseEntity<Map<String, Object>> googleLogin(@RequestBody GoogleLoginModel googleLoginModel) {
        String googleId = googleLoginModel.getGoogleId();
        String email = googleLoginModel.getEmail();
        String fullName = googleLoginModel.getFullName(); // Giả định rằng Google trả về tên đầy đủ
        String avatar = googleLoginModel.getAvatar(); // Giả định rằng Google trả về URL của avatar

        Map<String, Object> response = new HashMap<>();

        // Tìm kiếm người dùng dựa trên email
        Optional<User> userOpt = Optional.ofNullable(userRepository.findByEmail(email));

        if (userOpt.isEmpty()) {
            // Nếu không tồn tại, tạo người dùng mới
            User newUser = new User();
            newUser.setUsername(googleId); // Đặt username là Google ID (cần kiểm tra độ duy nhất)
            newUser.setEmail(email);
            newUser.setFullname(fullName); // Đặt tên đầy đủ từ Google
            newUser.setAvatar(avatar); // Lưu URL của avatar từ Google
            newUser.setLanguage("vietnam"); // Ngôn ngữ mặc định

            userRepository.save(newUser);
            response.put("message", "User registered successfully.");
        } else {
            User user = userOpt.get();
            response.put("message", "User logged in successfully.");
        }

        // Tạo JWT
        String secret = new Constant().getAUTH_KEY();
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        long expirationTimeMillis = 7 * 24 * 60 * 60 * 1000L; // 7 ngày
        JWT jwt = new JWT(secretKey, expirationTimeMillis);
        String token = jwt.generateToken(googleId); // Sử dụng googleId để tạo token

        response.put("success", true);
        response.put("token", token);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        // Xóa token hoặc session hiện tại
        // Xử lý logic logout tại đây

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logged out successfully.");
        return ResponseEntity.ok(response);
    }


}
