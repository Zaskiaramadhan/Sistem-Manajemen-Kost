package org.example.util;

import java.util.regex.Pattern;

/**
 * Validation Utility
 * Validasi input form
 */
public class ValidationUtil {

    // Regex patterns
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^(\\+62|62|0)[0-9]{9,12}$");

    private static final Pattern NUMBER_PATTERN =
            Pattern.compile("^[0-9]+$");

    /**
     * Validasi string tidak kosong
     */
    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Validasi email format
     */
    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validasi nomor HP Indonesia
     */
    public static boolean isValidPhone(String phone) {
        if (!isNotEmpty(phone)) return false;
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validasi angka positif
     */
    public static boolean isValidPositiveNumber(String text) {
        if (!isNotEmpty(text)) return false;
        try {
            double value = Double.parseDouble(text);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validasi integer
     */
    public static boolean isValidInteger(String text) {
        if (!isNotEmpty(text)) return false;
        return NUMBER_PATTERN.matcher(text).matches();
    }

    /**
     * Validasi panjang minimum
     */
    public static boolean hasMinLength(String text, int minLength) {
        return isNotEmpty(text) && text.length() >= minLength;
    }

    /**
     * Parse double dengan error handling
     */
    public static double parseDouble(String text, double defaultValue) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Parse integer dengan error handling
     */
    public static int parseInt(String text, int defaultValue) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}