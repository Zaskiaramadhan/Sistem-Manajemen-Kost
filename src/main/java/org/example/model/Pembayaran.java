package org.example.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Model class untuk Pembayaran
 */
public class Pembayaran {
    private String idPembayaran;
    private String idPenyewa;
    private String bulanTahun;     // Format: Januari 2025
    private LocalDate tanggalBayar;
    private double jumlah;
    private String metodeBayar;    // Cash, Transfer, E-Wallet
    private String status;         // Lunas, Belum Bayar, Terlambat

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Pembayaran() {}

    public Pembayaran(String idPembayaran, String idPenyewa, String bulanTahun,
                      LocalDate tanggalBayar, double jumlah, String metodeBayar, String status) {
        this.idPembayaran = idPembayaran;
        this.idPenyewa = idPenyewa;
        this.bulanTahun = bulanTahun;
        this.tanggalBayar = tanggalBayar;
        this.jumlah = jumlah;
        this.metodeBayar = metodeBayar;
        this.status = status;
    }

    // Getters
    public String getIdPembayaran() { return idPembayaran; }
    public String getIdPenyewa() { return idPenyewa; }
    public String getBulanTahun() { return bulanTahun; }
    public LocalDate getTanggalBayar() { return tanggalBayar; }
    public double getJumlah() { return jumlah; }
    public String getMetodeBayar() { return metodeBayar; }
    public String getStatus() { return status; }

    // Setters
    public void setIdPembayaran(String idPembayaran) { this.idPembayaran = idPembayaran; }
    public void setIdPenyewa(String idPenyewa) { this.idPenyewa = idPenyewa; }
    public void setBulanTahun(String bulanTahun) { this.bulanTahun = bulanTahun; }
    public void setTanggalBayar(LocalDate tanggalBayar) { this.tanggalBayar = tanggalBayar; }
    public void setJumlah(double jumlah) { this.jumlah = jumlah; }
    public void setMetodeBayar(String metodeBayar) { this.metodeBayar = metodeBayar; }
    public void setStatus(String status) { this.status = status; }

    // Format untuk file storage
    public String toFileString() {
        return String.format("%s,%s,%s,%s,%.2f,%s,%s",
                idPembayaran, idPenyewa, bulanTahun,
                tanggalBayar.format(DATE_FORMAT), jumlah, metodeBayar, status);
    }

    // Parse dari file string
    public static Pembayaran fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 7) {
            return new Pembayaran(
                    parts[0], parts[1], parts[2],
                    LocalDate.parse(parts[3], DATE_FORMAT),
                    Double.parseDouble(parts[4]),
                    parts[5], parts[6]
            );
        }
        return null;
    }
}