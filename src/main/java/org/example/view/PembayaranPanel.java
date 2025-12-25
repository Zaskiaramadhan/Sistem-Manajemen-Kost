package org.example.view;

import org.example.config.ColorPalette;
import org.example.config.FontManager;
import org.example.config.AppConfig;
import org.example.component.RButton;
import org.example.model.Pembayaran;
import org.example.model.Penyewa;
import org.example.dao.PembayaranDAO;
import org.example.dao.PenyewaDAO;
import org.example.util.DateUtil;
import org.example.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Pembayaran Panel - Input dan Kelola Pembayaran
 */
public class PembayaranPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private PembayaranDAO pembayaranDAO;
    private PenyewaDAO penyewaDAO;
    private JComboBox<String> filterBulanCombo;

    public PembayaranPanel() {
        pembayaranDAO = PembayaranDAO.getInstance();
        penyewaDAO = PenyewaDAO.getInstance();
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(ColorPalette.BG_OFF_WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(ColorPalette.BG_OFF_WHITE);

        centerPanel.add(createInputPanel(), BorderLayout.NORTH);
        centerPanel.add(createTablePanel(), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorPalette.BG_OFF_WHITE);

        JLabel titleLabel = new JLabel("PEMBAYARAN");
        titleLabel.setFont(FontManager.FONT_H1);
        titleLabel.setForeground(ColorPalette.NAVY_DARK);

        panel.add(titleLabel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(AppConfig.createCardBorder());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Title label - CENTERED
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Input Pembayaran Baru");
        titleLabel.setFont(FontManager.FONT_H3);
        titleLabel.setForeground(ColorPalette.NAVY_DARK);

        titlePanel.add(titleLabel);
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        panel.add(titlePanel);
        panel.add(Box.createVerticalStrut(30));

        // Form grid - Row 1
        JPanel formGrid1 = new JPanel(new GridLayout(1, 2, 15, 15));
        formGrid1.setBackground(Color.WHITE);
        formGrid1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Penyewa combo
        JComboBox<String> penyewaCombo = new JComboBox<>();
        penyewaCombo.setFont(FontManager.FONT_BODY);
        for (Penyewa p : penyewaDAO.getActivePenyewa()) {
            penyewaCombo.addItem(p.getIdPenyewa() + " - " + p.getNama());
        }

        // Bulan/Tahun combo
        JComboBox<String> bulanTahunCombo = new JComboBox<>();
        bulanTahunCombo.setFont(FontManager.FONT_BODY);

        // Generate bulan-tahun options
        LocalDate now = LocalDate.now();
        for (int i = 0; i < 12; i++) {
            LocalDate date = now.plusMonths(i);
            String bulanIndo = getBulanIndonesia(date.getMonthValue());
            bulanTahunCombo.addItem(bulanIndo + " " + date.getYear());
        }

        formGrid1.add(createFormField("Pilih Penyewa:", penyewaCombo));
        formGrid1.add(createFormField("Bulan/Tahun:", bulanTahunCombo));

        panel.add(formGrid1);
        panel.add(Box.createVerticalStrut(10));

        // Form grid - Row 2
        JPanel formGrid2 = new JPanel(new GridLayout(1, 3, 15, 15));
        formGrid2.setBackground(Color.WHITE);
        formGrid2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Tanggal bayar
        JTextField tanggalField = new JTextField(DateUtil.formatDate(LocalDate.now()));
        tanggalField.setFont(FontManager.FONT_BODY);
        tanggalField.setBorder(AppConfig.createInputBorder());

        // Jumlah
        JTextField jumlahField = new JTextField();
        jumlahField.setFont(FontManager.FONT_BODY);
        jumlahField.setBorder(AppConfig.createInputBorder());

        // Metode bayar
        JComboBox<String> metodeCombo = new JComboBox<>(new String[]{"Cash", "Transfer", "E-Wallet"});
        metodeCombo.setFont(FontManager.FONT_BODY);

        formGrid2.add(createFormField("Tanggal Bayar:", tanggalField));
        formGrid2.add(createFormField("Jumlah (Rp):", jumlahField));
        formGrid2.add(createFormField("Metode Bayar:", metodeCombo));

        panel.add(formGrid2);
        panel.add(Box.createVerticalStrut(10));

        // Form grid - Row 3: Catatan
        JPanel formGrid3 = new JPanel(new BorderLayout(5, 5));
        formGrid3.setBackground(Color.WHITE);
        formGrid3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel catatanLabel = new JLabel("Catatan:");
        catatanLabel.setFont(FontManager.FONT_BODY_SMALL);
        catatanLabel.setForeground(ColorPalette.GRAY_DARK);

        JTextField catatanField = new JTextField();
        catatanField.setFont(FontManager.FONT_BODY);
        catatanField.setBorder(AppConfig.createInputBorder());
        catatanField.setPreferredSize(new Dimension(0, AppConfig.INPUT_HEIGHT));

        formGrid3.add(catatanLabel, BorderLayout.NORTH);
        formGrid3.add(catatanField, BorderLayout.CENTER);

        panel.add(formGrid3);
        panel.add(Box.createVerticalStrut(15));

        // Save button
        RButton saveButton = new RButton("💾 Simpan Pembayaran");
        saveButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        saveButton.addActionListener(e -> {
            if (savePembayaran(penyewaCombo, bulanTahunCombo, tanggalField, jumlahField, metodeCombo)) {
                // Reset form
                jumlahField.setText("");
                catatanField.setText("");
                refreshData();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveButton);

        panel.add(buttonPanel);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(AppConfig.createCardBorder());

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(Color.WHITE);

        JLabel filterLabel = new JLabel("Filter Bulan:");
        filterLabel.setFont(FontManager.FONT_BODY);
        filterLabel.setForeground(ColorPalette.GRAY_DARK);

        filterBulanCombo = new JComboBox<>();
        filterBulanCombo.setFont(FontManager.FONT_BODY);
        filterBulanCombo.addItem("Semua");
        filterBulanCombo.addItem(DateUtil.getCurrentMonthYear());
        filterBulanCombo.addActionListener(e -> filterData());

        RButton refreshButton = new RButton("🔄 Refresh", RButton.ButtonType.SECONDARY);
        refreshButton.addActionListener(e -> refreshData());

        filterPanel.add(filterLabel);
        filterPanel.add(filterBulanCombo);
        filterPanel.add(refreshButton);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Nama Penyewa", "Bulan-Tahun", "Tanggal Bayar", "Jumlah", "Metode", "Status"};
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
        table.setShowGrid(true);
        table.setGridColor(ColorPalette.GRAY_LIGHT);
        table.setIntercellSpacing(new Dimension(1, 1));

        // Header styling with custom renderer
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(new Color(44, 62, 80)); // Navy Dark
                c.setForeground(Color.WHITE);
                setFont(FontManager.FONT_H4);
                setHorizontalAlignment(CENTER);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        };

        // Apply header renderer to all columns
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        table.getTableHeader().setReorderingAllowed(false);

        // Center alignment for all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Custom renderer for alternating colors and status column
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setHorizontalAlignment(CENTER);
                setFont(FontManager.FONT_BODY);

                if (!isSelected) {
                    // Alternating row colors
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(ColorPalette.BG_LIGHT_BLUE);
                    }

                    // Status column with colored text
                    if (column == 6) {
                        String status = (String) value;
                        if ("Lunas".equals(status)) {
                            c.setForeground(ColorPalette.SUCCESS_GREEN);
                            setFont(FontManager.FONT_BUTTON);
                        } else if ("Belum Bayar".equals(status)) {
                            c.setForeground(ColorPalette.WARNING_ORANGE);
                            setFont(FontManager.FONT_BUTTON);
                        } else if ("Terlambat".equals(status)) {
                            c.setForeground(ColorPalette.DANGER_RED);
                            setFont(FontManager.FONT_BUTTON);
                        } else {
                            c.setForeground(ColorPalette.GRAY_DARK);
                        }
                    } else {
                        c.setForeground(ColorPalette.NAVY_DARK);
                    }
                }

                return c;
            }
        });

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(180);  // Nama
        table.getColumnModel().getColumn(2).setPreferredWidth(120);  // Bulan
        table.getColumnModel().getColumn(3).setPreferredWidth(120);  // Tanggal
        table.getColumnModel().getColumn(4).setPreferredWidth(120);  // Jumlah
        table.getColumnModel().getColumn(5).setPreferredWidth(100);  // Metode
        table.getColumnModel().getColumn(6).setPreferredWidth(100);  // Status

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorPalette.GRAY_LIGHT, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        scrollPane.setMinimumSize(new Dimension(0, 300));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Action panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionPanel.setBackground(Color.WHITE);

        RButton deleteButton = new RButton("🗑️ Hapus", RButton.ButtonType.DANGER);
        deleteButton.addActionListener(e -> deletePembayaran());

        actionPanel.add(deleteButton);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private boolean savePembayaran(JComboBox<String> penyewaCombo, JComboBox<String> bulanTahunCombo,
                                   JTextField tanggalField, JTextField jumlahField, JComboBox<String> metodeCombo) {

        if (penyewaCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Tidak ada penyewa aktif!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String idPenyewa = ((String) penyewaCombo.getSelectedItem()).split(" - ")[0];
        String bulanTahun = (String) bulanTahunCombo.getSelectedItem();
        String tanggalStr = tanggalField.getText().trim();
        String jumlahStr = jumlahField.getText().trim();
        String metode = (String) metodeCombo.getSelectedItem();

        if (!ValidationUtil.isNotEmpty(jumlahStr)) {
            JOptionPane.showMessageDialog(this, "Jumlah harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!ValidationUtil.isValidPositiveNumber(jumlahStr)) {
            JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka positif!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        LocalDate tanggalBayar = DateUtil.parseDate(tanggalStr);
        if (tanggalBayar == null) {
            JOptionPane.showMessageDialog(this, "Format tanggal tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        double jumlah = Double.parseDouble(jumlahStr);

        Pembayaran pembayaran = new Pembayaran();
        pembayaran.setIdPembayaran(pembayaranDAO.generateNewId());
        pembayaran.setIdPenyewa(idPenyewa);
        pembayaran.setBulanTahun(bulanTahun);
        pembayaran.setTanggalBayar(tanggalBayar);
        pembayaran.setJumlah(jumlah);
        pembayaran.setMetodeBayar(metode);
        pembayaran.setStatus("Lunas");

        if (pembayaranDAO.create(pembayaran)) {
            JOptionPane.showMessageDialog(this, "Pembayaran berhasil disimpan!");
            return true;
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan pembayaran!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void deletePembayaran() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pembayaran yang akan dihapus!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String idPembayaran = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus data pembayaran ini?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (pembayaranDAO.delete(idPembayaran)) {
                JOptionPane.showMessageDialog(this, "Pembayaran berhasil dihapus!");
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus pembayaran!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void filterData() {
        String filter = (String) filterBulanCombo.getSelectedItem();
        tableModel.setRowCount(0);

        List<Pembayaran> dataList;
        if ("Semua".equals(filter)) {
            dataList = pembayaranDAO.getAll();
        } else {
            dataList = pembayaranDAO.getByMonthYear(filter);
        }

        for (Pembayaran p : dataList) {
            Penyewa penyewa = penyewaDAO.getById(p.getIdPenyewa());

            // ⚠️ NULL CHECK: Skip jika penyewa tidak ditemukan
            if (penyewa == null) {
                System.err.println("⚠️ WARNING: Penyewa dengan ID " + p.getIdPenyewa() + " tidak ditemukan");
                continue;
            }

            Object[] row = {
                    p.getIdPembayaran(),
                    penyewa.getNama(),
                    p.getBulanTahun(),
                    DateUtil.formatDate(p.getTanggalBayar()),
                    String.format("Rp %,.0f", p.getJumlah()),
                    p.getMetodeBayar(),
                    p.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private JPanel createFormField(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);

        JLabel jLabel = new JLabel(label);
        jLabel.setFont(FontManager.FONT_BODY_SMALL);
        jLabel.setForeground(ColorPalette.GRAY_DARK);

        if (field instanceof JTextField) {
            field.setPreferredSize(new Dimension(0, AppConfig.INPUT_HEIGHT));
        }

        panel.add(jLabel, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private String getBulanIndonesia(int month) {
        String[] bulanIndo = {"Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        return bulanIndo[month - 1];
    }

    public void refreshData() {
        filterBulanCombo.setSelectedItem(DateUtil.getCurrentMonthYear());
        filterData();
    }
}