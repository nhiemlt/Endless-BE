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
import com.datn.endless.utils.RandomUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

        if (!user.getActive()) {
            response.put("error", "User is blocked.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // Trả về mã lỗi 403 cho user bị khóa
        }

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
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) // Thêm token vào header
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

    @GetMapping("/verify")
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
            user.setActive(true);
            user.setForgetPassword(false);
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
            newUser.setActive(true);
            newUser.setForgetPassword(false);

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
    public ResponseEntity<String> logout() {
        // Không cần xử lý quá phức tạp vì sử dụng JWT, chỉ cần để client xoá JWT khỏi lưu trữ (local storage/session storage)
        return ResponseEntity.ok("Logout successful.");
    }

    @PostMapping("/update-profile")
    public ResponseEntity<String> updateProfile(@Valid @RequestBody User updatedUser, BindingResult error) {
        if (error.hasErrors()) {
            return ResponseEntity.badRequest().body("Please check all fields.");
        }

        Optional<User> userOpt = Optional.ofNullable(userRepository.findByUsername(updatedUser.getUsername()));
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User existingUser = userOpt.get();
        existingUser.setFullname(updatedUser.getFullname());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setAvatar(updatedUser.getAvatar());
        existingUser.setLanguage(updatedUser.getLanguage());
        userRepository.save(existingUser);

        return ResponseEntity.ok("Profile updated successfully.");
    }

    // Phương thức thay đổi mật khẩu
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> passwordMap) {
        String username = passwordMap.get("username");
        String oldPassword = passwordMap.get("oldPassword");
        String newPassword = passwordMap.get("newPassword");

        Optional<User> userOpt = Optional.ofNullable(userRepository.findByUsername(username));
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User user = userOpt.get();
        if (!Encode.checkCode(oldPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body("Old password is incorrect.");
        }

        user.setPassword(Encode.hashCode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok("Password changed successfully.");
    }

    // Phương thức quên mật khẩu
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) throws MessagingException {
        Optional<User> userOpt = Optional.ofNullable(userRepository.findByKeyword(email));
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
        }

        User user = userOpt.get();

        String secret = new Constant().getAUTH_KEY();
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        long expirationTimeMillis = 24 * 60 * 60 * 1000L; // Token có hiệu lực trong 24 giờ
        JWT jwt = new JWT(secretKey, expirationTimeMillis);

        // Tạo token reset mật khẩu
        String resetToken = jwt.generateToken(user.getUsername());

        // Gửi email chứa liên kết đặt lại mật khẩu
        String resetLink = "http://localhost:8080/reset-password?token=" + resetToken;
        mailService.sendResetPasswordMail(user.getUsername(), user.getEmail(), resetLink);

        return ResponseEntity.ok("Password reset email sent. Please check your email.");
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token) {
        // Lấy khóa bí mật từ Constant
        String secret = new Constant().getAUTH_KEY();
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        JWT jwt = new JWT(secretKey, 0);

        // Giải mã token để lấy username
        String username;
        try {
            username = jwt.getUsername(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
        }

        // Tìm user theo username
        Optional<User> userOpt = Optional.ofNullable(userRepository.findByUsername(username));
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User user = userOpt.get();

        // Tạo mật khẩu ngẫu nhiên mới
        String newPassword = RandomUtil.generateComplexRandomString();

        // Cập nhật mật khẩu và trạng thái forgotPassword
        user.setPassword(Encode.hashCode(newPassword)); // Cần hash mật khẩu trước khi lưu vào database
        user.setForgetPassword(true);
        userRepository.save(user);

        // Gửi mật khẩu mới qua email
        try {
            mailService.sendTemporaryPasswordMail(user.getUsername(), user.getEmail(), newPassword);
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email.");
        }

        return ResponseEntity.ok("Password has been reset successfully and sent to your email.");
    }

    // Phương thức kiểm tra token
    @PostMapping("/token/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestParam String token) {
        String secret = new Constant().getAUTH_KEY();
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        JWT jwt = new JWT(secretKey, 24 * 60 * 60 * 1000L);

        Map<String, Object> response = new HashMap<>();
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
}

