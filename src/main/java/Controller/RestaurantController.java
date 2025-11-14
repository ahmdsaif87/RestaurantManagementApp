package Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Model.Menu;
import Model.Order;

public class RestaurantController {
    public ArrayList<Menu> daftarMenu = new ArrayList<>();
    public ArrayList<Order> daftarPesanan = new ArrayList<>();

    public RestaurantController() {
        initDefaultMenu();
    }

    private void initDefaultMenu() {
        Object[][] dataMenu = {
            {"Nasi Goreng", 25000, "Makanan"},
            {"Mie Ayam", 20000, "Makanan"},
            {"Ayam Geprek", 28000, "Makanan"},
            {"Sate Ayam", 30000, "Makanan"},
            {"Es Teh", 8000, "Minuman"},
            {"Jus Alpukat", 15000, "Minuman"},
            {"Kopi Hitam", 12000, "Minuman"},
            {"Air Mineral", 5000, "Minuman"}
        };

        for (Object[] item : dataMenu) {
            daftarMenu.add(new Menu(
                (String) item[0],
                (double) (int) item[1],  // harga
                (String) item[2]         // kategori
            ));
        }
    }

    // Menu management
    public void tambahMenu(String nama, double harga, String kategori) {
        daftarMenu.add(new Menu(nama, harga, kategori));
    }

    public boolean hapusMenuAt(int index) {
        if (index >= 0 && index < daftarMenu.size()) {
            daftarMenu.remove(index);
            return true;
        }
        return false;
    }

    public boolean ubahHargaMenuAt(int index, double hargaBaru) {
        if (index >= 0 && index < daftarMenu.size()) {
            daftarMenu.get(index).setHarga(hargaBaru);
            return true;
        }
        return false;
    }

    // Order management
    public void tambahPesanan(Menu menu, int jumlah) {
        // if same menu exists in orders, accumulate jumlah
        for (Order o : daftarPesanan) {
            if (o.getMenu().equals(menu)) {
                o.setJumlah(o.getJumlah() + jumlah);
                return;
            }
        }
        daftarPesanan.add(new Order(menu, jumlah));
    }

    public void clearPesanan() {
        daftarPesanan.clear();
    }

    public double hitungSubtotal() {
        double total = 0;
        for (Order o : daftarPesanan) total += o.getSubtotal();
        return total;
    }

    public CalculationResult hitungDetailPembayaran() {
        double subtotal = hitungSubtotal();
        double pajak = subtotal * 0.10; // 10%
        double service = 20000; // flat rate
        double diskon = 0;

        // Diskon 10% jika subtotal > 100000
        if (subtotal > 100000) {
            diskon += subtotal * 0.10;
        }

        // Promo Beli 1 Gratis 1 (kategori minuman) jika subtotal > 50000
        double promoMinumanDiskon = 0;
        if (subtotal > 50000) {
            // Buat list unit-price per minuman (repeat by jumlah)
            List<Double> unitMinuman = new ArrayList<>();
            for (Order o : daftarPesanan) {
                if ("Minuman".equalsIgnoreCase(o.getMenu().getKategori())) {
                    for (int i = 0; i < o.getJumlah(); i++) {
                        unitMinuman.add(o.getMenu().getHarga());
                    }
                }
            }
            // sort ascending, free count = floor(n/2) (beli 1 gratis 1)
            Collections.sort(unitMinuman);
            int freeCount = unitMinuman.size() / 2;
            for (int i = 0; i < freeCount; i++) {
                // berikan gratis item yang harganya paling murah dulu
                promoMinumanDiskon += unitMinuman.get(i);
            }
        }

        diskon += promoMinumanDiskon;

        double total = subtotal + pajak + service - diskon;
        if (total < 0) total = 0;

        return new CalculationResult(subtotal, pajak, service, diskon, total);
    }

    // For UI convenience
    public static class CalculationResult {
        public final double subtotal;
        public final double pajak;
        public final double service;
        public final double diskon;
        public final double total;

        public CalculationResult(double subtotal, double pajak, double service, double diskon, double total) {
            this.subtotal = subtotal;
            this.pajak = pajak;
            this.service = service;
            this.diskon = diskon;
            this.total = total;
        }
    }
}
