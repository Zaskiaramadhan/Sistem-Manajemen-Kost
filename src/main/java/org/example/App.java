package org.example;

import org.example.view.LoginView;
import org.example.util.FileHandler;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize data directories
        FileHandler.initializeDataDirectories();

        // Launch Login View
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
}