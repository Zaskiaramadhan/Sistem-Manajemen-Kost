package org.example.config;

import java.awt.Font;

/**
 * Font Manager untuk Typography System
 */
public class FontManager {

    // ========== FONT FAMILIES ==========
    public static final String FONT_PRIMARY = "Segoe UI";
    public static final String FONT_FALLBACK = "Arial";
    public static final String FONT_MONOSPACE = "Consolas";

    // ========== HEADINGS ==========
    public static final Font FONT_H1 = new Font(FONT_PRIMARY, Font.BOLD, 32);
    public static final Font FONT_H2 = new Font(FONT_PRIMARY, Font.BOLD, 24);
    public static final Font FONT_H3 = new Font(FONT_PRIMARY, Font.BOLD, 18);
    public static final Font FONT_H4 = new Font(FONT_PRIMARY, Font.BOLD, 16);

    // ========== BODY TEXT ==========
    public static final Font FONT_BODY_LARGE = new Font(FONT_PRIMARY, Font.PLAIN, 16);
    public static final Font FONT_BODY = new Font(FONT_PRIMARY, Font.PLAIN, 14);
    public static final Font FONT_BODY_SMALL = new Font(FONT_PRIMARY, Font.PLAIN, 12);

    // ========== SPECIAL ==========
    public static final Font FONT_BUTTON = new Font(FONT_PRIMARY, Font.BOLD, 14);
    public static final Font FONT_CAPTION = new Font(FONT_PRIMARY, Font.PLAIN, 11);
    public static final Font FONT_NUMBER_BIG = new Font(FONT_PRIMARY, Font.BOLD, 28);
    public static final Font FONT_NUMBER_MED = new Font(FONT_PRIMARY, Font.BOLD, 20);
    public static final Font FONT_MONO = new Font(FONT_MONOSPACE, Font.PLAIN, 13);

    // ========== BRAND ==========
    public static final Font FONT_BRAND = new Font(FONT_PRIMARY, Font.BOLD, 36);
    public static final Font FONT_BRAND_SUBTITLE = new Font(FONT_PRIMARY, Font.PLAIN, 14);
}