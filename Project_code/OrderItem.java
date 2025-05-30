public class OrderItem {
    private int orderItemID;
    private int orderID;
    private int productID;
    private int quantity;
    private double totalPrice;
    private String productName;
    private String description;
    private double unitPrice;

    // Full constructor with all fields
    public OrderItem(int orderItemID, int orderID, int productID, int quantity, 
                    double totalPrice, String productName, String description, double unitPrice) {
        this.orderItemID = orderItemID;
        this.orderID = orderID;
        this.productID = productID;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.productName = productName;
        this.description = description;
        this.unitPrice = unitPrice;
    }

    // Simplified constructor (matching your second class)
    public OrderItem(int orderItemID, int orderID, int productID, int quantity, double totalPrice) {
        this(orderItemID, orderID, productID, quantity, totalPrice, null, null, 0.0);
    }

    // Constructor with basic product info
    public OrderItem(int orderItemID, int orderID, int productID, int quantity, 
                    String productName, double unitPrice) {
        this.orderItemID = orderItemID;
        this.orderID = orderID;
        this.productID = productID;
        this.quantity = quantity;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice; // Auto-calculate total
        this.description = null;
    }

    // Getters
    public int getOrderItemID() {
        return orderItemID;
    }

    public int getOrderID() {
        return orderID;
    }

    public int getProductID() {
        return productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getProductName() {
        return productName;
    }

    public String getDescription() {
        return description;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    // Setters
    public void setOrderItemID(int orderItemID) {
        this.orderItemID = orderItemID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        // Optionally recalculate total price if unit price is available
        if (this.unitPrice > 0) {
            this.totalPrice = quantity * this.unitPrice;
        }
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        // Optionally recalculate total price
        if (this.quantity > 0) {
            this.totalPrice = this.quantity * unitPrice;
        }
    }

    // Utility method to calculate total price
    public void calculateTotalPrice() {
        if (quantity > 0 && unitPrice > 0) {
            this.totalPrice = quantity * unitPrice;
        }
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemID=" + orderItemID +
                ", orderID=" + orderID +
                ", productID=" + productID +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                (productName != null ? ", productName='" + productName + '\'' : "") +
                (description != null ? ", description='" + description + '\'' : "") +
                (unitPrice > 0 ? ", unitPrice=" + unitPrice : "") +
                '}';
    }
}