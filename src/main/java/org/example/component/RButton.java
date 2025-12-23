package org.example.component;

import org.example.config.ColorPalette;
import org.example.config.FontManager;
import org.example.config.AppConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Custom Button Component dengan styling Ruma
 */
public class RButton extends JButton {

    public enum ButtonType {
        PRIMARY,
        SECONDARY,
        DANGER,
        SUCCESS
    }

    private ButtonType type;
    private Color normalColor;
    private Color hoverColor;

    public RButton(String text) {
        this(text, ButtonType.PRIMARY);
    }

    public RButton(String text, ButtonType type) {
        super(text);
        this.type = type;
        initButton();
    }

    private void initButton() {
        // Font
        setFont(FontManager.FONT_BUTTON);

        // Remove default styling
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(true);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Padding
        setBorder(AppConfig.createButtonBorder());

        // Set colors based on type
        switch (type) {
            case PRIMARY:
                normalColor = ColorPalette.GOLD_ACCENT;
                hoverColor = ColorPalette.GOLD_MUTED;
                setForeground(Color.WHITE);
                break;
            case SECONDARY:
                normalColor = ColorPalette.GRAY_LIGHT;
                hoverColor = ColorPalette.GRAY_MEDIUM;
                setForeground(ColorPalette.GRAY_DARK);
                break;
            case DANGER:
                normalColor = ColorPalette.DANGER_RED;
                hoverColor = new Color(200, 60, 50);
                setForeground(Color.WHITE);
                break;
            case SUCCESS:
                normalColor = ColorPalette.SUCCESS_GREEN;
                hoverColor = new Color(30, 140, 80);
                setForeground(Color.WHITE);
                break;
        }

        setBackground(normalColor);

        // Hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(normalColor);
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.height = AppConfig.BUTTON_HEIGHT;
        return size;
    }
}