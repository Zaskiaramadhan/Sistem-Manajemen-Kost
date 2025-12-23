package org.example.util;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * File Handler Utility
 * Menangani semua operasi File I/O
 */
public class FileHandler {

    private static final String DATA_DIR = "data/";
    private static final String IMAGES_DIR = "images/";

    // File paths
    public static final String KAMAR_FILE = DATA_DIR + "kamar.txt";
    public static final String PENYEWA_FILE = DATA_DIR + "penyewa.txt";
    public static final String PEMBAYARAN_FILE = DATA_DIR + "pembayaran.txt";

    /**
     * Initialize data directories jika belum ada
     */
    public static void initializeDataDirectories() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Files.createDirectories(Paths.get(IMAGES_DIR + "rooms/"));

            // Create empty files if not exist
            createFileIfNotExists(KAMAR_FILE);
            createFileIfNotExists(PENYEWA_FILE);
            createFileIfNotExists(PEMBAYARAN_FILE);

            System.out.println("✅ Data directories initialized successfully");
        } catch (IOException e) {
            System.err.println("❌ Error initializing directories: " + e.getMessage());
        }
    }

    /**
     * Buat file kosong jika belum ada
     */
    private static void createFileIfNotExists(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    /**
     * Baca semua baris dari file
     */
    public static List<String> readAllLines(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
            e.printStackTrace();
        }
        return lines;
    }

    /**
     * Tulis semua baris ke file (overwrite)
     */
    public static boolean writeAllLines(String filePath, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error writing file: " + filePath);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Append satu baris ke file
     */
    public static boolean appendLine(String filePath, String line) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(line);
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.err.println("Error appending to file: " + filePath);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hapus file dan buat ulang (clear data)
     */
    public static boolean clearFile(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
            createFileIfNotExists(filePath);
            return true;
        } catch (IOException e) {
            System.err.println("Error clearing file: " + filePath);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check apakah file exists
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
}