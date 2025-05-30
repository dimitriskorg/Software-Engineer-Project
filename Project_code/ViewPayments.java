import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.sql.*;
import java.sql.Date;

public class ViewPayments extends JFrame {
    private JPanel paymentsListPanel;
    private JDialog detailsDialog;
    private JTextArea paymentDetailsArea;
    private int currentOrderID;
    
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
        JButton backButton = new JButton("Back");
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
        controlPanel.add(scrollPane, BorderLayout.CENTER);
        
        containerPanel.add(controlPanel);
        
        // Create the details dialog
        createDetailsDialog();
        
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
}