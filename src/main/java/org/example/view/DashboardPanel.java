package org.example.view;

import org.example.config.ColorPalette;
import org.example.config.FontManager;
import org.example.config.AppConfig;
import org.example.dao.KamarDAO;
import org.example.dao.PenyewaDAO;
import org.example.dao.PembayaranDAO;
import org.example.model.Pembayaran;
import org.example.model.Penyewa;
import org.example.util.DateUtil;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DashboardPanel extends JPanel {

    private JLabel totalKamarValueLabel;
    private JLabel terisiValueLabel;
    private JLabel kosongValueLabel;
    private JLabel sudahBayarLabel;
    private JLabel belumBayarLabel;
    private JLabel totalPemasukanLabel;
    private JPanel notificationPanel;

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

        // Statistik Kamar dengan Gambar
        JPanel statsKamarPanel = createStatsKamarWithImages();
        contentPanel.add(statsKamarPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Statistik Pembayaran
        JPanel statsPembayaranPanel = createStatsPembayaranPanel();
        contentPanel.add(statsPembayaranPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Notifikasi
        JPanel notifOuterPanel = createNotificationPanel();
        contentPanel.add(notifOuterPanel);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ColorPalette.BG_OFF_WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        JLabel titleLabel = new JLabel("DASHBOARD");
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

    private JPanel createStatsKamarWithImages() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(ColorPalette.BG_OFF_WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        // Card Total Kamar
        panel.add(createImageStatCard(
                "Total Kamar",
                "0",
                ColorPalette.INFO_BLUE,
                "total",
                "images/icon-total.jpg"
        ));

        // Card Terisi
        panel.add(createImageStatCard(
                "Kamar Terisi",
                "0",
                ColorPalette.DANGER_RED,
                "terisi",
                "images/icon-terisi.jpg"
        ));

        // Card Tersedia
        panel.add(createImageStatCard(
                "Kamar Tersedia",
                "0",
                ColorPalette.WARNING_ORANGE,
                "kosong",
                "images/icon-tersedia.jpg"
        ));

        return panel;
    }

    private JPanel createImageStatCard(String title, String value, Color color, String id, String imagePath) {
        JPanel card = new JPanel() {
            private BufferedImage bgImage;

            {
                try {
                    File imgFile = new File(imagePath);
                    if (imgFile.exists()) {
                        bgImage = ImageIO.read(imgFile);
                        System.out.println("✓ Dashboard image loaded: " + imagePath);
                    } else {
                        System.out.println("✗ Dashboard image not found: " + imagePath);
                    }
                } catch (Exception e) {
                    System.out.println("✗ Error loading dashboard image: " + imagePath);
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                if (bgImage != null) {
                    g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
                    // Overlay untuk readability
                    g2d.setColor(new Color(0, 0, 0, 130));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    // Fallback gradient
                    GradientPaint gradient = new GradientPaint(
                            0, 0, color,
                            getWidth(), getHeight(), color.darker()
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.BG_CREAM, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FontManager.FONT_BODY_LARGE);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(FontManager.FONT_NUMBER_BIG);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Save reference
        switch (id) {
            case "total":
                totalKamarValueLabel = valueLabel;
                break;
            case "terisi":
                terisiValueLabel = valueLabel;
                break;
            case "kosong":
                kosongValueLabel = valueLabel;
                break;
        }

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(valueLabel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        card.add(textPanel, gbc);

        return card;
    }

    private JPanel createStatsPembayaranPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(ColorPalette.BG_OFF_WHITE);
        outerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(AppConfig.createCardBorder());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Status Pembayaran - " + DateUtil.getCurrentMonthYear());
        titleLabel.setFont(FontManager.FONT_H3);
        titleLabel.setForeground(ColorPalette.NAVY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));

        JPanel statsContainer = new JPanel();
        statsContainer.setLayout(new BoxLayout(statsContainer, BoxLayout.Y_AXIS));
        statsContainer.setBackground(Color.WHITE);
        statsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel row1 = createStatRow("Sudah Bayar");
        sudahBayarLabel = createStatValueLabel("0 orang (0%)");
        row1.add(sudahBayarLabel);
        statsContainer.add(row1);
        statsContainer.add(Box.createVerticalStrut(10));

        JPanel row2 = createStatRow("Belum Bayar");
        belumBayarLabel = createStatValueLabel("0 orang");
        row2.add(belumBayarLabel);
        statsContainer.add(row2);
        statsContainer.add(Box.createVerticalStrut(10));

        JPanel row3 = createStatRow("Total Pemasukan");
        totalPemasukanLabel = createStatValueLabel("Rp 0");
        row3.add(totalPemasukanLabel);
        statsContainer.add(row3);

        panel.add(statsContainer);

        outerPanel.add(panel, BorderLayout.CENTER);
        return outerPanel;
    }

    private JPanel createNotificationPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(ColorPalette.BG_OFF_WHITE);
        outerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(AppConfig.createCardBorder());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Notifikasi");
        titleLabel.setFont(FontManager.FONT_H3);
        titleLabel.setForeground(ColorPalette.NAVY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));

        outerPanel.add(panel, BorderLayout.CENTER);
        notificationPanel = panel;
        return outerPanel;
    }

    private void updateNotifications() {
        Component[] components = notificationPanel.getComponents();
        for (int i = components.length - 1; i > 0; i--) {
            notificationPanel.remove(i);
        }

        List<String> notifications = new ArrayList<>();
        PenyewaDAO penyewaDAO = PenyewaDAO.getInstance();
        PembayaranDAO pembayaranDAO = PembayaranDAO.getInstance();
        KamarDAO kamarDAO = KamarDAO.getInstance();

        String bulanIni = DateUtil.getCurrentMonthYear();
        LocalDate today = LocalDate.now();

        for (Penyewa penyewa : penyewaDAO.getActivePenyewa()) {
            boolean sudahBayar = pembayaranDAO.isPaid(penyewa.getIdPenyewa(), bulanIni);

            if (!sudahBayar) {
                String nomorKamar = kamarDAO.getById(penyewa.getIdKamar()).getNomorKamar();

                if (today.getDayOfMonth() > 5) {
                    int hariTerlambat = today.getDayOfMonth() - 5;
                    notifications.add("Kamar " + nomorKamar + " (" + penyewa.getNama() + ") - Pembayaran terlambat " + hariTerlambat + " hari");
                } else if (today.getDayOfMonth() >= 3) {
                    notifications.add("Kamar " + nomorKamar + " (" + penyewa.getNama() + ") - Pembayaran jatuh tempo dalam " + (5 - today.getDayOfMonth()) + " hari");
                }
            }
        }

        if (notifications.isEmpty()) {
            JLabel noNotifLabel = new JLabel("Tidak ada notifikasi. Semua pembayaran lancar!");
            noNotifLabel.setFont(FontManager.FONT_BODY);
            noNotifLabel.setForeground(ColorPalette.SUCCESS_GREEN);
            noNotifLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            notificationPanel.add(noNotifLabel);
        } else {
            for (String notif : notifications) {
                JLabel notifLabel = new JLabel("• " + notif);
                notifLabel.setFont(FontManager.FONT_BODY);
                notifLabel.setForeground(ColorPalette.DANGER_RED);
                notifLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                notificationPanel.add(notifLabel);
                notificationPanel.add(Box.createVerticalStrut(8));
            }
        }

        notificationPanel.revalidate();
        notificationPanel.repaint();
    }

    private JPanel createStatRow(String labelText) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setBackground(Color.WHITE);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(FontManager.FONT_BODY_LARGE);
        label.setForeground(ColorPalette.GRAY_DARK);
        label.setPreferredSize(new Dimension(150, 25));

        JLabel colon = new JLabel(": ");
        colon.setFont(FontManager.FONT_BODY_LARGE);
        colon.setForeground(ColorPalette.GRAY_DARK);

        row.add(label);
        row.add(colon);

        return row;
    }

    private JLabel createStatValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FontManager.FONT_NUMBER_MED);
        label.setForeground(ColorPalette.NAVY_DARK);
        return label;
    }

    public void refreshData() {
        System.out.println("\n📊 === DASHBOARD REFRESH START ===");

        // ✅ PENTING: Force reload dari file
        KamarDAO kamarDAO = KamarDAO.getInstance();
        PenyewaDAO penyewaDAO = PenyewaDAO.getInstance();
        PembayaranDAO pembayaranDAO = PembayaranDAO.getInstance();

        // Reload semua data dari file
        kamarDAO.refresh();
        penyewaDAO.refresh();
        pembayaranDAO.refresh();

        // Update statistik kamar
        int totalKamar = kamarDAO.getTotalRooms();
        int terisi = kamarDAO.getOccupiedRooms();
        int kosong = kamarDAO.getAvailableRoomsCount();

        System.out.println("📈 Stats Kamar:");
        System.out.println("   Total    : " + totalKamar);
        System.out.println("   Terisi   : " + terisi);
        System.out.println("   Tersedia : " + kosong);

        // Update UI
        if (totalKamarValueLabel != null) totalKamarValueLabel.setText(String.valueOf(totalKamar));
        if (terisiValueLabel != null) terisiValueLabel.setText(String.valueOf(terisi));
        if (kosongValueLabel != null) kosongValueLabel.setText(String.valueOf(kosong));

        // Update statistik pembayaran
        String bulanIni = DateUtil.getCurrentMonthYear();
        int totalPenyewa = penyewaDAO.getTotalActivePenyewa();
        List<Pembayaran> pembayaranList = pembayaranDAO.getPaidThisMonth(bulanIni);
        int sudahBayar = pembayaranList.size();
        int belumBayar = totalPenyewa - sudahBayar;
        double totalPemasukan = pembayaranDAO.getTotalIncomeByMonth(bulanIni);

        double persenSudahBayar = totalPenyewa > 0 ? (sudahBayar * 100.0 / totalPenyewa) : 0;

        System.out.println("💰 Stats Pembayaran:");
        System.out.println("   Sudah Bayar : " + sudahBayar + "/" + totalPenyewa);
        System.out.println("   Belum Bayar : " + belumBayar);
        System.out.println("   Pemasukan   : Rp " + String.format("%,.0f", totalPemasukan));

        if (sudahBayarLabel != null)
            sudahBayarLabel.setText(String.format("%d orang (%.0f%%)", sudahBayar, persenSudahBayar));
        if (belumBayarLabel != null) belumBayarLabel.setText(belumBayar + " orang");
        if (totalPemasukanLabel != null) totalPemasukanLabel.setText(String.format("Rp %,.0f", totalPemasukan));

        // Update notifikasi
        updateNotifications();

        System.out.println("✅ === DASHBOARD REFRESH COMPLETE ===\n");
    }
}