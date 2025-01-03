package com.datn.endless.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

public class JWT {

    private final SecretKey secretKey;
    private final long expirationTimeMillis;

    public JWT(SecretKey secretKey, long expirationTimeMillis) {
        this.secretKey = secretKey;
        this.expirationTimeMillis = expirationTimeMillis;
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims) // Thêm các Claims bổ sung
                .setSubject(subject) // Thêm subject (ví dụ: email hoặc username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMillis))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMillis))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()  // Dành cho phiên bản mới hơn
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }



    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public static SecretKey createSecretKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }
}
