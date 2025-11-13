package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import Controller.RestaurantController;
import Model.Order;

public class StrukPembayaran extends JDialog {
    private final RestaurantController controller;
    private JPanel strukPanel;

    // Warna utama
    private static final Color PRIMARY = new Color(41, 128, 185);
    private static final Color ACCENT = new Color(52, 152, 219);
    private static final Color SUCCESS = new Color(46, 204, 113);
    private static final Color BG = new Color(236, 240, 241);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT = new Color(52, 73, 94);

    public StrukPembayaran(Frame parent, RestaurantController controller) {
        super(parent, "Struk Pembayaran", true);
        this.controller = controller;
        initUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout(0, 15));
        main.setBackground(BG);
        main.setBorder(new EmptyBorder(15, 15, 15, 15));

        strukPanel = createStrukPanel();
        JScrollPane scroll = new JScrollPane(strukPanel);
        scroll.setPreferredSize(new Dimension(550, 600));
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        main.add(scroll, BorderLayout.CENTER);
        main.add(createButtonPanel(), BorderLayout.SOUTH);
        add(main);
    }

    private JPanel createStrukPanel() {
        JPanel p = panelY(CARD, 30, 30, 30, 30);

        // Header
        JLabel title = label("RESTORAN ABC", 28, PRIMARY, Font.BOLD);
        JLabel addr = label("Jl. Contoh No. 123, Kota", 12, TEXT, Font.PLAIN);
        JLabel phone = label("Telp: (021) 1234-5678", 12, TEXT, Font.PLAIN);
        p.add(centered(title, addr, phone));
        p.add(space(20));

        p.add(centered(label("Tanggal: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), 12, TEXT, Font.PLAIN)));
        p.add(space(15));
        p.add(line());
        p.add(space(15));

        // Detail pesanan
        p.add(label("DETAIL PESANAN", 14, TEXT, Font.BOLD));
        p.add(space(10));
        NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
        for (Order o : controller.daftarPesanan) {
            p.add(itemRow(o.getMenu().getNama(), o.getJumlah(), o.getMenu().getHarga(), o.getSubtotal(), nf));
            p.add(space(8));
        }

        p.add(space(10));
        p.add(line());
        p.add(space(15));

        // Perhitungan
        var res = controller.hitungDetailPembayaran();
        p.add(calcRow("Subtotal", res.subtotal, nf, false));
        p.add(calcRow("Pajak (10%)", res.pajak, nf, false));
        p.add(calcRow("Biaya Pelayanan", res.service, nf, false));
        p.add(calcRow("Diskon", res.diskon, nf, false));

        p.add(space(10));
        p.add(thickLine());
        p.add(space(10));
        p.add(calcRow("TOTAL BAYAR", res.total, nf, true));

        p.add(space(20));
        p.add(line());
        p.add(space(15));

        // Footer
        p.add(centered(label("Terima kasih atas kunjungan Anda!", 13, PRIMARY, Font.BOLD)));
        p.add(centered(label("Silakan datang kembali", 12, TEXT, Font.PLAIN)));

        return p;
    }

    private JPanel itemRow(String name, int qty, double price, double sub, NumberFormat nf) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CARD);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel left = new JLabel("<html>" + name + "<br><font color='#7F8C8D'>" +
                qty + " x Rp " + nf.format((long) price) + "</font></html>");
        left.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel right = new JLabel("Rp " + nf.format((long) sub));
        right.setFont(new Font("Segoe UI", Font.BOLD, 13));
        right.setForeground(TEXT);

        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    private JPanel calcRow(String label, double amount, NumberFormat nf, boolean total) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CARD);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        int size = total ? 16 : 13;
        JLabel l = label(label, size, total ? PRIMARY : TEXT, total ? Font.BOLD : Font.PLAIN);
        JLabel a = label("Rp " + nf.format((long) amount), total ? size + 2 : 13, total ? PRIMARY : TEXT, Font.BOLD);

        p.add(l, BorderLayout.WEST);
        p.add(a, BorderLayout.EAST);
        return p;
    }

    private JPanel createButtonPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        p.setBackground(BG);

        JButton save = button("Simpan sebagai JPG", ACCENT, e -> saveAsJPG());
        JButton pay = button("Bayar & Selesai", SUCCESS, e -> {
            if (JOptionPane.showConfirmDialog(this,
                    "Pembayaran selesai?\nPesanan akan dikosongkan.",
                    "Konfirmasi Pembayaran", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                controller.clearPesanan();
                JOptionPane.showMessageDialog(this, "✓ Pembayaran berhasil!\nTerima kasih!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        });
        JButton close = button("✕ Tutup", new Color(149, 165, 166), e -> dispose());

        p.add(save);
        p.add(pay);
        p.add(close);
        return p;
    }

    private JButton button(String text, Color color, java.awt.event.ActionListener act) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(180, 45));
        b.addActionListener(act);
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(color.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent e) { b.setBackground(color); }
        });
        return b;
    }

    private void saveAsJPG() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Simpan Struk sebagai JPG");
        fc.setSelectedFile(new File("Struk_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg"));
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JPG Image", "jpg", "jpeg"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = fc.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".jpg")) file = new File(file.getAbsolutePath() + ".jpg");

        try {
            BufferedImage img = new BufferedImage(strukPanel.getWidth(), strukPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            strukPanel.paint(g2);
            g2.dispose();
            ImageIO.write(img, "jpg", file);
            JOptionPane.showMessageDialog(this, "✓ Struk berhasil disimpan!\n" + file.getAbsolutePath(), "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 🔹 Utility Components
    private JPanel panelY(Color bg, int top, int left, int bottom, int right) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(bg);
        p.setBorder(new EmptyBorder(top, left, bottom, right));
        return p;
    }

    private JLabel label(String text, int size, Color color, int style) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(color);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private Component space(int h) { return Box.createRigidArea(new Dimension(0, h)); }

    private Component line() {
        JSeparator s = new JSeparator();
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        s.setForeground(new Color(189, 195, 199));
        return s;
    }

    private Component thickLine() {
        JPanel p = new JPanel();
        p.setBackground(PRIMARY);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 3));
        return p;
    }

    private JPanel centered(JComponent... comps) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(CARD);
        for (JComponent c : comps) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
            p.add(c);
        }
        return p;
    }
}
