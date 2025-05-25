import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Payment {
    private int paymentID;
    private int orderID;
    private String paymentMethod;
    private double amountPaid;
    private Date paymentDate;
    private String paymentStatus;

    // Default constructor
    public Payment() {}

    // Constructor with parameters
    public Payment(int paymentID, int orderID, String paymentMethod, double amountPaid, Date paymentDate, String paymentStatus) {
        this.paymentID = paymentID;
        this.orderID = orderID;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
        this.paymentDate = paymentDate;
        this.paymentStatus = paymentStatus;
    }

    // Getters and Setters
    public int getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(int paymentID) {
        this.paymentID = paymentID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getStatus() {
        return paymentStatus;
    }

    public void setStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public boolean insertToDatabase(Connection conn) {
        String sql = "INSERT INTO Payment (PaymentID, OrderID, paymentMethod, amountPaid, paymentDate, Status) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Ορίζουμε τις παραμέτρους του PreparedStatement
            stmt.setInt(1, this.paymentID);
            stmt.setInt(2, this.orderID);
            stmt.setString(3, this.paymentMethod);
            stmt.setDouble(4, this.amountPaid);
            stmt.setDate(5, this.paymentDate);
            stmt.setString(6, this.paymentStatus);

            // Εκτελούμε την εισαγωγή
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            // Εκτύπωση σφάλματος για debug και επιστροφή false
            e.printStackTrace();
            return false;
        }
    }
}