package org.example.view;

import org.example.config.ColorPalette;
import org.example.config.FontManager;
import org.example.config.AppConfig;
import org.example.component.RButton;
import org.example.model.Penyewa;
import org.example.model.Kamar;
import org.example.dao.PenyewaDAO;
import org.example.dao.KamarDAO;
import org.example.dao.PembayaranDAO;
import org.example.util.ValidationUtil;
import org.example.util.DateUtil;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PenyewaPanel extends JPanel {

    private PenyewaDAO penyewaDAO;
    private KamarDAO kamarDAO;
    private PembayaranDAO pembayaranDAO;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private JPanel cardContainer;
    private List<Penyewa> currentPenyewaList;

    public PenyewaPanel() {
        penyewaDAO = PenyewaDAO.getInstance();
        kamarDAO = KamarDAO.getInstance();
        pembayaranDAO = PembayaranDAO.getInstance();
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(ColorPalette.BG_OFF_WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(createHeaderPanel(), BorderLayout.NORTH);

        cardContainer = new JPanel();
        cardContainer.setLayout(new GridLayout(0, 4, 15, 15));
        cardContainer.setBackground(ColorPalette.BG_OFF_WHITE);

        JScrollPane scrollPane = new JScrollPane(cardContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorPalette.BG_OFF_WHITE);

        JLabel titleLabel = new JLabel("Kelola Penyewa");
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
        searchField.addActionListener(e -> searchPenyewa());

        RButton searchButton = new RButton("Cari", RButton.ButtonType.SECONDARY);
        searchButton.addActionListener(e -> searchPenyewa());

        JLabel filterLabel = new JLabel("Filter:");
        filterLabel.setFont(FontManager.FONT_BODY);
        filterLabel.setForeground(ColorPalette.GRAY_DARK);

        String[] filterOptions = {"Semua Status", "Sudah Bayar", "Belum Bayar", "Terlambat"};
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.setFont(FontManager.FONT_BODY);
        filterComboBox.addActionListener(e -> filterAndRefresh());

        RButton addButton = new RButton("Tambah Penyewa");
        addButton.addActionListener(e -> showAddDialog());

        controlPanel.add(searchLabel);
        controlPanel.add(searchField);
        controlPanel.add(searchButton);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(filterLabel);
        controlPanel.add(filterComboBox);
        controlPanel.add(addButton);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(controlPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createPenyewaCard(Penyewa penyewa) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.GRAY_LIGHT, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        Kamar kamar = kamarDAO.getById(penyewa.getIdKamar());
        if (kamar == null) return card;

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        JLabel idLabel = new JLabel(penyewa.getIdPenyewa());
        idLabel.setFont(FontManager.FONT_H4);
        idLabel.setForeground(ColorPalette.GRAY_DARK);
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel namaLabel = new JLabel(penyewa.getNama());
        namaLabel.setFont(FontManager.FONT_H3);
        namaLabel.setForeground(ColorPalette.NAVY_DARK);
        namaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel kamarLabel = new JLabel("Kamar: " + kamar.getNomorKamar());
        kamarLabel.setFont(FontManager.FONT_BODY);
        kamarLabel.setForeground(ColorPalette.GRAY_DARK);
        kamarLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hpLabel = new JLabel("HP: " + penyewa.getNoHp());
        hpLabel.setFont(FontManager.FONT_BODY);
        hpLabel.setForeground(ColorPalette.GRAY_DARK);
        hpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String bulanIni = DateUtil.getCurrentMonthYear();
        boolean sudahBayar = pembayaranDAO.isPaid(penyewa.getIdPenyewa(), bulanIni);
        LocalDate today = LocalDate.now();
        boolean terlambat = !sudahBayar && today.getDayOfMonth() > 5;

        JLabel statusLabel;
        if (sudahBayar) {
            statusLabel = new JLabel("✓ Sudah Bayar");
            statusLabel.setForeground(ColorPalette.SUCCESS_GREEN);
        } else if (terlambat) {
            statusLabel = new JLabel("✗ Terlambat");
            statusLabel.setForeground(ColorPalette.DANGER_RED);
        } else {
            statusLabel = new JLabel("⊙ Belum Bayar");
            statusLabel.setForeground(ColorPalette.WARNING_ORANGE);
        }
        statusLabel.setFont(FontManager.FONT_BODY.deriveFont(Font.BOLD));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(idLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(namaLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(kamarLabel);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(hpLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(statusLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPanel.setBackground(Color.WHITE);

        RButton detailButton = new RButton("Detail", RButton.ButtonType.SECONDARY);
        detailButton.addActionListener(e -> showDetailDialog(penyewa));

        buttonPanel.add(detailButton);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    private void searchPenyewa() {
        String keyword = searchField.getText().trim().toLowerCase();
        currentPenyewaList.clear();

        for (Penyewa penyewa : penyewaDAO.getActivePenyewa()) {
            Kamar kamar = kamarDAO.getById(penyewa.getIdKamar());
            if (kamar == null) continue;

            if (keyword.isEmpty() ||
                    penyewa.getNama().toLowerCase().contains(keyword) ||
                    kamar.getNomorKamar().toLowerCase().contains(keyword)) {
                currentPenyewaList.add(penyewa);
            }
        }

        filterAndRefresh();
    }

    private void filterAndRefresh() {
        String filterOption = (String) filterComboBox.getSelectedItem();
        String bulanIni = DateUtil.getCurrentMonthYear();
        LocalDate today = LocalDate.now();

        List<Penyewa> filteredList = new ArrayList<>(currentPenyewaList);

        if (!"Semua Status".equals(filterOption)) {
            filteredList.removeIf(penyewa -> {
                boolean sudahBayar = pembayaranDAO.isPaid(penyewa.getIdPenyewa(), bulanIni);
                boolean terlambat = !sudahBayar && today.getDayOfMonth() > 5;

                switch (filterOption) {
                    case "Sudah Bayar": return !sudahBayar;
                    case "Belum Bayar": return sudahBayar || terlambat;
                    case "Terlambat": return !terlambat;
                    default: return false;
                }
            });
        }

        displayCards(filteredList);
    }

    private void displayCards(List<Penyewa> penyewaList) {
        cardContainer.removeAll();
        for (Penyewa penyewa : penyewaList) {
            cardContainer.add(createPenyewaCard(penyewa));
        }
        cardContainer.revalidate();
        cardContainer.repaint();
    }

    private void showDetailDialog(Penyewa penyewa) {
        Kamar kamar = kamarDAO.getById(penyewa.getIdKamar());
        if (kamar == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Detail Penyewa", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("DETAIL PENYEWA");
        titleLabel.setFont(FontManager.FONT_H2);
        titleLabel.setForeground(ColorPalette.NAVY_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        mainPanel.add(separator);
        mainPanel.add(Box.createVerticalStrut(15));

        mainPanel.add(createInfoRow("ID Penyewa", penyewa.getIdPenyewa()));
        mainPanel.add(createInfoRow("Nama Lengkap", penyewa.getNama()));
        mainPanel.add(createInfoRow("No HP", penyewa.getNoHp()));
        mainPanel.add(createInfoRow("Kamar", kamar.getNomorKamar() + " (" + kamar.getTipe() + ")"));
        mainPanel.add(createInfoRow("Tanggal Masuk", DateUtil.formatDate(penyewa.getTanggalMasuk())));
        mainPanel.add(createInfoRow("Status", penyewa.getStatus()));

        mainPanel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        RButton editButton = new RButton("Edit", RButton.ButtonType.SECONDARY);
        editButton.addActionListener(e -> {
            dialog.dispose();
            showEditDialog(penyewa);
        });

        RButton deleteButton = new RButton("Hapus", RButton.ButtonType.DANGER);
        deleteButton.addActionListener(e -> {
            dialog.dispose();
            deletePenyewa(penyewa);
        });

        RButton closeButton = new RButton("Tutup", RButton.ButtonType.SECONDARY);
        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(FontManager.FONT_BODY_LARGE);
        labelComp.setForeground(ColorPalette.GRAY_DARK);
        labelComp.setPreferredSize(new Dimension(130, 25));

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

    private JPanel createStyledFormField(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(8, 6));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        JLabel jLabel = new JLabel(label);
        jLabel.setFont(FontManager.FONT_BODY_LARGE);
        jLabel.setForeground(ColorPalette.GRAY_DARK);

        if (field instanceof JTextField) {
            ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ColorPalette.GRAY_MEDIUM, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            field.setPreferredSize(new Dimension(0, 40));
        } else if (field instanceof JComboBox) {
            ((JComboBox<?>) field).setFont(FontManager.FONT_BODY);
            field.setPreferredSize(new Dimension(0, 40));
        }

        panel.add(jLabel, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private void showAddDialog() {
        List<Kamar> availableRooms = kamarDAO.getAvailableRooms();
        if (availableRooms.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada kamar tersedia!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tambah Penyewa Baru", true);
        dialog.setSize(550, 580);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        mainPanel.setBackground(Color.WHITE);

        JLabel headerLabel = new JLabel("FORM PENYEWA BARU");
        headerLabel.setFont(FontManager.FONT_H2);
        headerLabel.setForeground(ColorPalette.NAVY_DARK);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(headerLabel);
        mainPanel.add(Box.createVerticalStrut(8));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        mainPanel.add(sep);
        mainPanel.add(Box.createVerticalStrut(20));

        JComboBox<String> kamarCombo = new JComboBox<>();
        kamarCombo.setFont(FontManager.FONT_BODY);
        for (Kamar kamar : availableRooms) {
            kamarCombo.addItem(kamar.getNomorKamar() + " - " + kamar.getTipe());
        }

        JTextField idField = new JTextField();
        idField.setEditable(false);
        idField.setBackground(ColorPalette.BG_LIGHT_BLUE);
        idField.setFont(FontManager.FONT_BODY.deriveFont(Font.BOLD));
        idField.setForeground(ColorPalette.NAVY_DARK);

        kamarCombo.addActionListener(e -> {
            if (kamarCombo.getSelectedItem() != null) {
                String selected = (String) kamarCombo.getSelectedItem();
                String nomorKamar = selected.split(" - ")[0].trim();
                for (Kamar k : availableRooms) {
                    if (k.getNomorKamar().equals(nomorKamar)) {
                        String newId = penyewaDAO.generateIdFromKamar(k.getIdKamar());
                        idField.setText(newId);
                        break;
                    }
                }
            }
        });

        if (kamarCombo.getItemCount() > 0) {
            kamarCombo.setSelectedIndex(0);
        }

        JTextField namaField = new JTextField();
        namaField.setFont(FontManager.FONT_BODY);

        JTextField noHpField = new JTextField();
        noHpField.setFont(FontManager.FONT_BODY);

        JTextField tanggalField = new JTextField(DateUtil.formatDate(LocalDate.now()));
        tanggalField.setFont(FontManager.FONT_BODY);

        mainPanel.add(createStyledFormField("Pilih Kamar:", kamarCombo));
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(createStyledFormField("ID Penyewa (Auto):", idField));
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(createStyledFormField("Nama Lengkap:", namaField));
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(createStyledFormField("No HP:", noHpField));
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(createStyledFormField("Tanggal Masuk:", tanggalField));
        mainPanel.add(Box.createVerticalStrut(25));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        RButton cancelButton = new RButton("Batal", RButton.ButtonType.SECONDARY);
        cancelButton.addActionListener(e -> dialog.dispose());

        RButton saveButton = new RButton("Simpan");
        saveButton.addActionListener(e -> {
            if (validateAndSaveNewPenyewa(idField, namaField, noHpField, kamarCombo, tanggalField, availableRooms)) {
                dialog.dispose();
                refreshData();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showEditDialog(Penyewa penyewa) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Penyewa", true);
        dialog.setSize(550, 580);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        mainPanel.setBackground(Color.WHITE);

        JLabel headerLabel = new JLabel("EDIT DATA PENYEWA");
        headerLabel.setFont(FontManager.FONT_H2);
        headerLabel.setForeground(ColorPalette.NAVY_DARK);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(headerLabel);
        mainPanel.add(Box.createVerticalStrut(8));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        mainPanel.add(sep);
        mainPanel.add(Box.createVerticalStrut(20));

        JTextField idField = new JTextField(penyewa.getIdPenyewa());
        idField.setEditable(false);
        idField.setBackground(ColorPalette.BG_LIGHT_BLUE);
        idField.setFont(FontManager.FONT_BODY.deriveFont(Font.BOLD));
        idField.setForeground(ColorPalette.NAVY_DARK);

        JTextField namaField = new JTextField(penyewa.getNama());
        namaField.setFont(FontManager.FONT_BODY);

        JTextField noHpField = new JTextField(penyewa.getNoHp());
        noHpField.setFont(FontManager.FONT_BODY);

        JComboBox<String> kamarCombo = new JComboBox<>();
        kamarCombo.setFont(FontManager.FONT_BODY);

        Kamar currentKamar = kamarDAO.getById(penyewa.getIdKamar());
        kamarCombo.addItem(currentKamar.getNomorKamar() + " - " + currentKamar.getTipe() + " (Current)");

        List<Kamar> availableRooms = kamarDAO.getAvailableRooms();
        for (Kamar kamar : availableRooms) {
            kamarCombo.addItem(kamar.getNomorKamar() + " - " + kamar.getTipe());
        }

        JTextField tanggalField = new JTextField(DateUtil.formatDate(penyewa.getTanggalMasuk()));
        tanggalField.setFont(FontManager.FONT_BODY);

        mainPanel.add(createStyledFormField("ID Penyewa:", idField));
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(createStyledFormField("Nama Lengkap:", namaField));
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(createStyledFormField("No HP:", noHpField));
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(createStyledFormField("Kamar:", kamarCombo));
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(createStyledFormField("Tanggal Masuk:", tanggalField));
        mainPanel.add(Box.createVerticalStrut(25));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        RButton cancelButton = new RButton("Batal", RButton.ButtonType.SECONDARY);
        cancelButton.addActionListener(e -> dialog.dispose());

        RButton saveButton = new RButton("Simpan Perubahan");
        saveButton.addActionListener(e -> {
            if (validateAndUpdatePenyewa(idField, namaField, noHpField, kamarCombo, tanggalField, penyewa.getIdPenyewa(), availableRooms)) {
                dialog.dispose();
                refreshData();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private boolean validateAndSaveNewPenyewa(JTextField idField, JTextField namaField, JTextField noHpField,
                                              JComboBox<String> kamarCombo, JTextField tanggalField,
                                              List<Kamar> availableRooms) {
        String nama = namaField.getText().trim();
        String noHp = noHpField.getText().trim();
        String tanggalStr = tanggalField.getText().trim();
        String idPenyewa = idField.getText().trim();

        if (!ValidationUtil.isNotEmpty(nama) || !ValidationUtil.isNotEmpty(noHp)) {
            JOptionPane.showMessageDialog(this, "Nama dan No HP harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!ValidationUtil.isValidPhone(noHp)) {
            JOptionPane.showMessageDialog(this, "Format nomor HP tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        LocalDate tanggalMasuk = DateUtil.parseDate(tanggalStr);
        if (tanggalMasuk == null) {
            JOptionPane.showMessageDialog(this, "Format tanggal tidak valid! Gunakan dd/MM/yyyy", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String selectedKamar = (String) kamarCombo.getSelectedItem();
        if (selectedKamar == null || selectedKamar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih kamar terlebih dahulu!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String nomorKamar = selectedKamar.split(" - ")[0].trim();
        Kamar kamar = null;
        for (Kamar k : availableRooms) {
            if (k.getNomorKamar().equals(nomorKamar)) {
                kamar = k;
                break;
            }
        }

        if (kamar == null) {
            JOptionPane.showMessageDialog(this, "Kamar tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Penyewa penyewa = new Penyewa();
        penyewa.setIdPenyewa(idPenyewa);
        penyewa.setNama(nama);
        penyewa.setNoHp(noHp);
        penyewa.setEmail("");
        penyewa.setIdKamar(kamar.getIdKamar());
        penyewa.setTanggalMasuk(tanggalMasuk);
        penyewa.setStatus("Aktif");

        boolean success = penyewaDAO.create(penyewa);

        if (success) {
            JOptionPane.showMessageDialog(this, "Penyewa baru berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan penyewa! Kamar mungkin sudah terisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private boolean validateAndUpdatePenyewa(JTextField idField, JTextField namaField, JTextField noHpField,
                                             JComboBox<String> kamarCombo, JTextField tanggalField,
                                             String oldIdPenyewa, List<Kamar> availableRooms) {
        String nama = namaField.getText().trim();
        String noHp = noHpField.getText().trim();
        String tanggalStr = tanggalField.getText().trim();

        if (!ValidationUtil.isNotEmpty(nama) || !ValidationUtil.isNotEmpty(noHp)) {
            JOptionPane.showMessageDialog(this, "Nama dan No HP harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!ValidationUtil.isValidPhone(noHp)) {
            JOptionPane.showMessageDialog(this, "Format nomor HP tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        LocalDate tanggalMasuk = DateUtil.parseDate(tanggalStr);
        if (tanggalMasuk == null) {
            JOptionPane.showMessageDialog(this, "Format tanggal tidak valid! Gunakan dd/MM/yyyy", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String selectedKamar = (String) kamarCombo.getSelectedItem();
        if (selectedKamar == null || selectedKamar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih kamar terlebih dahulu!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Kamar kamar = null;

        if (selectedKamar.contains("(Current)")) {
            Penyewa oldPenyewa = penyewaDAO.getById(oldIdPenyewa);
            if (oldPenyewa != null) {
                kamar = kamarDAO.getById(oldPenyewa.getIdKamar());
            }
        } else {
            String nomorKamar = selectedKamar.split(" - ")[0].trim();
            for (Kamar k : availableRooms) {
                if (k.getNomorKamar().equals(nomorKamar)) {
                    kamar = k;
                    break;
                }
            }
        }

        if (kamar == null) {
            JOptionPane.showMessageDialog(this, "Kamar tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Penyewa penyewa = new Penyewa();
        penyewa.setIdPenyewa(oldIdPenyewa);
        penyewa.setNama(nama);
        penyewa.setNoHp(noHp);
        penyewa.setEmail("");
        penyewa.setIdKamar(kamar.getIdKamar());
        penyewa.setTanggalMasuk(tanggalMasuk);
        penyewa.setStatus("Aktif");

        boolean success = penyewaDAO.update(penyewa);

        if (success) {
            JOptionPane.showMessageDialog(this, "Data penyewa berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate penyewa! Kamar baru mungkin sudah terisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void deletePenyewa(Penyewa penyewa) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus penyewa " + penyewa.getNama() + "?\nStatus kamar akan berubah menjadi Tersedia.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (penyewaDAO.delete(penyewa.getIdPenyewa())) {
                JOptionPane.showMessageDialog(this, "Penyewa berhasil dihapus!");
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus penyewa!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refreshData() {
        currentPenyewaList = new ArrayList<>(penyewaDAO.getActivePenyewa());
        currentPenyewaList.sort(Comparator.comparing(Penyewa::getIdPenyewa));
        filterAndRefresh();
    }
}