import java.sql.*;

import javax.swing.JOptionPane;

public class Product {
    private int productID;
    private String name;
    private String description;
    private double price;
    private String category;

    public Product(int productID, String name, String description, double price, String category) {
        this.productID = productID;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    // Getters
    public int getProductID() {
        return productID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    // Προαιρετικά: Setters, toString()
    @Override
    public String toString() {
        return name + " (€" + price + ")";
    }

    public static int addNewProduct(Product p, Connection conn) {
        String sql = "INSERT INTO Product (name, description, price, category) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, p.getName());
            stmt.setString(2, p.getDescription());
            stmt.setDouble(3, p.getPrice());
            stmt.setString(4, p.getCategory());
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                JOptionPane.showMessageDialog(null, "Δεν επιστράφηκε productID από τη βάση.", 
                                             "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                return -1;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Σφάλμα κατά την προσθήκη προϊόντος: " + e.getMessage(), 
                                         "Σφάλμα Βάσης Δεδομένων", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }
}