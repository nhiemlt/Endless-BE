package com.datn.endless.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PaymentCallbackController {

    @RequestMapping(value = "/payment/callback", method = RequestMethod.GET)
    public String handleCallback(@RequestParam Map<String, String> params) {
        // Extract parameters
        String vnp_SecureHash = params.get("vnp_SecureHash");
        String vnp_SecureHashType = params.get("vnp_SecureHashType");
        // Validate checksum
        // Process payment
        return "Payment result";
    }
}