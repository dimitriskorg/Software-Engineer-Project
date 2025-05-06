import java.sql.Date;

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
}