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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import Controller.RestaurantController;
import Model.Menu;

public class AdminPanel extends JFrame {
    private final RestaurantController controller;
    private JTable menuTable;
    private DefaultTableModel tableModel;
    private JTextField namaField, hargaField;
    private JComboBox<String> kategoriCombo;

    // Warna tema
    private static final Color PRIMARY = new Color(41, 128, 185);
    private static final Color ACCENT = new Color(52, 152, 219);
    private static final Color SUCCESS = new Color(46, 204, 113);
    private static final Color DANGER = new Color(231, 76, 60);
    private static final Color GREY = new Color(149, 165, 166);
    private static final Color BORDER = new Color(189, 195, 199);
    private static final Color BG = new Color(236, 240, 241);
    private static final Color CARD = Color.WHITE;

    public AdminPanel(RestaurantController controller) {
        super("Panel Admin - Manajemen Menu");
        this.controller = controller;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);
        getContentPane().setBackground(BG);

        setLayout(new BorderLayout(10, 10));
        add(createHeader(), BorderLayout.NORTH);
        add(createMainContainer(), BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(PRIMARY);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        JButton backBtn = createButton("Kembali", new Color(52, 73, 94), e -> {
            dispose();
            new MainFrame(controller).setVisible(true);
        });
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(backBtn);

        JLabel title = createLabel("Panel Admin", 32, Font.BOLD, Color.WHITE);
        JLabel subtitle = createLabel("Kelola menu makanan dan minuman restoran", 16, Font.PLAIN, new Color(236, 240, 241));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(Box.createVerticalStrut(10));
        header.add(title);
        header.add(subtitle);
        return header;
    }

    private JPanel createMainContainer() {
        JPanel container = new JPanel(new BorderLayout(0, 15));
        container.setBackground(BG);
        container.setBorder(new EmptyBorder(15, 15, 15, 15));

        container.add(createTablePanel(), BorderLayout.CENTER);
        container.add(createFormPanel(), BorderLayout.SOUTH);
        return container;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = createLabel("Daftar Menu", 20, Font.BOLD, new Color(52, 73, 94));

        tableModel = new DefaultTableModel(new Object[]{"No", "Nama Menu", "Kategori", "Harga (Rp)"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        menuTable = new JTable(tableModel);
        styleTable(menuTable);
        refreshTable();

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(menuTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel form = new JPanel(new BorderLayout(0, 10));
        form.setBackground(BG);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(CARD);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(20, 25, 20, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel formTitle = createLabel("Form Input Menu", 18, Font.BOLD, new Color(52, 73, 94));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 6;
        inputPanel.add(formTitle, gbc);

        gbc.gridwidth = 1;

        addFormField(inputPanel, gbc, 0, 1, "Nama Menu:", namaField = new JTextField());
        addFormField(inputPanel, gbc, 2, 1, "Kategori:", kategoriCombo = new JComboBox<>(new String[]{"Makanan", "Minuman"}));
        addFormField(inputPanel, gbc, 4, 1, "Harga (Rp):", hargaField = new JTextField());

        form.add(inputPanel, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttons.setBackground(CARD);
        buttons.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(15, 25, 15, 25)
        ));

        buttons.add(createButton("Tambah Menu", SUCCESS, e -> addMenu()));
        buttons.add(createButton("Update Menu", ACCENT, e -> updateMenu()));
        buttons.add(createButton("Hapus Menu", DANGER, e -> deleteMenu()));
        buttons.add(createButton("Clear Form", GREY, e -> clearForm()));

        form.add(buttons, BorderLayout.CENTER);
        return form;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int x, int y, String label, JComponent input) {
        gbc.gridy = y; gbc.gridx = x;
        JLabel lbl = createLabel(label, 14, Font.BOLD, new Color(52, 73, 94));
        panel.add(lbl, gbc);

        gbc.gridx = x + 1; gbc.weightx = 1;
        input.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        input.setPreferredSize(new Dimension(150, 38));
        input.setBackground(Color.WHITE);
        input.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(5, 10, 5, 10)
        ));
        panel.add(input, gbc);
    }

    private JButton createButton(String text, Color bg, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 40));
        btn.addActionListener(action);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(bg.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    private JLabel createLabel(String text, int size, int style, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", style, size));
        lbl.setForeground(color);
        return lbl;
    }

    private void styleTable(JTable t) {
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setRowHeight(40);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setSelectionBackground(new Color(52, 152, 219, 50));
        t.setShowVerticalLines(false);
        t.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader head = t.getTableHeader();
        head.setFont(new Font("Segoe UI", Font.BOLD, 14));
        head.setBackground(PRIMARY);
        head.setForeground(Color.WHITE);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        t.getColumnModel().getColumn(0).setCellRenderer(center);
        t.getColumnModel().getColumn(2).setCellRenderer(center);

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(JLabel.RIGHT);
        t.getColumnModel().getColumn(3).setCellRenderer(right);

        t.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && t.getSelectedRow() != -1) populateForm();
        });
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Menu> list = controller.daftarMenu;
        for (int i = 0; i < list.size(); i++) {
            Menu m = list.get(i);
            tableModel.addRow(new Object[]{i + 1, m.getNama(), m.getKategori(), String.format("%,d", (long) m.getHarga())});
        }
    }

    private void addMenu() {
        if (!validateFields()) return;
        try {
            controller.tambahMenu(namaField.getText(), Double.parseDouble(hargaField.getText()), (String) kategoriCombo.getSelectedItem());
            refreshTable(); clearForm();
            info("✓ Menu berhasil ditambahkan!");
        } catch (NumberFormatException e) { error("Harga harus berupa angka!"); }
    }

    private void updateMenu() {
        int i = menuTable.getSelectedRow();
        if (i == -1) {
            warn("Pilih menu yang ingin diupdate!");
            return;
        }
        if (!validateFields()) return;

        try {
            Menu m = controller.daftarMenu.get(i);
            m.setNama(namaField.getText());
            m.setKategori((String) kategoriCombo.getSelectedItem());
            m.setHarga(Double.parseDouble(hargaField.getText()));
            refreshTable(); clearForm();
            info("✓ Menu berhasil diupdate!");
        } catch (NumberFormatException e) { error("Harga harus berupa angka!"); }
    }

    private void deleteMenu() {
        int i = menuTable.getSelectedRow();
        if (i == -1) {
            warn("Pilih menu yang ingin dihapus!");
            return;
        };
        String nama = controller.daftarMenu.get(i).getNama();
        if (JOptionPane.showConfirmDialog(this, "Hapus menu '" + nama + "'?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.daftarMenu.remove(i);
            refreshTable(); clearForm();
            info("✓ Menu berhasil dihapus!");
        }
    }

    private boolean validateFields() {
        if (namaField.getText().isEmpty() || hargaField.getText().isEmpty()) {
            error("Semua field harus diisi!");
            return false;
        }
        return true;
    }

    private void clearForm() {
        namaField.setText("");
        hargaField.setText("");
        kategoriCombo.setSelectedIndex(0);
        menuTable.clearSelection();
    }

    private void populateForm() {
        int i = menuTable.getSelectedRow();
        if (i != -1) {
            Menu m = controller.daftarMenu.get(i);
            namaField.setText(m.getNama());
            kategoriCombo.setSelectedItem(m.getKategori());
            hargaField.setText(String.valueOf((long) m.getHarga()));
        }
    }

    // Dialog helper
    private void info(String msg) { JOptionPane.showMessageDialog(this, msg, "Berhasil", JOptionPane.INFORMATION_MESSAGE); }
    private void error(String msg) { JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); }
    private void warn(String msg) { JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE); }
}
