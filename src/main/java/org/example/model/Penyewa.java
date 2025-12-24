package org.example.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Model class untuk Penyewa
 */
public class Penyewa {
    private String idPenyewa;
    private String nama;
    private String noHp;
    private String email;
    private String idKamar;
    private LocalDate tanggalMasuk;
    private String status;         // Aktif, Non-Aktif

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Penyewa() {}

    public Penyewa(String idPenyewa, String nama, String noHp, String email,
                   String idKamar, LocalDate tanggalMasuk, String status) {
        this.idPenyewa = idPenyewa;
        this.nama = nama;
        this.noHp = noHp;
        this.email = email;
        this.idKamar = idKamar;
        this.tanggalMasuk = tanggalMasuk;
        this.status = status;
    }

    // Getters
    public String getIdPenyewa() { return idPenyewa; }
    public String getNama() { return nama; }
    public String getNoHp() { return noHp; }
    public String getEmail() { return email; }
    public String getIdKamar() { return idKamar; }
    public LocalDate getTanggalMasuk() { return tanggalMasuk; }
    public String getStatus() { return status; }

    // Setters
    public void setIdPenyewa(String idPenyewa) { this.idPenyewa = idPenyewa; }
    public void setNama(String nama) { this.nama = nama; }
    public void setNoHp(String noHp) { this.noHp = noHp; }
    public void setEmail(String email) { this.email = email; }
    public void setIdKamar(String idKamar) { this.idKamar = idKamar; }
    public void setTanggalMasuk(LocalDate tanggalMasuk) { this.tanggalMasuk = tanggalMasuk; }
    public void setStatus(String status) { this.status = status; }

    // Format untuk file storage
    public String toFileString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s",
                idPenyewa, nama, noHp, email, idKamar,
                tanggalMasuk.format(DATE_FORMAT), status);
    }

    // Parse dari file string
    public static Penyewa fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 7) {
            return new Penyewa(
                    parts[0], parts[1], parts[2], parts[3], parts[4],
                    LocalDate.parse(parts[5], DATE_FORMAT),
                    parts[6]
            );
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", idPenyewa, nama);
    }

    
}