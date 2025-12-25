🏠 Sistem Manajemen Kost / Boarding House (RUMA)

Sistem Manajemen Kost RUMA adalah aplikasi desktop berbasis Java Swing yang dirancang untuk membantu pengelola kost dalam mengelola data kamar, penyewa, pembayaran, dashboard statistik, serta laporan bulanan secara terstruktur dan efisien.
Aplikasi ini menerapkan konsep Object-Oriented Programming (OOP), arsitektur modular (MVC/Layered), serta penyimpanan data berbasis file sebagai simulasi sistem manajemen kost skala kecil–menengah.

📌 Fitur Utama
🔐 Login & Keamanan Dasar

Halaman login dengan tampilan modern (custom UI).

Validasi input username & password.

Autentikasi sederhana (hardcoded untuk demo).

Redirect otomatis ke Main Dashboard setelah login berhasil.

📊 Dashboard

Ringkasan jumlah kamar:

Total kamar

Kamar terisi

Kamar tersedia

Statistik pembayaran bulan berjalan:

Jumlah penyewa sudah bayar

Jumlah penyewa belum bayar

Total pemasukan

Sistem notifikasi otomatis:

Pengingat jatuh tempo pembayaran

Notifikasi keterlambatan pembayaran

🏠 Manajemen Kamar

Data kamar meliputi:

ID & nomor kamar

Harga

Fasilitas

Status (Terisi / Kosong)

Perhitungan otomatis kamar terisi dan kosong.

👤 Manajemen Penyewa

Data penyewa:

Nama

Nomor telepon

Kamar yang ditempati

Tanggal masuk

Status (Aktif / Keluar)

💰 Manajemen Pembayaran

Pencatatan pembayaran bulanan penyewa.

Status pembayaran:

Lunas

Belum bayar

Perhitungan total pemasukan per bulan.

📈 Laporan & Statistik

Ringkasan bulanan:

Total pemasukan

Penyewa sudah bayar

Penyewa belum bayar

Keterlambatan pembayaran

Persentase hunian kamar

Tabel detail pembayaran.

Grafik pemasukan 6 bulan terakhir (Bar Chart).

Filter laporan berdasarkan bulan & tahun.

Placeholder fitur Export PDF (pengembangan selanjutnya).

🧱 Arsitektur & Struktur Proyek

Struktur folder proyek dirancang modular dan mudah dikembangkan:

src/
└── org.example
    ├── App.java
    ├── component/
    │   ├── RButton.java
    │   └── Sidebar.java
    ├── config/
    │   ├── AppConfig.java
    │   ├── ColorPalette.java
    │   └── FontManager.java
    ├── dao/
    │   ├── KamarDAO.java
    │   ├── PenyewaDAO.java
    │   └── PembayaranDAO.java
    ├── model/
    │   ├── Kamar.java
    │   ├── Penyewa.java
    │   └── Pembayaran.java
    ├── util/
    │   ├── DateUtil.java
    │   ├── FileHandler.java
    │   └── ValidationUtil.java
    └── view/
        ├── LoginView.java
        ├── MainFrame.java
        ├── DashboardPanel.java
        ├── KamarPanel.java
        ├── PenyewaPanel.java
        ├── PembayaranPanel.java
        └── LaporanPanel.java

🧠 Konsep Pemrograman yang Digunakan

Object-Oriented Programming (OOP)

Class & Object

Encapsulation

Separation of Concerns

DAO (Data Access Object Pattern)

MVC / Layered Architecture

Java Swing GUI

Custom UI Components

File Handling (TXT sebagai database sederhana)

Date & Time API (LocalDate, YearMonth)

Event Handling

Layout Manager (BorderLayout, GridLayout, BoxLayout)

🖥️ Teknologi & Library

Java JDK 8+

Java Swing

AWT Graphics2D

ImageIO

Java Time API

Tanpa database eksternal (menggunakan file .pdf)
