package com.datn.endless.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CookieService {
    @Autowired
    HttpServletResponse resp;

    @Autowired
    HttpServletRequest req;

    public void setCookie(String cookieName, String cookieValue, int time) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(time * 60 * 60); // thời gian tồn tại của cookie (giờ)
        cookie.setPath("/"); // Đảm bảo cookie có hiệu lực trên toàn bộ ứng dụng
        cookie.setHttpOnly(true); // Cookie chỉ truy cập được thông qua HTTP(S)
        cookie.setSecure(false); // Đặt thành true nếu sử dụng HTTPS
        resp.addCookie(cookie);
    }

    public String getCookie(String cookieName) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
