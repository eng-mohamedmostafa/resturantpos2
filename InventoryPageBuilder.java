package resturantpos2;

import java.awt.Component;

public class InventoryPageBuilder {
    private InventoryPage inventoryPage;

    public InventoryPageBuilder() {
        this.inventoryPage = new InventoryPage();
    }

    public InventoryPageBuilder setTitle(String title) {
        inventoryPage.setTitle(title);
        return this;
    }

    public InventoryPageBuilder setSize(int width, int height) {
        inventoryPage.setSize(width, height);
        return this;
    }

    public InventoryPageBuilder setLocationRelativeTo(Component c) {
        inventoryPage.setLocationRelativeTo(c);
        return this;
    }

    public InventoryPage build() {
        return inventoryPage;
    }
}
