package resturantpos2;

import javax.swing.JFrame;

public class PageFactory {
    public static JFrame createPage(String pageType) {
        switch (pageType) {
            case "SalesPage":
                return new SalesPageBuilder()
                    .setTitle("Sales")
                    .setSize(800, 600)
                    .setLocationRelativeTo(null)
                    .build();
            case "InventoryPage":
                return new InventoryPageBuilder()
                    .setTitle("Inventory Management")
                    .setSize(800, 600)
                    .setLocationRelativeTo(null)
                    .build();
            case "ReportPage":
                return new ReportPage();
            case "Dashboard":
                return new Dashboard();
            case "LogoutPage":
                return new LogoutPage();
            case "LoginForm":
                return new LoginForm();
            default:
                throw new IllegalArgumentException("Unknown page type: " + pageType);

        }
    }
}
