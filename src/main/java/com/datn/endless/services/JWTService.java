package com.datn.endless.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.List;

@Service
public class JWTService {
    private final SecretKey secretKey;

    public JWTService() {
        String secret = new Constant().getAUTH_KEY(); // Lấy secret từ config hoặc biến môi trường
        this.secretKey = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }

    // Tạo JWT
    public String generateToken(String username, List<String> permissions, boolean remember) {
        long expirationTime = remember ? 7 * 24 * 60 * 60 * 1000L : 24 * 60 * 60 * 1000L;

        return Jwts.builder()
                .setSubject(username)
                .claim("permissions", permissions)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    // Giải mã JWT để lấy Claims (payload)
    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    // Kiểm tra token có hết hạn không
    public boolean isTokenExpired(String token) {
        Date expirationDate = getClaims(token).getExpiration();
        return expirationDate.before(new Date());
    }

    // Lấy username từ token
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // Xác thực token và đối chiếu với userDetails
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Lấy danh sách permissions từ token
    public List<String> extractPermissions(String token) {
        Claims claims = getClaims(token);
        return (List<String>) claims.get("permissions");
    }
}
