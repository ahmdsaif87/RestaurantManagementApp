package View;

import Controller.RestaurantController;
import javax.swing.SwingUtilities;

public class MainMenu {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            RestaurantController controller = new RestaurantController();
            MainFrame frame = new MainFrame(controller);
            frame.setVisible(true);
        });
    }
}

