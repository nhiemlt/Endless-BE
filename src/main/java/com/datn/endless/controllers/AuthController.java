package com.datn.endless.controllers;

import com.datn.endless.models.GoogleLoginModel;
import com.datn.endless.models.LoginModel;
import com.datn.endless.models.RegisterModel;
import com.datn.endless.services.AuthService;
import com.datn.endless.services.UserLoginInfomation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    UserLoginInfomation userLoginInfomation;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginModel loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody @Valid RegisterModel registerModel,
                                                        BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra lỗi validation
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });
            response.put("errors", errors);
            response.put("message", "Dữ liệu đầu vào không hợp lệ");
            return ResponseEntity.badRequest().body(response);
        }

        // Gọi service xử lý logic đăng ký
        return authService.register(registerModel);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        return authService.verifyEmail(token);
    }

    @GetMapping("/verify-reset-email")
    public ResponseEntity<String> verifyResetEmail(@RequestParam("token") String token) {
        return authService.verifyResetEmail(token);
    }

    @GetMapping("/verify-auth-token")
    public ResponseEntity<Map<String, Object>> verifyAuthToken(@RequestParam("token") String token) {
        return authService.verifyAuthToken(token);
    }

    @PostMapping("/login/google")
    public ResponseEntity<Map<String, Object>> googleLogin(@RequestBody GoogleLoginModel googleLoginModel) {
        return authService.googleLogin(googleLoginModel);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = Map.of("success", true, "message", "Logout successful.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> passwordMap) {
        // Gọi phương thức service để thay đổi mật khẩu
        ResponseEntity<Map<String, Object>> response = authService.changePassword(passwordMap);

        // Trả về phản hồi từ service mà không cần ép kiểu
        return response;  // Trả về trực tiếp ResponseEntity từ service
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestParam String email) throws MessagingException {
        return authService.forgotPassword(email);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token) {
        String htmlResponse = authService.resetPassword(token);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(htmlResponse);
    }

    @PostMapping("/token/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestParam String token) {
        return authService.validateToken(token);
    }
}
