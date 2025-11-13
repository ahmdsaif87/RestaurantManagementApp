package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import Controller.RestaurantController;

public class MainFrame extends JFrame {
    private final RestaurantController controller;

    // Warna tema
    private static final Color PRIMARY = new Color(41, 128, 185);
    private static final Color ACCENT = new Color(52, 152, 219);
    private static final Color SUCCESS = new Color(46, 204, 113);
    private static final Color WARNING = new Color(230, 126, 34);
    private static final Color BG = new Color(236, 240, 241);
    private static final Color CARD = Color.WHITE;

    public MainFrame(RestaurantController controller) {
        this.controller = controller;
        setTitle("Aplikasi Restoran");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setContentPane(buildMainPanel());
    }

    private JPanel buildMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.add(buildHeader(), BorderLayout.NORTH);
        panel.add(buildCenter(), BorderLayout.CENTER);
        panel.add(buildFooter(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        JLabel title = makeLabel("Sistem Manajemen Restoran", 42, Font.BOLD, Color.WHITE);
        JLabel subtitle = makeLabel("Kelola pesanan dan menu dengan mudah", 18, Font.PLAIN, new Color(236, 240, 241));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(PRIMARY);
        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(subtitle);

        header.add(textPanel, BorderLayout.CENTER);
        return header;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG);
        center.setBorder(BorderFactory.createEmptyBorder(60, 50, 60, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 30, 20, 30);
        gbc.fill = GridBagConstraints.BOTH;

        // Kartu 1: Menu Pelanggan
        gbc.gridx = 0;
        center.add(createCard(
                "Menu Pelanggan",
                "Buat pesanan baru dan kelola transaksi pelanggan",
                "Mulai Order",
                SUCCESS,
                e -> openFrame(new OrderForm(controller))
        ), gbc);

        // Kartu 2: Panel Admin
        gbc.gridx = 1;
        center.add(createCard(
                "Panel Admin",
                "Kelola menu makanan dan minuman restoran",
                "Buka Admin",
                WARNING,
                e -> openFrame(new AdminPanel(controller))
        ), gbc);

        return center;
    }

    private JPanel createCard(String title, String desc, String btnText, Color color, java.awt.event.ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(cardBorder(false));
        card.setPreferredSize(new Dimension(450, 320));

        JLabel lblTitle = makeLabel(title, 28, Font.BOLD, new Color(52, 73, 94));
        JTextArea lblDesc = makeText(desc);
        JButton button = makeButton(btnText, color, action);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(20));
        card.add(lblDesc);
        card.add(Box.createVerticalStrut(35));
        card.add(button);

        // Hover effect untuk border card
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { card.setBorder(cardBorder(true)); }
            public void mouseExited(java.awt.event.MouseEvent e) { card.setBorder(cardBorder(false)); }
        });

        return card;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(new Color(52, 73, 94));
        footer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = makeLabel("© 2024 Sistem Manajemen Restoran - All Rights Reserved", 14, Font.PLAIN, new Color(189, 195, 199));
        footer.add(label);
        return footer;
    }

    // Helper factories
    private JLabel makeLabel(String text, int size, int style, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", style, size));
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JTextArea makeText(String text) {
        JTextArea area = new JTextArea(text);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        area.setForeground(new Color(127, 140, 141));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setBackground(CARD);
        area.setAlignmentX(Component.CENTER_ALIGNMENT);
        area.setMaximumSize(new Dimension(380, 80));
        return area;
    }

    private JButton makeButton(String text, Color bg, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(250, 55));
        btn.setMaximumSize(new Dimension(250, 55));
        btn.addActionListener(action);

        // Hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(bg.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(bg); }
        });

        return btn;
    }

    private Border cardBorder(boolean hover) {
        Color line = hover ? ACCENT : new Color(189, 195, 199);
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(line, hover ? 2 : 1),
                BorderFactory.createEmptyBorder(40, 35, 40, 35)
        );
    }

    private void openFrame(JFrame frame) {
        // jangan tambah file lain — hanya navigasi antar frame yang sudah ada
        setVisible(false);
        frame.setVisible(true);
    }
}
