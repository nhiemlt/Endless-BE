package com.datn.endless.configs;

public class ZaloPayConfig {
    public static String APP_ID = "554";
    public static String KEY1 = "8NdU5pG5R2spGHGhyO99HN1OhD8IQJBn";
    public static String KEY2 = "uUfsWgfLkRLzq6W2uNXTCxrfxs51auny";
    public static String CREATE_ORDER_URL = "https://sandbox.zalopay.com.vn/v001/tpe/createorder";
    public static String GET_STATUS_PAY_URL = "https://sandbox.zalopay.com.vn/v001/tpe/getstatusbyapptransid";
    public static String REDIRECT_URL ="http://localhost:8080/api/payment/callback";
}
