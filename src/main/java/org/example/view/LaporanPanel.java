package org.example.view;

import org.example.config.ColorPalette;
import org.example.config.FontManager;
import org.example.config.AppConfig;
import org.example.component.RButton;
import org.example.model.Pembayaran;
import org.example.model.Penyewa;
import org.example.dao.PembayaranDAO;
import org.example.dao.PenyewaDAO;
import org.example.dao.KamarDAO;
import org.example.util.DateUtil;
import org.example.util.FileHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Laporan Panel - Laporan dan Statistik
 */
public class LaporanPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private PembayaranDAO pembayaranDAO;
    private PenyewaDAO penyewaDAO;
    private KamarDAO kamarDAO;

    private JLabel totalPemasukanLabel;
    private JLabel sudahBayarLabel;
    private JLabel belumBayarLabel;
    private JLabel kamarTerisiLabel;
    private JLabel tingkatHunianLabel;

    private JComboBox<String> bulanCombo;
    private JTextField tahunField;

    public LaporanPanel() {
        pembayaranDAO = PembayaranDAO.getInstance();
        penyewaDAO = PenyewaDAO.getInstance();
        kamarDAO = KamarDAO.getInstance();
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(ColorPalette.BG_OFF_WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(ColorPalette.BG_OFF_WHITE);

        centerPanel.add(createFilterPanel());
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createSummaryPanel());
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createTablePanel());

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorPalette.BG_OFF_WHITE);

        JLabel titleLabel = new JLabel("📊 Laporan & Statistik");
        titleLabel.setFont(FontManager.FONT_H1);
        titleLabel.setForeground(ColorPalette.NAVY_DARK);

        panel.add(titleLabel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panel.setBackground(ColorPalette.BG_OFF_WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel filterLabel = new JLabel("Filter Periode:");
        filterLabel.setFont(FontManager.FONT_H4);
        filterLabel.setForeground(ColorPalette.NAVY_DARK);

        bulanCombo = new JComboBox<>(new String[]{
                "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        });
        bulanCombo.setFont(FontManager.FONT_BODY);
        bulanCombo.setPreferredSize(new Dimension(150, 35));

        tahunField = new JTextField(String.valueOf(LocalDate.now().getYear()));
        tahunField.setFont(FontManager.FONT_BODY);
        tahunField.setPreferredSize(new Dimension(80, 35));
        tahunField.setBorder(AppConfig.createInputBorder());

        RButton tampilkanButton = new RButton("📊 Tampilkan");
        tampilkanButton.addActionListener(e -> refreshData());

        RButton exportButton = new RButton("📄 Export TXT", RButton.ButtonType.SECONDARY);
        exportButton.addActionListener(e -> exportToTxt());

        panel.add(filterLabel);
        panel.add(bulanCombo);
        panel.add(tahunField);
        panel.add(tampilkanButton);
        panel.add(exportButton);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(AppConfig.createCardBorder());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        JLabel titleLabel = new JLabel("📈 Ringkasan");
        titleLabel.setFont(FontManager.FONT_H3);
        titleLabel.setForeground(ColorPalette.NAVY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));

        JPanel statsGrid = new JPanel(new GridLayout(5, 2, 15, 15));
        statsGrid.setBackground(Color.WHITE);
        statsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        totalPemasukanLabel = createValueLabel("Rp 0");
        sudahBayarLabel = createValueLabel("0 orang");
        belumBayarLabel = createValueLabel("0 orang");
        kamarTerisiLabel = createValueLabel("0 / 0");
        tingkatHunianLabel = createValueLabel("0%");

        statsGrid.add(createStatLabel("💰 Total Pemasukan:"));
        statsGrid.add(totalPemasukanLabel);

        statsGrid.add(createStatLabel("✅ Sudah Bayar:"));
        statsGrid.add(sudahBayarLabel);

        statsGrid.add(createStatLabel("⏳ Belum Bayar:"));
        statsGrid.add(belumBayarLabel);

        statsGrid.add(createStatLabel("🏠 Kamar Terisi:"));
        statsGrid.add(kamarTerisiLabel);

        statsGrid.add(createStatLabel("📊 Tingkat Hunian:"));
        statsGrid.add(tingkatHunianLabel);

        panel.add(statsGrid);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(AppConfig.createCardBorder());
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        JLabel titleLabel = new JLabel("📋 Detail Pembayaran");
        titleLabel.setFont(FontManager.FONT_H3);
        titleLabel.setForeground(ColorPalette.NAVY_DARK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Nama Penyewa", "Kamar", "Tanggal Bayar", "Jumlah", "Metode"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(FontManager.FONT_BODY);
        table.setRowHeight(AppConfig.TABLE_ROW_HEIGHT);
        table.setSelectionBackground(ColorPalette.BG_CREAM);

        table.getTableHeader().setBackground(ColorPalette.NAVY_DARK);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(FontManager.FONT_H4);
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FontManager.FONT_BODY);
        label.setForeground(ColorPalette.GRAY_DARK);
        return label;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FontManager.FONT_NUMBER_MED);
        label.setForeground(ColorPalette.NAVY_DARK);
        return label;
    }

    public void refreshData() {
        String bulan = (String) bulanCombo.getSelectedItem();
        String tahun = tahunField.getText().trim();
        String bulanTahun = bulan + " " + tahun;

        // Update summary
        double totalPemasukan = pembayaranDAO.getTotalIncomeByMonth(bulanTahun);
        List<Pembayaran> pembayaranList = pembayaranDAO.getByMonthYear(bulanTahun);
        int sudahBayar = pembayaranList.size();
        int totalPenyewa = penyewaDAO.getTotalActivePenyewa();
        int belumBayar = totalPenyewa - sudahBayar;

        int totalKamar = kamarDAO.getTotalRooms();
        int kamarTerisi = kamarDAO.getOccupiedRooms();
        double tingkatHunian = totalKamar > 0 ? (kamarTerisi * 100.0 / totalKamar) : 0;

        totalPemasukanLabel.setText(String.format("Rp %,.0f", totalPemasukan));
        sudahBayarLabel.setText(String.format("%d orang (%.0f%%)",
                sudahBayar, totalPenyewa > 0 ? (sudahBayar * 100.0 / totalPenyewa) : 0));
        belumBayarLabel.setText(belumBayar + " orang");
        kamarTerisiLabel.setText(String.format("%d / %d kamar", kamarTerisi, totalKamar));
        tingkatHunianLabel.setText(String.format("%.1f%%", tingkatHunian));

        // Update table
        tableModel.setRowCount(0);

        for (Pembayaran p : pembayaranList) {
            Penyewa penyewa = penyewaDAO.getById(p.getIdPenyewa());
            if (penyewa != null) {
                String nomorKamar = kamarDAO.getById(penyewa.getIdKamar()).getNomorKamar();

                Object[] row = {
                        penyewa.getNama(),
                        nomorKamar,
                        DateUtil.formatDate(p.getTanggalBayar()),
                        String.format("Rp %,.0f", p.getJumlah()),
                        p.getMetodeBayar()
                };
                tableModel.addRow(row);
            }
        }
    }

    private void exportToTxt() {
        String bulan = (String) bulanCombo.getSelectedItem();
        String tahun = tahunField.getText().trim();
        String bulanTahun = bulan + " " + tahun;

        String filename = "Laporan_" + bulan + "_" + tahun + ".txt";

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("========================================\n");
            writer.write("     LAPORAN PEMBAYARAN KOST RUMA\n");
            writer.write("========================================\n");
            writer.write("Periode: " + bulanTahun + "\n");
            writer.write("Tanggal Cetak: " + DateUtil.formatDate(LocalDate.now()) + "\n");
            writer.write("========================================\n\n");

            // Summary
            writer.write("RINGKASAN\n");
            writer.write("----------------------------------------\n");
            writer.write("Total Pemasukan    : " + totalPemasukanLabel.getText() + "\n");
            writer.write("Sudah Bayar        : " + sudahBayarLabel.getText() + "\n");
            writer.write("Belum Bayar        : " + belumBayarLabel.getText() + "\n");
            writer.write("Kamar Terisi       : " + kamarTerisiLabel.getText() + "\n");
            writer.write("Tingkat Hunian     : " + tingkatHunianLabel.getText() + "\n");
            writer.write("\n");

            // Detail
            writer.write("DETAIL PEMBAYARAN\n");
            writer.write("----------------------------------------\n");

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                writer.write(String.format("%d. %s (Kamar %s)\n",
                        i + 1,
                        tableModel.getValueAt(i, 0),
                        tableModel.getValueAt(i, 1)
                ));
                writer.write(String.format("   Tanggal: %s | Jumlah: %s | Metode: %s\n\n",
                        tableModel.getValueAt(i, 2),
                        tableModel.getValueAt(i, 3),
                        tableModel.getValueAt(i, 4)
                ));
            }

            writer.write("========================================\n");
            writer.write("         TERIMA KASIH\n");
            writer.write("========================================\n");

            JOptionPane.showMessageDialog(this,
                    "Laporan berhasil diekspor ke:\n" + filename,
                    "Export Berhasil",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengekspor laporan: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}