package com.datn.endless.utils;

import java.util.Random;


public class UUIDUtils {
    // Hàm sửa đổi UUID
    public static String modifyUUID(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            throw new IllegalArgumentException("Invalid UUID: UUID cannot be null or empty.");
        }

        // Bước 1: Loại bỏ dấu "-"
        String uuidWithoutDashes = uuid.replace("-", "");

        // Bước 2: Thêm 2 ký tự random vào đầu và cuối chuỗi
        char randomChar1 = getRandomChar();
        char randomChar2 = getRandomChar();
        uuidWithoutDashes = randomChar1 + uuidWithoutDashes + randomChar2;

        // Bước 3: Chia chuỗi thành 2 phần
        int midIndex = uuidWithoutDashes.length() / 2;
        String part1 = uuidWithoutDashes.substring(0, midIndex);
        String part2 = uuidWithoutDashes.substring(midIndex);

        // Bước 4: Đổi vị trí các phần
        String modifiedUUID = part2 + part1;

        // Bước 5: Lật ngược chuỗi
        return new StringBuilder(modifiedUUID).reverse().toString();
    }

    public static String decodeModifiedUUID(String modifiedUUID) {
        if (modifiedUUID == null || modifiedUUID.isEmpty()) {
            throw new IllegalArgumentException("Invalid input: Input cannot be null or empty.");
        }

        // Bước 1: Lật ngược chuỗi
        modifiedUUID = new StringBuilder(modifiedUUID).reverse().toString();

        // Bước 2: Chia chuỗi thành 2 phần
        int midIndex = modifiedUUID.length() / 2;
        String part1 = modifiedUUID.substring(0, midIndex);
        String part2 = modifiedUUID.substring(midIndex);

        // Bước 3: Đổi lại vị trí các phần
        String uuidWithoutDashes = part2 + part1;

        // Bước 4: Loại bỏ ký tự random đầu và cuối chuỗi
        uuidWithoutDashes = uuidWithoutDashes.substring(1, uuidWithoutDashes.length() - 1);

        // Bước 5: Thêm lại dấu "-"
        return addDashesToUUID(uuidWithoutDashes);
    }


    // Hàm thêm dấu "-" vào UUID
    private static String addDashesToUUID(String uuidWithoutDashes) {
        if (uuidWithoutDashes == null || uuidWithoutDashes.length() != 32) {
            throw new IllegalArgumentException("Invalid UUID format: Expected 32 characters without dashes.");
        }
        return uuidWithoutDashes.substring(0, 8) + "-" +
                uuidWithoutDashes.substring(8, 12) + "-" +
                uuidWithoutDashes.substring(12, 16) + "-" +
                uuidWithoutDashes.substring(16, 20) + "-" +
                uuidWithoutDashes.substring(20);
    }

    // Hàm tạo ký tự random
    private static char getRandomChar() {
        Random random = new Random();
        // Sinh ngẫu nhiên một ký tự giữa a-z hoặc 0-9
        int randomCharCode = random.nextInt(62);
        if (randomCharCode < 26) {
            return (char) ('a' + randomCharCode);
        } else if (randomCharCode < 52) {
            return (char) ('A' + (randomCharCode - 26));
        } else {
            return (char) ('0' + (randomCharCode - 52));
        }
    }
}

