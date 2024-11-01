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

    @Autowired
    UserLoginInfomation userLoginInfomation;

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
        Optional<User> userOpt = Optional.ofNullable(userRepository.findByKeyword(username));
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

    public ResponseEntity<Map<String, Object>> updateEmail(String username, String email) {
        Map<String, Object> response = new HashMap<>();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            response.put("error", "Không tìm thấy user");
            return ResponseEntity.badRequest().body(response);
        }

        if (userRepository.findByEmail(email) != null) {
            response.put("error", "Email đã được sử dụng, vui lòng nhập email khác.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String secret = new Constant().getAUTH_KEY();
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
            long expirationTimeMillis = 24 * 60 * 60 * 1000L;
            JWT jwt = new JWT(secretKey, expirationTimeMillis);

            Map<String, Object> claims = new HashMap<>();
            claims.put("email", email);

            String verificationToken = jwt.generateToken(username, claims);
            String verificationLink = "http://localhost:8080/verify-reset-email?token=" + verificationToken;

            mailService.sendVerificationUpdateMail(username, user.getEmail(), verificationLink);

            response.put("success", true);
            response.put("message", "Vui lòng kiểm tra email để hoàn tất cập nhật email");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<String> verifyResetEmail(String token) {
        String title = "Xác minh thất bại!";
        String message = "Có lỗi xảy ra.";
        String content = "Vui lòng thử lại sau vài phút.";

        try {
            String secret = new Constant().getAUTH_KEY();
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
            JWT jwt = new JWT(secretKey, 24 * 60 * 60 * 1000L);

            if (!jwt.isTokenValid(token)) {
                return ResponseEntity.badRequest().body(generateHtml(title, "Token không chính xác hoặc đã hết hạn", content));
            }

            Claims claims = jwt.getClaims(token);
            String username = (String) claims.getSubject();
            String email = (String) claims.get("email");

            User user = userRepository.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateHtml(title, "Không tìm thấy người dùng phù hợp", content));
            }
            user.setEmail(email);
            userRepository.save(user);

            title = "Xác minh thành công!";
            message = "Cảm ơn bạn!";
            content = "Email của bạn đã được cập nhật thành công.";
            return ResponseEntity.ok(generateHtml(title, message, content));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(generateHtml("Lỗi hệ thống!", "Internal server error: " + e.getMessage(), content));
        }
    }

    public ResponseEntity<String> verifyEmail(String token) {
        String title = "Xác minh thất bại!";
        String message = "Có lỗi xảy ra.";
        String content = "Vui lòng thử lại sau vài phút.";

        try {
            String secret = new Constant().getAUTH_KEY();
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
            JWT jwt = new JWT(secretKey, 24 * 60 * 60 * 1000L);

            if (!jwt.isTokenValid(token)) {
                return ResponseEntity.badRequest().body(generateHtml(title, "Token không chính xác hoặc đã hết hạn", content));
            }

            Claims claims = jwt.getClaims(token);
            String username = (String) claims.get("username");
            String email = (String) claims.get("email");
            String password = (String) claims.get("password");

            Optional<User> userOpt = Optional.ofNullable(userRepository.findByUsername(username));
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateHtml(title, "Không tìm thấy người dùng phù hợp", content));
            }

            User user = userOpt.get();
            user.setActive(true);
            user.setPassword(Encode.hashCode(password));
            userRepository.save(user);

            title = "Xác minh thành công!";
            message = "Cảm ơn bạn!";
            content = "Email của bạn đã được xác minh thành công. Bạn có thể đăng nhập ngay bây giờ.";
            return ResponseEntity.ok(generateHtml(title, message, content));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(generateHtml("Lỗi hệ thống!", "Internal server error: " + e.getMessage(), content));
        }
    }


    private String generateHtml(String title, String message,String content) {
        return "<!DOCTYPE html>" +
                "<html lang=\"vi\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>" + title + "</title>" +
                "<link href=\"https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css\" rel=\"stylesheet\">" +
                "</head>" +
                "<body>" +
                "<div class=\"grid h-screen place-content-center bg-white px-4\">" +
                "<div class=\"text-center\">" +
                "<h1 class=\"text-9xl font-black text-gray-200\">" + title + "</h1>" +
                "<p class=\"text-2xl font-bold tracking-tight text-gray-900 sm:text-4xl\">" + message + "</p>" +
                "<p class=\"mt-4 text-gray-500\">" + content + "</p>" +
                "<a href=\"http://localhost:3000/login\" class=\"mt-6 inline-block rounded bg-indigo-600 px-5 py-3 text-sm font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring\">" +
                "Đi đến trang đăng nhập" +
                "</a>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
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
            else{
                User user = userOpt.get();
                String role = !user.getRoles().isEmpty() ? "admin" : "customer";
                response.put("role", role);
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
            response.put("error", "Tài khoản này không tồn tại!");
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
        response.put("message", "Vui lòng kiểm tra email để đặt lại mật khẩu!");
        return ResponseEntity.ok(response);
    }

    public String resetPassword(String token) {
        String title;
        String message;
        String content;

        try {
            String secret = new Constant().getAUTH_KEY();
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
            JWT jwt = new JWT(secretKey, 0);

            String username = jwt.getUsername(token);
            Optional<User> userOpt = Optional.ofNullable(userRepository.findByUsername(username));

            if (userOpt.isEmpty()) {
                title = "Lỗi";
                message = "Người dùng không tồn tại.";
                content = "Vui lòng thử lại sau ít phút";
                return generateHtml(title, message, content);
            }

            User user = userOpt.get();
            String newPassword = RandomUtil.generateComplexRandomString();
            user.setPassword(Encode.hashCode(newPassword));
            user.setForgetPassword(true);
            userRepository.save(user);

            mailService.sendTemporaryPasswordMail(user.getUsername(), user.getEmail(), newPassword);

            title = "Thành công";
            message = "Mật khẩu đã được đặt lại thành công và đã được gửi đến email của bạn.";
            content = "Vui lòng kiểm tra email để xác thực!";
            return generateHtml(title, message, content);
        } catch (Exception e) {
            title = "Lỗi";
            message = "Token không hợp lệ hoặc đã hết hạn.";
            content = "Vui lòng thử lại sau ít phút";
            return generateHtml(title, message, content);
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

        String oldPassword = passwordMap.get("oldPassword");
        String newPassword = passwordMap.get("newPassword");

        // Lấy username từ thông tin người dùng đã đăng nhập
        String username = userLoginInfomation.getCurrentUsername();

        // Kiểm tra xem người dùng có tồn tại không
        Optional<User> userOpt = Optional.ofNullable(userRepository.findByUsername(username));
        if (userOpt.isEmpty()) {
            response.put("error", "User not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        User user = userOpt.get();

        // Kiểm tra xem người dùng có mật khẩu không
        if (user.getPassword() == null) {
            response.put("error", "User does not have a password set. Password cannot be changed.");
            return ResponseEntity.badRequest().body(response);
        }

        // Kiểm tra mật khẩu cũ
        if (!Encode.checkCode(oldPassword, user.getPassword())) {
            response.put("error", "Old password is incorrect.");
            return ResponseEntity.badRequest().body(response);
        }

        // Cập nhật mật khẩu mới
        user.setPassword(Encode.hashCode(newPassword));
        userRepository.save(user);

        response.put("success", true);
        response.put("message", "Password changed successfully.");
        return ResponseEntity.ok(response);
    }


}
