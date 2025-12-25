🏠 **Sistem Manajemen Kost / Boarding House (RUMA)**

Sistem Manajemen Kost RUMA adalah aplikasi desktop berbasis Java Swing yang dirancang untuk membantu pengelola kost dalam mengelola data kamar, penyewa, pembayaran, dashboard statistik, serta laporan bulanan secara terstruktur dan efisien. Aplikasi ini menerapkan konsep Object-Oriented Programming (OOP), arsitektur modular (MVC/Layered), serta penyimpanan data berbasis file.

___
📌 **Executive Summary**

Sistem Manajemen Rumah Kost menyediakan sebuah platform terpusat untuk mengelola informasi penting yang berkaitan dengan operasional rumah kost. Sistem ini mencakup pengelolaan data kamar, data penyewa, pencatatan pembayaran, serta penyajian laporan. Dengan adanya sistem ini, pengelola kost dapat mengurangi kesalahan pencatatan manual, meningkatkan efisiensi kerja, serta memperoleh data yang lebih terorganisir dan mudah diakses.Dokumentasi ini disusun sebagai panduan utama bagi pengembang maupun pengguna dalam memahami struktur sistem, cara instalasi, konfigurasi, serta arsitektur kode program.

___
⭐ **Fitur Utama Sistem**

Aplikasi ini memiliki beberapa fitur inti yang mendukung operasional rumah kost, antara lain:

Manajemen Kamar
Mengelola data kamar yang tersedia, termasuk nomor kamar, status ketersediaan, dan informasi terkait lainnya.

Manajemen Penyewa
Menyimpan dan mengelola data penyewa seperti identitas, kamar yang ditempati, serta masa sewa.

Manajemen Pembayaran
Mencatat transaksi pembayaran sewa kost dan memastikan data pembayaran tersimpan dengan baik.

Dashboard Informasi
Menampilkan ringkasan data penting seperti jumlah kamar, jumlah penyewa aktif, dan status pembayaran.

Laporan Administratif
Menyediakan laporan data kamar, penyewa, dan pembayaran untuk kebutuhan evaluasi dan dokumentasi.

___

🔐 **Login & Keamanan Dasar**

Halaman login dengan tampilan modern (custom UI).

Validasi input username & password.

Autentikasi sederhana (hardcoded untuk demo).

Redirect otomatis ke Main Dashboard setelah login berhasil.
___
📊 **Dashboard**

Ringkasan jumlah kamar:
• Bullet manual (boleh, tapi tidak standar Markdown)

•Total kamar

•Kamar terisi

•Kamar tersedia

•Statistik pembayaran bulan berjalan:

    •Jumlah penyewa sudah bayar
    
    •Jumlah penyewa belum bayar
    
    •Total pemasukan
    
•Sistem notifikasi otomatis:

    •Pengingat jatuh tempo pembayaran
    
    •Notifikasi keterlambatan pembayaran
___

📁 Project Structure (Source Code)
    src/

└── org.example

    ├── App.java
    
    │
    
    ├── component/
    
    │   ├── RButton.java
    
    │   └── Sidebar.java
    
    │
    
    ├── config/
    
    │   ├── AppConfig.java
    
    │   ├── ColorPalette.java
    
    │   └── FontManager.java
    
    │
    
    ├── dao/
    
    │   ├── KamarDAO.java
    
    │   ├── PenyewaDAO.java
    
    │   └── PembayaranDAO.java
    
    │
    
    ├── model/
    
    │   ├── Kamar.java
    
    │   ├── Penyewa.java
    
    │   └── Pembayaran.java
    
    │
    
    ├── util/
    
    │   ├── DateUtil.java
    
    │   ├── FileHandler.java
    
    │   └── ValidationUtil.java
    
    │
    
    └── view/
    
        ├── LoginView.java
        
        ├── MainFrame.java
        
        ├── DashboardPanel.java
        
        ├── KamarPanel.java
        
        ├── PenyewaPanel.java
        
        ├── PembayaranPanel.java
        
        └── LaporanPanel.java
___


2. Menjalankan Aplikasi

Java Development Kit (JDK) yang terpasang pada sistem.

    javac App.java
    java App
