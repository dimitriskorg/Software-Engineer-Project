import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Expense {
    private int orderID;
    private double amount;
    private Date date;
    
    // Constructor
    public Expense(int orderID, double amount, Date date) {
        this.orderID = orderID;
        this.amount = amount;
        this.date = date;
    }
    
    // Default constructor
    public Expense() {
        this.orderID = 0;
        this.amount = 0.0;
        this.date = null;
    }
    
    // Getters
    public int getOrderID() {
        return orderID;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public Date getDate() {
        return date;
    }
    
    // Setters
    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    // Override toString method
    @Override
    public String toString() {
        return "Expense [orderID=" + orderID + ", amount=" + amount + ", date=" + date + "]";
    }
    
    // Override equals method
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Expense expense = (Expense) obj;
        return orderID == expense.orderID;
    }
    
    // Override hashCode method
    @Override
    public int hashCode() {
        return orderID;
    }

    public boolean insertToDatabase(Connection conn) {
        String query = "INSERT INTO Expenses (OrderID, amount, Date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, this.orderID);
            stmt.setDouble(2, this.amount);
            stmt.setDate(3, this.date);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}