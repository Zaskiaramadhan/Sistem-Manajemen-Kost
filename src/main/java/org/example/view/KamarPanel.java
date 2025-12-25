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
import javax.swing.filechooser.FileFilter;
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
    private String selectedImagePath = "";

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

        add(createHeaderPanel(), BorderLayout.NORTH);

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

        JLabel titleLabel = new JLabel("KELOLA KAMAR");
        titleLabel.setFont(FontManager.FONT_H1);
        titleLabel.setForeground(ColorPalette.NAVY_DARK);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setBackground(ColorPalette.BG_OFF_WHITE);

        JLabel searchLabel = new JLabel("Cari:");
        searchLabel.setFont(FontManager.FONT_BODY);
        searchLabel.setForeground(ColorPalette.GRAY_DARK);

        searchField = new JTextField(15);
        searchField.setFont(FontManager.FONT_BODY);
        searchField.setBorder(AppConfig.createInputBorder());
        searchField.addActionListener(e -> searchKamar());

        RButton searchButton = new RButton("Cari", RButton.ButtonType.SECONDARY);
        searchButton.addActionListener(e -> searchKamar());

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

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        JLabel roomLabel = new JLabel(kamar.getNomorKamar());
        roomLabel.setFont(FontManager.FONT_H3);
        roomLabel.setForeground(ColorPalette.NAVY_DARK);
        roomLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel typeLabel = new JLabel(kamar.getTipe() + " bed");
        typeLabel.setFont(FontManager.FONT_BODY);
        typeLabel.setForeground(ColorPalette.GRAY_DARK);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPanel.setBackground(Color.WHITE);

        RButton statusButton;
        if ("Tersedia".equals(kamar.getStatus())) {
            statusButton = new RButton("Kosong", RButton.ButtonType.SUCCESS);
        } else {
            statusButton = new RButton("Terisi", RButton.ButtonType.DANGER);
        }
        statusButton.setEnabled(false);

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
        dialog.setSize(700, 750);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

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

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel headerLabel = new JLabel("KAMAR " + kamar.getNomorKamar());
        headerLabel.setFont(FontManager.FONT_H3);
        headerLabel.setForeground(ColorPalette.NAVY_DARK);
        headerPanel.add(headerLabel);

        infoPanel.add(headerPanel);
        infoPanel.add(Box.createVerticalStrut(5));

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        separator.setForeground(ColorPalette.GRAY_LIGHT);
        infoPanel.add(separator);
        infoPanel.add(Box.createVerticalStrut(15));

        infoPanel.add(createDetailRow("📐", "Tipe", kamar.getTipe() + " Room"));
        infoPanel.add(Box.createVerticalStrut(8));

        infoPanel.add(createDetailRow("💰", "Harga", String.format("Rp %,.0f / bulan", kamar.getHarga())));
        infoPanel.add(Box.createVerticalStrut(8));

        String ukuranDisplay = (kamar.getUkuran() != null && !kamar.getUkuran().isEmpty()) ? kamar.getUkuran() : "3 x 4 meter";
        infoPanel.add(createDetailRow("📏", "Ukuran", ukuranDisplay));
        infoPanel.add(Box.createVerticalStrut(8));

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

        String fasilitasRaw = kamar.getFasilitas();
        String[] fasilitasArray;
        fasilitasArray = fasilitasRaw.split(",");

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
        if (lower.contains("ac")) return "❄️";
        if (lower.contains("wifi")) return "📶";
        if (lower.contains("kasur")) return "🛏️";
        if (lower.contains("lemari")) return "🚪";
        if (lower.contains("kamar mandi")) return "🚿";
        if (lower.contains("meja belajar")) return "📋";
        if (lower.contains("kursi")) return "🪑";
        if (lower.contains("sofa")) return "🛋️";
        if (lower.contains("tv")) return "📺";
        if (lower.contains("balkon")) return "🌿";
        return "✓";
    }

    private void showAddDialog() {
        showFormDialog(null);
    }

    private void showEditDialog(Kamar kamar) {
        showFormDialog(kamar);
    }

    private void showFormDialog(Kamar kamar) {
        boolean isEdit = (kamar != null);
        String title = isEdit ? "Edit Kamar" : "Tambah Kamar Baru";

        // Reset selected image path
        selectedImagePath = isEdit && kamar.getImagePath() != null ? kamar.getImagePath() : "";

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(600, 750);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // ===== IMAGE UPLOAD SECTION =====
        JPanel imageUploadPanel = createImageUploadPanel(kamar);
        mainPanel.add(imageUploadPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // ===== ROOM INFO SECTION =====
        JLabel infoLabel = new JLabel("INFORMASI KAMAR");
        infoLabel.setFont(FontManager.FONT_BODY.deriveFont(Font.BOLD));
        infoLabel.setForeground(ColorPalette.NAVY_DARK);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel infoLabelWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        infoLabelWrapper.setBackground(Color.WHITE);
        infoLabelWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        infoLabelWrapper.add(infoLabel);
        mainPanel.add(infoLabelWrapper);
        mainPanel.add(Box.createVerticalStrut(10));

        // Nomor Kamar
        JTextField nomorField = new JTextField();
        nomorField.setFont(FontManager.FONT_BODY);
        nomorField.setBorder(AppConfig.createInputBorder());
        nomorField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        final String baseNomor;
        if (isEdit) {
            nomorField.setText(kamar.getNomorKamar());
            nomorField.setEditable(false);
            String[] parts = kamar.getNomorKamar().split(" - ");
            baseNomor = parts.length > 0 ? parts[0] : kamar.getNomorKamar();
        } else {
            baseNomor = String.format("K%02d", kamarDAO.getAll().size() + 1);
            nomorField.setText(baseNomor);
            nomorField.setEditable(false);
        }

        mainPanel.add(createSimpleLabel("Nomor Kamar:"));
        mainPanel.add(nomorField);
        mainPanel.add(Box.createVerticalStrut(10));

        // Tipe Kamar
        String[] tipeOptions = {"Single", "Double"};
        JComboBox<String> tipeCombo = new JComboBox<>(tipeOptions);
        if (isEdit) tipeCombo.setSelectedItem(kamar.getTipe());
        tipeCombo.setFont(FontManager.FONT_BODY);
        tipeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        mainPanel.add(createSimpleLabel("Tipe Kamar:"));
        mainPanel.add(tipeCombo);
        mainPanel.add(Box.createVerticalStrut(10));

        // Harga
        JPanel hargaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        hargaPanel.setBackground(Color.WHITE);
        hargaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel rpLabel = new JLabel("Rp.");
        rpLabel.setFont(FontManager.FONT_BODY);

        JTextField hargaField = new JTextField(15);
        if (isEdit) hargaField.setText(String.valueOf((int)kamar.getHarga()));
        hargaField.setFont(FontManager.FONT_BODY);
        hargaField.setBorder(AppConfig.createInputBorder());

        JLabel perLabel = new JLabel("/");
        perLabel.setFont(FontManager.FONT_BODY);

        String[] periodeOptions = {"Hari", "Bulan", "Tahun"};
        JComboBox<String> periodeCombo = new JComboBox<>(periodeOptions);
        periodeCombo.setSelectedIndex(1);
        periodeCombo.setFont(FontManager.FONT_BODY);

        hargaPanel.add(rpLabel);
        hargaPanel.add(hargaField);
        hargaPanel.add(perLabel);
        hargaPanel.add(periodeCombo);

        mainPanel.add(createSimpleLabel("Harga:"));
        mainPanel.add(hargaPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Ukuran
        JPanel ukuranPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        ukuranPanel.setBackground(Color.WHITE);
        ukuranPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JTextField panjangField = new JTextField(5);
        JTextField lebarField = new JTextField(5);

        if (isEdit && kamar.getUkuran() != null && !kamar.getUkuran().isEmpty()) {
            String[] ukuranParts = kamar.getUkuran().replace(" meter", "").split(" x ");
            if (ukuranParts.length == 2) {
                panjangField.setText(ukuranParts[0].trim());
                lebarField.setText(ukuranParts[1].trim());
            }
        }

        panjangField.setFont(FontManager.FONT_BODY);
        panjangField.setBorder(AppConfig.createInputBorder());

        JLabel xLabel = new JLabel("x");
        xLabel.setFont(FontManager.FONT_BODY);

        lebarField.setFont(FontManager.FONT_BODY);
        lebarField.setBorder(AppConfig.createInputBorder());

        JLabel meterLabel = new JLabel("meter");
        meterLabel.setFont(FontManager.FONT_BODY);

        ukuranPanel.add(panjangField);
        ukuranPanel.add(xLabel);
        ukuranPanel.add(lebarField);
        ukuranPanel.add(meterLabel);

        mainPanel.add(createSimpleLabel("Ukuran:"));
        mainPanel.add(ukuranPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Lantai
        String[] lantaiOptions = {"Lantai 1", "Lantai 2", "Lantai 3"};
        JComboBox<String> lantaiCombo = new JComboBox<>(lantaiOptions);
        lantaiCombo.setFont(FontManager.FONT_BODY);
        lantaiCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        if (!isEdit) {
            lantaiCombo.addActionListener(e -> {
                String lantaiDipilih = (String) lantaiCombo.getSelectedItem();
                nomorField.setText(baseNomor + " - " + lantaiDipilih);
            });
        }

        mainPanel.add(createSimpleLabel("Lantai:"));
        mainPanel.add(lantaiCombo);
        mainPanel.add(Box.createVerticalStrut(20));

        // ===== FASILITAS SECTION =====
        JLabel fasilitasHeaderLabel = new JLabel("FASILITAS (centang jika tersedia):");
        fasilitasHeaderLabel.setFont(FontManager.FONT_BODY.deriveFont(Font.BOLD));
        fasilitasHeaderLabel.setForeground(ColorPalette.NAVY_DARK);
        fasilitasHeaderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel fasilitasLabelWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fasilitasLabelWrapper.setBackground(Color.WHITE);
        fasilitasLabelWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        fasilitasLabelWrapper.add(fasilitasHeaderLabel);
        mainPanel.add(fasilitasLabelWrapper);
        mainPanel.add(Box.createVerticalStrut(10));

        String[] fasilitasList = {
                "Kasur", "Lemari", "Meja Belajar", "Sofa",
                "Meja Rias", "Cermin", "Rak Barang", "Rak Buku",
                "AC", "TV", "Water Heater", "Kamar Mandi Dalam",
                "Jendela", "Balkon", "Tirai/Gorden", "WiFi"
        };

        JCheckBox[] fasilitasCheckBoxes = new JCheckBox[fasilitasList.length];

        List<String> existingFasilitas = new ArrayList<>();
        if (isEdit && kamar.getFasilitas() != null) {
            String fasilitasRaw = kamar.getFasilitas();
            String[] parts = fasilitasRaw.split(",");
            for (String part : parts) {
                existingFasilitas.add(part.trim());
            }
        }

        JPanel fasilitasPanel = new JPanel();
        fasilitasPanel.setLayout(new GridLayout(0, 2, 10, 5));
        fasilitasPanel.setBackground(Color.WHITE);
        fasilitasPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        for (int i = 0; i < fasilitasList.length; i++) {
            fasilitasCheckBoxes[i] = new JCheckBox(fasilitasList[i]);
            fasilitasCheckBoxes[i].setFont(FontManager.FONT_BODY);
            fasilitasCheckBoxes[i].setBackground(Color.WHITE);

            if (existingFasilitas.contains(fasilitasList[i])) {
                fasilitasCheckBoxes[i].setSelected(true);
            }

            fasilitasPanel.add(fasilitasCheckBoxes[i]);
        }
        mainPanel.add(fasilitasPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        RButton cancelButton = new RButton("Batal", RButton.ButtonType.SECONDARY);
        cancelButton.addActionListener(e -> dialog.dispose());

        RButton saveButton = new RButton("Simpan Kamar");
        saveButton.addActionListener(e -> {
            if (validateAndSaveKamar(
                    nomorField, tipeCombo, hargaField,
                    panjangField, lebarField,
                    fasilitasCheckBoxes, fasilitasList,
                    isEdit ? kamar.getIdKamar() : null
            )) {
                dialog.dispose();
                refreshData();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel);

        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    // Inner class untuk preview panel
    private class ImagePreviewPanel extends JPanel {
        private BufferedImage previewImage;

        public ImagePreviewPanel(Kamar kamar) {
            if (kamar != null && kamar.getImagePath() != null && !kamar.getImagePath().isEmpty()) {
                try {
                    File imgFile = new File(kamar.getImagePath());
                    if (imgFile.exists()) {
                        previewImage = ImageIO.read(imgFile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (previewImage != null) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(previewImage, 0, 0, getWidth(), getHeight(), null);
            } else {
                g.setColor(ColorPalette.GRAY_LIGHT);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(ColorPalette.GRAY_DARK);
                g.setFont(FontManager.FONT_BODY);
                String text = "No Preview";
                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;
                g.drawString(text, x, y);
            }
        }

        public void setPreviewImage(BufferedImage img) {
            this.previewImage = img;
            repaint();
        }
    }

    private JPanel createImageUploadPanel(Kamar kamar) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JLabel uploadLabel = new JLabel("UPLOAD FOTO KAMAR");
        uploadLabel.setFont(FontManager.FONT_BODY.deriveFont(Font.BOLD));
        uploadLabel.setForeground(ColorPalette.NAVY_DARK);
        uploadLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel uploadLabelWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        uploadLabelWrapper.setBackground(Color.WHITE);
        uploadLabelWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        uploadLabelWrapper.add(uploadLabel);
        panel.add(uploadLabelWrapper);
        panel.add(Box.createVerticalStrut(10));

        // Panel untuk preview dan tombol
        JPanel uploadControlPanel = new JPanel(new BorderLayout(10, 10));
        uploadControlPanel.setBackground(Color.WHITE);
        uploadControlPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Preview panel menggunakan inner class
        ImagePreviewPanel previewPanel = new ImagePreviewPanel(kamar);
        previewPanel.setPreferredSize(new Dimension(120, 80));
        previewPanel.setBorder(BorderFactory.createLineBorder(ColorPalette.GRAY_LIGHT, 1));

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.WHITE);

        JLabel fileLabel = new JLabel("Belum ada file dipilih");
        fileLabel.setFont(FontManager.FONT_BODY);
        fileLabel.setForeground(ColorPalette.GRAY_DARK);
        fileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (kamar != null && kamar.getImagePath() != null && !kamar.getImagePath().isEmpty()) {
            File f = new File(kamar.getImagePath());
            fileLabel.setText(f.getName());
        }

        RButton chooseButton = new RButton("Pilih Gambar", RButton.ButtonType.SECONDARY);
        chooseButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        chooseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory()) return true;
                    String name = f.getName().toLowerCase();
                    return name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                            name.endsWith(".png") || name.endsWith(".gif");
                }

                @Override
                public String getDescription() {
                    return "Image Files (*.jpg, *.jpeg, *.png, *.gif)";
                }
            });

            int result = fileChooser.showOpenDialog(KamarPanel.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                selectedImagePath = selectedFile.getAbsolutePath();
                fileLabel.setText(selectedFile.getName());

                // Load preview
                try {
                    BufferedImage img = ImageIO.read(selectedFile);
                    previewPanel.setPreviewImage(img);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(KamarPanel.this,
                            "Gagal memuat preview gambar!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(fileLabel);
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(chooseButton);

        uploadControlPanel.add(previewPanel, BorderLayout.WEST);
        uploadControlPanel.add(buttonPanel, BorderLayout.CENTER);

        panel.add(uploadControlPanel);

        return panel;
    }

    private JPanel createSimpleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FontManager.FONT_BODY);
        label.setForeground(ColorPalette.GRAY_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setBackground(Color.WHITE);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        wrapper.add(label);

        return wrapper;
    }

    private String saveImageToProject(String sourceImagePath, String nomorKamar) {
        if (sourceImagePath == null || sourceImagePath.isEmpty()) {
            return "";
        }

        try {
            File sourceFile = new File(sourceImagePath);
            if (!sourceFile.exists()) {
                return "";
            }

            // Buat folder images/rooms jika belum ada
            File imagesDir = new File("images");
            if (!imagesDir.exists()) {
                imagesDir.mkdir();
            }

            File roomsDir = new File("images/rooms");
            if (!roomsDir.exists()) {
                roomsDir.mkdir();
            }

            // Generate nama file baru
            String extension = sourceImagePath.substring(sourceImagePath.lastIndexOf("."));
            String fileName = nomorKamar.replace(" - ", "_").replace(" ", "_") + extension;
            File destFile = new File("images/rooms/" + fileName);

            // Copy file
            BufferedImage image = ImageIO.read(sourceFile);
            String formatName = extension.substring(1).toLowerCase();
            if (formatName.equals("jpg")) formatName = "jpeg";
            ImageIO.write(image, formatName, destFile);

            System.out.println("Image saved to: " + destFile.getPath());
            return destFile.getPath();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to save image, using original path");
            return sourceImagePath; // Fallback ke path asli jika gagal copy
        }
    }

    private boolean validateAndSaveKamar(
            JTextField nomorField, JComboBox<String> tipeCombo,
            JTextField hargaField,
            JTextField panjangField, JTextField lebarField,
            JCheckBox[] fasilitasCheckBoxes, String[] fasilitasList,
            String editId) {

        String nomor = nomorField.getText().trim();
        if (nomor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nomor kamar harus diisi!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String hargaStr = hargaField.getText().trim();
        if (!ValidationUtil.isNotEmpty(hargaStr) || !ValidationUtil.isValidPositiveNumber(hargaStr)) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka positif!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String panjangStr = panjangField.getText().trim();
        String lebarStr = lebarField.getText().trim();
        if (!ValidationUtil.isNotEmpty(panjangStr) || !ValidationUtil.isNotEmpty(lebarStr)) {
            JOptionPane.showMessageDialog(this, "Ukuran kamar harus diisi!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String tipe = (String) tipeCombo.getSelectedItem();
        double harga = Double.parseDouble(hargaStr);
        String ukuran = panjangStr + " x " + lebarStr + " meter";

        StringBuilder fasilitasBuilder = new StringBuilder();
        for (int i = 0; i < fasilitasCheckBoxes.length; i++) {
            if (fasilitasCheckBoxes[i].isSelected()) {
                if (fasilitasBuilder.length() > 0) {
                    fasilitasBuilder.append(",");
                }
                fasilitasBuilder.append(fasilitasList[i]);
            }
        }
        String fasilitas = fasilitasBuilder.toString();

        if (fasilitas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih minimal 1 fasilitas!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Save image to project folder
        String savedImagePath = saveImageToProject(selectedImagePath, nomor);

        Kamar kamar = new Kamar();
        kamar.setIdKamar(editId != null ? editId : kamarDAO.generateNewId());
        kamar.setNomorKamar(nomor);
        kamar.setTipe(tipe);
        kamar.setHarga(harga);
        kamar.setUkuran(ukuran);
        kamar.setFasilitas(fasilitas);
        kamar.setStatus(editId != null ? kamarDAO.getById(editId).getStatus() : "Tersedia");
        kamar.setImagePath(savedImagePath);

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

    public void refreshData() {
        currentKamarList = new ArrayList<>(kamarDAO.getAll());
        sortAndRefresh();
    }
}