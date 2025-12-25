package org.example.view;

import org.example.config.ColorPalette;
import org.example.config.FontManager;
import org.example.config.AppConfig;
import org.example.component.RButton;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class
LoginView extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private RButton loginButton;

    public LoginView() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Login - " + AppConfig.APP_NAME);
        setSize(500, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel dengan background
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(ColorPalette.BG_OFF_WHITE);

        // Login card
        JPanel loginCard = createLoginCard();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(loginCard, gbc);

        add(mainPanel);
    }

    private JPanel createLoginCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.BG_CREAM, 1),
                BorderFactory.createEmptyBorder(0, 0, 30, 0)
        ));
        card.setPreferredSize(new Dimension(420, 580));

        // ========== FOTO HEADER WITH OVERLAY TEXT ==========
        JPanel imagePanel = createImageHeaderPanel();
        card.add(imagePanel);

        // Spacing after image
        card.add(Box.createVerticalStrut(30));

        // ========== FORM PANEL (dengan padding kiri-kanan) ==========
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        // Tagline
        JLabel taglineLabel = new JLabel(AppConfig.APP_TAGLINE);
        taglineLabel.setFont(FontManager.FONT_BODY);
        taglineLabel.setForeground(ColorPalette.GRAY_DARK);
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        formPanel.add(taglineLabel);
        formPanel.add(Box.createVerticalStrut(30));

        // Username field
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(FontManager.FONT_BODY);
        usernameLabel.setForeground(ColorPalette.GRAY_DARK);

        usernameField = new JTextField();
        usernameField.setFont(FontManager.FONT_BODY);
        usernameField.setPreferredSize(new Dimension(340, 45));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.GRAY_MEDIUM, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.Y_AXIS));
        usernamePanel.setBackground(Color.WHITE);
        usernamePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernamePanel.setMaximumSize(new Dimension(340, 80));

        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernamePanel.add(usernameLabel);
        usernamePanel.add(Box.createVerticalStrut(8));
        usernamePanel.add(usernameField);

        formPanel.add(usernamePanel);
        formPanel.add(Box.createVerticalStrut(20));


        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(FontManager.FONT_BODY);
        passwordLabel.setForeground(ColorPalette.GRAY_DARK);

        passwordField = new JPasswordField();
        passwordField.setFont(FontManager.FONT_BODY);
        passwordField.setPreferredSize(new Dimension(340, 45));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.GRAY_MEDIUM, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordPanel.setMaximumSize(new Dimension(340, 80));

        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordPanel.add(passwordLabel);
        passwordPanel.add(Box.createVerticalStrut(8));
        passwordPanel.add(passwordField);

        formPanel.add(passwordPanel);
        formPanel.add(Box.createVerticalStrut(30));

        // Login button
        loginButton = new RButton("LOGIN");
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginButton.setPreferredSize(new Dimension(340, 45));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(this::handleLogin);

        formPanel.add(loginButton);

        card.add(formPanel);

        return card;
    }

    private JPanel createImageHeaderPanel() {
        JPanel imagePanel = new JPanel() {
            private BufferedImage backgroundImage;

            {
                try {
                    File imgFile = new File("images/Page Login.jpg");
                    if (imgFile.exists()) {
                        backgroundImage = ImageIO.read(imgFile);
                    }
                } catch (IOException e) {
                    System.err.println("Gambar tidak ditemukan: " + e.getMessage());
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (backgroundImage != null) {
                    // Draw image (scaled to fit)
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

                    // Draw dark overlay untuk membuat teks lebih terlihat
                    g2d.setColor(new Color(0, 0, 0, 100)); // Semi-transparent black
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    // Fallback: gradient background jika gambar tidak ada
                    GradientPaint gradient = new GradientPaint(
                            0, 0, ColorPalette.BROWN_PRIMARY,
                            0, getHeight(), ColorPalette.BROWN_LIGHT
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }

                // Draw "RUMA" text di tengah
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font(FontManager.FONT_PRIMARY, Font.BOLD, 52));

                String text = "RUMA";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();

                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() - textHeight) / 2 + fm.getAscent();

                // Draw shadow untuk efek depth
                g2d.setColor(new Color(0, 0, 0, 120));
                g2d.drawString(text, x + 2, y + 2);

                // Draw actual text
                g2d.setColor(Color.WHITE);
                g2d.drawString(text, x, y);
            }
        };

        imagePanel.setPreferredSize(new Dimension(420, 200));
        imagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        imagePanel.setBackground(ColorPalette.BROWN_PRIMARY);

        return imagePanel;
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Validasi
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username dan Password tidak boleh kosong!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Simple authentication (hardcoded for demo)
        if (username.equals("admin") && password.equals("admin123")) {
            // Success - buka MainFrame
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this,
                    "Username atau Password salah!",
                    "Login Gagal",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
}