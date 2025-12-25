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

        String[] filterOptions = {
                "Semua Status",
                "Sudah Bayar",
                "Belum Bayar",
                "Terlambat"
        };
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
        if (kamar == null) {
            return card;
        }

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        // ID
        JLabel idLabel = new JLabel(penyewa.getIdPenyewa());
        idLabel.setFont(FontManager.FONT_H4);
        idLabel.setForeground(ColorPalette.GRAY_DARK);
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Nama
        JLabel namaLabel = new JLabel(penyewa.getNama());
        namaLabel.setFont(FontManager.FONT_H3);
        namaLabel.setForeground(ColorPalette.NAVY_DARK);
        namaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Kamar
        JLabel kamarLabel = new JLabel("Kamar: " + kamar.getNomorKamar());
        kamarLabel.setFont(FontManager.FONT_BODY);
        kamarLabel.setForeground(ColorPalette.GRAY_DARK);
        kamarLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // No HP
        JLabel hpLabel = new JLabel("HP: " + penyewa.getNoHp());
        hpLabel.setFont(FontManager.FONT_BODY);
        hpLabel.setForeground(ColorPalette.GRAY_DARK);
        hpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Status Pembayaran
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

        // Button Panel
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
                    case "Sudah Bayar":
                        return !sudahBayar;
                    case "Belum Bayar":
                        return sudahBayar || terlambat;
                    case "Terlambat":
                        return !terlambat;
                    default:
                        return false;
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

    private void showAddDialog() {
        List<Kamar> availableRooms = kamarDAO.getAvailableRooms();
        if (availableRooms.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Tidak ada kamar tersedia!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tambah Penyewa", true);
        dialog.setSize(500, 520);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // ID Penyewa (auto-generated, readonly)
        String newId = penyewaDAO.generateNewId();
        JTextField idField = new JTextField(newId);
        idField.setEditable(false);
        idField.setBackground(ColorPalette.BG_OFF_WHITE);

        JTextField namaField = new JTextField();
        JTextField noHpField = new JTextField();

        JComboBox<String> kamarCombo = new JComboBox<>();
        for (Kamar kamar : availableRooms) {
            kamarCombo.addItem(kamar.getNomorKamar() + " - " + kamar.getTipe());
        }

        JTextField tanggalField = new JTextField(DateUtil.formatDate(LocalDate.now()));

        panel.add(createFormField("ID Penyewa:", idField));
        panel.add(createFormField("Nama Lengkap:", namaField));
        panel.add(createFormField("No HP:", noHpField));
        panel.add(createFormField("Pilih Kamar:", kamarCombo));
        panel.add(createFormField("Tanggal Masuk (dd/MM/yyyy):", tanggalField));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        RButton saveButton = new RButton("Simpan");
        saveButton.addActionListener(e -> {
            if (validateAndSavePenyewa(namaField, noHpField, kamarCombo, tanggalField, null, availableRooms)) {
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

    private void showEditDialog(Penyewa penyewa) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Penyewa", true);
        dialog.setSize(500, 520);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // ID Penyewa (readonly)
        JTextField idField = new JTextField(penyewa.getIdPenyewa());
        idField.setEditable(false);
        idField.setBackground(ColorPalette.BG_OFF_WHITE);

        JTextField namaField = new JTextField(penyewa.getNama());
        JTextField noHpField = new JTextField(penyewa.getNoHp());

        JComboBox<String> kamarCombo = new JComboBox<>();
        Kamar currentKamar = kamarDAO.getById(penyewa.getIdKamar());
        kamarCombo.addItem(currentKamar.getNomorKamar() + " - " + currentKamar.getTipe() + " (Current)");

        List<Kamar> availableRooms = kamarDAO.getAvailableRooms();
        for (Kamar kamar : availableRooms) {
            kamarCombo.addItem(kamar.getNomorKamar() + " - " + kamar.getTipe());
        }

        JTextField tanggalField = new JTextField(DateUtil.formatDate(penyewa.getTanggalMasuk()));

        panel.add(createFormField("ID Penyewa:", idField));
        panel.add(createFormField("Nama Lengkap:", namaField));
        panel.add(createFormField("No HP:", noHpField));
        panel.add(createFormField("Kamar:", kamarCombo));
        panel.add(createFormField("Tanggal Masuk:", tanggalField));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        RButton saveButton = new RButton("Simpan");
        saveButton.addActionListener(e -> {
            if (validateAndSavePenyewa(namaField, noHpField, kamarCombo, tanggalField, penyewa.getIdPenyewa(), availableRooms)) {
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

    private void deletePenyewa(Penyewa penyewa) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus penyewa " + penyewa.getNama() + "?\nStatus kamar akan berubah menjadi Tersedia.",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (penyewaDAO.delete(penyewa.getIdPenyewa())) {
                JOptionPane.showMessageDialog(this, "Penyewa berhasil dihapus!");
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus penyewa!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateAndSavePenyewa(JTextField namaField, JTextField noHpField,
                                           JComboBox<String> kamarCombo, JTextField tanggalField,
                                           String editId, List<Kamar> availableRooms) {

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

        // Parse nomor kamar dari combo box
        // Format: "K01 - Double" atau "K01 - Double (Current)"
        String nomorKamar = selectedKamar.split(" - ")[0].trim();

        Kamar kamar = null;

        if (editId != null) {
            // Mode EDIT
            if (selectedKamar.contains("(Current)")) {
                // Masih pakai kamar yang sama
                Penyewa oldPenyewa = penyewaDAO.getById(editId);
                if (oldPenyewa != null) {
                    kamar = kamarDAO.getById(oldPenyewa.getIdKamar());
                }
            } else {
                // Pindah ke kamar baru
                for (Kamar k : availableRooms) {
                    if (k.getNomorKamar().equals(nomorKamar)) {
                        kamar = k;
                        break;
                    }
                }
            }
        } else {
            // Mode TAMBAH BARU
            // Cari di availableRooms berdasarkan nomor kamar
            for (Kamar k : availableRooms) {
                if (k.getNomorKamar().equals(nomorKamar)) {
                    kamar = k;
                    break;
                }
            }

            // Debug: print info untuk troubleshooting
            System.out.println("=== DEBUG INFO ===");
            System.out.println("Selected from combo: " + selectedKamar);
            System.out.println("Parsed nomor kamar: " + nomorKamar);
            System.out.println("Available rooms count: " + availableRooms.size());
            System.out.println("Available rooms:");
            for (Kamar k : availableRooms) {
                System.out.println("  - " + k.getNomorKamar() + " (" + k.getTipe() + ")");
            }
            System.out.println("Kamar found: " + (kamar != null ? kamar.getNomorKamar() : "NULL"));
            System.out.println("==================");
        }

        if (kamar == null) {
            JOptionPane.showMessageDialog(this,
                    "Kamar tidak ditemukan atau tidak tersedia!\n" +
                            "Nomor kamar yang dicari: " + nomorKamar + "\n" +
                            "Pilihan di combo: " + selectedKamar,
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Penyewa penyewa = new Penyewa();
        penyewa.setIdPenyewa(editId != null ? editId : penyewaDAO.generateNewId());
        penyewa.setNama(nama);
        penyewa.setNoHp(noHp);
        penyewa.setEmail(""); // Email dihapus sesuai permintaan
        penyewa.setIdKamar(kamar.getIdKamar());
        penyewa.setTanggalMasuk(tanggalMasuk);
        penyewa.setStatus("Aktif");

        boolean success = editId != null ? penyewaDAO.update(penyewa) : penyewaDAO.create(penyewa);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    editId != null ? "Penyewa berhasil diupdate!" : "Penyewa berhasil ditambahkan!");
            return true;
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan penyewa!", "Error", JOptionPane.ERROR_MESSAGE);
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
        currentPenyewaList = new ArrayList<>(penyewaDAO.getActivePenyewa());
        currentPenyewaList.sort(Comparator.comparing(Penyewa::getIdPenyewa));
        filterAndRefresh();
    }
}