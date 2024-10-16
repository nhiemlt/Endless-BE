package com.datn.endless.services;

import com.datn.endless.entities.Role;
import com.datn.endless.entities.User;
import com.datn.endless.models.*;
import com.datn.endless.repositories.UserRepository;
import com.datn.endless.utils.RandomUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    private JWT jwt;

    public ResponseEntity<Map<String, Object>> login(LoginModel loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        boolean remember = loginRequest.isRemember();

        Map<String, Object> response = new HashMap<>();

        // Kiểm tra thông tin đăng nhập
        if (username == null || username.isEmpty()) {
            response.put("error", "Username is required.");
            return ResponseEntity.badRequest().body(response);
        }

        if (password == null || password.isEmpty()) {
            response.put("error", "Password is required.");
            return ResponseEntity.badRequest().body(response);
        }

        // Kiểm tra user trong database
        Optional<User> userOpt = Optional.ofNullable(userRepository.findByUsername(username));
        if (userOpt.isEmpty()) {
            response.put("error", "User not found.");
            return ResponseEntity.badRequest().body(response);
        }

        User user = userOpt.get();

        if (!user.getActive()) {
            response.put("error", "User is blocked.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // Kiểm tra mật khẩu
        if (Encode.checkCode(password, user.getPassword())) {
            // Tạo JWT token
            String secret = new Constant().getAUTH_KEY();
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
            long expirationTimeMillis = remember ? 7 * 24 * 60 * 60 * 1000L : 30 * 60 * 1000L;
            JWT jwt = new JWT(secretKey, expirationTimeMillis);
            String token = jwt.generateToken(user.getUsername());

            List<Role> roles = userRepository.findRolesByUserId(user.getUserID());
            String roleName = roles.isEmpty() ? "user" : roles.get(0).getRoleName();

            CustomUserDetails userDetails = new CustomUserDetails(user);

            // Thêm dữ liệu vào body phản hồi
            response.put("role", roleName);
            response.put("permissions", userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
            response.put("token", token); // Thêm token vào body
            response.put("success", true);

            // Trả về phản hồi với body chứa token
            return ResponseEntity.ok().body(response);
        } else {
            response.put("error", "Invalid password.");
            return ResponseEntity.badRequest().body(response);
        }
    }


    public ResponseEntity<Map<String, Object>> register(RegisterModel registerModel) {
        Map<String, Object> response = new HashMap<>();

        if (userRepository.findByUsername(registerModel.getUsername()) != null) {
            response.put("error", "Username is already in use, please choose another one.");
            return ResponseEntity.badRequest().body(response);
        }

        if (userRepository.findByEmail(registerModel.getEmail()) != null) {
            response.put("error", "Email is already in use, please choose another one.");
            return ResponseEntity.badRequest().body(response);
        }

        String encryptedPassword = Encode.hashCode(registerModel.getPassword());

        try {
            String secret = new Constant().getAUTH_KEY();
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
            long expirationTimeMillis = 24 * 60 * 60 * 1000L;
            JWT jwt = new JWT(secretKey, expirationTimeMillis);

            Map<String, Object> claims = new HashMap<>();
            claims.put("username", registerModel.getUsername());
            claims.put("email", registerModel.getEmail());
            claims.put("password", registerModel.getPassword());

            String verificationToken = jwt.generateToken(registerModel.getEmail(), claims);
            String verificationLink = "http://localhost:8080/verify?token=" + verificationToken;

            mailService.sendVerificationMail(registerModel.getUsername(), registerModel.getEmail(), verificationLink);

            User user = new User();
            user.setUsername(registerModel.getUsername());
            user.setEmail(registerModel.getEmail());
            user.setPassword(encryptedPassword);
            user.setActive(false);
            user.setForgetPassword(false);
            userRepository.save(user);

            response.put("success", true);
            response.put("message", "Registration successful. Please check your email for verification.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> verifyEmail(String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            String secret = new Constant().getAUTH_KEY();
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
            JWT jwt = new JWT(secretKey, 24 * 60 * 60 * 1000L);

            if (!jwt.isTokenValid(token)) {
                response.put("error", "Invalid or expired token.");
                return ResponseEntity.badRequest().body(response);
            }

            Claims claims = jwt.getClaims(token);
            String username = (String) claims.get("username");
            String email = (String) claims.get("email");
            String password = (String) claims.get("password");

            Optional<User> userOpt = Optional.ofNullable(userRepository.findByUsername(username));
            if (userOpt.isEmpty()) {
                response.put("error", "User not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            User user = userOpt.get();
            user.setActive(true);
            user.setPassword(Encode.hashCode(password));
            userRepository.save(user);

            response.put("success", true);
            response.put("message", "Email successfully verified. You can now log in.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> verifyAuthToken(String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            String secret = new Constant().getAUTH_KEY(); // Có thể inject vào constructor
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
            JWT jwt = new JWT(secretKey, 24 * 60 * 60 * 1000L); // Thời gian có thể cấu hình

            if (!jwt.isTokenValid(token)) {
                response.put("error", "Invalid or expired token.");
                return ResponseEntity.badRequest().body(response);
            }

            Claims claims = jwt.getClaims(token);
            String username = claims.getSubject(); // Lấy username từ subject

            // Kiểm tra người dùng
            Optional<User> userOpt = Optional.ofNullable(userRepository.findByUsername(username));
            if (userOpt.isEmpty()) {
                response.put("error", "User not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("success", true);
            response.put("message", "Token verified successfully. You can now log in.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Ghi log lỗi (nếu cần thiết)
            response.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    public ResponseEntity<Map<String, Object>> googleLogin(GoogleLoginModel googleLoginModel) {
        String googleId = googleLoginModel.getGoogleId();
        String email = googleLoginModel.getEmail();
        String fullName = googleLoginModel.getFullName();
        String avatar = googleLoginModel.getAvatar();

        Map<String, Object> response = new HashMap<>();

        Optional<User> userOpt = Optional.ofNullable(userRepository.findByEmail(email));

        if (userOpt.isEmpty()) {
            User newUser = new User();
            newUser.setUsername(googleId);
            newUser.setEmail(email);
            newUser.setFullname(fullName);
            newUser.setAvatar(avatar);
            newUser.setLanguage("vietnam");
            newUser.setActive(true);
            newUser.setForgetPassword(false);

            userRepository.save(newUser);
            response.put("message", "User registered successfully.");
        } else {
            response.put("message", "User logged in successfully.");
        }

        String secret = new Constant().getAUTH_KEY();
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        long expirationTimeMillis = 7 * 24 * 60 * 60 * 1000L;
        JWT jwt = new JWT(secretKey, expirationTimeMillis);
        String token = jwt.generateToken(googleId);

        response.put("success", true);
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> forgotPassword(String email) throws MessagingException {
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOpt = Optional.ofNullable(userRepository.findByKeyword(email));
        if (userOpt.isEmpty()) {
            response.put("error", "Email not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        User user = userOpt.get();

        String secret = new Constant().getAUTH_KEY();
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        long expirationTimeMillis = 24 * 60 * 60 * 1000L;
        JWT jwt = new JWT(secretKey, expirationTimeMillis);

        String resetToken = jwt.generateToken(user.getUsername());

        String resetLink = "http://localhost:8080/reset-password?token=" + resetToken;
        mailService.sendResetPasswordMail(user.getUsername(), user.getEmail(), resetLink);

        response.put("success", true);
        response.put("message", "Password reset email sent. Please check your email.");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> resetPassword(String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            String secret = new Constant().getAUTH_KEY();
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
            JWT jwt = new JWT(secretKey, 0);

            String username = jwt.getUsername(token);

            Optional<User> userOpt = Optional.ofNullable(userRepository.findByUsername(username));
            if (userOpt.isEmpty()) {
                response.put("error", "User not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            User user = userOpt.get();
            String newPassword = RandomUtil.generateComplexRandomString();

            user.setPassword(Encode.hashCode(newPassword));
            user.setForgetPassword(true);
            userRepository.save(user);

            mailService.sendTemporaryPasswordMail(user.getUsername(), user.getEmail(), newPassword);

            response.put("success", true);
            response.put("message", "Password has been reset successfully and sent to your email.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Invalid or expired token.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> validateToken(String token) {
        Map<String, Object> response = new HashMap<>();
        String secret = new Constant().getAUTH_KEY();
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        JWT jwt = new JWT(secretKey, 24 * 60 * 60 * 1000L);

        if (!jwt.isTokenValid(token)) {
            response.put("valid", false);
            response.put("message", "Invalid or expired token.");
            return ResponseEntity.ok(response);
        }

        Claims claims = jwt.getClaims(token);
        response.put("valid", true);
        response.put("username", claims.getSubject());
        response.put("roles", claims.get("roles"));
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> updateProfile(UpdateProfileModel updateProfileModel) {
        Map<String, Object> response = new HashMap<>();

        String username = updateProfileModel.getUsername();
        String email = updateProfileModel.getEmail();
        String fullName = updateProfileModel.getFullName();
        String avatar = updateProfileModel.getAvatar();

        Optional<User> userOpt = Optional.ofNullable(userRepository.findByUsername(username));
        if (userOpt.isEmpty()) {
            response.put("error", "User not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        User user = userOpt.get();
        user.setEmail(email);
        user.setFullname(fullName);
        user.setAvatar(avatar);

        userRepository.save(user);

        response.put("success", true);
        response.put("message", "Profile updated successfully.");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> changePassword(Map<String, String> passwordMap) {
        Map<String, Object> response = new HashMap<>();

        String username = passwordMap.get("username");
        String oldPassword = passwordMap.get("oldPassword");
        String newPassword = passwordMap.get("newPassword");

        Optional<User> userOpt = Optional.ofNullable(userRepository.findByUsername(username));
        if (userOpt.isEmpty()) {
            response.put("error", "User not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        User user = userOpt.get();

        if (!Encode.checkCode(oldPassword, user.getPassword())) {
            response.put("error", "Old password is incorrect.");
            return ResponseEntity.badRequest().body(response);
        }

        user.setPassword(Encode.hashCode(newPassword));
        userRepository.save(user);

        response.put("success", true);
        response.put("message", "Password changed successfully.");
        return ResponseEntity.ok(response);
    }

}
