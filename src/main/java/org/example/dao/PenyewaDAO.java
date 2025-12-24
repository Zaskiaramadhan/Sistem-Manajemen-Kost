package org.example.dao;

import org.example.model.Penyewa;
import org.example.util.FileHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object untuk Penyewa
 */
public class PenyewaDAO {

    private static PenyewaDAO instance;
    private List<Penyewa> penyewaList;

    private PenyewaDAO() {
        penyewaList = new ArrayList<>();
        loadFromFile();
    }

    public static PenyewaDAO getInstance() {
        if (instance == null) {
            instance = new PenyewaDAO();
        }
        return instance;
    }

    /**
     * CREATE - Tambah penyewa baru
     */
    public boolean create(Penyewa penyewa) {
        try {
            penyewaList.add(penyewa);

            // Update status kamar jadi "Terisi"
            KamarDAO.getInstance().getById(penyewa.getIdKamar()).setStatus("Terisi");
            KamarDAO.getInstance().update(KamarDAO.getInstance().getById(penyewa.getIdKamar()));

            return saveToFile();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * READ - Get semua penyewa
     */
    public List<Penyewa> getAll() {
        return new ArrayList<>(penyewaList);
    }

    /**
     * READ - Get penyewa aktif
     */
    public List<Penyewa> getActivePenyewa() {
        return penyewaList.stream()
                .filter(p -> "Aktif".equals(p.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * READ - Get by ID
     */
    public Penyewa getById(String idPenyewa) {
        return penyewaList.stream()
                .filter(p -> p.getIdPenyewa().equals(idPenyewa))
                .findFirst()
                .orElse(null);
    }

    /**
     * READ - Get by kamar
     */
    public Penyewa getByKamar(String idKamar) {
        return penyewaList.stream()
                .filter(p -> p.getIdKamar().equals(idKamar) && "Aktif".equals(p.getStatus()))
                .findFirst()
                .orElse(null);
    }

    /**
     * UPDATE - Update penyewa
     */
    public boolean update(Penyewa updatedPenyewa) {
        try {
            for (int i = 0; i < penyewaList.size(); i++) {
                if (penyewaList.get(i).getIdPenyewa().equals(updatedPenyewa.getIdPenyewa())) {
                    penyewaList.set(i, updatedPenyewa);
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
     * DELETE - Hapus penyewa (set Non-Aktif)
     */
    public boolean delete(String idPenyewa) {
        try {
            Penyewa penyewa = getById(idPenyewa);
            if (penyewa != null) {
                penyewa.setStatus("Non-Aktif");

                // Update status kamar jadi "Tersedia"
                KamarDAO.getInstance().getById(penyewa.getIdKamar()).setStatus("Tersedia");
                KamarDAO.getInstance().update(KamarDAO.getInstance().getById(penyewa.getIdKamar()));

                return update(penyewa);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Generate ID penyewa baru
     */
    public String generateNewId() {
        if (penyewaList.isEmpty()) {
            return "P001";
        }

        int maxNum = penyewaList.stream()
                .map(p -> p.getIdPenyewa().substring(1))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);

        return String.format("P%03d", maxNum + 1);
    }

    /**
     * Get nama penghuni berdasarkan ID kamar
     */
    public String getPenghuniByKamar(String idKamar) {
        return penyewaList.stream()
                .filter(p -> "Aktif".equals(p.getStatus()) && p.getIdKamar().equals(idKamar))
                .map(org.example.model.Penyewa::getNama)
                .findFirst()
                .orElse(null);
    }

    /**
     * Get total penyewa aktif
     */
    public int getTotalActivePenyewa() {
        return (int) penyewaList.stream()
                .filter(p -> "Aktif".equals(p.getStatus()))
                .count();
    }

    /**
     * Load dari file
     */
    private void loadFromFile() {
        penyewaList.clear();
        List<String> lines = FileHandler.readAllLines(FileHandler.PENYEWA_FILE);

        for (String line : lines) {
            Penyewa penyewa = Penyewa.fromFileString(line);
            if (penyewa != null) {
                penyewaList.add(penyewa);
            }
        }

        System.out.println("✅ Loaded " + penyewaList.size() + " penyewa from file");
    }

    /**
     * Save ke file
     */
    private boolean saveToFile() {
        List<String> lines = penyewaList.stream()
                .map(Penyewa::toFileString)
                .collect(Collectors.toList());

        return FileHandler.writeAllLines(FileHandler.PENYEWA_FILE, lines);
    }

    /**
     * Refresh data
     */
    public void refresh() {
        loadFromFile();
    }

    // Tambahkan method ini ke PenyewaDAO.java

}