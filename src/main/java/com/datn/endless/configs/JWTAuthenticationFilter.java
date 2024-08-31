package com.datn.endless.configs;

import com.datn.endless.services.Constant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Date;

public class JWTAuthenticationFilter extends GenericFilterBean {

    private final UserDetailsService userDetailsService;
    private final String secretKey;

    private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    public JWTAuthenticationFilter(UserDetailsService userDetailsService, Constant constant) {
        this.userDetailsService = userDetailsService;
        this.secretKey = constant.getAUTH_KEY();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim(); // Xóa "Bearer " và khoảng trắng

            if (!token.isEmpty() && !isTokenExpired(token)) { // Sửa điều kiện kiểm tra hết hạn token
                String username = extractUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        chain.doFilter(request, response);
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("Invalid JWT token", e);
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    private boolean isTokenExpired(String token) {
        Date expirationDate = extractExpiration(token);
        return expirationDate.before(new Date()); // Sửa điều kiện kiểm tra hết hạn token
    }

    private String extractUsername(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    private boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); // Sửa điều kiện kiểm tra token
    }

    private Date extractExpiration(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration();
    }
}
