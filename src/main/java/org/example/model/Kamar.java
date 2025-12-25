package org.example.model;

public class Kamar {
    private String idKamar;
    private String nomorKamar;
    private String tipe;           // Single, Double, VIP
    private double harga;
    private String ukuran;         // ✅ TAMBAHAN (DITARUH DI SINI)
    private String fasilitas;
    private String status;         // Tersedia, Terisi
    private String imagePath;

    public Kamar() {}

    public Kamar(String idKamar, String nomorKamar, String tipe, double harga,
                 String ukuran, String fasilitas, String status, String imagePath) {
        this.idKamar = idKamar;
        this.nomorKamar = nomorKamar;
        this.tipe = tipe;
        this.harga = harga;
        this.ukuran = ukuran;      // ✅ TAMBAHAN
        this.fasilitas = fasilitas;
        this.status = status;
        this.imagePath = imagePath;
    }

    // ===== GETTERS =====
    public String getIdKamar() { return idKamar; }
    public String getNomorKamar() { return nomorKamar; }
    public String getTipe() { return tipe; }
    public double getHarga() { return harga; }
    public String getUkuran() { return ukuran; }   // ✅ TAMBAHAN
    public String getFasilitas() { return fasilitas; }
    public String getStatus() { return status; }
    public String getImagePath() { return imagePath; }

    // ===== SETTERS =====
    public void setIdKamar(String idKamar) { this.idKamar = idKamar; }
    public void setNomorKamar(String nomorKamar) { this.nomorKamar = nomorKamar; }
    public void setTipe(String tipe) { this.tipe = tipe; }
    public void setHarga(double harga) { this.harga = harga; }
    public void setUkuran(String ukuran) { this.ukuran = ukuran; } // ✅ TAMBAHAN
    public void setFasilitas(String fasilitas) { this.fasilitas = fasilitas; }
    public void setStatus(String status) { this.status = status; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    // ===== FORMAT FILE =====
    public String toFileString() {
        return String.format("%s,%s,%s,%.2f,%s,%s,%s,%s",
                idKamar, nomorKamar, tipe, harga, ukuran, fasilitas, status, imagePath);
    }

    // ===== PARSE FILE =====
    public static Kamar fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 8) {
            return new Kamar(
                    parts[0], parts[1], parts[2],
                    Double.parseDouble(parts[3]),
                    parts[4], parts[5], parts[6], parts[7]
            );
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s m²) Rp %.0f",
                nomorKamar, tipe, ukuran, harga);
    }
}
