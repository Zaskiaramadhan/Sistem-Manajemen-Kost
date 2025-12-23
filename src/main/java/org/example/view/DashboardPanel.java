package org.example.view;

import org.example.config.ColorPalette;
import org.example.config.FontManager;
import org.example.config.AppConfig;
import org.example.dao.KamarDAO;
import org.example.dao.PenyewaDAO;
import org.example.dao.PembayaranDAO;
import org.example.util.DateUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Dashboard Panel - Halaman Utama
 * Menampilkan ringkasan statistik
 */
public class DashboardPanel extends JPanel {

    private JLabel totalKamarLabel;
    private JLabel terisiLabel;
    private JLabel kosongLabel;
    private JLabel sudahBayarLabel;
    private JLabel belumBayarLabel;
    private JLabel totalPemasukanLabel;

    public DashboardPanel() {
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(ColorPalette.BG_OFF_WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(ColorPalette.BG_OFF_WHITE);

        // Statistik Kamar
        JPanel statsKamarPanel = createStatsKamarPanel();
        contentPanel.add(statsKamarPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Statistik Pembayaran
        JPanel statsPembayaranPanel = createStatsPembayaranPanel();
        contentPanel.add(statsPembayaranPanel);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ColorPalette.BG_OFF_WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("🏠 Dashboard");
        titleLabel.setFont(FontManager.FONT_H1);
        titleLabel.setForeground(ColorPalette.NAVY_DARK);

        JLabel subtitleLabel = new JLabel("Ringkasan Data Kost Ruma");
        subtitleLabel.setFont(FontManager.FONT_BODY);
        subtitleLabel.setForeground(ColorPalette.GRAY_DARK);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(ColorPalette.BG_OFF_WHITE);
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(subtitleLabel);

        header.add(leftPanel, BorderLayout.WEST);

        return header;
    }

    private JPanel createStatsKamarPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(ColorPalette.BG_OFF_WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // Card Total Kamar
        JPanel totalCard = createStatCard(
                "Total Kamar",
                "0",
                ColorPalette.INFO_BLUE,
                "totalKamar"
        );

        // Card Terisi
        JPanel terisiCard = createStatCard(
                "Kamar Terisi",
                "0",
                ColorPalette.DANGER_RED,
                "terisi"
        );

        // Card Kosong
        JPanel kosongCard = createStatCard(
                "Kamar Tersedia",
                "0",
                ColorPalette.WARNING_ORANGE,
                "kosong"
        );

        panel.add(totalCard);
        panel.add(terisiCard);
        panel.add(kosongCard);

        return panel;
    }

    private JPanel createStatsPembayaranPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(AppConfig.createCardBorder());
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        // Title
        JLabel titleLabel = new JLabel("💰 Status Pembayaran - " + DateUtil.getCurrentMonthYear());
        titleLabel.setFont(FontManager.FONT_H3);
        titleLabel.setForeground(ColorPalette.NAVY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));

        // Stats Grid
        JPanel statsGrid = new JPanel(new GridLayout(3, 2, 15, 15));
        statsGrid.setBackground(Color.WHITE);
        statsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Sudah Bayar
        statsGrid.add(createStatLabel("✅ Sudah Bayar:"));
        sudahBayarLabel = createStatValueLabel("0 orang");
        statsGrid.add(sudahBayarLabel);

        // Belum Bayar
        statsGrid.add(createStatLabel("⏳ Belum Bayar:"));
        belumBayarLabel = createStatValueLabel("0 orang");
        statsGrid.add(belumBayarLabel);

        // Total Pemasukan
        statsGrid.add(createStatLabel("📈 Total Pemasukan:"));
        totalPemasukanLabel = createStatValueLabel("Rp 0");
        statsGrid.add(totalPemasukanLabel);

        panel.add(statsGrid);

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color, String id) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.BG_CREAM, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FontManager.FONT_BODY);
        titleLabel.setForeground(ColorPalette.GRAY_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(FontManager.FONT_NUMBER_BIG);
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Save reference untuk update nanti
        switch(id) {
            case "totalKamar":
                totalKamarLabel = valueLabel;
                break;
            case "terisi":
                terisiLabel = valueLabel;
                break;
            case "kosong":
                kosongLabel = valueLabel;
                break;
        }

        card.add(Box.createVerticalGlue());
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FontManager.FONT_BODY);
        label.setForeground(ColorPalette.GRAY_DARK);
        return label;
    }

    private JLabel createStatValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FontManager.FONT_NUMBER_MED);
        label.setForeground(ColorPalette.NAVY_DARK);
        return label;
    }

    /**
     * Refresh data dari database
     */
    public void refreshData() {
        KamarDAO kamarDAO = KamarDAO.getInstance();
        PenyewaDAO penyewaDAO = PenyewaDAO.getInstance();
        PembayaranDAO pembayaranDAO = PembayaranDAO.getInstance();

        // Update statistik kamar
        int totalKamar = kamarDAO.getTotalRooms();
        int terisi = kamarDAO.getOccupiedRooms();
        int kosong = kamarDAO.getAvailableRoomsCount();

        totalKamarLabel.setText(String.valueOf(totalKamar));
        terisiLabel.setText(String.valueOf(terisi));
        kosongLabel.setText(String.valueOf(kosong));

        // Update statistik pembayaran bulan ini
        String bulanIni = DateUtil.getCurrentMonthYear();
        int totalPenyewa = penyewaDAO.getTotalActivePenyewa();
        int sudahBayar = pembayaranDAO.getPaidThisMonth(bulanIni).size();
        int belumBayar = totalPenyewa - sudahBayar;
        double totalPemasukan = pembayaranDAO.getTotalIncomeByMonth(bulanIni);

        sudahBayarLabel.setText(sudahBayar + " orang (" +
                (totalPenyewa > 0 ? String.format("%.0f%%", (sudahBayar * 100.0 / totalPenyewa)) : "0%") + ")");
        belumBayarLabel.setText(belumBayar + " orang");
        totalPemasukanLabel.setText(String.format("Rp %,.0f", totalPemasukan));
    }
}