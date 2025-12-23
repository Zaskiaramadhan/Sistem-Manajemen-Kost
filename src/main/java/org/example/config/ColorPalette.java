package org.example.config;

import java.awt.Color;

/**
 * Color Palette untuk Aplikasi Ruma
 * Kombinasi Earth Tone & Blue Modern
 */
public class ColorPalette {

    // ========== PRIMARY COLORS ==========
    public static final Color BROWN_PRIMARY = new Color(139, 111, 71);      // #8B6F47
    public static final Color BROWN_LIGHT = new Color(160, 130, 109);       // #A0826D
    public static final Color BLUE_PRIMARY = new Color(30, 58, 95);         // #1E3A5F
    public static final Color BLUE_OCEAN = new Color(44, 95, 141);          // #2C5F8D

    // ========== BACKGROUND ==========
    public static final Color BG_CREAM = new Color(232, 220, 196);          // #E8DCC4
    public static final Color BG_OFF_WHITE = new Color(245, 241, 232);      // #F5F1E8
    public static final Color BG_WHITE = new Color(255, 255, 255);          // #FFFFFF
    public static final Color BG_LIGHT_BLUE = new Color(232, 244, 248);     // #E8F4F8

    // ========== ACCENT ==========
    public static final Color GOLD_ACCENT = new Color(212, 165, 116);       // #D4A574
    public static final Color GOLD_MUTED = new Color(184, 149, 106);        // #B8956A
    public static final Color BLUE_ACCENT = new Color(74, 144, 226);        // #4A90E2

    // ========== NEUTRAL ==========
    public static final Color NAVY_DARK = new Color(44, 62, 80);            // #2C3E50
    public static final Color GRAY_DARK = new Color(93, 109, 126);          // #5D6D7E
    public static final Color GRAY_MEDIUM = new Color(149, 165, 166);       // #95A5A6
    public static final Color GRAY_LIGHT = new Color(236, 240, 241);        // #ECF0F1
    public static final Color CHARCOAL = new Color(45, 55, 72);             // #2D3748

    // ========== SEMANTIC COLORS ==========
    public static final Color SUCCESS_GREEN = new Color(39, 174, 96);       // #27AE60
    public static final Color WARNING_ORANGE = new Color(243, 156, 18);     // #F39C12
    public static final Color DANGER_RED = new Color(231, 76, 60);          // #E74C3C
    public static final Color INFO_BLUE = new Color(52, 152, 219);          // #3498DB

    // ========== STATUS SPECIFIC ==========
    public static final Color STATUS_AVAILABLE = WARNING_ORANGE;            // Kamar Tersedia
    public static final Color STATUS_OCCUPIED = DANGER_RED;                 // Kamar Terisi
    public static final Color STATUS_PAID = SUCCESS_GREEN;                  // Sudah Bayar
    public static final Color STATUS_UNPAID = WARNING_ORANGE;               // Belum Bayar
    public static final Color STATUS_LATE = DANGER_RED;                     // Terlambat

    // ========== TRANSPARENCY ==========
    public static final Color OVERLAY_DARK = new Color(0, 0, 0, 120);       // Semi-transparent black
    public static final Color OVERLAY_LIGHT = new Color(255, 255, 255, 200); // Semi-transparent white
}