package resturantpos2;

public class ProductAdapterImpl implements ProductAdapter {
    private Product product;

    public ProductAdapterImpl(Product product) {
        this.product = product;
    }

    @Override
    public String getProductName() {
        return product.getName();
    }

    @Override
    public String getProductCategory() {
        return product.getCategory();
    }

    @Override
    public double getProductPrice() {
        return product.getPrice();
    }
}
