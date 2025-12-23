package org.example.config;

import javax.swing.border.Border;
import javax.swing.BorderFactory;

public class AppConfig {

    // ========== APP INFO ==========
    public static final String APP_NAME = "RUMA";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_TAGLINE = "Ruang Usaha Manajemen Akomodasi";

    // ========== SIZING ==========
    public static final int SIDEBAR_WIDTH = 240;
    public static final int NAVBAR_HEIGHT = 60;
    public static final int BUTTON_HEIGHT = 40;
    public static final int INPUT_HEIGHT = 40;
    public static final int TABLE_ROW_HEIGHT = 48;

    // ========== SPACING ==========
    public static final int SPACING_XS = 4;
    public static final int SPACING_SM = 8;
    public static final int SPACING_MD = 16;
    public static final int SPACING_LG = 24;
    public static final int SPACING_XL = 32;
    public static final int SPACING_2XL = 48;

    // ========== BORDER RADIUS ==========
    public static final int RADIUS_SM = 4;
    public static final int RADIUS_MD = 8;
    public static final int RADIUS_LG = 12;
    public static final int RADIUS_XL = 16;
    public static final int RADIUS_ROUND = 999;

    // ========== ICON SIZES ==========
    public static final int ICON_SM = 16;
    public static final int ICON_MD = 24;
    public static final int ICON_LG = 32;

    // ========== FILE PATHS ==========
    public static final String DATA_DIR = "data/";
    public static final String IMAGES_DIR = "images/";
    public static final String KAMAR_FILE = DATA_DIR + "kamar.txt";
    public static final String PENYEWA_FILE = DATA_DIR + "penyewa.txt";
    public static final String PEMBAYARAN_FILE = DATA_DIR + "pembayaran.txt";

    // ========== BORDERS ==========
    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.BG_CREAM, 1),
                BorderFactory.createEmptyBorder(SPACING_MD, SPACING_MD, SPACING_MD, SPACING_MD)
        );
    }

    public static Border createButtonBorder() {
        return BorderFactory.createEmptyBorder(10, 20, 10, 20);
    }

    public static Border createInputBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.GRAY_MEDIUM, 1),
                BorderFactory.createEmptyBorder(SPACING_SM, SPACING_MD, SPACING_SM, SPACING_MD)
        );
    }
}