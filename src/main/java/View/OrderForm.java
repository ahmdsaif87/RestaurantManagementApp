package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import Controller.RestaurantController;
import Model.Menu;
import Model.Order;

public class OrderForm extends JFrame {
    private final RestaurantController controller;
    private JTable menuTable, orderTable;
    private DefaultTableModel menuModel, orderModel;
    private JTextField qtyField;
    private JLabel totalLabel;

    // Warna tema
    private final Color PRIMARY = new Color(41, 128, 185);
    private final Color SUCCESS = new Color(46, 204, 113);
    private final Color WARNING = new Color(231, 76, 60);
    private final Color ORANGE = new Color(230, 126, 34);
    private final Color BACKGROUND = new Color(236, 240, 241);
    private final Color CARD = Color.WHITE;
    private final Color BORDER = new Color(189, 195, 199);

    public OrderForm(RestaurantController controller) {
        super("Form Pemesanan");
        this.controller = controller;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);
        initUI();
    }

    private void initUI() {
        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout(10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel main = new JPanel(new BorderLayout(0, 15));
        main.setBackground(BACKGROUND);
        main.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Center berisi dua tabel
        main.add(createCenterPanel(), BorderLayout.CENTER);

        // Panel bawah (total + kontrol)
        JPanel bottom = new JPanel(new BorderLayout(0, 12));
        bottom.setBackground(BACKGROUND);
        bottom.add(createTotalPanel(), BorderLayout.NORTH);
        bottom.add(createControlPanel(), BorderLayout.CENTER);

        main.add(bottom, BorderLayout.SOUTH);
        add(main, BorderLayout.CENTER);

        // 🔥 Setelah semua UI siap, baru refresh data
        refreshMenu();
        refreshOrder();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY);
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JButton back = styledButton("Kembali", new Color(52, 73, 94));
        back.addActionListener(e -> {
            dispose();
            new MainFrame(controller).setVisible(true);
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        top.setOpaque(false);
        top.add(back);

        JLabel title = new JLabel("Panel Order", JLabel.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Buat Orderan dan cetak struk", JLabel.LEFT);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(Color.WHITE);

        Box box = Box.createVerticalBox();
        box.add(top);
        box.add(Box.createVerticalStrut(10));
        box.add(title);
        box.add(subtitle);

        panel.add(box, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBackground(BACKGROUND);
        panel.add(createTablePanel("Daftar Menu", true));
        panel.add(createTablePanel("Pesanan Anda", false));
        return panel;
    }

    private JPanel createTablePanel(String title, boolean isMenu) {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(new Color(52, 73, 94));

        String[] cols = isMenu
                ? new String[]{"No", "Nama Menu", "Kategori", "Harga (Rp)"}
                : new String[]{"No", "Nama Menu", "Qty", "Harga/unit", "Subtotal"};

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        styleTable(table);

        if (isMenu) {
            menuModel = model;
            menuTable = table;
        } else {
            orderModel = model;
            orderTable = table;
        }

        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTotalPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.setBackground(BACKGROUND);

        JPanel box = new JPanel(new BorderLayout(15, 0));
        box.setBackground(new Color(241, 196, 15));
        box.setBorder(new CompoundBorder(
                new LineBorder(new Color(243, 156, 18), 3),
                new EmptyBorder(15, 30, 15, 30)));

        JLabel text = new JLabel("TOTAL PEMBAYARAN (BELUM TAX DAN SERVICE):");
        text.setFont(new Font("Segoe UI", Font.BOLD, 16));
        text.setForeground(new Color(52, 73, 94));

        totalLabel = new JLabel("Rp 0");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        totalLabel.setForeground(new Color(52, 73, 94));

        box.add(text, BorderLayout.WEST);
        box.add(totalLabel, BorderLayout.EAST);
        panel.add(box);
        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = borderedPanel(CARD, new EmptyBorder(18, 25, 18, 25));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setBackground(CARD);

        JLabel qtyLbl = new JLabel("Jumlah:");
        qtyLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

        qtyField = new JTextField("1", 5);
        qtyField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        qtyField.setPreferredSize(new Dimension(70, 40));
        qtyField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(5, 12, 5, 12)));

        JButton add = styledButton("Tambah ke Pesanan", SUCCESS);
        add.addActionListener(e -> addMenu());
        JButton remove = styledButton("Hapus Item", WARNING);
        remove.addActionListener(e -> removeOrder());
        JButton clear = styledButton("Bersihkan Semua", new Color(149, 165, 166));
        clear.addActionListener(e -> clearOrders());

        left.add(qtyLbl);
        left.add(qtyField);
        left.add(add);
        left.add(remove);
        left.add(clear);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setBackground(CARD);

        JButton checkout = styledButton("Checkout", ORANGE);
        checkout.setFont(new Font("Segoe UI", Font.BOLD, 16));
        checkout.setPreferredSize(new Dimension(200, 50));
        checkout.addActionListener(e -> doCheckout());
        right.add(checkout);

        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    // === Util ===
    private JPanel borderedPanel(Color bg, EmptyBorder padding) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bg);
        p.setBorder(new CompoundBorder(new LineBorder(BORDER, 1), padding));
        return p;
    }

    private JButton styledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(bg.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }

    private void styleTable(JTable t) {
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setRowHeight(40);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 14));
        h.setBackground(PRIMARY);
        h.setForeground(Color.WHITE);
        ((DefaultTableCellRenderer) h.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(JLabel.RIGHT);
        t.getColumnModel().getColumn(0).setCellRenderer(center);
    }

    // === Data Handling ===
    private void refreshMenu() {
        if (menuModel == null) return;
        menuModel.setRowCount(0);
        int i = 1;
        for (Menu m : controller.daftarMenu)
            menuModel.addRow(new Object[]{i++, m.getNama(), m.getKategori(), String.format("%,d", (long) m.getHarga())});
    }

    private void refreshOrder() {
        if (orderModel == null || totalLabel == null) return;
        orderModel.setRowCount(0);
        int i = 1;
        for (Order o : controller.daftarPesanan)
            orderModel.addRow(new Object[]{i++, o.getMenu().getNama(), o.getJumlah(),
                    String.format("%,d", (long) o.getMenu().getHarga()),
                    String.format("%,d", (long) o.getSubtotal())});
        updateTotal();
    }

    private void updateTotal() {
        double total = controller.daftarPesanan.stream().mapToDouble(Order::getSubtotal).sum();
        totalLabel.setText(String.format("Rp %,d", (long) total));
    }

    private void addMenu() {
        int sel = menuTable.getSelectedRow();
        if (sel == -1) {
            warn("Pilih menu yang ingin ditambahkan ke pesanan!");
            return;
        };
        try {
            int qty = Integer.parseInt(qtyField.getText().trim());
            if (qty <= 0) throw new NumberFormatException();
            Menu m = controller.daftarMenu.get(sel);
            controller.tambahPesanan(m, qty);
            refreshOrder();
            qtyField.setText("1");
            info(String.format("✓ %s (x%d) ditambahkan ke pesanan", m.getNama(), qty));
        } catch (NumberFormatException e) {
            error("Masukkan jumlah yang valid (angka > 0).");
        }
    }

    private void removeOrder() {
        int sel = orderTable.getSelectedRow();
        if (sel == -1) {
            warn("Pilih item pesanan yang ingin dihapus!");
            return;
        };
        String item = controller.daftarPesanan.get(sel).getMenu().getNama();
        int c = JOptionPane.showConfirmDialog(this, "Hapus '" + item + "' dari pesanan?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            controller.daftarPesanan.remove(sel);
            refreshOrder();
        }
    }

    private void clearOrders() {
        if (controller.daftarPesanan.isEmpty()) {
            warn("Tidak ada pesanan untuk dihapus.");
            return;
        }
        int c = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus semua pesanan?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            controller.clearPesanan();
            refreshOrder();
        }
    }

    private void doCheckout() {
        if (controller.daftarPesanan.isEmpty()) {
            warn("Tidak ada pesanan untuk checkout.");
            return;
        }
        new StrukPembayaran(this, controller).setVisible(true);
        refreshOrder();
    }

    // === Dialog Util ===
    private void info(String msg) { JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE); }
    private void warn(String msg) { JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE); }
    private void error(String msg) { JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); }
}
