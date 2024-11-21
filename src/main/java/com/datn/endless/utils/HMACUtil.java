package com.datn.endless.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HMACUtil {

    public static final String HMACSHA256 = "HmacSHA256";

    public static String HMacHexStringEncode(String algorithm, String key, String data) throws Exception {
        Mac mac = Mac.getInstance(algorithm);
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), algorithm);
        mac.init(secretKey);
        byte[] hash = mac.doFinal(data.getBytes());
        return bytesToHex(hash);
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
