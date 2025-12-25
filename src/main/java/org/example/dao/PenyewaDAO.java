package org.example.dao;

import org.example.model.Penyewa;
import org.example.model.Kamar;
import org.example.util.FileHandler;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PenyewaDAO {

    private static PenyewaDAO instance;
    private List<Penyewa> penyewaList;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
            // Validasi: Cek apakah kamar sudah ada penyewa aktif
            Penyewa existingPenyewa = getByKamar(penyewa.getIdKamar());
            if (existingPenyewa != null) {
                System.err.println("❌ ERROR: Kamar " + penyewa.getIdKamar() + " sudah ditempati oleh " + existingPenyewa.getNama());
                return false;
            }

            penyewaList.add(penyewa);

            // Update status kamar jadi "Terisi"
            Kamar kamar = KamarDAO.getInstance().getById(penyewa.getIdKamar());
            if (kamar != null) {
                kamar.setStatus("Terisi");
                KamarDAO.getInstance().update(kamar);
                System.out.println("✅ Kamar " + kamar.getNomorKamar() + " status updated to: Terisi");
            }

            return saveToFile();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * UPDATE - Update penyewa (termasuk pindah kamar)
     */
    public boolean update(Penyewa updatedPenyewa) {
        try {
            Penyewa oldPenyewa = getById(updatedPenyewa.getIdPenyewa());
            if (oldPenyewa == null) {
                return false;
            }

            String oldKamarId = oldPenyewa.getIdKamar();
            String newKamarId = updatedPenyewa.getIdKamar();

            // Jika pindah kamar
            if (!oldKamarId.equals(newKamarId)) {
                // Validasi: Cek apakah kamar baru sudah ada penyewa aktif
                Penyewa existingPenyewa = getByKamar(newKamarId);
                if (existingPenyewa != null && !existingPenyewa.getIdPenyewa().equals(updatedPenyewa.getIdPenyewa())) {
                    System.err.println("❌ ERROR: Kamar baru sudah ditempati oleh " + existingPenyewa.getNama());
                    return false;
                }

                // Update kamar lama jadi "Tersedia"
                Kamar oldKamar = KamarDAO.getInstance().getById(oldKamarId);
                if (oldKamar != null) {
                    oldKamar.setStatus("Tersedia");
                    KamarDAO.getInstance().update(oldKamar);
                    System.out.println("✅ Kamar lama " + oldKamar.getNomorKamar() + " status: Tersedia");
                }

                // Update kamar baru jadi "Terisi"
                Kamar newKamar = KamarDAO.getInstance().getById(newKamarId);
                if (newKamar != null) {
                    newKamar.setStatus("Terisi");
                    KamarDAO.getInstance().update(newKamar);
                    System.out.println("✅ Kamar baru " + newKamar.getNomorKamar() + " status: Terisi");
                }
            }

            // Update data penyewa
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
                Kamar kamar = KamarDAO.getInstance().getById(penyewa.getIdKamar());
                if (kamar != null) {
                    kamar.setStatus("Tersedia");
                    KamarDAO.getInstance().update(kamar);
                    System.out.println("✅ Kamar " + kamar.getNomorKamar() + " status updated to: Tersedia");
                }

                return update(penyewa);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Validasi apakah kamar sudah ditempati
     */
    public boolean isKamarOccupied(String idKamar, String excludePenyewaId) {
        return penyewaList.stream()
                .anyMatch(p -> "Aktif".equals(p.getStatus())
                        && p.getIdKamar().equals(idKamar)
                        && !p.getIdPenyewa().equals(excludePenyewaId));
    }

    public boolean isKamarOccupied(String idKamar) {
        return isKamarOccupied(idKamar, "");
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
                .map(Penyewa::getNama)
                .findFirst()
                .orElse(null);
    }

    /**
     * Get nomor HP penghuni berdasarkan ID kamar
     */
    public String getNomorHPByKamar(String idKamar) {
        return penyewaList.stream()
                .filter(p -> "Aktif".equals(p.getStatus()) && p.getIdKamar().equals(idKamar))
                .map(Penyewa::getNoHp)
                .findFirst()
                .orElse(null);
    }

    /**
     * Get tanggal masuk penghuni berdasarkan ID kamar
     */
    public String getTanggalMasukByKamar(String idKamar) {
        return penyewaList.stream()
                .filter(p -> "Aktif".equals(p.getStatus()) && p.getIdKamar().equals(idKamar))
                .map(p -> p.getTanggalMasuk().format(DATE_FORMAT))
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
}