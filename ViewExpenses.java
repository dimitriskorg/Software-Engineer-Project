import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class ViewExpenses extends JFrame {
    private JPanel expensesListPanel;
    private JDialog detailsDialog;
    private JDialog addExpenseDialog;
    private JTextArea expenseDetailsArea;
    private int currentOrderID;
    private ArrayList<OrderInfo> availableOrders;
    
    // Database connection variables
    public Connection conn;
    public User user;

    public ViewExpenses(User user, Connection conn) {
        this.user = user;
        this.conn = conn;

        // Basic window setup
        setTitle("View Expenses");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
                
        // Create header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(122, 156, 95)); // #7a9c5f
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        
        JLabel titleLabel = new JLabel("View Expenses");
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
        
        // Add button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(244, 244, 244));
        
        JButton addExpenseButton = new JButton("Add Expense");
        addExpenseButton.setBackground(new Color(122, 156, 95)); // #7a9c5f
        addExpenseButton.setForeground(Color.WHITE);
        addExpenseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addExpenseButton.setOpaque(true);
        addExpenseButton.setContentAreaFilled(true);
        addExpenseButton.setBorderPainted(false);
        addExpenseButton.addActionListener(e -> showAddExpenseDialog());
        buttonPanel.add(addExpenseButton);
        
        // Create expenses list
        expensesListPanel = new JPanel();
        expensesListPanel.setLayout(new BoxLayout(expensesListPanel, BoxLayout.Y_AXIS));
        expensesListPanel.setBackground(new Color(244, 244, 244)); // #f4f4f4
        
        // Add expenses dynamically from the database
        loadExpenses();
        
        // Create scroll panel for expenses list
        JScrollPane scrollPane = new JScrollPane(expensesListPanel);
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
        
        // Create the add expense dialog
        createAddExpenseDialog();
        
        // Load available orders for the dropdown
        loadAvailableOrders();
        
        // Add all components to the frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(containerPanel, BorderLayout.CENTER);
    }
    
    private void loadExpenses() {
        String query = "";
        
        // Only managers can view expenses
        if ("Manager".equals(user.getRole())) {
            query = "SELECT e.OrderID, e.amount, e.Date, o.status, o.orderDate, o.deliveryDate, o.totalAmount, o.CustomerID, " +
                    "c.name, c.phone, c.email, c.address " +
                    "FROM Expenses e " +
                    "INNER JOIN OrderTable o ON e.OrderID = o.OrderID " +
                    "INNER JOIN Customer c ON o.CustomerID = c.CustomerID";
        } else {
            JOptionPane.showMessageDialog(this, "You don't have permission to view expenses", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Expense Info
                    int orderID = rs.getInt("OrderID");
                    double amount = rs.getDouble("amount");
                    Date expenseDate = rs.getDate("Date");
                    
                    // Order Info
                    int customerID = rs.getInt("CustomerID");
                    String status = rs.getString("status");
                    Date orderDate = rs.getDate("orderDate");
                    Date deliveryDate = rs.getDate("deliveryDate");
                    double totalAmount = rs.getDouble("totalAmount");
                    
                    // Customer Info
                    String name = rs.getString("name");
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    String address = rs.getString("address");
    
                    // Create an Order object
                    Order order = new Order(orderID, customerID, status, orderDate, deliveryDate, totalAmount);
                    Customer customer = new Customer(customerID, name, phone, email, address);
                    
                    // Create an Expense object (we'll define this below)
                    Expense expense = new Expense(orderID, amount, expenseDate);
                    
                    // Show expense in the UI
                    showExpense(expense, order, customer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading expenses: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showExpense(Expense expense, Order order, Customer customer) {
        JPanel expensePanel = new JPanel();
        expensePanel.setLayout(new BoxLayout(expensePanel, BoxLayout.Y_AXIS));
        expensePanel.setBackground(Color.WHITE);
        expensePanel.setBorder(new CompoundBorder(
            BorderFactory.createEmptyBorder(10, 0, 10, 0),
            BorderFactory.createCompoundBorder()
        ));
        expensePanel.setMaximumSize(new Dimension(600, 200));
        expensePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Expense Info
        JLabel idLabel = new JLabel("<html><b>Expense for Order #" + expense.getOrderID() + "</b></html>");
        JLabel amountLabel = new JLabel("Expense Amount: $" + expense.getAmount());
        JLabel dateLabel = new JLabel("Expense Date: " + expense.getDate());
        
        // Order Info
        JLabel orderStatusLabel = new JLabel("Order Status: " + order.getStatus());
        JLabel orderTotalLabel = new JLabel("Order Total: $" + order.getTotalAmount());
        
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
        detailsButton.addActionListener(e -> showDetailsPopup(expense.getOrderID()));
        buttonsPanel.add(detailsButton);

        // Add to panel
        expensePanel.add(idLabel);
        expensePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        expensePanel.add(amountLabel);
        expensePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        expensePanel.add(dateLabel);
        expensePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        expensePanel.add(orderStatusLabel);
        expensePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        expensePanel.add(orderTotalLabel);
        expensePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        expensePanel.add(customerNameLabel);
        expensePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        expensePanel.add(buttonsPanel);
        
        expensesListPanel.add(expensePanel);
        expensesListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    
    private void createDetailsDialog() {
        detailsDialog = new JDialog(this, "Expense Details", true);
        detailsDialog.setSize(400, 300);
        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.setLayout(new BorderLayout());
    
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        JLabel titleLabel = new JLabel("Expense Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        // Text area for details
        expenseDetailsArea = new JTextArea(10, 30);
        expenseDetailsArea.setEditable(false);
        expenseDetailsArea.setLineWrap(true);
        expenseDetailsArea.setWrapStyleWord(true);
        expenseDetailsArea.setBackground(contentPanel.getBackground());
        expenseDetailsArea.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        JScrollPane detailsScrollPane = new JScrollPane(expenseDetailsArea);
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
        getExpenseDetails(orderID);
        detailsDialog.setTitle("Expense Details for Order #" + orderID);
        detailsDialog.setVisible(true);
    }

    private void getExpenseDetails(int orderID) {
        String query = "SELECT e.OrderID, e.amount, e.Date, " +
                      "o.status, o.orderDate, o.deliveryDate, o.totalAmount, o.CustomerID, " +
                      "c.name, c.phone, c.email, c.address " +
                      "FROM Expenses e " +
                      "INNER JOIN OrderTable o ON e.OrderID = o.OrderID " +
                      "INNER JOIN Customer c ON o.CustomerID = c.CustomerID " +
                      "WHERE e.OrderID = ?";
                      
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Expense Info
                    double amount = rs.getDouble("amount");
                    Date expenseDate = rs.getDate("Date");
                    
                    // Order Info
                    String status = rs.getString("status");
                    Date orderDate = rs.getDate("orderDate");
                    Date deliveryDate = rs.getDate("deliveryDate");
                    double totalAmount = rs.getDouble("totalAmount");
                    
                    // Customer Info
                    String name = rs.getString("name");
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    String address = rs.getString("address");

                    StringBuilder details = new StringBuilder();
                    details.append("EXPENSE INFORMATION\n");
                    details.append("-------------------\n");
                    details.append("Order ID: ").append(orderID).append("\n");
                    details.append("Expense Amount: $").append(amount).append("\n");
                    details.append("Expense Date: ").append(expenseDate).append("\n\n");
                    
                    details.append("ORDER INFORMATION\n");
                    details.append("----------------\n");
                    details.append("Status: ").append(status).append("\n");
                    details.append("Order Date: ").append(orderDate).append("\n");
                    details.append("Delivery Date: ").append(deliveryDate).append("\n");
                    details.append("Total Amount: $").append(totalAmount).append("\n\n");
                    
                    details.append("CUSTOMER INFORMATION\n");
                    details.append("-------------------\n");
                    details.append("Name: ").append(name).append("\n");
                    details.append("Phone: ").append(phone).append("\n");
                    details.append("Email: ").append(email).append("\n");
                    details.append("Address: ").append(address);

                    expenseDetailsArea.setText(details.toString());
                } else {
                    expenseDetailsArea.setText("No information found for this expense.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            expenseDetailsArea.setText("Error fetching expense details: " + e.getMessage());
        }
    }
    
    // Helper class για τα orders στο dropdown
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
        
        // Φορτώνουμε όλα τα orders που δεν έχουν ήδη expense
        String query = "SELECT o.OrderID, o.totalAmount, o.orderDate, c.name " +
                       "FROM OrderTable o " +
                       "INNER JOIN Customer c ON o.CustomerID = c.CustomerID " +
                       "WHERE o.OrderID NOT IN (SELECT OrderID FROM Expenses) " +
                       "AND o.status = 'Completed'"; // Προσθέτουμε μόνο ολοκληρωμένες παραγγελίες
        
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
    
    private void createAddExpenseDialog() {
        addExpenseDialog = new JDialog(this, "Add New Expense", true);
        addExpenseDialog.setSize(450, 350);
        addExpenseDialog.setLocationRelativeTo(this);
        addExpenseDialog.setLayout(new BorderLayout());
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Add New Expense");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Δημιουργία των πεδίων φόρμας
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Order ID (dropdown)
        JLabel orderLabel = new JLabel("Order:");
        JComboBox<OrderInfo> orderComboBox = new JComboBox<>();
        
        // Amount
        JLabel amountLabel = new JLabel("Amount ($):");
        JTextField amountField = new JTextField(10);
        
        // Date
        JLabel dateLabel = new JLabel("Date (yyyy-MM-dd):");
        JTextField dateField = new JTextField(10);
        // Βάζουμε την τρέχουσα ημερομηνία ως προεπιλογή
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateField.setText(dateFormat.format(new java.util.Date()));
        
        formPanel.add(orderLabel);
        formPanel.add(orderComboBox);
        formPanel.add(amountLabel);
        formPanel.add(amountField);
        formPanel.add(dateLabel);
        formPanel.add(dateField);
        
        // Κουμπιά
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
            JOptionPane.showMessageDialog(addExpenseDialog, "Expense not saved", "Canceled", JOptionPane.INFORMATION_MESSAGE);
            addExpenseDialog.setVisible(false);
        });
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Προσθήκη όλων των components
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(formPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(buttonPanel);
        
        addExpenseDialog.add(contentPanel, BorderLayout.CENTER);
        
        // Event listeners
        cancelButton.addActionListener(e -> addExpenseDialog.setVisible(false));
        
        saveButton.addActionListener(e -> {
            // Validation
            if (orderComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(addExpenseDialog, "Please select an order", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(addExpenseDialog, "Amount is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double amount;
            try {
                amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(addExpenseDialog, "Amount must be greater than zero", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addExpenseDialog, "Invalid amount format. Please enter a valid number", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String dateText = dateField.getText().trim();
            if (dateText.isEmpty()) {
                JOptionPane.showMessageDialog(addExpenseDialog, "Date is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Date expenseDate;
            try {
                java.util.Date parsedDate = dateFormat.parse(dateText);
                expenseDate = new Date(parsedDate.getTime());
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(addExpenseDialog, "Invalid date format. Please use yyyy-MM-dd", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get selected order
            OrderInfo selectedOrder = (OrderInfo) orderComboBox.getSelectedItem();
            int orderID = selectedOrder.getOrderID();
            
            // Create Expense object
            Expense expense = new Expense(orderID, amount, expenseDate);
            
            // Insert to database
            if (expense.insertToDatabase(conn)) {
                JOptionPane.showMessageDialog(addExpenseDialog, "Expense added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                addExpenseDialog.setVisible(false);
                
                // Refresh the expense list and available orders
                refreshExpenseList();
            }
        });
    }
    
    private void showAddExpenseDialog() {
        // Ανανέωση της λίστας διαθέσιμων παραγγελιών
        loadAvailableOrders();
        
        JComboBox<OrderInfo> orderComboBox = (JComboBox<OrderInfo>) 
            ((JPanel)((JPanel)addExpenseDialog.getContentPane().getComponent(0)).getComponent(2)).getComponent(1);
            
        // Καθαρίζουμε και προσθέτουμε τα διαθέσιμα orders
        orderComboBox.removeAllItems();
        for (OrderInfo order : availableOrders) {
            orderComboBox.addItem(order);
        }
        
        // Αν δεν υπάρχουν διαθέσιμες παραγγελίες
        if (availableOrders.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No available orders to add expenses. Only completed orders without existing expenses can be selected.",
                "No Available Orders", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        addExpenseDialog.setVisible(true);
    }
    
    // Ανανέωση της λίστας εξόδων
    private void refreshExpenseList() {
        // Καθαρίζουμε την υπάρχουσα λίστα
        expensesListPanel.removeAll();
        // Φορτώνουμε τα έξοδα ξανά
        loadExpenses();
        // Ανανεώνουμε την οθόνη
        expensesListPanel.revalidate();
        expensesListPanel.repaint();
    }
}