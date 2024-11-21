package com.datn.endless.utils;

public class ZaloPay {
    public static final String APP_ID = "2553"; // ID ứng dụng của bạn
    public static final String KEY1 = "PcY4iZIKFCIdgZvA6ueMcMHHUbRLYjPL"; // Khóa bảo mật
    public static final String CREATE_ORDER_URL = "https://sb-openapi.zalopay.vn/v2/create";
    public static final String REDIRECT_URL = "http://localhost:8080/redirect"; // URL để ZaloPay chuyển hướng sau khi thanh toán
}
