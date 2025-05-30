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
        getContentPane().add(containerPanel, BorderLayout.WEST);
    }
    
    private void loadOrders() {
        String query = "";
        if ("Manager".equals(user.getRole())) {
            // Ο Manager βλέπει όλες τις παραγγελίες
            query = "SELECT ordtbl.*, c.* FROM OrderTable ordtbl INNER JOIN Customer c ON ordtbl.CustomerID = c.CustomerID";
        } else if ("DeliveryDriver".equals(user.getRole())) {
            // Ο DeliveryDriver βλέπει μόνο τις παραγγελίες που έχουν ανατεθεί σε αυτόν
            query = "SELECT o.*, c.* FROM OrderTable o " +
                    "INNER JOIN Customer c ON o.CustomerID = c.CustomerID " +
                    "INNER JOIN DriverOrders d ON o.OrderID = d.OrderID " +
                    "WHERE d.DriverID = ?";
        }
    
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            if ("DeliveryDriver".equals(user.getRole())) {
                // Προσοχή: Το user.getUserID() πρέπει να δίνει το DriverID και όχι το UserID
                // Αν ο DeliveryDriver χρειάζεται να έχει πρόσβαση στο δικό του DriverID
                DeliveryDriver driver = getDeliveryDriverByUserID(user.getUserID());
                if (driver != null) {
                    stmt.setInt(1, driver.getDriverID());
                } else {
                    // Αν δεν βρεθεί ο driver, δεν θα εμφανιστούν παραγγελίες
                    JOptionPane.showMessageDialog(this, "Δεν βρέθηκε ο οδηγός με το συγκεκριμένο UserID", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
    
            try (ResultSet rs = stmt.executeQuery()) {
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
                    showOrders(order, customer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Μέθοδος για εύρεση του DeliveryDriver με βάση το UserID
    private DeliveryDriver getDeliveryDriverByUserID(int userID) {
        String query = "SELECT * FROM DeliveryDriver WHERE UserID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int driverID = rs.getInt("driverID");
                    String name = rs.getString("name");
                    String phone = rs.getString("phone");
                    String licenseNumber = rs.getString("licenseNumber");
                    int assignedOrders = rs.getInt("assignedOrders");
                    
                    // Δημιουργία του αντικειμένου DeliveryDriver
                    return new DeliveryDriver(driverID, userID, user.getUsername(), 
                                            user.getPassword(), user.getEmail(), user.getRole(), 
                                            name, phone, licenseNumber, assignedOrders);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Έλεγχος αν η παραγγελία έχει ήδη ανατεθεί
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
        
        // Έλεγχος εάν η παραγγελία έχει ανατεθεί
        boolean isAssigned = isOrderAssigned(order.getOrderID());
        
        // Το κουμπί ανάθεσης εμφανίζεται μόνο στον Manager
        if ("Manager".equals(user.getRole())) {
            if (!isAssigned) {
                // Κουμπί ανάθεσης - καφέ φόντο (#c2a56c) με μαύρο κείμενο
                JButton assignButton = new JButton("Assign");
                assignButton.setBackground(new Color(194, 165, 108)); // #c2a56c
                assignButton.setForeground(Color.BLACK);
                assignButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                assignButton.setOpaque(true);
                assignButton.setContentAreaFilled(true);
                assignButton.setBorderPainted(false);
                assignButton.addActionListener(e -> showAssignmentPopup(order.getOrderID()));
                buttonsPanel.add(assignButton);
            } else {
                // Κουμπί ακύρωσης ανάθεσης - πορτοκαλί φόντο με μαύρο κείμενο
                JButton unassignButton = new JButton("Cancel Assign");
                unassignButton.setBackground(new Color(255, 165, 0)); // πορτοκαλί
                unassignButton.setForeground(Color.BLACK);
                unassignButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                unassignButton.setOpaque(true);
                unassignButton.setContentAreaFilled(true);
                unassignButton.setBorderPainted(false);
                unassignButton.addActionListener(e -> unassignOrder(order.getOrderID()));
                buttonsPanel.add(unassignButton);
            }
        }
        
        // Fixed status button - green background (#7a9c5f) with white text
        JButton statusButton = new JButton("Status");
        statusButton.setBackground(new Color(122, 156, 95)); // #7a9c5f
        statusButton.setForeground(Color.WHITE);
        statusButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        statusButton.setOpaque(true);
        statusButton.setContentAreaFilled(true);
        statusButton.setBorderPainted(false);
        statusButton.addActionListener(e -> checkOrderStatus(order.getOrderID()));
        buttonsPanel.add(statusButton);
        
        // Τα κουμπιά Complete και Cancel εμφανίζονται μόνο στον DeliveryDriver
        if ("DeliveryDriver".equals(user.getRole())) {
            // Fixed status button - green background (#7a9c5f) with white text
            JButton completeButton = new JButton("Complete");
            completeButton.setBackground(new Color(0, 200, 0)); // πράσινο
            completeButton.setForeground(Color.WHITE);
            completeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            completeButton.setOpaque(true);
            completeButton.setContentAreaFilled(true);
            completeButton.setBorderPainted(false);
            completeButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to complete the order ?", "Yes", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    order.updateOrderStatus(order.getOrderID(), "Completed", conn);
                }
            });
            buttonsPanel.add(completeButton);
            
            // Fixed status button - red background with white text
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setBackground(new Color(255, 0, 0)); // κόκκινο
            cancelButton.setForeground(Color.WHITE);
            cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            cancelButton.setOpaque(true);
            cancelButton.setContentAreaFilled(true);
            cancelButton.setBorderPainted(false);
            cancelButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to cancel the order ?", "Yes", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    order.updateOrderStatus(order.getOrderID(), "Canceled", conn);
                }
            });
            buttonsPanel.add(cancelButton);
        }

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
    
    // Μέθοδος για την ακύρωση ανάθεσης παραγγελίας
    private void unassignOrder(int orderID) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to unaasign the order #" + orderID + "?", 
            "Confirm", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Βρίσκουμε πρώτα το DriverID για να ενημερώσουμε το assignedOrders
                String getDriverQuery = "SELECT DriverID FROM DriverOrders WHERE OrderID = ?";
                PreparedStatement getDriverStmt = conn.prepareStatement(getDriverQuery);
                getDriverStmt.setInt(1, orderID);
                ResultSet rs = getDriverStmt.executeQuery();
                
                if (rs.next()) {
                    int driverID = rs.getInt("DriverID");
                    
                    // 1. Μειώνουμε το assignedOrders του οδηγού
                    String updateDriverQuery = "UPDATE DeliveryDriver SET assignedOrders = assignedOrders - 1 WHERE driverID = ?";
                    PreparedStatement updateDriverStmt = conn.prepareStatement(updateDriverQuery);
                    updateDriverStmt.setInt(1, driverID);
                    updateDriverStmt.executeUpdate();
                    
                    // 2. Διαγράφουμε την εγγραφή από το DriverOrders
                    String deleteAssignmentQuery = "DELETE FROM DriverOrders WHERE OrderID = ?";
                    PreparedStatement deleteStmt = conn.prepareStatement(deleteAssignmentQuery);
                    deleteStmt.setInt(1, orderID);
                    deleteStmt.executeUpdate();
                    
                    // 3. Ενημερώνουμε την κατάσταση της παραγγελίας πίσω σε "Pending"
                    String updateOrderStatusQuery = "UPDATE OrderTable SET status = 'Pending' WHERE orderID = ?";
                    PreparedStatement updateOrderStmt = conn.prepareStatement(updateOrderStatusQuery);
                    updateOrderStmt.setInt(1, orderID);
                    updateOrderStmt.executeUpdate();
                    
                    JOptionPane.showMessageDialog(this, 
                        "Η ανάθεση της παραγγελίας #" + orderID + " ακυρώθηκε επιτυχώς.", 
                        "Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
                        
                    // Ανανεώνουμε την οθόνη για να εμφανιστούν οι αλλαγές
                    refreshOrderList();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Σφάλμα κατά την ακύρωση της ανάθεσης: " + e.getMessage(), 
                    "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Μέθοδος για ανανέωση της λίστας παραγγελιών
    private void refreshOrderList() {
        // Καθαρίζουμε την υπάρχουσα λίστα
        orderListPanel.removeAll();
        // Φορτώνουμε τις παραγγελίες ξανά
        loadOrders();
        // Ανανεώνουμε την οθόνη
        orderListPanel.revalidate();
        orderListPanel.repaint();
    }
    
    private void loadDrivers() {
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
            // Χρησιμοποιούμε το currentOrderID που έχει οριστεί κατά την κλήση της showAssignmentPopup
            JOptionPane.showMessageDialog(this, "Assigned order #" + currentOrderID + " to: " + selectedDriver.getName());
            
            // Εδώ θα μπορούσαμε να καλέσουμε μια μέθοδο για να αποθηκεύσουμε την ανάθεση στη βάση
            assignOrderToDriver(currentOrderID, selectedDriver.getDriverID());
            
            assignDialog.setVisible(false);
            
            // Ανανέωση της λίστας παραγγελιών
            refreshOrderList();
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

    // Μέθοδος για να αναθέσουμε την παραγγελία σε έναν οδηγό
    private void assignOrderToDriver(int orderID, int driverID) {
        try {
            // Έλεγχος αν η παραγγελία είναι ήδη ανατεθειμένη
            String checkQuery = "SELECT * FROM DriverOrders WHERE OrderID = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, orderID);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Αν υπάρχει ήδη, κάνουμε update
                String updateQuery = "UPDATE DriverOrders SET DriverID = ? WHERE OrderID = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, driverID);
                updateStmt.setInt(2, orderID);
                updateStmt.executeUpdate();
            } else {
                // Αν δεν υπάρχει, κάνουμε insert
                String insertQuery = "INSERT INTO DriverOrders (DriverID, OrderID) VALUES (?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setInt(1, driverID);
                insertStmt.setInt(2, orderID);
                insertStmt.executeUpdate();
            }
            
            // Ενημέρωση του πλήθους των παραγγελιών του οδηγού
            String updateDriverQuery = "UPDATE DeliveryDriver SET assignedOrders = assignedOrders + 1 WHERE driverID = ?";
            PreparedStatement updateDriverStmt = conn.prepareStatement(updateDriverQuery);
            updateDriverStmt.setInt(1, driverID);
            updateDriverStmt.executeUpdate();
            
            // Ενημέρωση της κατάστασης της παραγγελίας σε "Assigned"
            String updateOrderStatusQuery = "UPDATE OrderTable SET status = 'Assigned' WHERE orderID = ?";
            PreparedStatement updateOrderStmt = conn.prepareStatement(updateOrderStatusQuery);
            updateOrderStmt.setInt(1, orderID);
            updateOrderStmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error assigning order: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        // Αποθηκεύουμε το OrderID στην μεταβλητή της κλάσης
        this.currentOrderID = orderID;
        // Εμφανίζουμε το dialog ανάθεσης
        assignDialog.setTitle("Assign Order #" + orderID);
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