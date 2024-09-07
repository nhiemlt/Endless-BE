package com.datn.endless.services;

import org.jasypt.util.password.BasicPasswordEncryptor;


public class Encode {
    private static final BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

    public static String hashCode(String plainPassword) {
        return passwordEncryptor.encryptPassword(plainPassword);
    }

    public static boolean checkCode(String plainPassword, String encryptedPassword) {
        return passwordEncryptor.checkPassword(plainPassword, encryptedPassword);
    }
}