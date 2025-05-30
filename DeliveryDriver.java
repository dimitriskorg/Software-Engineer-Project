import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class DeliveryDriver extends User {
    private int driverID;
    private String name;
    private String phone;
    private String licenseNumber;
    private int assignedOrders;

    // Constructors
    public DeliveryDriver() {
        // Default constructor
    }

    public DeliveryDriver(int driverID, int userID, String username, String password, String email, String role, String name, String phone, String licenseNumber, int assignedOrders) {
        super(userID, username, password, email, role); // Call to the superclass (User) constructor
        this.driverID = driverID;
        this.name = name;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.assignedOrders = assignedOrders;
    }

    // Getters and Setters
    public int getDriverID() {
        return driverID;
    }

    public void setDriverID(int driverID) {
        this.driverID = driverID;
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

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public int getAssignedOrders() {
        return assignedOrders;
    }

    public void setAssignedOrders(int assignedOrders) {
        this.assignedOrders = assignedOrders;
    }
    
    @Override
    public String toString() {
        return super.getUsername() + " DriverID: " + driverID + " UserID: " + super.getUserID() + " Phone: " + phone;
    }
}
