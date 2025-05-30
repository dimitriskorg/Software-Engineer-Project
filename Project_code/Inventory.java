import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private Map<Product, Integer> stock;

    public Inventory() {
        stock = new HashMap<>();
    }

    public void addProduct(Product product, int quantity) {
        stock.put(product, stock.getOrDefault(product, 0) + quantity);
    }

    public boolean removeProduct(Product product, int quantity) {
        int currentStock = stock.getOrDefault(product, 0);
        if (currentStock < quantity) return false;
        stock.put(product, currentStock - quantity);
        return true;
    }

    public int getStock(Product product) {
        return stock.getOrDefault(product, 0);
    }

    public Map<Product, Integer> getAllStock() {
        return stock;
    }
}