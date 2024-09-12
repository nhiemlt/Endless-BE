package com.datn.endless.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Service
public class GHTKService {

    @Value("${ghtk.api.url}")
    private String ghtkApiUrl;

    @Value("${ghtk.api.key}")
    private String ghtkApiToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<String> createShippingOrder(Map<String, Object> requestBody) {
        String url = ghtkApiUrl + "/create_order"; // Ví dụ URL tạo đơn hàng
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + ghtkApiToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForEntity(url, entity, String.class);
    }

    public ResponseEntity<String> trackShippingOrder(String trackingCode) {
        String url = ghtkApiUrl + "/track_order/" + trackingCode; // Ví dụ URL theo dõi đơn hàng
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + ghtkApiToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }
}
