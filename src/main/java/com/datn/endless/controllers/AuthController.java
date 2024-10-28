package com.datn.endless.controllers;

import com.datn.endless.entities.User;
import com.datn.endless.models.GoogleLoginModel;
import com.datn.endless.models.LoginModel;
import com.datn.endless.models.RegisterModel;
import com.datn.endless.services.AuthService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginModel loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterModel registerModel) {
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
        Map<String, Object> response = (Map<String, Object>) authService.changePassword(passwordMap);
        return ResponseEntity.status(HttpStatus.OK).body(response);
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
