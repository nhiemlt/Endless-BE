package com.datn.endless.controllers;

import com.datn.endless.services.GHTKService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ghtk")
public class GHTKController {

    @Autowired
    private GHTKService ghtkService;

    @PostMapping("/create-order")
    public ResponseEntity<String> createShippingOrder(@RequestBody Map<String, Object> requestBody) {
        return ghtkService.createShippingOrder(requestBody);
    }

    @GetMapping("/track-order/{trackingCode}")
    public ResponseEntity<String> trackShippingOrder(@PathVariable String trackingCode) {
        return ghtkService.trackShippingOrder(trackingCode);
    }
}
