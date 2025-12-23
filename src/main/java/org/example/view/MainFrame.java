package org.example.view;

import org.example.config.ColorPalette;
import org.example.config.AppConfig;
import org.example.component.Sidebar;

import javax.swing.*;
import java.awt.*;

/**
 * Main Frame - Container utama aplikasi dengan Sidebar
 */
public class MainFrame extends JFrame {

    private Sidebar sidebar;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Panel-panel utama
    private DashboardPanel dashboardPanel;
    private KamarPanel kamarPanel;
    private PenyewaPanel penyewaPanel;
    private PembayaranPanel pembayaranPanel;
    private LaporanPanel laporanPanel;

    public MainFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle(AppConfig.APP_NAME + " - " + AppConfig.APP_TAGLINE);
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main layout
        setLayout(new BorderLayout());

        // Sidebar
        sidebar = new Sidebar(this);
        add(sidebar, BorderLayout.WEST);

        // Content area dengan CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(ColorPalette.BG_OFF_WHITE);

        // Initialize panels
        dashboardPanel = new DashboardPanel();
        kamarPanel = new KamarPanel();
        penyewaPanel = new PenyewaPanel();
        pembayaranPanel = new PembayaranPanel();
        laporanPanel = new LaporanPanel();

        // Add panels to card layout
        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(kamarPanel, "kamar");
        contentPanel.add(penyewaPanel, "penyewa");
        contentPanel.add(pembayaranPanel, "pembayaran");
        contentPanel.add(laporanPanel, "laporan");

        add(contentPanel, BorderLayout.CENTER);

        // Show dashboard by default
        showPanel("dashboard");
    }

    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);

        // Refresh panel data jika perlu
        switch (panelName) {
            case "dashboard":
                dashboardPanel.refreshData();
                break;
            case "kamar":
                kamarPanel.refreshData();
                break;
            case "penyewa":
                penyewaPanel.refreshData();
                break;
            case "pembayaran":
                pembayaranPanel.refreshData();
                break;
            case "laporan":
                laporanPanel.refreshData();
                break;
        }
    }
}