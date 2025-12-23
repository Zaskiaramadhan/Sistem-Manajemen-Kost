package org.example.component;

import org.example.config.ColorPalette;
import org.example.config.FontManager;
import org.example.config.AppConfig;
import org.example.view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Sidebar Component - Navigation Menu
 */
public class Sidebar extends JPanel {

    private MainFrame mainFrame;
    private JPanel menuPanel;
    private String activeMenu = "dashboard";

    public Sidebar(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
    }

    private void initComponents() {
        setPreferredSize(new Dimension(AppConfig.SIDEBAR_WIDTH, 0));
        setBackground(ColorPalette.NAVY_DARK);
        setLayout(new BorderLayout());

        // Header/Brand
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Menu items
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(ColorPalette.NAVY_DARK);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        addMenuItem("🏠  Dashboard", "dashboard");
        addMenuItem("🏢  Kelola Kamar", "kamar");
        addMenuItem("👥  Kelola Penyewa", "penyewa");
        addMenuItem("💰  Pembayaran", "pembayaran");
        addMenuItem("📊  Laporan", "laporan");

        add(menuPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setBackground(ColorPalette.NAVY_DARK);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        JLabel brandLabel = new JLabel(AppConfig.APP_NAME);
        brandLabel.setFont(new Font(FontManager.FONT_PRIMARY, Font.BOLD, 28));
        brandLabel.setForeground(ColorPalette.GOLD_ACCENT);
        brandLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel versionLabel = new JLabel("v" + AppConfig.APP_VERSION);
        versionLabel.setFont(FontManager.FONT_CAPTION);
        versionLabel.setForeground(ColorPalette.GRAY_MEDIUM);
        versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(brandLabel);
        header.add(Box.createVerticalStrut(5));
        header.add(versionLabel);

        return header;
    }

    private void addMenuItem(String text, String panelName) {
        JPanel menuItem = new JPanel(new BorderLayout());
        menuItem.setBackground(ColorPalette.NAVY_DARK);
        menuItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        menuItem.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(text);
        label.setFont(FontManager.FONT_BODY);
        label.setForeground(ColorPalette.GRAY_LIGHT);

        menuItem.add(label, BorderLayout.CENTER);

        // Set active state
        if (panelName.equals(activeMenu)) {
            setMenuActive(menuItem, label);
        }

        // Click handler
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setActiveMenu(panelName);
                mainFrame.showPanel(panelName);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!panelName.equals(activeMenu)) {
                    menuItem.setBackground(new Color(52, 73, 94));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!panelName.equals(activeMenu)) {
                    menuItem.setBackground(ColorPalette.NAVY_DARK);
                }
            }
        });

        menuPanel.add(menuItem);
        menuPanel.add(Box.createVerticalStrut(5));
    }

    private void setActiveMenu(String panelName) {
        activeMenu = panelName;

        // Reset all menu items
        Component[] components = menuPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel item = (JPanel) comp;
                item.setBackground(ColorPalette.NAVY_DARK);
                if (item.getComponentCount() > 0 && item.getComponent(0) instanceof JLabel) {
                    JLabel label = (JLabel) item.getComponent(0);
                    label.setForeground(ColorPalette.GRAY_LIGHT);
                }
            }
        }

        // Set active item
        int index = getMenuIndex(panelName);
        if (index >= 0 && index < menuPanel.getComponentCount()) {
            Component comp = menuPanel.getComponent(index * 2); // *2 karena ada spacing
            if (comp instanceof JPanel) {
                JPanel item = (JPanel) comp;
                if (item.getComponentCount() > 0 && item.getComponent(0) instanceof JLabel) {
                    setMenuActive(item, (JLabel) item.getComponent(0));
                }
            }
        }
    }

    private void setMenuActive(JPanel item, JLabel label) {
        item.setBackground(ColorPalette.GOLD_ACCENT);
        label.setForeground(Color.WHITE);
    }

    private int getMenuIndex(String panelName) {
        String[] menus = {"dashboard", "kamar", "penyewa", "pembayaran", "laporan"};
        for (int i = 0; i < menus.length; i++) {
            if (menus[i].equals(panelName)) return i;
        }
        return -1;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel();
        footer.setBackground(ColorPalette.NAVY_DARK);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 20, 30, 20));
        footer.setLayout(new BorderLayout());

        JButton logoutButton = new JButton("🚪 Logout");
        logoutButton.setFont(FontManager.FONT_BODY);
        logoutButton.setForeground(ColorPalette.GRAY_LIGHT);
        logoutButton.setBackground(ColorPalette.NAVY_DARK);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);

        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Apakah Anda yakin ingin logout?",
                    "Konfirmasi Logout",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Close MainFrame dan kembali ke Login
                mainFrame.dispose();
                SwingUtilities.invokeLater(() -> {
                    org.example.view.LoginView loginView = new org.example.view.LoginView();
                    loginView.setVisible(true);
                });
            }
        });

        footer.add(logoutButton, BorderLayout.CENTER);

        return footer;
    }
}