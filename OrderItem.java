public class OrderItem {
    private int orderItemID;
    private int orderID;
    private int productID;
    private int quantity;
    private double totalPrice;

    public OrderItem(int orderItemID, int orderID, int productID, int quantity, double totalPrice) {
        this.orderItemID = orderItemID;
        this.orderID = orderID;
        this.productID = productID;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    // Getters
    public int getOrderItemID() { return orderItemID; }
    public int getOrderID() { return orderID; }
    public int getProductID() { return productID; }
    public int getQuantity() { return quantity; }
    public double getTotalPrice() { return totalPrice; }

    // Optionally, you can add toString() for debug
    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemID=" + orderItemID +
                ", orderID=" + orderID +
                ", productID=" + productID +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
