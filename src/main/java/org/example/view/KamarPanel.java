package org.example.view;

import org.example.config.ColorPalette;
import org.example.config.FontManager;
import org.example.config.AppConfig;
import org.example.component.RButton;
import org.example.model.Kamar;
import org.example.dao.KamarDAO;
import org.example.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Kamar Panel - Kelola Data Kamar
 */
public class KamarPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private KamarDAO kamarDAO;
    private JTextField searchField;

    public KamarPanel() {
        kamarDAO = KamarDAO.getInstance();
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(ColorPalette.BG_OFF_WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Table
        add(createTablePanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorPalette.BG_OFF_WHITE);

        // Title
        JLabel titleLabel = new JLabel("🏢 Kelola Kamar");
        titleLabel.setFont(FontManager.FONT_H1);
        titleLabel.setForeground(ColorPalette.NAVY_DARK);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(ColorPalette.BG_OFF_WHITE);

        // Search
        searchField = new JTextField(20);
        searchField.setFont(FontManager.FONT_BODY);
        searchField.setBorder(AppConfig.createInputBorder());
        searchField.addActionListener(e -> searchKamar());

        RButton searchButton = new RButton("🔍 Cari", RButton.ButtonType.SECONDARY);
        searchButton.addActionListener(e -> searchKamar());

        RButton addButton = new RButton("➕ Tambah Kamar");
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

        // Table Model
        String[] columns = {"ID", "No. Kamar", "Tipe", "Harga", "Fasilitas", "Status"};
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

        // Header styling
        table.getTableHeader().setBackground(ColorPalette.NAVY_DARK);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(FontManager.FONT_H4);
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(200);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);

        // Status column renderer dengan warna
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String status = (String) value;
                    if ("Tersedia".equals(status)) {
                        c.setForeground(ColorPalette.WARNING_ORANGE);
                    } else if ("Terisi".equals(status)) {
                        c.setForeground(ColorPalette.DANGER_RED);
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

        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionPanel.setBackground(Color.WHITE);

        RButton detailButton = new RButton("👁️ Detail", RButton.ButtonType.SECONDARY);
        detailButton.addActionListener(e -> showDetailDialog());

        RButton editButton = new RButton("✏️ Edit", RButton.ButtonType.SECONDARY);
        editButton.addActionListener(e -> showEditDialog());

        RButton deleteButton = new RButton("🗑️ Hapus", RButton.ButtonType.DANGER);
        deleteButton.addActionListener(e -> deleteKamar());

        actionPanel.add(detailButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);

        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tambah Kamar", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Form fields
        JTextField nomorField = new JTextField();
        String[] tipeOptions = {"Single", "Double", "VIP"};
        JComboBox<String> tipeCombo = new JComboBox<>(tipeOptions);
        JTextField hargaField = new JTextField();
        JTextField fasilitasField = new JTextField();
        String[] statusOptions = {"Tersedia", "Terisi"};
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);

        panel.add(createFormField("Nomor Kamar:", nomorField));
        panel.add(createFormField("Tipe:", tipeCombo));
        panel.add(createFormField("Harga per Bulan:", hargaField));
        panel.add(createFormField("Fasilitas:", fasilitasField));
        panel.add(createFormField("Status:", statusCombo));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        RButton saveButton = new RButton("💾 Simpan");
        saveButton.addActionListener(e -> {
            if (validateAndSaveKamar(nomorField, tipeCombo, hargaField, fasilitasField, statusCombo, null)) {
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
            JOptionPane.showMessageDialog(this, "Pilih kamar yang akan diedit!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String idKamar = (String) tableModel.getValueAt(selectedRow, 0);
        Kamar kamar = kamarDAO.getById(idKamar);

        if (kamar == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Kamar", true);
        dialog.setSize(500, 500);
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
        String[] statusOptions = {"Tersedia", "Terisi"};
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
        statusCombo.setSelectedItem(kamar.getStatus());

        panel.add(createFormField("Nomor Kamar:", nomorField));
        panel.add(createFormField("Tipe:", tipeCombo));
        panel.add(createFormField("Harga per Bulan:", hargaField));
        panel.add(createFormField("Fasilitas:", fasilitasField));
        panel.add(createFormField("Status:", statusCombo));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        RButton saveButton = new RButton("💾 Simpan");
        saveButton.addActionListener(e -> {
            if (validateAndSaveKamar(nomorField, tipeCombo, hargaField, fasilitasField, statusCombo, idKamar)) {
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
            JOptionPane.showMessageDialog(this, "Pilih kamar untuk melihat detail!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String idKamar = (String) tableModel.getValueAt(selectedRow, 0);
        Kamar kamar = kamarDAO.getById(idKamar);

        if (kamar == null) return;

        String detail = String.format(
                "ID Kamar: %s\nNomor Kamar: %s\nTipe: %s\nHarga: Rp %,.0f\nFasilitas: %s\nStatus: %s",
                kamar.getIdKamar(), kamar.getNomorKamar(), kamar.getTipe(),
                kamar.getHarga(), kamar.getFasilitas(), kamar.getStatus()
        );

        JOptionPane.showMessageDialog(this, detail, "Detail Kamar", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteKamar() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih kamar yang akan dihapus!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String idKamar = (String) tableModel.getValueAt(selectedRow, 0);
        String nomorKamar = (String) tableModel.getValueAt(selectedRow, 1);
        String status = (String) tableModel.getValueAt(selectedRow, 5);

        if ("Terisi".equals(status)) {
            JOptionPane.showMessageDialog(this,
                    "Tidak dapat menghapus kamar yang sedang terisi!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus kamar " + nomorKamar + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (kamarDAO.delete(idKamar)) {
                JOptionPane.showMessageDialog(this, "Kamar berhasil dihapus!");
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus kamar!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateAndSaveKamar(JTextField nomorField, JComboBox<String> tipeCombo,
                                         JTextField hargaField, JTextField fasilitasField, JComboBox<String> statusCombo, String editId) {

        String nomor = nomorField.getText().trim();
        String tipe = (String) tipeCombo.getSelectedItem();
        String hargaStr = hargaField.getText().trim();
        String fasilitas = fasilitasField.getText().trim();
        String status = (String) statusCombo.getSelectedItem();

        if (!ValidationUtil.isNotEmpty(nomor) || !ValidationUtil.isNotEmpty(hargaStr) || !ValidationUtil.isNotEmpty(fasilitas)) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!ValidationUtil.isValidPositiveNumber(hargaStr)) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka positif!", "Error", JOptionPane.ERROR_MESSAGE);
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
        kamar.setImagePath("");

        boolean success;
        if (editId != null) {
            success = kamarDAO.update(kamar);
        } else {
            success = kamarDAO.create(kamar);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, editId != null ? "Kamar berhasil diupdate!" : "Kamar berhasil ditambahkan!");
            return true;
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan kamar!", "Error", JOptionPane.ERROR_MESSAGE);
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

    private void searchKamar() {
        String keyword = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);

        for (Kamar kamar : kamarDAO.getAll()) {
            if (keyword.isEmpty() ||
                    kamar.getNomorKamar().toLowerCase().contains(keyword) ||
                    kamar.getTipe().toLowerCase().contains(keyword) ||
                    kamar.getStatus().toLowerCase().contains(keyword)) {

                Object[] row = {
                        kamar.getIdKamar(),
                        kamar.getNomorKamar(),
                        kamar.getTipe(),
                        String.format("Rp %,.0f", kamar.getHarga()),
                        kamar.getFasilitas(),
                        kamar.getStatus()
                };
                tableModel.addRow(row);
            }
        }
    }

    public void refreshData() {
        tableModel.setRowCount(0);

        for (Kamar kamar : kamarDAO.getAll()) {
            Object[] row = {
                    kamar.getIdKamar(),
                    kamar.getNomorKamar(),
                    kamar.getTipe(),
                    String.format("Rp %,.0f", kamar.getHarga()),
                    kamar.getFasilitas(),
                    kamar.getStatus()
            };
            tableModel.addRow(row);
        }
    }
}