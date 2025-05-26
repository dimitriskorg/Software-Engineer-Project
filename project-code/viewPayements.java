import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class ViewPayments extends JFrame {
    private JPanel paymentsListPanel;
    private JDialog detailsDialog;
    private JDialog addPaymentDialog;
    private JTextArea paymentDetailsArea;
    private int currentOrderID;
    private ArrayList<OrderInfo> availableOrders;
    
    // Database connection variables
    public Connection conn;
    public User user;

    public ViewPayments(User user, Connection conn) {
        this.user = user;
        this.conn = conn;

        // Basic window setup
        setTitle("View Payments");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
                
        // Create header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(122, 156, 95)); // #7a9c5f
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        
        JLabel titleLabel = new JLabel("View Payments");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Fixed logout button - red background with white text
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(178, 34, 34)); // #b22222
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setOpaque(true);
        logoutButton.setContentAreaFilled(true);
        logoutButton.setBorderPainted(false);
        logoutButton.addActionListener(e -> System.exit(0));

        // Back button
        JButton backButton = new JButton("Πίσω");
        backButton.setBackground(new Color(194, 165, 108));
        backButton.setForeground(Color.WHITE);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            this.dispose();
            new HomePage(new User(user.getUserID(), user.getUsername(), 
                       user.getPassword(), user.getEmail(), user.getRole()), conn);
        });
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        // Create main container
        JPanel containerPanel = new JPanel();
        containerPanel.setBackground(new Color(244, 244, 244)); // #f4f4f4
        containerPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        
        // Add button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(244, 244, 244));
        
        JButton addPaymentButton = new JButton("Add Payment");
        addPaymentButton.setBackground(new Color(122, 156, 95)); // #7a9c5f
        addPaymentButton.setForeground(Color.WHITE);
        addPaymentButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addPaymentButton.setOpaque(true);
        addPaymentButton.setContentAreaFilled(true);
        addPaymentButton.setBorderPainted(false);
        addPaymentButton.addActionListener(e -> showAddPaymentDialog());
        buttonPanel.add(addPaymentButton);
        
        // Create payments list
        paymentsListPanel = new JPanel();
        paymentsListPanel.setLayout(new BoxLayout(paymentsListPanel, BoxLayout.Y_AXIS));
        paymentsListPanel.setBackground(new Color(244, 244, 244)); // #f4f4f4
        
        // Add payments dynamically from the database
        loadPayments();
        
        // Create scroll panel for payments list
        JScrollPane scrollPane = new JScrollPane(paymentsListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(new Color(244, 244, 244));
        controlPanel.add(buttonPanel, BorderLayout.NORTH);
        controlPanel.add(scrollPane, BorderLayout.CENTER);
        
        containerPanel.add(controlPanel);
        
        // Create the details dialog
        createDetailsDialog();
        
        // Create the add payment dialog
        createAddPaymentDialog();
        
        // Load available orders for the dropdown
        loadAvailableOrders();
        
        // Add all components to the frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(containerPanel, BorderLayout.CENTER);
    }
    
    private void loadPayments() {
        String query = "";
        
        // Only managers can view payments, just like with expenses
        if ("Manager".equals(user.getRole())) {
            query = "SELECT p.PaymentID, p.OrderID, p.paymentMethod, p.amountPaid, p.paymentDate, p.Status as paymentStatus, " +
                    "o.status as orderStatus, o.orderDate, o.deliveryDate, o.totalAmount, o.CustomerID, " +
                    "c.name, c.phone, c.email, c.address " +
                    "FROM Payment p " +
                    "INNER JOIN OrderTable o ON p.OrderID = o.OrderID " +
                    "INNER JOIN Customer c ON o.CustomerID = c.CustomerID";
        } else {
            JOptionPane.showMessageDialog(this, "You don't have permission to view payments", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Payment Info
                    int paymentID = rs.getInt("PaymentID");
                    int orderID = rs.getInt("OrderID");
                    String paymentMethod = rs.getString("paymentMethod");
                    double amountPaid = rs.getDouble("amountPaid");
                    Date paymentDate = rs.getDate("paymentDate");
                    String paymentStatus = rs.getString("paymentStatus");
                    
                    // Order Info
                    int customerID = rs.getInt("CustomerID");
                    String orderStatus = rs.getString("orderStatus");
                    Date orderDate = rs.getDate("orderDate");
                    Date deliveryDate = rs.getDate("deliveryDate");
                    double totalAmount = rs.getDouble("totalAmount");
                    
                    // Customer Info
                    String name = rs.getString("name");
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    String address = rs.getString("address");
    
                    // Create an Order object
                    Order order = new Order(orderID, customerID, orderStatus, orderDate, deliveryDate, totalAmount);
                    Customer customer = new Customer(customerID, name, phone, email, address);
                    
                    // Create a Payment object
                    Payment payment = new Payment(paymentID, orderID, paymentMethod, amountPaid, paymentDate, paymentStatus);
                    
                    // Show payment in the UI
                    showPayment(payment, order, customer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading payments: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showPayment(Payment payment, Order order, Customer customer) {
        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));
        paymentPanel.setBackground(Color.WHITE);
        paymentPanel.setBorder(new CompoundBorder(
            BorderFactory.createEmptyBorder(10, 0, 10, 0),
            BorderFactory.createCompoundBorder()
        ));
        paymentPanel.setMaximumSize(new Dimension(600, 200));
        paymentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Payment Info
        JLabel paymentIDLabel = new JLabel("<html><b>Payment #" + payment.getPaymentID() + " for Order #" + payment.getOrderID() + "</b></html>");
        JLabel amountLabel = new JLabel("Amount Paid: $" + payment.getAmountPaid());
        JLabel methodLabel = new JLabel("Payment Method: " + payment.getPaymentMethod());
        JLabel dateLabel = new JLabel("Payment Date: " + payment.getPaymentDate());
        JLabel statusLabel = new JLabel("Payment Status: " + payment.getStatus());
        
        // Order Info
        JLabel orderStatusLabel = new JLabel("Order Status: " + order.getStatus());
        
        // Customer Info
        JLabel customerNameLabel = new JLabel("Customer: " + customer.getName());
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.setBackground(Color.WHITE);
        
        // Button to view details
        JButton detailsButton = new JButton("View Details");
        detailsButton.setBackground(new Color(122, 156, 95)); // #7a9c5f
        detailsButton.setForeground(Color.WHITE);
        detailsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        detailsButton.setOpaque(true);
        detailsButton.setContentAreaFilled(true);
        detailsButton.setBorderPainted(false);
        detailsButton.addActionListener(e -> showDetailsPopup(payment.getOrderID()));
        buttonsPanel.add(detailsButton);
        
        // Check if 3 days have passed since the order date to show the notify button
        boolean showNotifyButton = false;
        if (order.getOrderDate() != null) {
            long orderTime = order.getOrderDate().getTime();
            long currentTime = System.currentTimeMillis();
            long threeDaysInMillis = 3 * 24 * 60 * 60 * 1000L; // 3 days in milliseconds
            
            if ((currentTime - orderTime) > threeDaysInMillis) {
                showNotifyButton = true;
            }
        }
        
        // Add Notify button if needed
        if (showNotifyButton) {
            JButton notifyButton = new JButton("Notify");
            notifyButton.setBackground(new Color(70, 130, 180)); // Steel blue
            notifyButton.setForeground(Color.WHITE);
            notifyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            notifyButton.setOpaque(true);
            notifyButton.setContentAreaFilled(true);
            notifyButton.setBorderPainted(false);
            notifyButton.addActionListener(e -> createNotification(payment, order, customer));
            buttonsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
            buttonsPanel.add(notifyButton);
        }

        // Add to panel
        paymentPanel.add(paymentIDLabel);
        paymentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        paymentPanel.add(amountLabel);
        paymentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        paymentPanel.add(methodLabel);
        paymentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        paymentPanel.add(dateLabel);
        paymentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        paymentPanel.add(statusLabel);
        paymentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        paymentPanel.add(orderStatusLabel);
        paymentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        paymentPanel.add(customerNameLabel);
        paymentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        paymentPanel.add(buttonsPanel);
        
        paymentsListPanel.add(paymentPanel);
        paymentsListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    
    private void createDetailsDialog() {
        detailsDialog = new JDialog(this, "Payment Details", true);
        detailsDialog.setSize(400, 300);
        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.setLayout(new BorderLayout());
    
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        JLabel titleLabel = new JLabel("Payment Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        // Text area for details
        paymentDetailsArea = new JTextArea(10, 30);
        paymentDetailsArea.setEditable(false);
        paymentDetailsArea.setLineWrap(true);
        paymentDetailsArea.setWrapStyleWord(true);
        paymentDetailsArea.setBackground(contentPanel.getBackground());
        paymentDetailsArea.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        JScrollPane detailsScrollPane = new JScrollPane(paymentDetailsArea);
        detailsScrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        detailsScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton closeButton = new JButton("Close");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> detailsDialog.setVisible(false));
    
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(detailsScrollPane);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(closeButton);
    
        detailsDialog.add(contentPanel, BorderLayout.CENTER);
    }
    
    private void showDetailsPopup(int orderID) {
        this.currentOrderID = orderID;
        getPaymentDetails(orderID);
        detailsDialog.setTitle("Payment Details for Order #" + orderID);
        detailsDialog.setVisible(true);
    }

    private void getPaymentDetails(int orderID) {
        String query = "SELECT p.PaymentID, p.OrderID, p.paymentMethod, p.amountPaid, p.paymentDate, p.Status as paymentStatus, " +
                      "o.status as orderStatus, o.orderDate, o.deliveryDate, o.totalAmount, o.CustomerID, " +
                      "c.name, c.phone, c.email, c.address " +
                      "FROM Payment p " +
                      "INNER JOIN OrderTable o ON p.OrderID = o.OrderID " +
                      "INNER JOIN Customer c ON o.CustomerID = c.CustomerID " +
                      "WHERE p.OrderID = ?";
                      
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Payment Info
                    int paymentID = rs.getInt("PaymentID");
                    String paymentMethod = rs.getString("paymentMethod");
                    double amountPaid = rs.getDouble("amountPaid");
                    Date paymentDate = rs.getDate("paymentDate");
                    String paymentStatus = rs.getString("paymentStatus");
                    
                    // Order Info
                    String orderStatus = rs.getString("orderStatus");
                    Date orderDate = rs.getDate("orderDate");
                    Date deliveryDate = rs.getDate("deliveryDate");
                    double totalAmount = rs.getDouble("totalAmount");
                    
                    // Customer Info
                    String name = rs.getString("name");
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    String address = rs.getString("address");

                    StringBuilder details = new StringBuilder();
                    details.append("PAYMENT INFORMATION\n");
                    details.append("-------------------\n");
                    details.append("Payment ID: ").append(paymentID).append("\n");
                    details.append("Order ID: ").append(orderID).append("\n");
                    details.append("Payment Method: ").append(paymentMethod).append("\n");
                    details.append("Amount Paid: $").append(amountPaid).append("\n");
                    details.append("Payment Date: ").append(paymentDate).append("\n");
                    details.append("Payment Status: ").append(paymentStatus).append("\n\n");
                    
                    details.append("ORDER INFORMATION\n");
                    details.append("----------------\n");
                    details.append("Status: ").append(orderStatus).append("\n");
                    details.append("Order Date: ").append(orderDate).append("\n");
                    details.append("Delivery Date: ").append(deliveryDate).append("\n");
                    details.append("Total Amount: $").append(totalAmount).append("\n\n");
                    
                    details.append("CUSTOMER INFORMATION\n");
                    details.append("-------------------\n");
                    details.append("Name: ").append(name).append("\n");
                    details.append("Phone: ").append(phone).append("\n");
                    details.append("Email: ").append(email).append("\n");
                    details.append("Address: ").append(address);

                    paymentDetailsArea.setText(details.toString());
                } else {
                    paymentDetailsArea.setText("No information found for this payment.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            paymentDetailsArea.setText("Error fetching payment details: " + e.getMessage());
        }
    }
    
    // Helper class for the orders in dropdown
    private class OrderInfo {
        private int orderID;
        private String customerName;
        private double totalAmount;
        private Date orderDate;
        
        public OrderInfo(int orderID, String customerName, double totalAmount, Date orderDate) {
            this.orderID = orderID;
            this.customerName = customerName;
            this.totalAmount = totalAmount;
            this.orderDate = orderDate;
        }
        
        public int getOrderID() {
            return orderID;
        }
        
        @Override
        public String toString() {
            return "Order #" + orderID + " - " + customerName + " - $" + totalAmount + " (" + orderDate + ")";
        }
    }
    
    private void loadAvailableOrders() {
        availableOrders = new ArrayList<>();
        
        // Load all orders that don't already have a payment
        String query = "SELECT o.OrderID, o.totalAmount, o.orderDate, c.name " +
                       "FROM OrderTable o " +
                       "INNER JOIN Customer c ON o.CustomerID = c.CustomerID " +
                       "WHERE o.OrderID NOT IN (SELECT OrderID FROM Payment) " +
                       "AND o.status IN ('Processing', 'Completed')"; // Orders that can receive payments
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                int orderID = rs.getInt("OrderID");
                String customerName = rs.getString("name");
                double totalAmount = rs.getDouble("totalAmount");
                Date orderDate = rs.getDate("orderDate");
                
                availableOrders.add(new OrderInfo(orderID, customerName, totalAmount, orderDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading available orders: " + e.getMessage(), 
                                         "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void createAddPaymentDialog() {
        addPaymentDialog = new JDialog(this, "Add New Payment", true);
        addPaymentDialog.setSize(450, 400);
        addPaymentDialog.setLocationRelativeTo(this);
        addPaymentDialog.setLayout(new BorderLayout());
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Add New Payment");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Form fields
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Order ID (dropdown)
        JLabel orderLabel = new JLabel("Order:");
        JComboBox<OrderInfo> orderComboBox = new JComboBox<>();
        
        // Payment Method
        JLabel methodLabel = new JLabel("Payment Method:");
        String[] methods = {"Credit Card", "Cash", "Bank Transfer", "PayPal"};
        JComboBox<String> methodComboBox = new JComboBox<>(methods);
        
        // Amount
        JLabel amountLabel = new JLabel("Amount ($):");
        JTextField amountField = new JTextField(10);
        
        // Date
        JLabel dateLabel = new JLabel("Date (yyyy-MM-dd):");
        JTextField dateField = new JTextField(10);
        // Default to current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateField.setText(dateFormat.format(new java.util.Date()));
        
        // Status
        JLabel statusLabel = new JLabel("Status:");
        String[] statuses = {"Completed", "Pending", "Failed"};
        JComboBox<String> statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setSelectedItem("Completed"); // Default status
        
        formPanel.add(orderLabel);
        formPanel.add(orderComboBox);
        formPanel.add(methodLabel);
        formPanel.add(methodComboBox);
        formPanel.add(amountLabel);
        formPanel.add(amountField);
        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(statusLabel);
        formPanel.add(statusComboBox);
        
        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(122, 156, 95)); // #7a9c5f
        saveButton.setForeground(Color.WHITE);
        saveButton.setOpaque(true);
        saveButton.setContentAreaFilled(true);
        saveButton.setBorderPainted(false);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(178, 34, 34)); // #b22222
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setOpaque(true);
        cancelButton.setContentAreaFilled(true);
        cancelButton.setBorderPainted(false);
        cancelButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(addPaymentDialog, "Payment not saved", "Canceled", JOptionPane.INFORMATION_MESSAGE);
            addPaymentDialog.setVisible(false);
        });
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add all components
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(formPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(buttonPanel);
        
        addPaymentDialog.add(contentPanel, BorderLayout.CENTER);
        
        // Event listeners
        cancelButton.addActionListener(e -> addPaymentDialog.setVisible(false));
        
        saveButton.addActionListener(e -> {
            // Validation
            if (orderComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(addPaymentDialog, "Please select an order", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(addPaymentDialog, "Amount is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double amount;
            try {
                amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(addPaymentDialog, "Amount must be greater than zero", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addPaymentDialog, "Invalid amount format. Please enter a valid number", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String dateText = dateField.getText().trim();
            if (dateText.isEmpty()) {
                JOptionPane.showMessageDialog(addPaymentDialog, "Date is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Date paymentDate;
            try {
                java.util.Date parsedDate = dateFormat.parse(dateText);
                paymentDate = new Date(parsedDate.getTime());
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(addPaymentDialog, "Invalid date format. Please use yyyy-MM-dd", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get selected order and other values
            OrderInfo selectedOrder = (OrderInfo) orderComboBox.getSelectedItem();
            int orderID = selectedOrder.getOrderID();
            String paymentMethod = (String) methodComboBox.getSelectedItem();
            String status = (String) statusComboBox.getSelectedItem();
            
            // Generate a payment ID (Note: In a real application, this should be handled by the database)
            int paymentID = generatePaymentID();
            
            // Create Payment object
            Payment payment = new Payment(paymentID, orderID, paymentMethod, amount, paymentDate, status);
            
            // Insert to database
            if (payment.insertToDatabase(conn)) {
                JOptionPane.showMessageDialog(addPaymentDialog, "Payment added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                addPaymentDialog.setVisible(false);
                
                // Refresh the payment list and available orders
                refreshPaymentList();
            }
        });
    }
    
    // Generate a unique payment ID
    private int generatePaymentID() {
        int newID = 1; // Default starting ID
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(PaymentID) as maxID FROM Payment")) {
            
            if (rs.next()) {
                newID = rs.getInt("maxID") + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return newID;
    }
    
    private void showAddPaymentDialog() {
        // Refresh the list of available orders
        loadAvailableOrders();
        
        JComboBox<OrderInfo> orderComboBox = (JComboBox<OrderInfo>) 
            ((JPanel)((JPanel)addPaymentDialog.getContentPane().getComponent(0)).getComponent(2)).getComponent(1);
            
        // Clear and add available orders
        orderComboBox.removeAllItems();
        for (OrderInfo order : availableOrders) {
            orderComboBox.addItem(order);
        }
        
        // If no available orders
        if (availableOrders.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No available orders to add payments. Only orders with status 'Processing' or 'Completed' without existing payments can be selected.",
                "No Available Orders", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        addPaymentDialog.setVisible(true);
    }
    
    // Method to create a notification for a payment
    private void createNotification(Payment payment, Order order, Customer customer) {
        // Create dialog for confirmation
        JDialog notifyDialog = new JDialog(this, "Create Payment Notification", true);
        notifyDialog.setSize(400, 300);
        notifyDialog.setLocationRelativeTo(this);
        notifyDialog.setLayout(new BorderLayout());
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Create Payment Notification");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create message area
        JLabel messageLabel = new JLabel("Notification Message:");
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Default message text
        String defaultMessage = "Reminder: Payment #" + payment.getPaymentID() + 
                               " for Order #" + payment.getOrderID() + 
                               " processed on " + payment.getPaymentDate() + 
                               " with status: " + payment.getStatus();
        
        JTextArea messageArea = new JTextArea(defaultMessage);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setRows(5);
        
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton sendButton = new JButton("Send Notification");
        sendButton.setBackground(new Color(122, 156, 95)); // #7a9c5f
        sendButton.setForeground(Color.WHITE);
        sendButton.setOpaque(true);
        sendButton.setContentAreaFilled(true);
        sendButton.setBorderPainted(false);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(178, 34, 34)); // #b22222
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setOpaque(true);
        cancelButton.setContentAreaFilled(true);
        cancelButton.setBorderPainted(false);
        
        buttonPanel.add(sendButton);
        buttonPanel.add(cancelButton);
        
        // Add components to panel
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(messageLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(buttonPanel);
        
        notifyDialog.add(contentPanel, BorderLayout.CENTER);
        
        // Add event listeners
        sendButton.addActionListener(e -> {
            String message = messageArea.getText().trim();
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(notifyDialog, "Please enter a notification message", 
                                            "Empty Message", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create notification in database
            if (saveNotification(payment.getPaymentID(), message, customer.getCustomerID())) {
                JOptionPane.showMessageDialog(notifyDialog, "Notification sent successfully", 
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                notifyDialog.setVisible(false);
            }
        });
        
        cancelButton.addActionListener(e -> notifyDialog.setVisible(false));
        
        notifyDialog.setVisible(true);
    }
    
    // Method to save notification to database
    private boolean saveNotification(int paymentID, String message, int customerID) {
        String insertQuery = "INSERT INTO Notifications (recipientID, Message, SendDate) " +
        "VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setInt(1, customerID); // recipientID
            stmt.setString(2, message); // Message
            stmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis())); // SendDate
        
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Σφάλμα κατά την αποθήκευση ειδοποίησης: " + e.getMessage(),
                                          "Σφάλμα Βάσης Δεδομένων", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // Method to refresh the payment list
    private void refreshPaymentList() {
        // Clear existing payments
        paymentsListPanel.removeAll();
        
        // Reload from database
        loadPayments();
        
        // Refresh UI
        paymentsListPanel.revalidate();
        paymentsListPanel.repaint();
        
        // Reload available orders for add payment dialog
        loadAvailableOrders();
    }
}