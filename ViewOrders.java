import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class ViewOrders extends JFrame {
    private JPanel orderListPanel;
    private JDialog assignDialog;
    private JDialog statusDialog;
    private JTextArea orderDetailsArea;
    private int currentOrderID; // Προσθήκη μεταβλητής για αποθήκευση του τρέχοντος OrderID

    // Database connection variables
    public Connection conn;
    private List<DeliveryDriver> deliveryDrivers = new ArrayList<>();
    public User user;

    public ViewOrders(User user, Connection conn) {
        this.user = user;
        this.conn = conn;

        // Basic window setup
        setTitle("View Orders");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(122, 156, 95)); // #7a9c5f
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel titleLabel = new JLabel("View Orders");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        // Fixed logout button - red background with white text
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(178, 34, 34)); // #b22222
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setOpaque(true);
        logoutButton.setContentAreaFilled(true);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> System.exit(0));

        // Back button
        JButton backButton = new JButton("Back");
        backButton.setBackground(new Color(194, 165, 108));
        backButton.setForeground(Color.WHITE);
        backButton.setOpaque(true);
        backButton.setContentAreaFilled(true);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
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

        // Create order list
        orderListPanel = new JPanel();
        orderListPanel.setLayout(new BoxLayout(orderListPanel, BoxLayout.Y_AXIS));
        orderListPanel.setBackground(new Color(244, 244, 244)); // #f4f4f4

        // Add orders dynamically from the database
        loadOrders();

        // Create scroll panel for order list
        JScrollPane scrollPane = new JScrollPane(orderListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        containerPanel.add(scrollPane);

        // Create the modals
        loadDrivers();
        createStatusDialog();

        // Add all components to the frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(containerPanel, BorderLayout.CENTER); // Changed to CENTER for better layout with scrollPane
        setVisible(true); // Make sure frame is visible
    }

    private void loadOrders() {
        orderListPanel.removeAll(); // Clear previous orders before loading new ones
        String query = "";
        if ("Manager".equals(user.getRole())) {
            query = "SELECT ordtbl.*, c.* FROM OrderTable ordtbl INNER JOIN Customer c ON ordtbl.CustomerID = c.CustomerID";
        } else if ("DeliveryDriver".equals(user.getRole())) {
            query = "SELECT o.*, c.* FROM OrderTable o " +
                    "INNER JOIN Customer c ON o.CustomerID = c.CustomerID " +
                    "INNER JOIN DriverOrders d ON o.OrderID = d.OrderID " +
                    "WHERE d.DriverID = ?";
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            if ("DeliveryDriver".equals(user.getRole())) {
                DeliveryDriver driver = getDeliveryDriverByUserID(user.getUserID());
                if (driver != null) {
                    stmt.setInt(1, driver.getDriverID());
                } else {
                    JOptionPane.showMessageDialog(this, "No driver found with specificο UserID", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int orderID = rs.getInt("orderID");
                    int customerID = rs.getInt("customerID");
                    String status = rs.getString("status");
                    java.sql.Date orderDate = rs.getDate("orderDate");
                    java.sql.Date deliveryDate = rs.getDate("deliveryDate");
                    double totalAmount = rs.getDouble("totalAmount");

                    String name = rs.getString("name");
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    String address = rs.getString("address");

                    Order order = new Order(orderID, customerID, status, orderDate, deliveryDate, totalAmount);
                    Customer customer = new Customer(customerID, name, phone, email, address);
                    showOrders(order, customer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        orderListPanel.revalidate();
        orderListPanel.repaint();
    }

    private DeliveryDriver getDeliveryDriverByUserID(int userID) {
        String query = "SELECT dd.*, u.username, u.password, u.email, u.role FROM DeliveryDriver dd JOIN User u ON dd.UserID = u.UserID WHERE dd.UserID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int driverID = rs.getInt("driverID");
                    String name = rs.getString("name");
                    String phone = rs.getString("phone");
                    String licenseNumber = rs.getString("licenseNumber");
                    int assignedOrders = rs.getInt("assignedOrders");
                    String username = rs.getString("username");
                    String password = rs.getString("password"); // Consider security if using password directly
                    String email = rs.getString("email");
                    String role = rs.getString("role");

                    return new DeliveryDriver(driverID, userID, username,
                            password, email, role,
                            name, phone, licenseNumber, assignedOrders);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isOrderAssigned(int orderID) {
        String query = "SELECT COUNT(*) FROM DriverOrders WHERE OrderID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showOrders(Order order, Customer customer) {
        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
        orderPanel.setBackground(Color.WHITE);
        orderPanel.setBorder(new CompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10), // Added some horizontal padding
                BorderFactory.createLineBorder(Color.LIGHT_GRAY) // Added a light border
        ));
        orderPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250)); // Allow wider, fixed height
        orderPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel idLabel = new JLabel("<html><b>Order #" + order.getOrderID() + "</b></html>");
        JLabel customerIDLabel = new JLabel("Customer ID: " + order.getCustomerID());
        JLabel nameLabel = new JLabel("Name: " + customer.getName());
        JLabel phoneLabel = new JLabel("Phone: " + customer.getPhone());
        JLabel emailLabel = new JLabel("Email: " + customer.getEmail());
        JLabel addressLabel = new JLabel("Address: " + customer.getAddress());
        JLabel statusLabel = new JLabel("Status: " + order.getStatus());
        JLabel orderDateLabel = new JLabel("Order Date: " + order.getOrderDate());
        JLabel deliveryDateLabel = new JLabel("Delivery Date: " + (order.getDeliveryDate() != null ? order.getDeliveryDate().toString() : "N/A"));
        JLabel totalAmountLabel = new JLabel("Total Amount: $" + String.format("%.2f", order.getTotalAmount()));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.setBackground(Color.WHITE);

        boolean isAssigned = isOrderAssigned(order.getOrderID());

        if ("Manager".equals(user.getRole())) {
            if (!isAssigned) {
                JButton assignButton = new JButton("Assign");
                assignButton.setBackground(new Color(194, 165, 108));
                assignButton.setForeground(Color.BLACK);
                assignButton.setOpaque(true);
                assignButton.setContentAreaFilled(true);
                assignButton.setBorderPainted(false);
                assignButton.setFocusPainted(false);
                assignButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                assignButton.addActionListener(e -> showAssignmentPopup(order.getOrderID()));
                buttonsPanel.add(assignButton);
            } else {
                JButton unassignButton = new JButton("Cancel Assign");
                unassignButton.setBackground(new Color(255, 165, 0));
                unassignButton.setForeground(Color.BLACK);
                unassignButton.setOpaque(true);
                unassignButton.setContentAreaFilled(true);
                unassignButton.setBorderPainted(false);
                unassignButton.setFocusPainted(false);
                unassignButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                unassignButton.addActionListener(e -> unassignOrder(order.getOrderID()));
                buttonsPanel.add(unassignButton);
            }
        }

        JButton statusButton = new JButton("Status");
        statusButton.setBackground(new Color(122, 156, 95));
        statusButton.setForeground(Color.WHITE);
        statusButton.setOpaque(true);
        statusButton.setContentAreaFilled(true);
        statusButton.setBorderPainted(false);
        statusButton.setFocusPainted(false);
        statusButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        statusButton.addActionListener(e -> checkOrderStatus(order.getOrderID()));
        buttonsPanel.add(statusButton);

        if ("DeliveryDriver".equals(user.getRole())) {
            JButton completeButton = new JButton("Complete");
            completeButton.setBackground(new Color(0, 200, 0));
            completeButton.setForeground(Color.WHITE);
            completeButton.setOpaque(true);
            completeButton.setContentAreaFilled(true);
            completeButton.setBorderPainted(false);
            completeButton.setFocusPainted(false);
            completeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            completeButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to complete the order #" + order.getOrderID() + "?", "Confirm Completion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    order.updateOrderStatus(order.getOrderID(), "Completed", conn);
                }
            });
            buttonsPanel.add(completeButton);

            JButton cancelButton = new JButton("Cancel");
            cancelButton.setBackground(new Color(255, 0, 0));
            cancelButton.setForeground(Color.WHITE);
            cancelButton.setOpaque(true);
            cancelButton.setContentAreaFilled(true);
            cancelButton.setBorderPainted(false);
            cancelButton.setFocusPainted(false);
            cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            cancelButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel the order #" + order.getOrderID() + "?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    order.updateOrderStatus(order.getOrderID(), "Canceled", conn); // Assume Order class has this method
                }
            });
            buttonsPanel.add(cancelButton);

            // --- ΝΕΟ ΚΟΥΜΠΙ ADD PAYMENT ---
            String orderStatus = order.getStatus();
            if (("Assigned".equalsIgnoreCase(orderStatus) || "Completed".equalsIgnoreCase(orderStatus)) // Προσθέστε καταστάσεις όπου η πληρωμή είναι δυνατή
                && !isPaymentAddedForOrder(order.getOrderID())) {
                JButton addPaymentButton = new JButton("Add Payment");
                addPaymentButton.setBackground(new Color(0, 128, 128)); // Teal color
                addPaymentButton.setForeground(Color.WHITE);
                addPaymentButton.setOpaque(true);
                addPaymentButton.setContentAreaFilled(true);
                addPaymentButton.setBorderPainted(false);
                addPaymentButton.setFocusPainted(false);
                addPaymentButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                addPaymentButton.addActionListener(e -> handleAddPayment(order));
                buttonsPanel.add(addPaymentButton);
            } else if (isPaymentAddedForOrder(order.getOrderID())) {
                JLabel paymentMadeLabel = new JLabel("Payment Recorded");
                paymentMadeLabel.setForeground(new Color(0, 100, 0)); // Dark Green
                paymentMadeLabel.setFont(new Font("Arial", Font.ITALIC, 12));
                buttonsPanel.add(paymentMadeLabel);
            }
        }

        orderPanel.add(idLabel);
        orderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        orderPanel.add(customerIDLabel);
        orderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        orderPanel.add(nameLabel);
        orderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        orderPanel.add(phoneLabel);
        orderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        orderPanel.add(emailLabel);
        orderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        orderPanel.add(addressLabel);
        orderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        orderPanel.add(statusLabel);
        orderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        orderPanel.add(orderDateLabel);
        orderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        orderPanel.add(deliveryDateLabel);
        orderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        orderPanel.add(totalAmountLabel);
        orderPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        orderPanel.add(buttonsPanel);

        orderListPanel.add(orderPanel);
        orderListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    // --- ΝΕΑ ΜΕΘΟΔΟΣ: isPaymentAddedForOrder ---
    private boolean isPaymentAddedForOrder(int orderID) {
        String query = "SELECT COUNT(*) FROM Payment WHERE OrderID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error checking payment status: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return false; // Default to false if error, allowing payment attempt
    }

    // --- ΝΕΑ ΜΕΘΟΔΟΣ: getNextPaymentID ---
    private int getNextPaymentID() {
        String query = "SELECT MAX(PaymentID) FROM Payment";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1) + 1;
            } else {
                return 1; // First payment in the table
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating PaymentID: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            // Fallback, not ideal for production. Ensure PaymentID is auto-increment or handle this more robustly.
            return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        }
    }

    // --- ΝΕΑ ΜΕΘΟΔΟΣ: handleAddPayment ---
    private void handleAddPayment(Order order) {
        if (isPaymentAddedForOrder(order.getOrderID())) {
             JOptionPane.showMessageDialog(this, "Payment has already been recorded for Order #" + order.getOrderID(), "Payment Info", JOptionPane.INFORMATION_MESSAGE);
             return;
        }

        String[] paymentOptions = {"Cash", "Card"};
        String paymentMethod = (String) JOptionPane.showInputDialog(
                this,
                "Select Payment Method for Order #" + order.getOrderID() + ":",
                "Add Payment",
                JOptionPane.QUESTION_MESSAGE,
                null,
                paymentOptions,
                paymentOptions[0]
        );

        if (paymentMethod == null) { // User cancelled or closed the dialog
            return;
        }

        int paymentID = getNextPaymentID();
        int orderID = order.getOrderID();
        double amountPaid = order.getTotalAmount();
        java.sql.Date paymentDate = new java.sql.Date(System.currentTimeMillis()); // Current date for payment
        String paymentStatus = "Completed"; // Or "Completed", "Successful" as per your system logic

        Payment newPayment = new Payment(paymentID, orderID, paymentMethod, amountPaid, paymentDate, paymentStatus);

        if (newPayment.insertToDatabase(conn)) {
            JOptionPane.showMessageDialog(this, "Payment added successfully for Order #" + orderID + "!", "Payment Success", JOptionPane.INFORMATION_MESSAGE);
            // Optionally, update order status to something like "Paid" if you have such a status in OrderTable
            // order.updateOrderStatusInDB(orderID, "Paid", conn); 
            refreshOrderList(); // Refresh the list to show "Payment Recorded"
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add payment for Order #" + orderID + ".", "Payment Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void unassignOrder(int orderID) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to unassign the order #" + orderID + "?",
                "Confirm Unassignment", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                conn.setAutoCommit(false); // Start transaction

                String getDriverQuery = "SELECT DriverID FROM DriverOrders WHERE OrderID = ?";
                int driverID = -1;
                try (PreparedStatement getDriverStmt = conn.prepareStatement(getDriverQuery)) {
                    getDriverStmt.setInt(1, orderID);
                    ResultSet rs = getDriverStmt.executeQuery();
                    if (rs.next()) {
                        driverID = rs.getInt("DriverID");
                    }
                }

                if (driverID != -1) {
                    String updateDriverQuery = "UPDATE DeliveryDriver SET assignedOrders = assignedOrders - 1 WHERE driverID = ? AND assignedOrders > 0";
                    try (PreparedStatement updateDriverStmt = conn.prepareStatement(updateDriverQuery)) {
                        updateDriverStmt.setInt(1, driverID);
                        updateDriverStmt.executeUpdate();
                    }
                }

                String deleteAssignmentQuery = "DELETE FROM DriverOrders WHERE OrderID = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteAssignmentQuery)) {
                    deleteStmt.setInt(1, orderID);
                    deleteStmt.executeUpdate();
                }

                String updateOrderStatusQuery = "UPDATE OrderTable SET status = 'Pending' WHERE orderID = ?";
                try (PreparedStatement updateOrderStmt = conn.prepareStatement(updateOrderStatusQuery)) {
                    updateOrderStmt.setInt(1, orderID);
                    updateOrderStmt.executeUpdate();
                }

                conn.commit(); // Commit transaction
                JOptionPane.showMessageDialog(this,
                        "Order #" + orderID + " has been unassigned successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshOrderList();

            } catch (SQLException e) {
                try {
                    conn.rollback(); // Rollback transaction on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error unassigning order: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    conn.setAutoCommit(true); // Restore default auto-commit behavior
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void refreshOrderList() {
        // Store current scroll position
        JScrollPane scrollPane = (JScrollPane) orderListPanel.getParent().getParent(); // Assuming orderListPanel is in a JViewport in a JScrollPane
        int currentScrollValue = 0;
        if (scrollPane != null && scrollPane.getVerticalScrollBar() != null) {
             currentScrollValue = scrollPane.getVerticalScrollBar().getValue();
        }

        orderListPanel.removeAll();
        loadOrders(); // This will repopulate and call revalidate/repaint
        // orderListPanel.revalidate();
        // orderListPanel.repaint();

        // Restore scroll position
        final int scrollValueToRestore = currentScrollValue;
        if (scrollPane != null && scrollPane.getVerticalScrollBar() != null) {
            SwingUtilities.invokeLater(() -> { // Ensure this runs after UI updates
                 scrollPane.getVerticalScrollBar().setValue(scrollValueToRestore);
            });
        }
    }

    private void loadDrivers() {
        assignDialog = new JDialog(this, "Select Courier", true);
        assignDialog.setSize(450, 200);
        assignDialog.setLocationRelativeTo(this);
        assignDialog.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabelDialog = new JLabel("Select Courier"); // Renamed to avoid conflict
        titleLabelDialog.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabelDialog.setAlignmentX(Component.CENTER_ALIGNMENT);

        deliveryDrivers.clear(); // Clear list before reloading
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT dd.*, u.username, u.password, u.email, u.role FROM DeliveryDriver dd JOIN User u ON dd.UserID = u.UserID")) {
            while (rs.next()) {
                int driverID = rs.getInt("driverID");
                int userID_db = rs.getInt("userID"); // Use different name from class member
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String role = rs.getString("role");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String licenseNumber = rs.getString("licenseNumber");
                int assignedOrders = rs.getInt("assignedOrders");
                DeliveryDriver deliveryDriver = new DeliveryDriver(driverID, userID_db, username, password, email, role, name, phone, licenseNumber, assignedOrders);
                deliveryDrivers.add(deliveryDriver);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JComboBox<DeliveryDriver> delivererComboBox = new JComboBox<>(deliveryDrivers.toArray(new DeliveryDriver[0]));
        delivererComboBox.setMaximumSize(new Dimension(450, 30));
        delivererComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Custom renderer to show driver's name
        delivererComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof DeliveryDriver) {
                    setText(((DeliveryDriver) value).getName());
                }
                return this;
            }
        });


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton confirmButton = new JButton("Complete Assignment");
        confirmButton.setBackground(new Color(122, 156, 95));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setOpaque(true);
        confirmButton.setContentAreaFilled(true);
        confirmButton.setBorderPainted(false);
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmButton.setFocusPainted(false);
        confirmButton.addActionListener(e -> {
            DeliveryDriver selectedDriver = (DeliveryDriver) delivererComboBox.getSelectedItem();
            if (selectedDriver != null) {
                JOptionPane.showMessageDialog(this, "Assigned order #" + currentOrderID + " to: " + selectedDriver.getName());
                assignOrderToDriver(currentOrderID, selectedDriver.getDriverID());
                assignDialog.setVisible(false);
                refreshOrderList();
            } else {
                 JOptionPane.showMessageDialog(this, "Please select a driver.", "Selection Missing", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton closeButtonDialog = new JButton("Close"); // Renamed to avoid conflict
        closeButtonDialog.setBackground(new Color(178, 34, 34));
        closeButtonDialog.setForeground(Color.WHITE);
        closeButtonDialog.setOpaque(true);
        closeButtonDialog.setContentAreaFilled(true);
        closeButtonDialog.setBorderPainted(false);
        closeButtonDialog.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButtonDialog.setFocusPainted(false);
        closeButtonDialog.addActionListener(e -> assignDialog.setVisible(false));

        buttonPanel.add(confirmButton);
        buttonPanel.add(closeButtonDialog);

        contentPanel.add(titleLabelDialog);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(delivererComboBox);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(buttonPanel);

        assignDialog.add(contentPanel, BorderLayout.CENTER);
    }

    private void assignOrderToDriver(int orderID, int driverID) {
         try {
            conn.setAutoCommit(false); // Start transaction

            // Check if already assigned, if so, perhaps unassign from old driver first or prevent
            // For simplicity, current code updates or inserts.

            String checkQuery = "SELECT DriverID FROM DriverOrders WHERE OrderID = ?";
            int oldDriverID = -1;
            try(PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, orderID);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    oldDriverID = rs.getInt("DriverID");
                }
            }

            if (oldDriverID != -1 && oldDriverID != driverID) {
                // Order was assigned to another driver, decrement their count
                String updateOldDriverQuery = "UPDATE DeliveryDriver SET assignedOrders = assignedOrders - 1 WHERE driverID = ? AND assignedOrders > 0";
                try(PreparedStatement updateOldStmt = conn.prepareStatement(updateOldDriverQuery)){
                    updateOldStmt.setInt(1, oldDriverID);
                    updateOldStmt.executeUpdate();
                }
            }


            if (oldDriverID != -1) { // Order exists in DriverOrders
                String updateQuery = "UPDATE DriverOrders SET DriverID = ? WHERE OrderID = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, driverID);
                    updateStmt.setInt(2, orderID);
                    updateStmt.executeUpdate();
                }
            } else { // New assignment
                String insertQuery = "INSERT INTO DriverOrders (DriverID, OrderID) VALUES (?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, driverID);
                    insertStmt.setInt(2, orderID);
                    insertStmt.executeUpdate();
                }
            }

            // Increment new driver's count only if it's a new assignment or different driver
            if (oldDriverID != driverID) {
                 String updateDriverQuery = "UPDATE DeliveryDriver SET assignedOrders = assignedOrders + 1 WHERE driverID = ?";
                 try (PreparedStatement updateDriverStmt = conn.prepareStatement(updateDriverQuery)) {
                     updateDriverStmt.setInt(1, driverID);
                     updateDriverStmt.executeUpdate();
                 }
            }

            String updateOrderStatusQuery = "UPDATE OrderTable SET status = 'Assigned' WHERE orderID = ?";
            try (PreparedStatement updateOrderStmt = conn.prepareStatement(updateOrderStatusQuery)) {
                updateOrderStmt.setInt(1, orderID);
                updateOrderStmt.executeUpdate();
            }

            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error assigning order: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void createStatusDialog() {
        statusDialog = new JDialog(this, "Order Status", true);
        statusDialog.setSize(400, 200);
        statusDialog.setLocationRelativeTo(this);
        statusDialog.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabelDialog = new JLabel("Order Status"); // Renamed
        titleLabelDialog.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabelDialog.setAlignmentX(Component.CENTER_ALIGNMENT);

        orderDetailsArea = new JTextArea(5, 30);
        orderDetailsArea.setEditable(false);
        orderDetailsArea.setLineWrap(true);
        orderDetailsArea.setWrapStyleWord(true);
        orderDetailsArea.setBackground(contentPanel.getBackground());
        orderDetailsArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton closeButtonDialog = new JButton("Close"); // Renamed
        closeButtonDialog.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButtonDialog.addActionListener(e -> statusDialog.setVisible(false));

        contentPanel.add(titleLabelDialog);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(orderDetailsArea);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(closeButtonDialog);

        statusDialog.add(contentPanel, BorderLayout.CENTER);
    }

    private void showAssignmentPopup(int orderID) {
        this.currentOrderID = orderID;
        assignDialog.setTitle("Assign Order #" + orderID); // Set title dynamically
        // Reload drivers in JComboBox if they might change or to ensure freshness
        JComboBox<DeliveryDriver> comboBox = (JComboBox<DeliveryDriver>) ((JPanel) assignDialog.getContentPane().getComponent(0)).getComponent(2); // Fragile way to get combobox
        comboBox.removeAllItems();
        deliveryDrivers.forEach(comboBox::addItem);

        assignDialog.setVisible(true);
    }

    private void checkOrderStatus(int orderID) {
        getOrderStatus(orderID);
    }

    private void getOrderStatus(int orderID) {
        String query = "SELECT status, orderDate, deliveryDate FROM OrderTable WHERE orderID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    java.sql.Date orderDate = rs.getDate("orderDate");
                    java.sql.Date deliveryDate = rs.getDate("deliveryDate");

                    String message = String.format("Order ID: %d\nStatus: %s\nOrder Date: %s\nDelivery Date: %s",
                            orderID, status,
                            (orderDate != null ? orderDate.toString() : "N/A"),
                            (deliveryDate != null ? deliveryDate.toString() : "N/A"));

                    orderDetailsArea.setText(message);
                } else {
                    orderDetailsArea.setText("No information found for order #" + orderID + ".");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            orderDetailsArea.setText("Error fetching order status for order #" + orderID + ".");
        }
        statusDialog.setTitle("Status for Order #" + orderID); // Dynamic title
        statusDialog.setVisible(true);
    }
}