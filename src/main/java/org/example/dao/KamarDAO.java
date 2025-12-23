package org.example.dao;

import org.example.model.Kamar;
import org.example.util.FileHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object untuk Kamar
 * Menangani CRUD operations
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
     */
    public boolean create(Kamar kamar) {
        try {
            kamarList.add(kamar);
            return saveToFile();
        } catch (Exception e) {
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
     */
    public boolean update(Kamar updatedKamar) {
        try {
            for (int i = 0; i < kamarList.size(); i++) {
                if (kamarList.get(i).getIdKamar().equals(updatedKamar.getIdKamar())) {
                    kamarList.set(i, updatedKamar);
                    return saveToFile();
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * DELETE - Hapus kamar
     */
    public boolean delete(String idKamar) {
        try {
            boolean removed = kamarList.removeIf(k -> k.getIdKamar().equals(idKamar));
            if (removed) {
                return saveToFile();
            }
            return false;
        } catch (Exception e) {
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
     */
    private void loadFromFile() {
        kamarList.clear();
        List<String> lines = FileHandler.readAllLines(FileHandler.KAMAR_FILE);

        for (String line : lines) {
            Kamar kamar = Kamar.fromFileString(line);
            if (kamar != null) {
                kamarList.add(kamar);
            }
        }

        System.out.println("✅ Loaded " + kamarList.size() + " kamar from file");
    }

    /**
     * Save data ke file
     */
    private boolean saveToFile() {
        List<String> lines = kamarList.stream()
                .map(Kamar::toFileString)
                .collect(Collectors.toList());

        return FileHandler.writeAllLines(FileHandler.KAMAR_FILE, lines);
    }

    /**
     * Refresh data dari file
     */
    public void refresh() {
        loadFromFile();
    }
}