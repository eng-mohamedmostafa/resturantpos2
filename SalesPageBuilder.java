package resturantpos2;

import java.awt.Component;

public class SalesPageBuilder {
    private SalesPage salesPage;

    public SalesPageBuilder() {
        this.salesPage = new SalesPage();
    }

    public SalesPageBuilder setTitle(String title) {
        salesPage.setTitle(title);
        return this;
    }

    public SalesPageBuilder setSize(int width, int height) {
        salesPage.setSize(width, height);
        return this;
    }

    public SalesPageBuilder setLocationRelativeTo(Component c) {
        salesPage.setLocationRelativeTo(c);
        return this;
    }

    public SalesPage build() {
        return salesPage;
    }
}
