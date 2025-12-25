package org.example.model;

/**
 * Model class untuk Kamar
 * Format file: idKamar,nomorKamar,tipe,harga,ukuran,fasilitas,status,imagePath
 */
public class Kamar {

    private String idKamar;
    private String nomorKamar;
    private String tipe;
    private double harga;
    private String ukuran;
    private String fasilitas;
    private String status;
    private String imagePath;

    // Constructor kosong
    public Kamar() {
    }

    // Constructor lengkap
    public Kamar(String idKamar, String nomorKamar, String tipe, double harga,
                 String ukuran, String fasilitas, String status, String imagePath) {
        this.idKamar = idKamar;
        this.nomorKamar = nomorKamar;
        this.tipe = tipe;
        this.harga = harga;
        this.ukuran = ukuran;
        this.fasilitas = fasilitas;
        this.status = status;
        this.imagePath = imagePath;
    }

    // Getters and Setters
    public String getIdKamar() {
        return idKamar;
    }

    public void setIdKamar(String idKamar) {
        this.idKamar = idKamar;
    }

    public String getNomorKamar() {
        return nomorKamar;
    }

    public void setNomorKamar(String nomorKamar) {
        this.nomorKamar = nomorKamar;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public String getUkuran() {
        return ukuran;
    }

    public void setUkuran(String ukuran) {
        this.ukuran = ukuran;
    }

    public String getFasilitas() {
        return fasilitas;
    }

    public void setFasilitas(String fasilitas) {
        this.fasilitas = fasilitas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Convert Kamar object to file string
     * Format: idKamar,nomorKamar,tipe,harga,ukuran,fasilitas,status,imagePath
     */
    public String toFileString() {
        return String.format("%s,%s,%s,%.0f,%s,%s,%s,%s",
                idKamar,
                nomorKamar,
                tipe,
                harga,
                ukuran,
                fasilitas,
                status,
                imagePath != null ? imagePath : "");
    }

    /**
     * Create Kamar object from file string
     * Format: idKamar,nomorKamar,tipe,harga,ukuran,fasilitas,status,imagePath
     */
    public static Kamar fromFileString(String line) {
        try {
            // Split dengan limit untuk handle kasus ada koma di fasilitas
            String[] parts = line.split(",", 8);

            if (parts.length < 7) {
                System.err.println("⚠️ Invalid kamar data (insufficient fields): " + line);
                return null;
            }

            Kamar kamar = new Kamar();
            kamar.setIdKamar(parts[0].trim());
            kamar.setNomorKamar(parts[1].trim());
            kamar.setTipe(parts[2].trim());

            // Parse harga dengan error handling
            try {
                kamar.setHarga(Double.parseDouble(parts[3].trim()));
            } catch (NumberFormatException e) {
                System.err.println("⚠️ Invalid harga format: " + parts[3]);
                kamar.setHarga(0);
            }

            kamar.setUkuran(parts[4].trim());
            kamar.setFasilitas(parts[5].trim());
            kamar.setStatus(parts[6].trim());

            // Image path (optional, bisa kosong)
            if (parts.length >= 8) {
                String imgPath = parts[7].trim();
                kamar.setImagePath(imgPath.isEmpty() ? null : imgPath);
            } else {
                kamar.setImagePath(null);
            }

            return kamar;

        } catch (Exception e) {
            System.err.println("❌ Error parsing kamar data: " + line);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "Kamar{" +
                "idKamar='" + idKamar + '\'' +
                ", nomorKamar='" + nomorKamar + '\'' +
                ", tipe='" + tipe + '\'' +
                ", harga=" + harga +
                ", ukuran='" + ukuran + '\'' +
                ", fasilitas='" + fasilitas + '\'' +
                ", status='" + status + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kamar kamar = (Kamar) o;
        return idKamar != null && idKamar.equals(kamar.idKamar);
    }

    @Override
    public int hashCode() {
        return idKamar != null ? idKamar.hashCode() : 0;
    }
}