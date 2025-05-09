import java.sql.*;
import javax.swing.JOptionPane;

public class Customer {
    private int customerID;
    private String name;
    private String phone;
    private String email;
    private String address;

    // Constructor
    public Customer(int customerID, String name, String phone, String email, String address) {
        this.customerID = customerID;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    // Default constructor
    public Customer() {
    }

    // Getters and setters
    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return name + " (" + email + ")";
    }
    
    public static int addNewCustomer(Customer c, Connection conn) {
        String sql = "INSERT INTO Customer (name, phone, email, address) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, c.getName());
            stmt.setString(2, c.getPhone());
            stmt.setString(3, c.getEmail());
            stmt.setString(4, c.getAddress());
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                JOptionPane.showMessageDialog(null, "Δεν επιστράφηκε customerID από τη βάση.", 
                                             "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                return -1;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Σφάλμα κατά την προσθήκη πελάτη: " + e.getMessage(), 
                                         "Σφάλμα Βάσης Δεδομένων", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }
}