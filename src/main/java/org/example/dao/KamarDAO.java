package org.example.dao;

import org.example.model.Kamar;
import org.example.util.FileHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object untuk Kamar
 * Menangani CRUD operations dengan sinkronisasi otomatis ke file
 */
public class KamarDAO {

    private static KamarDAO instance;
    private List<Kamar> kamarList;

    private KamarDAO() {
        kamarList = new ArrayList<>();
        loadFromFile();
    }

    public static KamarDAO getInstance() {
        if (instance == null) {
            instance = new KamarDAO();
        }
        return instance;
    }

    /**
     * CREATE - Tambah kamar baru
     * Otomatis menyimpan ke file
     */
    public boolean create(Kamar kamar) {
        try {
            kamarList.add(kamar);
            boolean saved = saveToFile();

            if (saved) {
                System.out.println("✅ Kamar created: " + kamar.getNomorKamar() +
                        " | Status: " + kamar.getStatus() +
                        " | Image: " + kamar.getImagePath());
            } else {
                System.err.println("❌ Failed to save kamar to file");
                kamarList.remove(kamar); // Rollback jika gagal save
            }

            return saved;
        } catch (Exception e) {
            System.err.println("❌ Error creating kamar: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * READ - Get semua kamar
     */
    public List<Kamar> getAll() {
        return new ArrayList<>(kamarList);
    }

    /**
     * READ - Get kamar by ID
     */
    public Kamar getById(String idKamar) {
        return kamarList.stream()
                .filter(k -> k.getIdKamar().equals(idKamar))
                .findFirst()
                .orElse(null);
    }

    /**
     * READ - Get kamar tersedia
     */
    public List<Kamar> getAvailableRooms() {
        return kamarList.stream()
                .filter(k -> "Tersedia".equals(k.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * UPDATE - Update kamar
     * Otomatis menyimpan ke file
     */
    public boolean update(Kamar updatedKamar) {
        try {
            Kamar oldKamar = null;
            int index = -1;

            for (int i = 0; i < kamarList.size(); i++) {
                if (kamarList.get(i).getIdKamar().equals(updatedKamar.getIdKamar())) {
                    oldKamar = kamarList.get(i);
                    index = i;
                    break;
                }
            }

            if (index == -1) {
                System.err.println("❌ Kamar not found: " + updatedKamar.getIdKamar());
                return false;
            }

            kamarList.set(index, updatedKamar);
            boolean saved = saveToFile();

            if (saved) {
                System.out.println("✅ Kamar updated: " + updatedKamar.getNomorKamar() +
                        " | Status: " + updatedKamar.getStatus() +
                        " | Image: " + updatedKamar.getImagePath());
            } else {
                System.err.println("❌ Failed to save updated kamar");
                kamarList.set(index, oldKamar); // Rollback
            }

            return saved;
        } catch (Exception e) {
            System.err.println("❌ Error updating kamar: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * DELETE - Hapus kamar
     * Otomatis menyimpan ke file
     */
    public boolean delete(String idKamar) {
        try {
            Kamar kamarToDelete = getById(idKamar);
            if (kamarToDelete == null) {
                System.err.println("❌ Kamar not found for deletion: " + idKamar);
                return false;
            }

            boolean removed = kamarList.removeIf(k -> k.getIdKamar().equals(idKamar));

            if (removed) {
                boolean saved = saveToFile();

                if (saved) {
                    System.out.println("✅ Kamar deleted: " + kamarToDelete.getNomorKamar());
                } else {
                    System.err.println("❌ Failed to save after deletion");
                    kamarList.add(kamarToDelete); // Rollback
                    return false;
                }
            }

            return removed;
        } catch (Exception e) {
            System.err.println("❌ Error deleting kamar: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Generate ID kamar baru (auto-increment)
     */
    public String generateNewId() {
        if (kamarList.isEmpty()) {
            return "K001";
        }

        int maxNum = kamarList.stream()
                .map(k -> k.getIdKamar().substring(1))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);

        return String.format("K%03d", maxNum + 1);
    }

    /**
     * Get total kamar
     */
    public int getTotalRooms() {
        return kamarList.size();
    }

    /**
     * Get jumlah kamar terisi
     */
    public int getOccupiedRooms() {
        return (int) kamarList.stream()
                .filter(k -> "Terisi".equals(k.getStatus()))
                .count();
    }

    /**
     * Get jumlah kamar tersedia
     */
    public int getAvailableRoomsCount() {
        return (int) kamarList.stream()
                .filter(k -> "Tersedia".equals(k.getStatus()))
                .count();
    }

    /**
     * Load data dari file
     * Dipanggil saat aplikasi start
     */
    private void loadFromFile() {
        kamarList.clear();
        List<String> lines = FileHandler.readAllLines(FileHandler.KAMAR_FILE);

        System.out.println("📂 Loading kamar from file...");

        for (String line : lines) {
            Kamar kamar = Kamar.fromFileString(line);
            if (kamar != null) {
                kamarList.add(kamar);
            }
        }

        System.out.println("✅ Loaded " + kamarList.size() + " kamar from file");

        // Debug: print semua kamar dengan detail
        if (!kamarList.isEmpty()) {
            System.out.println("\n📋 Kamar List:");
            for (Kamar k : kamarList) {
                System.out.println("   - " + k.getNomorKamar() +
                        " | Status: " + k.getStatus() +
                        " | Image: " + (k.getImagePath() != null && !k.getImagePath().isEmpty() ? k.getImagePath() : "None"));
            }
            System.out.println();
        }
    }

    /**
     * Save data ke file
     * Dipanggil setiap kali ada perubahan data (Create, Update, Delete)
     */
    private boolean saveToFile() {
        List<String> lines = kamarList.stream()
                .map(Kamar::toFileString)
                .collect(Collectors.toList());

        boolean result = FileHandler.writeAllLines(FileHandler.KAMAR_FILE, lines);

        if (result) {
            System.out.println("💾 Successfully saved " + lines.size() + " kamar to file: " + FileHandler.KAMAR_FILE);
        } else {
            System.err.println("❌ Failed to save kamar to file: " + FileHandler.KAMAR_FILE);
        }

        return result;
    }

    /**
     * Refresh data dari file
     * Berguna jika file diubah secara eksternal
     */
    public void refresh() {
        System.out.println("🔄 Refreshing kamar data from file...");
        loadFromFile();
    }

    /**
     * Update status kamar (Tersedia/Terisi)
     */
    public boolean updateStatus(String idKamar, String newStatus) {
        try {
            Kamar kamar = getById(idKamar);
            if (kamar == null) {
                System.err.println("❌ Kamar not found: " + idKamar);
                return false;
            }

            String oldStatus = kamar.getStatus();
            kamar.setStatus(newStatus);

            boolean result = update(kamar);

            if (result) {
                System.out.println("✅ Status updated: " + kamar.getNomorKamar() +
                        " | " + oldStatus + " → " + newStatus);
            }

            return result;
        } catch (Exception e) {
            System.err.println("❌ Error updating status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}