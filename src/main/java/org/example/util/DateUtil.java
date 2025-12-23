package org.example.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Date Utility
 * Helper untuk formatting & parsing tanggal
 */
public class DateUtil {

    // Format tanggal standar
    public static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static final DateTimeFormatter FILE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static final DateTimeFormatter MONTH_YEAR_FORMAT =
            DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("id", "ID"));

    /**
     * Format LocalDate ke string (dd/MM/yyyy)
     */
    public static String formatDate(LocalDate date) {
        if (date == null) return "-";
        return date.format(DISPLAY_FORMAT);
    }

    /**
     * Parse string ke LocalDate
     */
    public static LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString, DISPLAY_FORMAT);
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing date: " + dateString);
            return null;
        }
    }

    /**
     * Get tanggal hari ini
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * Format bulan-tahun (Januari 2025)
     */
    public static String formatMonthYear(LocalDate date) {
        if (date == null) return "-";
        return date.format(MONTH_YEAR_FORMAT);
    }

    /**
     * Get bulan-tahun sekarang
     */
    public static String getCurrentMonthYear() {
        return formatMonthYear(today());
    }

    /**
     * Check apakah tanggal sudah lewat (terlambat)
     */
    public static boolean isLate(LocalDate date, int graceDays) {
        LocalDate deadline = date.plusDays(graceDays);
        return today().isAfter(deadline);
    }

    /**
     * Hitung selisih hari
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return java.time.temporal.ChronoUnit.DAYS.between(start, end);
    }
}