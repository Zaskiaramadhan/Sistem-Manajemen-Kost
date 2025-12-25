package org.example.util;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * File Handler untuk operasi file I/O
 * Menggunakan atomic write untuk mencegah file corruption
 */
public class FileHandler {

    public static final String DATA_DIR = "data";
    public static final String KAMAR_FILE = "kamar.txt";
    public static final String PENYEWA_FILE = "penyewa.txt";
    public static final String PEMBAYARAN_FILE = "pembayaran.txt";

    static {
        // Buat folder data jika belum ada
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            boolean created = dataDir.mkdirs();
            if (created) {
                System.out.println("✅ Created data directory: " + DATA_DIR);
            } else {
                System.err.println("❌ Failed to create data directory");
            }
        }
    }

    /**
     * Read all lines from file
     */
    public static List<String> readAllLines(String filename) {
        List<String> lines = new ArrayList<>();
        File file = new File(DATA_DIR, filename);

        if (!file.exists()) {
            System.out.println("ℹ️ File not found, returning empty list: " + filename);
            return lines;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
            System.out.println("✅ Read " + lines.size() + " lines from: " + filename);
        } catch (IOException e) {
            System.err.println("❌ Error reading file: " + filename);
            e.printStackTrace();
        }

        return lines;
    }

    /**
     * Write all lines to file (ATOMIC WRITE)
     * Menggunakan temporary file untuk mencegah corruption
     */
    public static boolean writeAllLines(String filename, List<String> lines) {
        File file = new File(DATA_DIR, filename);
        File tempFile = new File(DATA_DIR, filename + ".tmp");
        File backupFile = new File(DATA_DIR, filename + ".bak");

        try {
            // 1. Tulis ke temporary file dulu
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
                writer.flush();
            }

            // 2. Backup file lama (jika ada)
            if (file.exists()) {
                // Hapus backup lama jika ada
                if (backupFile.exists()) {
                    backupFile.delete();
                }
                // Copy file lama ke backup
                Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            // 3. Rename temp file ke file asli (atomic operation)
            Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

            System.out.println("✅ Successfully wrote " + lines.size() + " lines to: " + filename);
            return true;

        } catch (IOException e) {
            System.err.println("❌ Error writing file: " + filename);
            e.printStackTrace();

            // Coba restore dari backup jika ada
            if (backupFile.exists()) {
                try {
                    Files.copy(backupFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("✅ Restored from backup");
                } catch (IOException ex) {
                    System.err.println("❌ Failed to restore from backup");
                    ex.printStackTrace();
                }
            }

            return false;
        } finally {
            // Cleanup temporary file jika masih ada
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * Append a line to file
     */
    public static boolean appendLine(String filename, String line) {
        File file = new File(DATA_DIR, filename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(line);
            writer.newLine();
            writer.flush();
            System.out.println("✅ Appended line to: " + filename);
            return true;
        } catch (IOException e) {
            System.err.println("❌ Error appending to file: " + filename);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if file exists
     */
    public static boolean fileExists(String filename) {
        File file = new File(DATA_DIR, filename);
        return file.exists();
    }

    /**
     * Delete file
     */
    public static boolean deleteFile(String filename) {
        File file = new File(DATA_DIR, filename);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("✅ Deleted file: " + filename);
            } else {
                System.err.println("❌ Failed to delete file: " + filename);
            }
            return deleted;
        }
        return false;
    }

    /**
     * Get full file path
     */
    public static String getFullPath(String filename) {
        File file = new File(DATA_DIR, filename);
        return file.getAbsolutePath();
    }

    public static void initializeDataDirectories() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            boolean created = dataDir.mkdirs();
            if (created) {
                System.out.println("✅ Created data directory: " + DATA_DIR);
            }
        }
    }
}