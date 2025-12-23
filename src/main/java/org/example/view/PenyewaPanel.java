package org.example.view;

import org.example.config.ColorPalette;
import org.example.config.FontManager;
import org.example.config.AppConfig;
import org.example.component.RButton;
import org.example.model.Penyewa;
import org.example.model.Kamar;
import org.example.dao.PenyewaDAO;
import org.example.dao.KamarDAO;
import org.example.util.ValidationUtil;
import org.example.util.DateUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Penyewa Panel - Kelola Data Penyewa
 */
public class PenyewaPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private PenyewaDAO penyewaDAO;
    private KamarDAO kamarDAO;
    private JTextField searchField;

    public PenyewaPanel() {
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
        add(createTablePanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorPalette.BG_OFF_WHITE);

        JLabel titleLabel = new JLabel("👥 Kelola Penyewa");
        titleLabel.setFont(FontManager.FONT_H1);
        titleLabel.setForeground(ColorPalette.NAVY_DARK);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(ColorPalette.BG_OFF_WHITE);

        searchField = new JTextField(20);
        searchField.setFont(FontManager.FONT_BODY);
        searchField.setBorder(AppConfig.createInputBorder());
        searchField.addActionListener(e -> searchPenyewa());

        RButton searchButton = new RButton("🔍 Cari", RButton.ButtonType.SECONDARY);
        searchButton.addActionListener(e -> searchPenyewa());

        RButton addButton = new RButton("➕ Tambah Penyewa");
        addButton.addActionListener(e -> showAddDialog());

        buttonPanel.add(new JLabel("Cari:"));
        buttonPanel.add(searchField);
        buttonPanel.add(searchButton);
        buttonPanel.add(addButton);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(AppConfig.createCardBorder());

        String[] columns = {"ID", "Nama", "No HP", "Email", "Kamar", "Tanggal Masuk", "Status"};
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
        table.setSelectionForeground(ColorPalette.NAVY_DARK);
        table.setGridColor(ColorPalette.GRAY_LIGHT);

        table.getTableHeader().setBackground(ColorPalette.NAVY_DARK);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(FontManager.FONT_H4);
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));

        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(180);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);

        // Status renderer
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String status = (String) value;
                    if ("Aktif".equals(status)) {
                        c.setForeground(ColorPalette.SUCCESS_GREEN);
                    } else {
                        c.setForeground(ColorPalette.GRAY_MEDIUM);
                    }
                    setFont(FontManager.FONT_BUTTON);
                }
                setHorizontalAlignment(CENTER);
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionPanel.setBackground(Color.WHITE);

        RButton detailButton = new RButton("👁️ Detail", RButton.ButtonType.SECONDARY);
        detailButton.addActionListener(e -> showDetailDialog());

        RButton editButton = new RButton("✏️ Edit", RButton.ButtonType.SECONDARY);
        editButton.addActionListener(e -> showEditDialog());

        RButton deleteButton = new RButton("🗑️ Hapus", RButton.ButtonType.DANGER);
        deleteButton.addActionListener(e -> deletePenyewa());

        actionPanel.add(detailButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);

        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showAddDialog() {
        // Check apakah ada kamar tersedia
        List<Kamar> availableRooms = kamarDAO.getAvailableRooms();
        if (availableRooms.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Tidak ada kamar tersedia!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tambah Penyewa", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JTextField namaField = new JTextField();
        JTextField noHpField = new JTextField();
        JTextField emailField = new JTextField();

        // Combo box kamar tersedia
        JComboBox<String> kamarCombo = new JComboBox<>();
        for (Kamar kamar : availableRooms) {
            kamarCombo.addItem(kamar.getNomorKamar() + " - " + kamar.getTipe());
        }

        JTextField tanggalField = new JTextField(DateUtil.formatDate(LocalDate.now()));

        panel.add(createFormField("Nama Lengkap:", namaField));
        panel.add(createFormField("No HP:", noHpField));
        panel.add(createFormField("Email:", emailField));
        panel.add(createFormField("Pilih Kamar:", kamarCombo));
        panel.add(createFormField("Tanggal Masuk (dd/MM/yyyy):", tanggalField));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        RButton saveButton = new RButton("💾 Simpan");
        saveButton.addActionListener(e -> {
            if (validateAndSavePenyewa(namaField, noHpField, emailField, kamarCombo, tanggalField, null, availableRooms)) {
                dialog.dispose();
                refreshData();
            }
        });

        RButton cancelButton = new RButton("❌ Batal", RButton.ButtonType.SECONDARY);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih penyewa yang akan diedit!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String idPenyewa = (String) tableModel.getValueAt(selectedRow, 0);
        Penyewa penyewa = penyewaDAO.getById(idPenyewa);

        if (penyewa == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Penyewa", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JTextField namaField = new JTextField(penyewa.getNama());
        JTextField noHpField = new JTextField(penyewa.getNoHp());
        JTextField emailField = new JTextField(penyewa.getEmail());

        // Kamar combo (current + available)
        JComboBox<String> kamarCombo = new JComboBox<>();
        Kamar currentKamar = kamarDAO.getById(penyewa.getIdKamar());
        kamarCombo.addItem(currentKamar.getNomorKamar() + " - " + currentKamar.getTipe() + " (Current)");

        List<Kamar> availableRooms = kamarDAO.getAvailableRooms();
        for (Kamar kamar : availableRooms) {
            kamarCombo.addItem(kamar.getNomorKamar() + " - " + kamar.getTipe());
        }

        JTextField tanggalField = new JTextField(DateUtil.formatDate(penyewa.getTanggalMasuk()));

        panel.add(createFormField("Nama Lengkap:", namaField));
        panel.add(createFormField("No HP:", noHpField));
        panel.add(createFormField("Email:", emailField));
        panel.add(createFormField("Kamar:", kamarCombo));
        panel.add(createFormField("Tanggal Masuk:", tanggalField));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        RButton saveButton = new RButton("💾 Simpan");
        saveButton.addActionListener(e -> {
            if (validateAndSavePenyewa(namaField, noHpField, emailField, kamarCombo, tanggalField, idPenyewa, availableRooms)) {
                dialog.dispose();
                refreshData();
            }
        });

        RButton cancelButton = new RButton("❌ Batal", RButton.ButtonType.SECONDARY);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showDetailDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih penyewa untuk melihat detail!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String idPenyewa = (String) tableModel.getValueAt(selectedRow, 0);
        Penyewa penyewa = penyewaDAO.getById(idPenyewa);
        Kamar kamar = kamarDAO.getById(penyewa.getIdKamar());

        // ⚠️ NULL CHECK: Jika kamar tidak ada
        String kamarInfo = (kamar != null) ?
                kamar.getNomorKamar() + " (" + kamar.getTipe() + ")" :
                "Kamar tidak ditemukan";

        String detail = String.format(
                "ID Penyewa: %s\nNama: %s\nNo HP: %s\nEmail: %s\nKamar: %s\nTanggal Masuk: %s\nStatus: %s",
                penyewa.getIdPenyewa(), penyewa.getNama(), penyewa.getNoHp(), penyewa.getEmail(),
                kamarInfo,
                DateUtil.formatDate(penyewa.getTanggalMasuk()), penyewa.getStatus()
        );

        JOptionPane.showMessageDialog(this, detail, "Detail Penyewa", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deletePenyewa() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih penyewa yang akan dihapus!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String idPenyewa = (String) tableModel.getValueAt(selectedRow, 0);
        String nama = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus penyewa " + nama + "?\nStatus kamar akan berubah menjadi Tersedia.",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (penyewaDAO.delete(idPenyewa)) {
                JOptionPane.showMessageDialog(this, "Penyewa berhasil dihapus!");
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus penyewa!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateAndSavePenyewa(JTextField namaField, JTextField noHpField,
                                           JTextField emailField, JComboBox<String> kamarCombo, JTextField tanggalField,
                                           String editId, List<Kamar> availableRooms) {

        String nama = namaField.getText().trim();
        String noHp = noHpField.getText().trim();
        String email = emailField.getText().trim();
        String tanggalStr = tanggalField.getText().trim();

        if (!ValidationUtil.isNotEmpty(nama) || !ValidationUtil.isNotEmpty(noHp) || !ValidationUtil.isNotEmpty(email)) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!ValidationUtil.isValidPhone(noHp)) {
            JOptionPane.showMessageDialog(this, "Format nomor HP tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Format email tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        LocalDate tanggalMasuk = DateUtil.parseDate(tanggalStr);
        if (tanggalMasuk == null) {
            JOptionPane.showMessageDialog(this, "Format tanggal tidak valid! Gunakan dd/MM/yyyy", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Get kamar ID
        String selectedKamar = (String) kamarCombo.getSelectedItem();
        String nomorKamar = selectedKamar.split(" - ")[0];

        Kamar kamar = null;
        if (editId != null) {
            // Edit mode: bisa current atau available
            kamar = kamarDAO.getAll().stream()
                    .filter(k -> k.getNomorKamar().equals(nomorKamar))
                    .findFirst().orElse(null);
        } else {
            // Add mode: dari available rooms
            kamar = availableRooms.stream()
                    .filter(k -> k.getNomorKamar().equals(nomorKamar))
                    .findFirst().orElse(null);
        }

        if (kamar == null) {
            JOptionPane.showMessageDialog(this, "Kamar tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Penyewa penyewa = new Penyewa();
        penyewa.setIdPenyewa(editId != null ? editId : penyewaDAO.generateNewId());
        penyewa.setNama(nama);
        penyewa.setNoHp(noHp);
        penyewa.setEmail(email);
        penyewa.setIdKamar(kamar.getIdKamar());
        penyewa.setTanggalMasuk(tanggalMasuk);
        penyewa.setStatus("Aktif");

        boolean success;
        if (editId != null) {
            success = penyewaDAO.update(penyewa);
        } else {
            success = penyewaDAO.create(penyewa);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, editId != null ? "Penyewa berhasil diupdate!" : "Penyewa berhasil ditambahkan!");
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

    private void searchPenyewa() {
        String keyword = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);

        for (Penyewa penyewa : penyewaDAO.getAll()) {
            Kamar kamar = kamarDAO.getById(penyewa.getIdKamar());

            // ⚠️ NULL CHECK: Skip jika kamar tidak ditemukan
            if (kamar == null) {
                continue;
            }

            if (keyword.isEmpty() ||
                    penyewa.getNama().toLowerCase().contains(keyword) ||
                    penyewa.getNoHp().contains(keyword) ||
                    kamar.getNomorKamar().toLowerCase().contains(keyword)) {

                Object[] row = {
                        penyewa.getIdPenyewa(),
                        penyewa.getNama(),
                        penyewa.getNoHp(),
                        penyewa.getEmail(),
                        kamar.getNomorKamar(),
                        DateUtil.formatDate(penyewa.getTanggalMasuk()),
                        penyewa.getStatus()
                };
                tableModel.addRow(row);
            }
        }
    }

    public void refreshData() {
        tableModel.setRowCount(0);

        for (Penyewa penyewa : penyewaDAO.getAll()) {
            Kamar kamar = kamarDAO.getById(penyewa.getIdKamar());

            // ⚠️ NULL CHECK: Skip jika kamar tidak ditemukan
            if (kamar == null) {
                System.err.println("⚠️ WARNING: Kamar dengan ID " + penyewa.getIdKamar() +
                        " tidak ditemukan untuk penyewa " + penyewa.getNama());
                continue; // Skip data ini
            }

            Object[] row = {
                    penyewa.getIdPenyewa(),
                    penyewa.getNama(),
                    penyewa.getNoHp(),
                    penyewa.getEmail(),
                    kamar.getNomorKamar(),
                    DateUtil.formatDate(penyewa.getTanggalMasuk()),
                    penyewa.getStatus()
            };
            tableModel.addRow(row);
        }
    }
}