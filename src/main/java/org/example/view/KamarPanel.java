package org.example.view;

import org.example.config.ColorPalette;
import org.example.config.FontManager;
import org.example.config.AppConfig;
import org.example.component.RButton;
import org.example.model.Kamar;
import org.example.dao.KamarDAO;
import org.example.dao.PenyewaDAO;
import org.example.util.ValidationUtil;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KamarPanel extends JPanel {

    private KamarDAO kamarDAO;
    private PenyewaDAO penyewaDAO;
    private JTextField searchField;
    private JComboBox<String> sortComboBox;
    private JPanel cardContainer;
    private List<Kamar> currentKamarList;

    public KamarPanel() {
        kamarDAO = KamarDAO.getInstance();
        penyewaDAO = PenyewaDAO.getInstance();
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(ColorPalette.BG_OFF_WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Card Container with ScrollPane
        cardContainer = new JPanel();
        cardContainer.setLayout(new GridLayout(0, 3, 20, 20));
        cardContainer.setBackground(ColorPalette.BG_OFF_WHITE);

        JScrollPane scrollPane = new JScrollPane(cardContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorPalette.BG_OFF_WHITE);

        // Title
        JLabel titleLabel = new JLabel("Kelola Kamar");
        titleLabel.setFont(FontManager.FONT_H1);
        titleLabel.setForeground(ColorPalette.NAVY_DARK);

        // Control panel (search, sort, buttons)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setBackground(ColorPalette.BG_OFF_WHITE);

        // Search
        JLabel searchLabel = new JLabel("Cari:");
        searchLabel.setFont(FontManager.FONT_BODY);
        searchLabel.setForeground(ColorPalette.GRAY_DARK);

        searchField = new JTextField(15);
        searchField.setFont(FontManager.FONT_BODY);
        searchField.setBorder(AppConfig.createInputBorder());
        searchField.addActionListener(e -> searchKamar());

        RButton searchButton = new RButton("Cari", RButton.ButtonType.SECONDARY);
        searchButton.addActionListener(e -> searchKamar());

        // Sort
        JLabel sortLabel = new JLabel("Sort:");
        sortLabel.setFont(FontManager.FONT_BODY);
        sortLabel.setForeground(ColorPalette.GRAY_DARK);

        String[] sortOptions = {
                "Nomor Kamar (Rendah-Tinggi)",
                "Nomor Kamar (Tinggi-Rendah)",
                "Harga (Rendah-Tinggi)",
                "Harga (Tinggi-Rendah)",
                "Status: Tersedia",
                "Status: Terisi"
        };
        sortComboBox = new JComboBox<>(sortOptions);
        sortComboBox.setFont(FontManager.FONT_BODY);
        sortComboBox.addActionListener(e -> sortAndRefresh());

        // Tambah Kamar button
        RButton addButton = new RButton("Tambah Kamar");
        addButton.addActionListener(e -> showAddDialog());

        controlPanel.add(searchLabel);
        controlPanel.add(searchField);
        controlPanel.add(searchButton);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(sortLabel);
        controlPanel.add(sortComboBox);
        controlPanel.add(addButton);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(controlPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createKamarCard(Kamar kamar) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.GRAY_LIGHT, 1),
                BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));
        card.setPreferredSize(new Dimension(300, 350));

        // Image Panel
        JPanel imagePanel = new JPanel() {
            private BufferedImage image;

            {
                try {
                    String imagePath = kamar.getImagePath();
                    if (imagePath != null && !imagePath.isEmpty()) {
                        File imgFile = new File(imagePath);
                        if (imgFile.exists()) {
                            image = ImageIO.read(imgFile);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error loading image for " + kamar.getNomorKamar());
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                if (image != null) {
                    g2d.drawImage(image, 0, 0, getWidth(), getHeight(), null);
                } else {
                    // Placeholder
                    g2d.setColor(ColorPalette.GRAY_LIGHT);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(ColorPalette.GRAY_DARK);
                    g2d.setFont(FontManager.FONT_BODY);
                    String text = "No Image";
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = (getHeight() + fm.getAscent()) / 2;
                    g2d.drawString(text, x, y);
                }
            }
        };
        imagePanel.setPreferredSize(new Dimension(300, 180));
        card.add(imagePanel, BorderLayout.NORTH);

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        // Room number
        JLabel roomLabel = new JLabel(kamar.getNomorKamar());
        roomLabel.setFont(FontManager.FONT_H3);
        roomLabel.setForeground(ColorPalette.NAVY_DARK);
        roomLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Type
        JLabel typeLabel = new JLabel(kamar.getTipe() + " bed");
        typeLabel.setFont(FontManager.FONT_BODY);
        typeLabel.setForeground(ColorPalette.GRAY_DARK);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Price
        JLabel priceLabel = new JLabel(String.format("Rp %,.0f", kamar.getHarga()));
        priceLabel.setFont(FontManager.FONT_NUMBER_MED);
        priceLabel.setForeground(ColorPalette.NAVY_DARK);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(roomLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(typeLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(priceLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPanel.setBackground(Color.WHITE);

        // Status button
        RButton statusButton;
        if ("Tersedia".equals(kamar.getStatus())) {
            statusButton = new RButton("Kosong", RButton.ButtonType.SUCCESS);
        } else {
            statusButton = new RButton("Terisi", RButton.ButtonType.DANGER);
        }
        statusButton.setEnabled(false);

        // Detail button
        RButton detailButton = new RButton("Detail", RButton.ButtonType.SECONDARY);
        detailButton.addActionListener(e -> showDetailDialog(kamar));

        buttonPanel.add(statusButton);
        buttonPanel.add(detailButton);

        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    private void searchKamar() {
        String keyword = searchField.getText().trim().toLowerCase();
        currentKamarList.clear();

        for (Kamar kamar : kamarDAO.getAll()) {
            boolean matchesRoom = kamar.getNomorKamar().toLowerCase().contains(keyword);

            // Search by penghuni name
            boolean matchesPenghuni = false;
            if ("Terisi".equals(kamar.getStatus())) {
                String penghuni = penyewaDAO.getPenghuniByKamar(kamar.getIdKamar());
                if (penghuni != null && penghuni.toLowerCase().contains(keyword)) {
                    matchesPenghuni = true;
                }
            }

            if (keyword.isEmpty() || matchesRoom || matchesPenghuni) {
                currentKamarList.add(kamar);
            }
        }

        sortAndRefresh();
    }

    private void sortAndRefresh() {
        String sortOption = (String) sortComboBox.getSelectedItem();

        switch (sortOption) {
            case "Nomor Kamar (Rendah-Tinggi)":
                currentKamarList.sort(Comparator.comparing(Kamar::getNomorKamar));
                break;
            case "Nomor Kamar (Tinggi-Rendah)":
                currentKamarList.sort(Comparator.comparing(Kamar::getNomorKamar).reversed());
                break;
            case "Harga (Rendah-Tinggi)":
                currentKamarList.sort(Comparator.comparingDouble(Kamar::getHarga));
                break;
            case "Harga (Tinggi-Rendah)":
                currentKamarList.sort(Comparator.comparingDouble(Kamar::getHarga).reversed());
                break;
            case "Status: Tersedia":
                currentKamarList.removeIf(k -> !"Tersedia".equals(k.getStatus()));
                break;
            case "Status: Terisi":
                currentKamarList.removeIf(k -> !"Terisi".equals(k.getStatus()));
                break;
        }

        displayCards();
    }

    private void displayCards() {
        cardContainer.removeAll();

        for (Kamar kamar : currentKamarList) {
            cardContainer.add(createKamarCard(kamar));
        }

        cardContainer.revalidate();
        cardContainer.repaint();
    }

    private void showDetailDialog(Kamar kamar) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Detail Kamar", true);
        dialog.setSize(650, 750);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Image
        JPanel imagePanel = new JPanel() {
            private BufferedImage image;

            {
                try {
                    String imagePath = kamar.getImagePath();
                    if (imagePath != null && !imagePath.isEmpty()) {
                        File imgFile = new File(imagePath);
                        if (imgFile.exists()) {
                            image = ImageIO.read(imgFile);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (image != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.drawImage(image, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g.setColor(ColorPalette.GRAY_LIGHT);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        imagePanel.setPreferredSize(new Dimension(610, 280));
        mainPanel.add(imagePanel, BorderLayout.NORTH);

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        // Header dengan nomor kamar dan lantai
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel headerLabel = new JLabel("KAMAR " + kamar.getNomorKamar() + " - Lantai 2");
        headerLabel.setFont(FontManager.FONT_H3);
        headerLabel.setForeground(ColorPalette.NAVY_DARK);
        headerPanel.add(headerLabel);

        infoPanel.add(headerPanel);
        infoPanel.add(Box.createVerticalStrut(5));

        // Separator line
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        separator.setForeground(ColorPalette.GRAY_LIGHT);
        infoPanel.add(separator);
        infoPanel.add(Box.createVerticalStrut(15));

        // Info rows with icons
        infoPanel.add(createDetailRow("📐", "Tipe", kamar.getTipe() + " Room"));
        infoPanel.add(Box.createVerticalStrut(8));

        infoPanel.add(createDetailRow("💰", "Harga", String.format("Rp %,.0f / bulan", kamar.getHarga())));
        infoPanel.add(Box.createVerticalStrut(8));

        infoPanel.add(createDetailRow("📏", "Ukuran", "3 x 4 meter"));
        infoPanel.add(Box.createVerticalStrut(8));

        // Status dengan warna
        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusRow.setBackground(Color.WHITE);
        statusRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel statusIcon = new JLabel("📊");
        statusIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        statusIcon.setPreferredSize(new Dimension(30, 25));

        JLabel statusLabel = new JLabel("Status");
        statusLabel.setFont(FontManager.FONT_BODY_LARGE);
        statusLabel.setForeground(ColorPalette.GRAY_DARK);
        statusLabel.setPreferredSize(new Dimension(120, 25));

        JLabel colonStatus = new JLabel(": ");
        colonStatus.setFont(FontManager.FONT_BODY_LARGE);

        JLabel statusValue = new JLabel("Tersedia".equals(kamar.getStatus()) ? "[ ● TERSEDIA]" : "[ ● TERISI]");
        statusValue.setFont(FontManager.FONT_BODY_LARGE);
        statusValue.setForeground("Tersedia".equals(kamar.getStatus()) ? new Color(34, 197, 94) : new Color(239, 68, 68));

        statusRow.add(statusIcon);
        statusRow.add(statusLabel);
        statusRow.add(colonStatus);
        statusRow.add(statusValue);

        infoPanel.add(statusRow);
        infoPanel.add(Box.createVerticalStrut(15));

        // Fasilitas section
        JPanel fasilitasHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fasilitasHeader.setBackground(Color.WHITE);
        fasilitasHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel fasilitasIcon = new JLabel("✨");
        fasilitasIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        fasilitasIcon.setPreferredSize(new Dimension(30, 25));

        JLabel fasilitasLabel = new JLabel("FASILITAS:");
        fasilitasLabel.setFont(FontManager.FONT_BODY_LARGE.deriveFont(Font.BOLD));
        fasilitasLabel.setForeground(ColorPalette.NAVY_DARK);

        fasilitasHeader.add(fasilitasIcon);
        fasilitasHeader.add(fasilitasLabel);
        infoPanel.add(fasilitasHeader);
        infoPanel.add(Box.createVerticalStrut(8));

        // Parse fasilitas dan tampilkan per baris
        String[] fasilitasArray = kamar.getFasilitas().split("\\+");
        for (String fas : fasilitasArray) {
            JPanel fasRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
            fasRow.setBackground(Color.WHITE);
            fasRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

            String icon = getFasilitasIcon(fas.trim());
            JLabel fasIcon = new JLabel(icon);
            fasIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            fasIcon.setPreferredSize(new Dimension(25, 20));

            JLabel fasText = new JLabel(fas.trim());
            fasText.setFont(FontManager.FONT_BODY);
            fasText.setForeground(ColorPalette.GRAY_DARK);

            fasRow.add(fasIcon);
            fasRow.add(fasText);
            infoPanel.add(fasRow);
            infoPanel.add(Box.createVerticalStrut(3));
        }

        infoPanel.add(Box.createVerticalStrut(15));

        // Penyewa section
        JPanel penyewaHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        penyewaHeader.setBackground(Color.WHITE);
        penyewaHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel penyewaIcon = new JLabel("👤");
        penyewaIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        penyewaIcon.setPreferredSize(new Dimension(30, 25));

        JLabel penyewaLabel = new JLabel("PENYEWA SAAT INI:");
        penyewaLabel.setFont(FontManager.FONT_BODY_LARGE.deriveFont(Font.BOLD));
        penyewaLabel.setForeground(ColorPalette.NAVY_DARK);

        penyewaHeader.add(penyewaIcon);
        penyewaHeader.add(penyewaLabel);
        infoPanel.add(penyewaHeader);
        infoPanel.add(Box.createVerticalStrut(8));

        if ("Terisi".equals(kamar.getStatus())) {
            String nama = penyewaDAO.getPenghuniByKamar(kamar.getIdKamar());
            String noHp = penyewaDAO.getNomorHPByKamar(kamar.getIdKamar());
            String tanggal = penyewaDAO.getTanggalMasukByKamar(kamar.getIdKamar());

            infoPanel.add(createPenyewaRow("Nama", nama != null ? nama : "-"));
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(createPenyewaRow("No HP", noHp != null ? noHp : "-"));
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(createPenyewaRow("Sejak", tanggal != null ? tanggal : "-"));
        } else {
            JPanel emptyRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
            emptyRow.setBackground(Color.WHITE);
            emptyRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

            JLabel emptyLabel = new JLabel("Kamar kosong, belum ada penyewa");
            emptyLabel.setFont(FontManager.FONT_BODY);
            emptyLabel.setForeground(ColorPalette.GRAY_DARK);

            emptyRow.add(emptyLabel);
            infoPanel.add(emptyRow);
        }

        mainPanel.add(infoPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        RButton editButton = new RButton("Edit", RButton.ButtonType.SECONDARY);
        editButton.addActionListener(e -> {
            dialog.dispose();
            showEditDialog(kamar);
        });

        RButton deleteButton = new RButton("Hapus", RButton.ButtonType.DANGER);
        deleteButton.addActionListener(e -> {
            dialog.dispose();
            deleteKamar(kamar);
        });

        RButton closeButton = new RButton("Tutup", RButton.ButtonType.SECONDARY);
        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private JPanel createDetailRow(String icon, String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconLabel.setPreferredSize(new Dimension(30, 25));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(FontManager.FONT_BODY_LARGE);
        labelComp.setForeground(ColorPalette.GRAY_DARK);
        labelComp.setPreferredSize(new Dimension(120, 25));

        JLabel colon = new JLabel(": ");
        colon.setFont(FontManager.FONT_BODY_LARGE);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(FontManager.FONT_BODY_LARGE);
        valueComp.setForeground(ColorPalette.NAVY_DARK);

        row.add(iconLabel);
        row.add(labelComp);
        row.add(colon);
        row.add(valueComp);

        return row;
    }

    private JPanel createPenyewaRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(FontManager.FONT_BODY);
        labelComp.setForeground(ColorPalette.GRAY_DARK);
        labelComp.setPreferredSize(new Dimension(80, 20));

        JLabel colon = new JLabel(": ");
        colon.setFont(FontManager.FONT_BODY);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(FontManager.FONT_BODY);
        valueComp.setForeground(ColorPalette.NAVY_DARK);

        row.add(labelComp);
        row.add(colon);
        row.add(valueComp);

        return row;
    }

    private String getFasilitasIcon(String fasilitas) {
        String lower = fasilitas.toLowerCase();
        if (lower.contains("AC")) return "❄️";
        if (lower.contains("wifi")) return "📶";
        if (lower.contains("kasur")) return "🛏️";
        if (lower.contains("lemari")) return "🚪";
        if (lower.contains("kamar mandi")) return "🚿";
        if (lower.contains("meja Belajar")) return "📋";
        if (lower.contains("kursi")) return "🪑";
        return "✓";
    }

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(FontManager.FONT_BODY_LARGE);
        labelComp.setForeground(ColorPalette.GRAY_DARK);
        labelComp.setPreferredSize(new Dimension(150, 25));

        JLabel colon = new JLabel(": ");
        colon.setFont(FontManager.FONT_BODY_LARGE);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(FontManager.FONT_BODY_LARGE);
        valueComp.setForeground(ColorPalette.NAVY_DARK);

        row.add(labelComp);
        row.add(colon);
        row.add(valueComp);

        return row;
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tambah Kamar", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JTextField nomorField = new JTextField();
        String[] tipeOptions = {"Single", "Double", "VIP"};
        JComboBox<String> tipeCombo = new JComboBox<>(tipeOptions);
        JTextField hargaField = new JTextField();
        JTextField fasilitasField = new JTextField();
        JTextField imagePathField = new JTextField();
        String[] statusOptions = {"Tersedia", "Terisi"};
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);

        panel.add(createFormField("Nomor Kamar:", nomorField));
        panel.add(createFormField("Tipe:", tipeCombo));
        panel.add(createFormField("Harga per Bulan:", hargaField));
        panel.add(createFormField("Fasilitas:", fasilitasField));
        panel.add(createFormField("Path Gambar:", imagePathField));
        panel.add(createFormField("Status:", statusCombo));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        RButton saveButton = new RButton("Simpan");
        saveButton.addActionListener(e -> {
            if (validateAndSaveKamar(nomorField, tipeCombo, hargaField, fasilitasField,
                    imagePathField, statusCombo, null)) {
                dialog.dispose();
                refreshData();
            }
        });

        RButton cancelButton = new RButton("Batal", RButton.ButtonType.SECONDARY);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditDialog(Kamar kamar) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Kamar", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JTextField nomorField = new JTextField(kamar.getNomorKamar());
        String[] tipeOptions = {"Single", "Double", "VIP"};
        JComboBox<String> tipeCombo = new JComboBox<>(tipeOptions);
        tipeCombo.setSelectedItem(kamar.getTipe());
        JTextField hargaField = new JTextField(String.valueOf((int)kamar.getHarga()));
        JTextField fasilitasField = new JTextField(kamar.getFasilitas());
        JTextField imagePathField = new JTextField(kamar.getImagePath());
        String[] statusOptions = {"Tersedia", "Terisi"};
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
        statusCombo.setSelectedItem(kamar.getStatus());

        panel.add(createFormField("Nomor Kamar:", nomorField));
        panel.add(createFormField("Tipe:", tipeCombo));
        panel.add(createFormField("Harga per Bulan:", hargaField));
        panel.add(createFormField("Fasilitas:", fasilitasField));
        panel.add(createFormField("Path Gambar:", imagePathField));
        panel.add(createFormField("Status:", statusCombo));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        RButton saveButton = new RButton("Simpan");
        saveButton.addActionListener(e -> {
            if (validateAndSaveKamar(nomorField, tipeCombo, hargaField, fasilitasField,
                    imagePathField, statusCombo, kamar.getIdKamar())) {
                dialog.dispose();
                refreshData();
            }
        });

        RButton cancelButton = new RButton("Batal", RButton.ButtonType.SECONDARY);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteKamar(Kamar kamar) {
        if ("Terisi".equals(kamar.getStatus())) {
            JOptionPane.showMessageDialog(this,
                    "Tidak dapat menghapus kamar yang sedang terisi!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus kamar " + kamar.getNomorKamar() + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (kamarDAO.delete(kamar.getIdKamar())) {
                JOptionPane.showMessageDialog(this, "Kamar berhasil dihapus!");
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus kamar!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateAndSaveKamar(JTextField nomorField, JComboBox<String> tipeCombo,
                                         JTextField hargaField, JTextField fasilitasField,
                                         JTextField imagePathField, JComboBox<String> statusCombo,
                                         String editId) {

        String nomor = nomorField.getText().trim();
        String tipe = (String) tipeCombo.getSelectedItem();
        String hargaStr = hargaField.getText().trim();
        String fasilitas = fasilitasField.getText().trim();
        String imagePath = imagePathField.getText().trim();
        String status = (String) statusCombo.getSelectedItem();

        if (!ValidationUtil.isNotEmpty(nomor) || !ValidationUtil.isNotEmpty(hargaStr) ||
                !ValidationUtil.isNotEmpty(fasilitas)) {
            JOptionPane.showMessageDialog(this, "Nomor, harga, dan fasilitas harus diisi!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!ValidationUtil.isValidPositiveNumber(hargaStr)) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka positif!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        double harga = Double.parseDouble(hargaStr);

        Kamar kamar = new Kamar();
        kamar.setIdKamar(editId != null ? editId : kamarDAO.generateNewId());
        kamar.setNomorKamar(nomor);
        kamar.setTipe(tipe);
        kamar.setHarga(harga);
        kamar.setFasilitas(fasilitas);
        kamar.setStatus(status);
        kamar.setImagePath(imagePath);

        boolean success = editId != null ? kamarDAO.update(kamar) : kamarDAO.create(kamar);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    editId != null ? "Kamar berhasil diupdate!" : "Kamar berhasil ditambahkan!");
            return true;
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan kamar!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private JPanel createFormField(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel jLabel = new JLabel(label);
        jLabel.setFont(FontManager.FONT_BODY);
        jLabel.setForeground(ColorPalette.GRAY_DARK);

        if (field instanceof JTextField) {
            ((JTextField) field).setFont(FontManager.FONT_BODY);
            ((JTextField) field).setBorder(AppConfig.createInputBorder());
            field.setPreferredSize(new Dimension(0, AppConfig.INPUT_HEIGHT));
        } else if (field instanceof JComboBox) {
            ((JComboBox<?>) field).setFont(FontManager.FONT_BODY);
            field.setPreferredSize(new Dimension(0, AppConfig.INPUT_HEIGHT));
        }

        panel.add(jLabel, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    public void refreshData() {
        currentKamarList = new ArrayList<>(kamarDAO.getAll());
        sortAndRefresh();
    }
}