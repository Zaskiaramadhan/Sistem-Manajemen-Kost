package org.example.view;

import org.example.model.Kamar;
import org.example.model.Pembayaran;
import org.example.model.Penyewa;
import org.example.util.DateUtil;
import org.example.util.FileHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class LaporanPanel extends JPanel {

    private JComboBox<String> filterBulanCombo;
    private JPanel totalPemasukanLabel;
    private JPanel sudahBayarLabel;
    private JPanel belumBayarLabel;
    private JPanel terlambatLabel;
    private JPanel kamarTerisiLabel;
    private JPanel tingkatOkupasiLabel;
    private JTable detailTable;
    private DefaultTableModel tableModel;
    private JPanel grafikPanel;
    private JLabel trendLabel;
    private JLabel proyeksiLabel;

    private List<Kamar> kamarList;
    private List<Penyewa> penyewaList;
    private List<Pembayaran> pembayaranList;
    private YearMonth selectedMonth;

    private static final Locale LOCALE_ID = new Locale("id", "ID");

    public LaporanPanel() {
        setLayout(new BorderLayout(15, 15));
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
        add(createHeaderPanel(), BorderLayout.NORTH);
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(createRingkasanPanel(), BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(createDetailPanel());
        bottomPanel.add(createGrafikPanel());
        contentPanel.add(bottomPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("LAPORAN & STATISTIK");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(Color.WHITE);

        JLabel periodeLbl = new JLabel("Periode:");
        periodeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterPanel.add(periodeLbl);

        String[] bulanOptions = generateBulanOptions();
        filterBulanCombo = new JComboBox<>(bulanOptions);
        filterBulanCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterBulanCombo.setPreferredSize(new Dimension(160, 35));
        filterBulanCombo.addActionListener(e -> onFilterChanged());
        filterPanel.add(filterBulanCombo);

        JButton tampilkanBtn = createButton("TAMPILKAN", new Color(52, 152, 219));
        tampilkanBtn.addActionListener(e -> loadData());
        filterPanel.add(tampilkanBtn);

        JButton refreshBtn = createButton("REFRESH", new Color(46, 204, 113));
        refreshBtn.addActionListener(e -> refresh());
        filterPanel.add(refreshBtn);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(filterPanel, BorderLayout.EAST);
        return panel;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(120, 35));
        return btn;
    }

    private JPanel createRingkasanPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                new EmptyBorder(20, 20, 20, 20)));

        String bulanTahun = selectedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", LOCALE_ID));
        JLabel titleLabel = new JLabel("RINGKASAN BULAN " + bulanTahun.toUpperCase());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 20, 15));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        totalPemasukanLabel = createStatCard("💰", "Total Pemasukan", "Rp 0", new Color(39, 174, 96));
        sudahBayarLabel = createStatCard("✅", "Sudah Bayar", "0", new Color(52, 152, 219));
        belumBayarLabel = createStatCard("⏳", "Belum Bayar", "0", new Color(241, 196, 15));
        terlambatLabel = createStatCard("⚠️", "Terlambat", "0", new Color(231, 76, 60));
        kamarTerisiLabel = createStatCard("🏠", "Kamar Terisi", "0/0", new Color(155, 89, 182));
        tingkatOkupasiLabel = createStatCard("📊", "Okupasi", "0%", new Color(52, 73, 94));

        statsPanel.add(totalPemasukanLabel);
        statsPanel.add(sudahBayarLabel);
        statsPanel.add(belumBayarLabel);
        statsPanel.add(terlambatLabel);
        statsPanel.add(kamarTerisiLabel);
        statsPanel.add(tingkatOkupasiLabel);
        panel.add(statsPanel);
        return panel;
    }

    private JPanel createStatCard(String icon, String title, String value, Color bg) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(bg);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLbl.setForeground(Color.WHITE);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLbl.setForeground(new Color(255, 255, 255, 200));

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLbl.setForeground(Color.WHITE);

        textPanel.add(titleLbl);
        textPanel.add(valueLbl);
        card.add(iconLbl, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        card.putClientProperty("valueLabel", valueLbl);
        return card;
    }

    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                new EmptyBorder(15, 15, 15, 15)));

        String bulanTahun = selectedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", LOCALE_ID));
        JLabel titleLabel = new JLabel("DETAIL PEMBAYARAN " + bulanTahun.toUpperCase());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"No", "Nama", "Kamar", "Tgl Bayar", "Jumlah", "Metode", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        detailTable = new JTable(tableModel);
        detailTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailTable.setRowHeight(35);
        detailTable.setShowGrid(true);
        detailTable.setGridColor(new Color(220, 220, 220));
        detailTable.setBackground(Color.WHITE);

        // PERBAIKAN HEADER - Background gelap dengan text putih tebal
        JTableHeader header = detailTable.getTableHeader();
        header.setBackground(new Color(44, 62, 80)); // Warna gelap
        header.setForeground(Color.WHITE); // Text putih
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 45));
        header.setReorderingAllowed(false);
        header.setOpaque(true);

        // Custom renderer untuk header agar pasti terlihat
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value != null ? value.toString() : "");
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setBackground(new Color(44, 62, 80));
                label.setForeground(Color.WHITE);
                label.setOpaque(true);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 1, new Color(189, 195, 199)),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                return label;
            }
        };

        for (int i = 0; i < detailTable.getColumnCount(); i++) {
            detailTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < 7; i++) {
            if (i != 1) detailTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        detailTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        JScrollPane scroll = new JScrollPane(detailTable);
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createGrafikPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel titleLabel = new JLabel("GRAFIK PEMASUKAN 6 BULAN TERAKHIR");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        panel.add(titleLabel, BorderLayout.NORTH);

        grafikPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBarChart(g);
            }
        };
        grafikPanel.setBackground(Color.WHITE);
        grafikPanel.setPreferredSize(new Dimension(0, 250));
        panel.add(grafikPanel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBackground(Color.WHITE);
        trendLabel = new JLabel("Trend: Stabil");
        trendLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        proyeksiLabel = new JLabel("Proyeksi: Rp 0");
        proyeksiLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        infoPanel.add(trendLabel);
        infoPanel.add(proyeksiLabel);
        panel.add(infoPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void drawBarChart(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = grafikPanel.getWidth();
        int h = grafikPanel.getHeight();
        if (w < 100 || h < 100) return;

        int pad = 50;
        int chartH = h - pad * 2;
        int chartW = w - pad * 2;

        Map<YearMonth, Double> data = getMonthlyData(6);
        List<YearMonth> months = new ArrayList<>(data.keySet());
        Collections.sort(months);

        if (months.isEmpty()) {
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            String msg = "Belum ada data pembayaran";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(msg, (w - fm.stringWidth(msg))/2, h/2);
            return;
        }

        double maxVal = Collections.max(data.values());
        if (maxVal == 0) maxVal = 1000000;
        maxVal = Math.ceil(maxVal / 1000000) * 1000000;

        // Grid
        g2d.setColor(new Color(240, 240, 240));
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        for (int i = 0; i <= 5; i++) {
            int y = h - pad - (chartH * i / 5);
            g2d.drawLine(pad, y, w - pad, y);
            String label = String.format("%.0fK", (maxVal * i / 5) / 1000);
            g2d.setColor(Color.GRAY);
            g2d.drawString(label, 5, y + 3);
            g2d.setColor(new Color(240, 240, 240));
        }

        // Bars
        int barW = Math.min(50, chartW / months.size() - 10);
        int gap = (chartW - barW * months.size()) / (months.size() + 1);

        for (int i = 0; i < months.size(); i++) {
            double val = data.get(months.get(i));
            int barH = (int)((val / maxVal) * chartH);
            int x = pad + gap + i * (barW + gap);
            int y = h - pad - barH;

            GradientPaint gp = new GradientPaint(x, y, new Color(52, 152, 219), x, y + barH, new Color(41, 128, 185));
            g2d.setPaint(gp);
            g2d.fillRoundRect(x, y, barW, barH, 5, 5);

            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 9));
            String monthLbl = months.get(i).format(DateTimeFormatter.ofPattern("MMM yy", LOCALE_ID));
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(monthLbl, x + (barW - fm.stringWidth(monthLbl))/2, h - pad + 15);

            if (val > 0) {
                String valLbl = String.format("%.0fK", val / 1000);
                g2d.setColor(barH > 20 ? Color.WHITE : new Color(52, 152, 219));
                g2d.drawString(valLbl, x + (barW - fm.stringWidth(valLbl))/2, barH > 20 ? y + 15 : y - 5);
            }
        }

        // Axes
        g2d.setColor(new Color(44, 62, 80));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(pad, h - pad, w - pad, h - pad);
        g2d.drawLine(pad, pad, pad, h - pad);
    }

    private void loadData() {
        initializeData();
        tableModel.setRowCount(0);

        int totalKamar = kamarList.size();

        // PERBAIKAN ALGORITMA: Hanya hitung penyewa AKTIF dan unique berdasarkan idPenyewa
        Map<String, Penyewa> penyewaAktifMap = new HashMap<>();
        for (Penyewa p : penyewaList) {
            if ("Aktif".equalsIgnoreCase(p.getStatus())) {
                // Ambil data terakhir jika ada duplicate idPenyewa (pindah kamar)
                penyewaAktifMap.put(p.getIdPenyewa(), p);
            }
        }

        int kamarTerisi = penyewaAktifMap.size(); // Hitung unique penyewa aktif
        int sudahBayar = 0;
        int belumBayar = 0;
        int terlambat = 0;
        double totalPemasukan = 0;

        // Process pembayaran
        Map<String, Pembayaran> penyewaBayarMap = new HashMap<>();
        for (Pembayaran pb : pembayaranList) {
            if (pb.getTanggalBayar() != null && YearMonth.from(pb.getTanggalBayar()).equals(selectedMonth)) {
                if ("Lunas".equalsIgnoreCase(pb.getStatus())) {
                    penyewaBayarMap.put(pb.getIdPenyewa(), pb);
                    totalPemasukan += pb.getJumlah();
                    sudahBayar++;

                    Penyewa penyewa = penyewaAktifMap.get(pb.getIdPenyewa());
                    if (penyewa != null) {
                        tableModel.addRow(new Object[]{
                                tableModel.getRowCount() + 1,
                                penyewa.getNama(),
                                penyewa.getIdKamar(),
                                DateUtil.formatDate(pb.getTanggalBayar()),
                                String.format("Rp %,.0f", pb.getJumlah()),
                                pb.getMetodeBayar(),
                                "Lunas"
                        });
                    }
                }
            }
        }

        // Check penyewa aktif yang belum bayar
        LocalDate deadline = selectedMonth.atDay(5);
        LocalDate now = LocalDate.now();

        for (Map.Entry<String, Penyewa> entry : penyewaAktifMap.entrySet()) {
            String idPenyewa = entry.getKey();
            Penyewa penyewa = entry.getValue();

            if (!penyewaBayarMap.containsKey(idPenyewa)) {
                belumBayar++;

                boolean isLate = selectedMonth.isBefore(YearMonth.now()) ||
                        (selectedMonth.equals(YearMonth.now()) && now.isAfter(deadline));
                if (isLate) terlambat++;

                tableModel.addRow(new Object[]{
                        tableModel.getRowCount() + 1,
                        penyewa.getNama(),
                        penyewa.getIdKamar(),
                        "-", "-", "-",
                        isLate ? "Terlambat" : "Belum"
                });
            }
        }

        double okupasi = totalKamar > 0 ? (kamarTerisi * 100.0 / totalKamar) : 0;

        updateCard(totalPemasukanLabel, String.format("Rp %,.0f", totalPemasukan));
        updateCard(sudahBayarLabel, sudahBayar + " penyewa");
        updateCard(belumBayarLabel, belumBayar + " penyewa");
        updateCard(terlambatLabel, terlambat + " penyewa");
        updateCard(kamarTerisiLabel, kamarTerisi + " / " + totalKamar);
        updateCard(tingkatOkupasiLabel, String.format("%.1f%%", okupasi));

        updateTrend();
        grafikPanel.repaint();
    }

    private void updateCard(JPanel card, String val) {
        JLabel lbl = (JLabel) card.getClientProperty("valueLabel");
        if (lbl != null) lbl.setText(val);
    }

    private void updateTrend() {
        Map<YearMonth, Double> data = getMonthlyData(6);
        List<YearMonth> months = new ArrayList<>(data.keySet());
        Collections.sort(months);

        if (months.size() >= 2) {
            double now = data.get(months.get(months.size() - 1));
            double prev = data.get(months.get(months.size() - 2));
            double change = prev > 0 ? ((now - prev) / prev * 100) : 0;

            String trend = change > 5 ? "Naik " + String.format("%.1f%%", change) :
                    change < -5 ? "Turun " + String.format("%.1f%%", Math.abs(change)) : "Stabil";
            trendLabel.setText("Trend: " + trend);
        } else {
            trendLabel.setText("Trend: Stabil");
        }

        double avg = data.values().stream().mapToDouble(d -> d).average().orElse(0);
        proyeksiLabel.setText(String.format("Proyeksi: Rp %,.0f", avg));
    }

    private void onFilterChanged() {
        selectedMonth = YearMonth.now().minusMonths(filterBulanCombo.getSelectedIndex());
    }

    private String[] generateBulanOptions() {
        String[] opts = new String[12];
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMMM yyyy", LOCALE_ID);
        for (int i = 0; i < 12; i++) {
            opts[i] = YearMonth.now().minusMonths(i).format(fmt);
        }
        return opts;
    }

    private Map<YearMonth, Double> getMonthlyData(int months) {
        Map<YearMonth, Double> map = new LinkedHashMap<>();
        YearMonth now = YearMonth.now();
        for (int i = months - 1; i >= 0; i--) {
            YearMonth m = now.minusMonths(i);
            double total = 0;
            for (Pembayaran p : pembayaranList) {
                if (p.getTanggalBayar() != null && YearMonth.from(p.getTanggalBayar()).equals(m)) {
                    if ("Lunas".equalsIgnoreCase(p.getStatus())) {
                        total += p.getJumlah();
                    }
                }
            }
            map.put(m, total);
        }
        return map;
    }

    private List<Kamar> loadKamarData() {
        List<Kamar> list = new ArrayList<>();
        List<String> lines = FileHandler.readAllLines(FileHandler.KAMAR_FILE);

        for (String line : lines) {
            try {
                // Parse format: K001,K01,Double,3000000,9 x 9 meter,...
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    Kamar k = new Kamar();
                    k.setIdKamar(parts[0].trim());
                    k.setNomorKamar(parts[1].trim());
                    k.setTipe(parts[2].trim());
                    k.setHarga(Double.parseDouble(parts[3].trim()));
                    k.setUkuran(parts[4].trim());
                    k.setFasilitas(parts[5].trim());
                    k.setStatus(parts[6].trim());
                    if (parts.length > 7) {
                        k.setImagePath(parts[parts.length - 1].trim());
                    }
                    list.add(k);
                }
            } catch (Exception e) {
                System.err.println("Error parsing kamar: " + line);
            }
        }

        System.out.println("Loaded " + list.size() + " kamar");
        return list;
    }

    private List<Penyewa> loadPenyewaData() {
        List<Penyewa> list = new ArrayList<>();
        for (String line : FileHandler.readAllLines(FileHandler.PENYEWA_FILE)) {
            Penyewa p = Penyewa.fromFileString(line);
            if (p != null) list.add(p);
        }
        System.out.println("Loaded " + list.size() + " penyewa records");
        return list;
    }

    private List<Pembayaran> loadPembayaranData() {
        List<Pembayaran> list = new ArrayList<>();
        for (String line : FileHandler.readAllLines(FileHandler.PEMBAYARAN_FILE)) {
            Pembayaran p = Pembayaran.fromFileString(line);
            if (p != null) list.add(p);
        }
        System.out.println("Loaded " + list.size() + " pembayaran");
        return list;
    }

    public void refresh() {
        initializeData();
        loadData();
    }

    public void refreshData() {
        refresh();
    }

    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            setHorizontalAlignment(CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            if (!isSelected) {
                String s = value.toString();
                if (s.contains("Lunas")) {
                    setBackground(new Color(46, 204, 113, 80));
                    setForeground(new Color(39, 174, 96));
                } else if (s.contains("Terlambat")) {
                    setBackground(new Color(231, 76, 60, 80));
                    setForeground(new Color(192, 57, 43));
                } else {
                    setBackground(new Color(241, 196, 15, 80));
                    setForeground(new Color(243, 156, 18));
                }
            }
            return c;
        }
    }
}