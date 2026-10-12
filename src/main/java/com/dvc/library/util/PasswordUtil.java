package com.dvc.library.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Tien ich hash mat khau bang SHA-256 (don gian, phuc vu muc dich hoc tap).
 */
public class PasswordUtil {

    private PasswordUtil() {}

    public static String hash(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(plainPassword.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Loi hash mat khau", e);
        }
    }

    public static boolean verify(String plainPassword, String hashedPassword) {
        return hash(plainPassword).equalsIgnoreCase(hashedPassword);
    }
}
