/* Εδώ φορτώνονται όλες οι παραγγελίες από τον πίνακα OrderTable και προβάλονται οι σχετικές
 * πληροφορίες αυτών και των πελατών. Με την συνάρτηση showOrders αντλούνται οι πληροφορίες
 * για τις παραγγελίες και τους πελάτες και με την addOrderInfo προβάλονται στην οθόνη. Τέλος, 
 * ανάλογα με το ποιο κουμπί πατήθηκε (Ολοκλήρωση/Ακύρωση) καλέιται η μέθοδος του deliveryDriver
 * updateOrderStatus. 
 * !!! Ο λόγος που έγινε όλο αυτό με το να περνάμε σαν όρισμα τον χρήστη που συνδέθηκε
 * είναι επειδή η λειτουργία της μεθόδου updateOrderStatus, δεν αφορά την οθόνη ώστε να είναι απλά
 * ορισμένη εκεί, αλλά του διανομέα !!!.
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.List;

public class ViewOrders extends JFrame {
    private JPanel orderListPanel;
    private JDialog assignDialog;
    private JDialog statusDialog;
    private JTextArea orderDetailsArea;
    
    // Database connection variables
    public Connection conn;
    private List<DeliveryDriver> deliveryDrivers = new ArrayList<>();
    public DeliveryDriver deliveryDriver;

    public ViewOrders(DeliveryDriver deliveryDriver, Connection conn) {
        this.deliveryDriver = deliveryDriver;
        this.conn = conn;

        // Basic window setup
        setTitle("View Orders");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize database connection
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/softengproject", "root", "12345");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
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
        logoutButton.addActionListener(e -> System.exit(0));
        
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
        showOrders();
        
        // Create scroll panel for order list
        JScrollPane scrollPane = new JScrollPane(orderListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        containerPanel.add(scrollPane);
        
        // Create the modals
        createAssignDialog();
        createStatusDialog();
        
        // Add all components to the frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(containerPanel, BorderLayout.WEST);
    }
    
    private void showOrders() {
        String query = "SELECT * FROM  OrderTable ordtbl INNER JOIN Customer c ON ordtbl.CustomerID = c.CustomerID;"; // Replace with your actual query
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                // Order Info
                int orderID = rs.getInt("orderID");
                int customerID = rs.getInt("customerID");
                String status = rs.getString("status");
                Date orderDate = rs.getDate("orderDate");
                Date deliveryDate = rs.getDate("deliveryDate");
                double totalAmount = rs.getDouble("totalAmount");
                // Customer Info
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                String address = rs.getString("address");
                
                // Create an Order object and add it to the panel
                Order order = new Order(orderID, customerID, status, orderDate, deliveryDate, totalAmount);
                Customer customer = new Customer(customerID, name, phone, email, address);
                addOrderInfo(order, customer);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void addOrderInfo(Order order, Customer customer) {
        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
        orderPanel.setBackground(Color.WHITE);
        orderPanel.setBorder(new CompoundBorder(
            BorderFactory.createEmptyBorder(10, 0, 10, 0),
            BorderFactory.createCompoundBorder()
        ));
        orderPanel.setMaximumSize(new Dimension(600, 200));
        orderPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Order Info
        JLabel idLabel = new JLabel("<html><b>Order #" + order.getOrderID() + "</b></html>");
        JLabel customerIDLabel = new JLabel("Customer ID: " + order.getCustomerID());
        // Customer Info
        JLabel nameLabel = new JLabel("Name: " + customer.getName());
        JLabel phoneLabel = new JLabel("Phone: " + customer.getPhone());
        JLabel emaiLabel = new JLabel("Email: " + customer.getEmail());
        JLabel addressLabel = new JLabel("Address: " + customer.getAddress());       
        //
        JLabel statusLabel = new JLabel("Status: " + order.getStatus());
        JLabel orderDateLabel = new JLabel("Order Date: " + order.getOrderDate());
        JLabel deliveryDateLabel = new JLabel("Delivery Date: " + order.getDeliveryDate());
        JLabel totalAmountLabel = new JLabel("Total Amount: $" + order.getTotalAmount());

        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.setBackground(Color.WHITE);
        
        // Fixed assign button - brown background (#c2a56c) with black text
        JButton assignButton = new JButton("Assign");
        assignButton.setBackground(new Color(194, 165, 108)); // #c2a56c
        assignButton.setForeground(Color.BLACK);
        assignButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        assignButton.setOpaque(true);
        assignButton.setContentAreaFilled(true);
        assignButton.setBorderPainted(false);
        assignButton.addActionListener(e -> showAssignmentPopup(order.getOrderID()));
        
        // Fixed status button - green background (#7a9c5f) with white text
        JButton statusButton = new JButton("Status");
        statusButton.setBackground(new Color(122, 156, 95)); // #7a9c5f
        statusButton.setForeground(Color.WHITE);
        statusButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        statusButton.setOpaque(true);
        statusButton.setContentAreaFilled(true);
        statusButton.setBorderPainted(false);
        statusButton.addActionListener(e -> checkOrderStatus(order.getOrderID()));
        
        // Fixed status button - green background (#7a9c5f) with white text
        JButton completeButton = new JButton("Complete");
        completeButton.setBackground(new Color(0, 200, 0)); // #7a9c5f
        completeButton.setForeground(Color.WHITE);
        completeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        completeButton.setOpaque(true);
        completeButton.setContentAreaFilled(true);
        completeButton.setBorderPainted(false);
        completeButton.addActionListener(e -> {
            
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to complete the order ?", "Yes", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    
                    deliveryDriver.updateOrderStatus(order.getOrderID(), "Completed", conn);
                }
            
        });
        
        // Fixed status button - green background (#7a9c5f) with white text
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(255, 0, 0)); // #7a9c5f
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setOpaque(true);
        cancelButton.setContentAreaFilled(true);
        cancelButton.setBorderPainted(false);
        cancelButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to cancel the order ?", "Yes", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deliveryDriver.updateOrderStatus(order.getOrderID(), "Canceled", conn);
            }
        });
        
        buttonsPanel.add(assignButton);
        buttonsPanel.add(statusButton);
        buttonsPanel.add(completeButton);
        buttonsPanel.add(cancelButton);
       
        // Order Info
        orderPanel.add(idLabel);
        orderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        orderPanel.add(customerIDLabel);
        orderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        orderPanel.add(nameLabel);
        
        //Customer Info
        orderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        orderPanel.add(phoneLabel);
        orderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        orderPanel.add(emaiLabel);
        orderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        orderPanel.add(addressLabel);
        //
        
        orderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        orderPanel.add(totalAmountLabel);
        orderPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        orderPanel.add(buttonsPanel);
        
        orderListPanel.add(orderPanel);
        orderListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    
    private void createAssignDialog() {
    assignDialog = new JDialog(this, "Select Courier", true);
    assignDialog.setSize(450, 200);
    assignDialog.setLocationRelativeTo(this);
    assignDialog.setLayout(new BorderLayout());

    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JLabel titleLabel = new JLabel("Select Courier");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    // === Φόρτωση οδηγών από τη βάση ===
    try (
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM DeliveryDriver INNER JOIN User on DeliveryDriver.UserID = User.UserID")) {

        while (rs.next()) {
            int driverID = rs.getInt("driverID");
            int userID = rs.getInt("userID");
            String username = rs.getString("username");
            String password = rs.getString("password");
            String email = rs.getString("email");
            String role = rs.getString("role");
            String name = rs.getString("name");
            String phone = rs.getString("phone");
            String licenseNumber = rs.getString("licenseNumber");
            int assignedOrders = rs.getInt("assignedOrders");

            DeliveryDriver deliveryDriver = new DeliveryDriver(driverID, userID, username, password, email, role, name, phone, licenseNumber, assignedOrders);
            deliveryDrivers.add(deliveryDriver);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    // Δημιουργία JComboBox με τα ονόματα των οδηγών
    JComboBox<DeliveryDriver> delivererComboBox = new JComboBox<>(deliveryDrivers.toArray(new DeliveryDriver[0]));
    delivererComboBox.setMaximumSize(new Dimension(450, 30));
    delivererComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

    JButton confirmButton = new JButton("Complete Assignment");
    confirmButton.addActionListener(e -> {
        DeliveryDriver selectedDriver = (DeliveryDriver) delivererComboBox.getSelectedItem();
        if (selectedDriver != null) {
            JOptionPane.showMessageDialog(this, "Assigned to: " + selectedDriver.getName());
            
            assignDialog.setVisible(false);
        }
    });

    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(e -> assignDialog.setVisible(false));

    buttonPanel.add(confirmButton);
    buttonPanel.add(closeButton);

    contentPanel.add(titleLabel);
    contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    contentPanel.add(delivererComboBox);
    contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    contentPanel.add(buttonPanel);

    assignDialog.add(contentPanel, BorderLayout.CENTER);
}
   
    private void createStatusDialog() {
        statusDialog = new JDialog(this, "Order Status", true);
        statusDialog.setSize(400, 200);
        statusDialog.setLocationRelativeTo(this);
        statusDialog.setLayout(new BorderLayout());
    
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        JLabel titleLabel = new JLabel("Order Status");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        // Make orderDetailsArea a class-level field
        orderDetailsArea = new JTextArea(5, 30);
        orderDetailsArea.setEditable(false);
        orderDetailsArea.setLineWrap(true);
        orderDetailsArea.setWrapStyleWord(true);
        orderDetailsArea.setBackground(contentPanel.getBackground());
        orderDetailsArea.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        JButton closeButton = new JButton("Close");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> statusDialog.setVisible(false));
    
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(orderDetailsArea);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(closeButton);
    
        statusDialog.add(contentPanel, BorderLayout.CENTER);
    }
    
    private void showAssignmentPopup(int orderID) {
        // Implement action to show assignment dialog and handle it
        assignDialog.setVisible(true);
    }
    
    private void checkOrderStatus(int orderID) { 
    getOrderStatus(orderID);
}

    private void getOrderStatus(int orderID){
    String query = "SELECT status, orderDate, deliveryDate FROM OrderTable WHERE orderID = ?";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, orderID);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                String status = rs.getString("status");
                Date orderDate = rs.getDate("orderDate");
                Date deliveryDate = rs.getDate("deliveryDate");

                String message = String.format("Order ID: %d\nStatus: %s\nOrder Date: %s\nDelivery Date: %s",
                        orderID, status, orderDate, deliveryDate);

                orderDetailsArea.setText(message);
            } else {
                orderDetailsArea.setText("No information found for this order.");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        orderDetailsArea.setText("Error fetching order status.");
    }
    statusDialog.setVisible(true);
}
    
}