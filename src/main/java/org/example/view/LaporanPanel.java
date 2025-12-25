package org.example.view;

import org.example.util.DateUtil;
import org.example.util.FileHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel Laporan & Statistik
 * Menampilkan ringkasan bulanan dan detail pembayaran
 */
public class LaporanPanel extends JPanel {

    // Components
    private JComboBox<String> filterBulanCombo;
    private JLabel totalPemasukanLabel;
    private JLabel sudahBayarLabel;
    private JLabel belumBayarLabel;
    private JLabel terlambatLabel;
    private JLabel kamarTerisiLabel;
    private JLabel kamarKosongLabel;
    private JTable detailTable;
    private DefaultTableModel tableModel;
    private JPanel grafikPanel;

    // Data
    private List<Kamar> kamarList;
    private List<Penyewa> penyewaList;
    private List<Pembayaran> pembayaranList;

    // Filter
    private YearMonth selectedMonth;

    public LaporanPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        initializeData();
        initComponents();
        loadData();
    }

    private void initializeData() {
        kamarList = loadKamarData();
        penyewaList = loadPenyewaData();
        pembayaranList = loadPembayaranData();
        selectedMonth = YearMonth.now();
    }

    private void initComponents() {
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);

        // Ringkasan Panel
        JPanel ringkasanPanel = createRingkasanPanel();
        contentPanel.add(ringkasanPanel, BorderLayout.NORTH);

        // Detail & Grafik Panel
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(Color.WHITE);

        JPanel detailPanel = createDetailPanel();
        bottomPanel.add(detailPanel, BorderLayout.CENTER);

        JPanel grafikWrapperPanel = createGrafikPanel();
        bottomPanel.add(grafikWrapperPanel, BorderLayout.SOUTH);

        contentPanel.add(bottomPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("📊 LAPORAN & STATISTIK");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(52, 73, 94));

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setBackground(Color.WHITE);

        JLabel filterLabel = new JLabel("Filter: ▼");
        filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Generate bulan options (6 bulan terakhir)
        String[] bulanOptions = generateBulanOptions();
        filterBulanCombo = new JComboBox<>(bulanOptions);
        filterBulanCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterBulanCombo.setPreferredSize(new Dimension(150, 30));
        filterBulanCombo.addActionListener(e -> onFilterChanged());

        JButton tampilkanBtn = new JButton("TAMPILKAN");
        tampilkanBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tampilkanBtn.setBackground(new Color(52, 152, 219));
        tampilkanBtn.setForeground(Color.WHITE);
        tampilkanBtn.setFocusPainted(false);
        tampilkanBtn.setBorderPainted(false);
        tampilkanBtn.setPreferredSize(new Dimension(120, 30));
        tampilkanBtn.addActionListener(e -> loadData());

        JButton exportBtn = new JButton("EXPORT PDF");
        exportBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        exportBtn.setBackground(new Color(231, 76, 60));
        exportBtn.setForeground(Color.WHITE);
        exportBtn.setFocusPainted(false);
        exportBtn.setBorderPainted(false);
        exportBtn.setPreferredSize(new Dimension(120, 30));
        exportBtn.addActionListener(e -> exportToPDF());

        filterPanel.add(filterLabel);
        filterPanel.add(filterBulanCombo);
        filterPanel.add(tampilkanBtn);
        filterPanel.add(exportBtn);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(filterPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createRingkasanPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Title
        String bulanTahun = selectedMonth.format(
                DateTimeFormatter.ofPattern("MMMM yyyy", new java.util.Locale("id", "ID"))
        );
        JLabel titleLabel = new JLabel("📈 RINGKASAN BULAN " + bulanTahun.toUpperCase());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Stats Grid
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 20, 10));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Total Pemasukan
        totalPemasukanLabel = createStatLabel("💰", "Total Pemasukan", "Rp 12.000.000",
                new Color(39, 174, 96), new Color(46, 204, 113));

        // Sudah Bayar
        sudahBayarLabel = createStatLabel("✅", "Sudah Bayar", "15 penyewa (100%)",
                new Color(39, 174, 96), new Color(46, 204, 113));

        // Belum Bayar
        belumBayarLabel = createStatLabel("🏆", "Belum Bayar", "0 penyewa",
                new Color(241, 196, 15), new Color(243, 156, 18));

        // Terlambat
        terlambatLabel = createStatLabel("⚠", "Terlambat", "0 penyewa",
                new Color(231, 76, 60), new Color(192, 57, 43));

        // Kamar Terisi
        kamarTerisiLabel = createStatLabel("🏠", "Kamar Terisi", "15 / 20 (75%)",
                new Color(52, 152, 219), new Color(41, 128, 185));

        // Kamar Kosong
        kamarKosongLabel = createStatLabel("🔓", "Kamar Kosong", "5 kamar",
                new Color(149, 165, 166), new Color(127, 140, 141));

        statsPanel.add(totalPemasukanLabel);
        statsPanel.add(sudahBayarLabel);
        statsPanel.add(belumBayarLabel);
        statsPanel.add(terlambatLabel);
        statsPanel.add(kamarTerisiLabel);
        statsPanel.add(kamarKosongLabel);

        panel.add(statsPanel);

        return panel;
    }

    private JLabel createStatLabel(String icon, String title, String value, Color bgColor, Color borderColor) {
        JLabel label = new JLabel();
        label.setLayout(new BorderLayout(5, 5));
        label.setOpaque(true);
        label.setBackground(bgColor);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 2),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLabel.setForeground(Color.WHITE);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueLbl.setForeground(Color.WHITE);
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLbl);
        textPanel.add(valueLbl);

        label.add(iconLabel, BorderLayout.WEST);
        label.add(textPanel, BorderLayout.CENTER);

        return label;
    }

    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                new EmptyBorder(15, 15, 15, 15)
        ));

        String bulanTahun = selectedMonth.format(
                DateTimeFormatter.ofPattern("MMMM yyyy", new java.util.Locale("id", "ID"))
        );
        JLabel titleLabel = new JLabel("📋 DETAIL PEMBAYARAN " + bulanTahun.toUpperCase());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"No", "Nama Penyewa", "No. Kamar", "Tanggal Bayar", "Jumlah", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        detailTable = new JTable(tableModel);
        detailTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailTable.setRowHeight(30);
        detailTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailTable.getTableHeader().setBackground(new Color(52, 73, 94));
        detailTable.getTableHeader().setForeground(Color.WHITE);
        detailTable.setSelectionBackground(new Color(52, 152, 219));
        detailTable.setSelectionForeground(Color.WHITE);
        detailTable.setGridColor(new Color(189, 195, 199));

        // Center alignment untuk kolom tertentu
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        detailTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        detailTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        detailTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        // Set column widths
        detailTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        detailTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        detailTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        detailTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        detailTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        detailTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(detailTable);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createGrafikPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("📊 GRAFIK PEMASUKAN (Opsional)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);

        grafikPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBarChart(g);
            }
        };
        grafikPanel.setBackground(Color.WHITE);
        grafikPanel.setPreferredSize(new Dimension(0, 200));

        panel.add(grafikPanel, BorderLayout.CENTER);

        JLabel noteLabel = new JLabel("[Bar chart 6 bulan terakhir]");
        noteLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        noteLabel.setForeground(Color.GRAY);
        panel.add(noteLabel, BorderLayout.SOUTH);

        return panel;
    }

    private void drawBarChart(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = grafikPanel.getWidth();
        int height = grafikPanel.getHeight();
        int padding = 40;
        int chartHeight = height - 2 * padding;

        // Get 6 bulan terakhir data
        Map<YearMonth, Double> monthlyData = getMonthlyData(6);
        List<YearMonth> months = new ArrayList<>(monthlyData.keySet());

        if (months.isEmpty()) return;

        int barWidth = (width - 2 * padding) / months.size() - 10;
        double maxValue = monthlyData.values().stream().mapToDouble(Double::doubleValue).max().orElse(1000000);

        // Draw bars
        for (int i = 0; i < months.size(); i++) {
            YearMonth month = months.get(i);
            double value = monthlyData.get(month);

            int barHeight = (int) ((value / maxValue) * chartHeight);
            int x = padding + i * (barWidth + 10);
            int y = height - padding - barHeight;

            // Bar
            g2d.setColor(new Color(52, 152, 219));
            g2d.fillRect(x, y, barWidth, barHeight);
            g2d.setColor(new Color(41, 128, 185));
            g2d.drawRect(x, y, barWidth, barHeight);

            // Month label
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            String monthLabel = month.format(DateTimeFormatter.ofPattern("MMM yy", new java.util.Locale("id", "ID")));
            int labelWidth = g2d.getFontMetrics().stringWidth(monthLabel);
            g2d.drawString(monthLabel, x + (barWidth - labelWidth) / 2, height - padding + 15);

            // Value label
            String valueLabel = String.format("%.0fK", value / 1000);
            int valueWidth = g2d.getFontMetrics().stringWidth(valueLabel);
            g2d.drawString(valueLabel, x + (barWidth - valueWidth) / 2, y - 5);
        }

        // Draw axes
        g2d.setColor(Color.GRAY);
        g2d.drawLine(padding, height - padding, width - padding, height - padding); // X-axis
        g2d.drawLine(padding, padding, padding, height - padding); // Y-axis
    }

    private void loadData() {
        // Clear table
        tableModel.setRowCount(0);

        // Calculate statistics
        int totalKamar = kamarList.size();
        int kamarTerisi = 0;
        int sudahBayar = 0;
        int belumBayar = 0;
        int terlambat = 0;
        double totalPemasukan = 0;

        // Filter pembayaran by selected month
        List<Pembayaran> filteredPembayaran = new ArrayList<>();
        for (Pembayaran p : pembayaranList) {
            LocalDate tanggal = p.tanggalBayar;
            if (tanggal != null && YearMonth.from(tanggal).equals(selectedMonth)) {
                filteredPembayaran.add(p);
            }
        }

        // Count kamar terisi
        for (Kamar k : kamarList) {
            if (k.status.equalsIgnoreCase("Terisi")) {
                kamarTerisi++;
            }
        }

        // Process pembayaran
        Map<String, Boolean> penyewaBayar = new HashMap<>();
        for (Pembayaran p : filteredPembayaran) {
            penyewaBayar.put(p.idPenyewa, true);
            totalPemasukan += p.jumlah;
            sudahBayar++;

            // Add to table
            Penyewa penyewa = findPenyewaById(p.idPenyewa);
            String namaPenyewa = penyewa != null ? penyewa.nama : "-";
            String noKamar = penyewa != null ? penyewa.idKamar : "-";

            tableModel.addRow(new Object[]{
                    tableModel.getRowCount() + 1,
                    namaPenyewa,
                    noKamar,
                    DateUtil.formatDate(p.tanggalBayar),
                    String.format("Rp %,.0f", p.jumlah),
                    "✅ Lunas"
            });
        }

        // Check untuk penyewa yang belum bayar
        for (Penyewa penyewa : penyewaList) {
            if (!penyewaBayar.containsKey(penyewa.idPenyewa)) {
                belumBayar++;
                // Check if late (lebih dari 5 hari dari tanggal 1)
                LocalDate deadline = selectedMonth.atDay(1).plusDays(5);
                if (LocalDate.now().isAfter(deadline)) {
                    terlambat++;
                }
            }
        }

        int kamarKosong = totalKamar - kamarTerisi;

        // Update labels
        updateStatLabel(totalPemasukanLabel, "💰", "Total Pemasukan",
                String.format("Rp %,.0f", totalPemasukan));

        updateStatLabel(sudahBayarLabel, "✅", "Sudah Bayar",
                String.format("%d penyewa (%.0f%%)", sudahBayar,
                        kamarTerisi > 0 ? (sudahBayar * 100.0 / kamarTerisi) : 0));

        updateStatLabel(belumBayarLabel, "🏆", "Belum Bayar",
                String.format("%d penyewa", belumBayar));

        updateStatLabel(terlambatLabel, "⚠", "Terlambat",
                String.format("%d penyewa", terlambat));

        updateStatLabel(kamarTerisiLabel, "🏠", "Kamar Terisi",
                String.format("%d / %d (%.0f%%)", kamarTerisi, totalKamar,
                        totalKamar > 0 ? (kamarTerisi * 100.0 / totalKamar) : 0));

        updateStatLabel(kamarKosongLabel, "🔓", "Kamar Kosong",
                String.format("%d kamar", kamarKosong));

        // Refresh grafik
        grafikPanel.repaint();
    }

    private void updateStatLabel(JLabel label, String icon, String title, String value) {
        label.removeAll();

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLabel.setForeground(Color.WHITE);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueLbl.setForeground(Color.WHITE);
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLbl);
        textPanel.add(valueLbl);

        label.add(iconLabel, BorderLayout.WEST);
        label.add(textPanel, BorderLayout.CENTER);

        label.revalidate();
        label.repaint();
    }

    private void onFilterChanged() {
        int index = filterBulanCombo.getSelectedIndex();
        selectedMonth = YearMonth.now().minusMonths(index);
    }

    private String[] generateBulanOptions() {
        String[] options = new String[12];
        YearMonth current = YearMonth.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", new java.util.Locale("id", "ID"));

        for (int i = 0; i < 12; i++) {
            YearMonth month = current.minusMonths(i);
            options[i] = month.format(formatter);
        }

        return options;
    }

    private Map<YearMonth, Double> getMonthlyData(int months) {
        Map<YearMonth, Double> data = new HashMap<>();
        YearMonth current = YearMonth.now();

        for (int i = months - 1; i >= 0; i--) {
            YearMonth month = current.minusMonths(i);
            double total = 0;

            for (Pembayaran p : pembayaranList) {
                LocalDate tanggal = p.tanggalBayar;
                if (tanggal != null && YearMonth.from(tanggal).equals(month)) {
                    total += p.jumlah;
                }
            }

            data.put(month, total);
        }

        return data;
    }

    private void exportToPDF() {
        JOptionPane.showMessageDialog(this,
                "Fitur export PDF akan segera ditambahkan!",
                "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Data loading methods
    private List<Kamar> loadKamarData() {
        List<Kamar> list = new ArrayList<>();
        List<String> lines = FileHandler.readAllLines(FileHandler.KAMAR_FILE);

        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 5) {
                Kamar k = new Kamar();
                k.idKamar = parts[0];
                k.nomorKamar = parts[1];
                k.harga = Double.parseDouble(parts[2]);
                k.fasilitas = parts[3];
                k.status = parts[4];
                list.add(k);
            }
        }

        return list;
    }

    private List<Penyewa> loadPenyewaData() {
        List<Penyewa> list = new ArrayList<>();
        List<String> lines = FileHandler.readAllLines(FileHandler.PENYEWA_FILE);

        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 6) {
                Penyewa p = new Penyewa();
                p.idPenyewa = parts[0];
                p.nama = parts[1];
                p.noTelepon = parts[2];
                p.idKamar = parts[3];
                p.tanggalMasuk = DateUtil.parseDate(parts[4]);
                p.status = parts[5];
                list.add(p);
            }
        }

        return list;
    }

    private List<Pembayaran> loadPembayaranData() {
        List<Pembayaran> list = new ArrayList<>();
        List<String> lines = FileHandler.readAllLines(FileHandler.PEMBAYARAN_FILE);

        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 5) {
                Pembayaran p = new Pembayaran();
                p.idPembayaran = parts[0];
                p.idPenyewa = parts[1];
                p.tanggalBayar = DateUtil.parseDate(parts[2]);
                p.jumlah = Double.parseDouble(parts[3]);
                p.status = parts[4];
                list.add(p);
            }
        }

        return list;
    }

    private Penyewa findPenyewaById(String id) {
        for (Penyewa p : penyewaList) {
            if (p.idPenyewa.equals(id)) {
                return p;
            }
        }
        return null;
    }

    public void refresh() {
        initializeData();
        loadData();
    }

    public void refreshData() {
        initializeData();
        loadData();
    }

    // ============= INNER CLASSES - MODEL =============

    /**
     * Model class untuk Kamar
     */
    private static class Kamar {
        String idKamar;
        String nomorKamar;
        double harga;
        String fasilitas;
        String status; // "Terisi" atau "Kosong"
    }

    /**
     * Model class untuk Penyewa
     */
    private static class Penyewa {
        String idPenyewa;
        String nama;
        String noTelepon;
        String idKamar;
        LocalDate tanggalMasuk;
        String status; // "Aktif" atau "Keluar"
    }

    /**
     * Model class untuk Pembayaran
     */
    private static class Pembayaran {
        String idPembayaran;
        String idPenyewa;
        LocalDate tanggalBayar;
        double jumlah;
        String status; // "Lunas" atau "Belum"
    }
}