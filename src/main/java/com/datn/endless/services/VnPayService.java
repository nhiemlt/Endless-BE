package com.datn.endless.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VnPayService {

    @Value("${vnpay.api.key}")
    private String vnPayApiKey;

    @Value("${vnpay.api.url}")
    private String vnPayApiUrl;

    @Value("${vnpay.api.terminal}")
    private String vnPayTmnCode;

    public String createPaymentUrl(Map<String, String> paymentDetails) {
        String url = vnPayApiUrl;
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", "2.0.0");
        params.put("vnp_TmnCode", vnPayTmnCode);
        params.put("vnp_Amount", paymentDetails.get("amount"));
        params.put("vnp_Command", "pay");
        params.put("vnp_CreateDate", paymentDetails.get("createDate"));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_OrderInfo", paymentDetails.get("orderInfo"));
        params.put("vnp_OrderType", "other");
        params.put("vnp_ReturnUrl", paymentDetails.get("returnUrl"));
        params.put("vnp_TxnRef", paymentDetails.get("txnRef"));

        // Generate checksum
        String checksum = generateChecksum(params);
        params.put("vnp_SecureHash", checksum);

        // Build URL
        String queryString = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
        return url + "?" + queryString;
    }

    private String generateChecksum(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> sb.append(e.getKey()).append("=").append(e.getValue()).append("&"));
        sb.append("vnp_HashSecret=").append(vnPayApiKey);
        return md5(sb.toString());
    }

    private String md5(String input) {
        // Implement MD5 hashing logic here
        return ""; // Replace with actual MD5 implementation
    }
}
