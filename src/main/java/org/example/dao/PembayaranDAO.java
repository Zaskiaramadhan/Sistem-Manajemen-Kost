package org.example.dao;

import org.example.model.Pembayaran;
import org.example.util.FileHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object untuk Pembayaran
 */
public class PembayaranDAO {

    private static PembayaranDAO instance;
    private List<Pembayaran> pembayaranList;

    private PembayaranDAO() {
        pembayaranList = new ArrayList<>();
        loadFromFile();
    }

    public static PembayaranDAO getInstance() {
        if (instance == null) {
            instance = new PembayaranDAO();
        }
        return instance;
    }

    /**
     * CREATE - Tambah pembayaran baru
     */
    public boolean create(Pembayaran pembayaran) {
        try {
            pembayaranList.add(pembayaran);
            return saveToFile();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * READ - Get semua pembayaran
     */
    public List<Pembayaran> getAll() {
        return new ArrayList<>(pembayaranList);
    }

    /**
     * READ - Get by ID
     */
    public Pembayaran getById(String idPembayaran) {
        return pembayaranList.stream()
                .filter(p -> p.getIdPembayaran().equals(idPembayaran))
                .findFirst()
                .orElse(null);
    }

    /**
     * READ - Get by penyewa
     */
    public List<Pembayaran> getByPenyewa(String idPenyewa) {
        return pembayaranList.stream()
                .filter(p -> p.getIdPenyewa().equals(idPenyewa))
                .collect(Collectors.toList());
    }

    /**
     * READ - Get by bulan-tahun
     */
    public List<Pembayaran> getByMonthYear(String bulanTahun) {
        return pembayaranList.stream()
                .filter(p -> p.getBulanTahun().equals(bulanTahun))
                .collect(Collectors.toList());
    }

    /**
     * READ - Get pembayaran lunas bulan ini
     */
    public List<Pembayaran> getPaidThisMonth(String bulanTahun) {
        return pembayaranList.stream()
                .filter(p -> p.getBulanTahun().equals(bulanTahun) && "Lunas".equals(p.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * UPDATE - Update pembayaran
     */
    public boolean update(Pembayaran updatedPembayaran) {
        try {
            for (int i = 0; i < pembayaranList.size(); i++) {
                if (pembayaranList.get(i).getIdPembayaran().equals(updatedPembayaran.getIdPembayaran())) {
                    pembayaranList.set(i, updatedPembayaran);
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
     * DELETE - Hapus pembayaran
     */
    public boolean delete(String idPembayaran) {
        try {
            boolean removed = pembayaranList.removeIf(p -> p.getIdPembayaran().equals(idPembayaran));
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
     * Generate ID pembayaran baru
     */
    public String generateNewId() {
        if (pembayaranList.isEmpty()) {
            return "PAY001";
        }

        int maxNum = pembayaranList.stream()
                .map(p -> p.getIdPembayaran().substring(3))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);

        return String.format("PAY%03d", maxNum + 1);
    }

    /**
     * Get total pemasukan bulan tertentu
     */
    public double getTotalIncomeByMonth(String bulanTahun) {
        return pembayaranList.stream()
                .filter(p -> p.getBulanTahun().equals(bulanTahun) && "Lunas".equals(p.getStatus()))
                .mapToDouble(Pembayaran::getJumlah)
                .sum();
    }

    /**
     * Check apakah penyewa sudah bayar bulan ini
     */
    public boolean isPaid(String idPenyewa, String bulanTahun) {
        return pembayaranList.stream()
                .anyMatch(p -> p.getIdPenyewa().equals(idPenyewa)
                        && p.getBulanTahun().equals(bulanTahun)
                        && "Lunas".equals(p.getStatus()));
    }

    /**
     * Load dari file
     */
    private void loadFromFile() {
        pembayaranList.clear();
        List<String> lines = FileHandler.readAllLines(FileHandler.PEMBAYARAN_FILE);

        for (String line : lines) {
            Pembayaran pembayaran = Pembayaran.fromFileString(line);
            if (pembayaran != null) {
                pembayaranList.add(pembayaran);
            }
        }

        System.out.println("✅ Loaded " + pembayaranList.size() + " pembayaran from file");
    }

    /**
     * Save ke file
     */
    private boolean saveToFile() {
        List<String> lines = pembayaranList.stream()
                .map(Pembayaran::toFileString)
                .collect(Collectors.toList());

        return FileHandler.writeAllLines(FileHandler.PEMBAYARAN_FILE, lines);
    }

    /**
     * Refresh data
     */
    public void refresh() {
        loadFromFile();
        System.out.println("✅ PembayaranDAO refreshed from file");
    }
}