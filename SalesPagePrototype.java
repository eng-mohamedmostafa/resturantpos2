package resturantpos2;

public class SalesPagePrototype extends SalesPage implements Prototype<SalesPagePrototype> {
    @Override
    public SalesPagePrototype clone() {
        return new SalesPagePrototype();
    }
}
