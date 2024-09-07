package com.datn.endless.utils;

import java.util.Random;

public class RandomUtil {
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // Chứa các chữ cái in hoa
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz"; // Chứa các chữ cái thường
    private static final String DIGITS = "0123456789"; // Chứa các chữ số
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+<>?"; // Chứa các ký tự đặc biệt
    private static final String ALL_CHARACTERS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARACTERS; // Kết hợp tất cả các loại ký tự
    private static final int LENGTH = 8; // Độ dài mật khẩu mong muốn
    private static final Random RANDOM = new Random(); // Sử dụng SecureRandom để đảm bảo ngẫu nhiên an toàn hơn

    public static String generateComplexRandomString() {
        StringBuilder sb = new StringBuilder(LENGTH);

        sb.append(UPPERCASE.charAt(RANDOM.nextInt(UPPERCASE.length())));
        sb.append(LOWERCASE.charAt(RANDOM.nextInt(LOWERCASE.length())));
        sb.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        sb.append(SPECIAL_CHARACTERS.charAt(RANDOM.nextInt(SPECIAL_CHARACTERS.length())));

        for (int i = 4; i < LENGTH; i++) {
            sb.append(ALL_CHARACTERS.charAt(RANDOM.nextInt(ALL_CHARACTERS.length())));
        }

        return shuffleString(sb.toString());
    }

    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int index = RANDOM.nextInt(i + 1);
            char temp = characters[index];
            characters[index] = characters[i];
            characters[i] = temp;
        }
        return new String(characters);
    }
}
