import java.sql.*;
import javax.swing.JOptionPane; 

public class Order {
    private int orderID;
    private int customerID;
    private String status;
    private Date orderDate;
    private Date deliveryDate;
    private double totalAmount;

    // Constructor
    public Order(int orderID, int customerID, String status, Date orderDate, Date deliveryDate, double totalAmount) {
        this.orderID = orderID;
        this.customerID = customerID;
        this.status = status;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    // Method to display order details
    @Override
    public String toString() {
        return "OrderID: " + orderID + ", CustomerID: " + customerID + ", Status: " + status + 
               ", OrderDate: " + orderDate + ", DeliveryDate: " + deliveryDate + ", TotalAmount: " + totalAmount;
    }

    public int saveOrder(Connection conn, int productId, int quantity) {
        try {
            conn.setAutoCommit(false); // Ξεκινάμε transaction
            
            // 1. Εισαγωγή στο OrderTable
            String insertOrderSQL = "INSERT INTO OrderTable (CustomerID, status, orderDate, DeliveryDate, totalAmount) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement orderStmt = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, customerID);
            orderStmt.setString(2, status);
            orderStmt.setDate(3, orderDate);
            orderStmt.setDate(4, deliveryDate);
            orderStmt.setDouble(5, totalAmount);
    
            int rows = orderStmt.executeUpdate();
            if (rows == 0) {
                conn.rollback();
                return -1; // Επιστρέφουμε -1 για να δείξουμε ότι απέτυχε
            }
    
            // Λαμβάνουμε το αυτόματα παραγόμενο OrderID
            ResultSet rs = orderStmt.getGeneratedKeys();
            if (rs.next()) {
                this.orderID = rs.getInt(1); // Ενημερώνουμε το orderID του αντικειμένου
            } else {
                conn.rollback();
                return -1; // Επιστρέφουμε -1 για να δείξουμε ότι απέτυχε
            }
    
            // 3. Εισαγωγή στο OrderItem - το totalAmount που έχουμε είναι ήδη το συνολικό ποσό (quantity * τιμή_προϊόντος)
            String insertItemSQL = "INSERT INTO OrderItem (orderID, productID, quantity, totalPrice) VALUES (?, ?, ?, ?)";
            PreparedStatement itemStmt = conn.prepareStatement(insertItemSQL, Statement.RETURN_GENERATED_KEYS);
            itemStmt.setInt(1, this.orderID);
            itemStmt.setInt(2, productId);
            itemStmt.setInt(3, quantity);
            itemStmt.setDouble(4, totalAmount); // Το totalAmount είναι ήδη το συνολικό ποσό
    
            int itemRows = itemStmt.executeUpdate();
            if (itemRows == 0) {
                conn.rollback();
                return -1; // Επιστρέφουμε -1 για να δείξουμε ότι απέτυχε
            }
    
            ResultSet itemRs = itemStmt.getGeneratedKeys();
            int orderItemID = -1;
            if (itemRs.next()) {
                orderItemID = itemRs.getInt(1);
            }
    
            // Ολοκλήρωση transaction
            conn.commit();
            
            // Δημιουργία αντικειμένου OrderItem (αν χρειάζεται)
            OrderItem orderItem = new OrderItem(orderItemID, this.orderID, productId, quantity, totalAmount);
    
            return this.orderID; // Επιστρέφουμε το OrderID που δημιουργήθηκε
    
        } catch (SQLException e) {
            try {
                // Σε περίπτωση σφάλματος, κάνουμε rollback
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return -1; // Επιστρέφουμε -1 για να δείξουμε ότι απέτυχε
        } finally {
            try {
                // Επαναφορά του autoCommit στην προεπιλεγμένη τιμή
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException autoCommitEx) {
                autoCommitEx.printStackTrace();
            }
        }
    }
    
    public void updateOrderStatus(int orderID, String status, Connection conn){
        if (status.equals("Completed")){
            String updateSql = "UPDATE OrderTable SET status = 'Completed' WHERE OrderID = ?";
        
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setInt(1, orderID);    // βάζουμε το orderID στο ερώτημα
                int rows = pstmt.executeUpdate();           // εκτελεί την ενημέρωση
                if (rows > 0) {
                    JOptionPane.showMessageDialog(null, "Order #" + orderID + " has been completed.");
                } else {
                    JOptionPane.showMessageDialog(null, "No Order found with ID=" + orderID);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,"Error updating order status.");
            }

        } else if (status.equals("Canceled")){
            String updateSql = "UPDATE OrderTable SET status = 'Canceled' WHERE OrderID = ?";
        
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setInt(1, orderID);    // βάζουμε το orderID στο ερώτημα
                int rows = pstmt.executeUpdate();           // εκτελεί την ενημέρωση
                if (rows > 0) {
                    JOptionPane.showMessageDialog(null, "Order #" + orderID + " has been cancelled.");
                } else {
                    JOptionPane.showMessageDialog(null, "No Order found with ID=" + orderID);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,"Error updating order status.");
            }
        }
    }
}