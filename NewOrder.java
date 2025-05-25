import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class NewOrder extends JFrame {
    private Connection conn;
    private JButton submitButton, logoutButton;
    private JTextField fullnameField, emailField, phoneField, addressField, dateField, totalAmountField;
    private JSpinner quantitySpinner;
    private DeliveryDriver deliveryDriver;
    private JComboBox customerCombo;
    private JComboBox<Product> productCombo;

    public NewOrder(DeliveryDriver deliveryDriver, ArrayList<Customer> customers, ArrayList<Product> products, Connection conn) {
        this.conn = conn;
        this.deliveryDriver = deliveryDriver;
    
        setTitle("Add New Order");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 600);
        setLayout(new BorderLayout());
    
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(122, 156, 95));
        JLabel titleLabel = new JLabel("Add New Order", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
    
        logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(255,0,0));
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> System.exit(0));
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setOpaque(false);
        logoutPanel.add(logoutButton);
        headerPanel.add(logoutPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Back button
        JButton backButton = new JButton("Back");
        backButton.setBackground(new Color(194, 165, 108));
        backButton.setForeground(Color.WHITE);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            this.dispose();
            new HomePage(new User(deliveryDriver.getUserID(), deliveryDriver.getUsername(), 
                       deliveryDriver.getPassword(), deliveryDriver.getEmail(), deliveryDriver.getRole()), conn);
        });
        headerPanel.add(backButton, BorderLayout.WEST);
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        // Customers dropdown
        customerCombo = new JComboBox<>();
        for (Customer customer : customers) {
            customerCombo.addItem(customer);
        }
        
        // Products dropdown
        productCombo = new JComboBox<>();
        for (Product product : products) {
            productCombo.addItem(product);
        }
        
        // Αυτόματος υπολογισμός συνολικού ποσού όταν αλλάζει το προϊόν ή η ποσότητα
        productCombo.addActionListener(e -> updateTotalAmount());
        
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        quantitySpinner.addChangeListener(e -> updateTotalAmount());
        
        dateField = new JTextField();
        totalAmountField = new JTextField();
        totalAmountField.setEditable(false); // Αποτροπή χειροκίνητης επεξεργασίας
    
        formPanel.add(new JLabel("Select Customer:")); formPanel.add(customerCombo);
        formPanel.add(new JLabel("Product ID:")); formPanel.add(productCombo);
        formPanel.add(new JLabel("Quantity:")); formPanel.add(quantitySpinner);
        formPanel.add(new JLabel("Delivery Date (YYYY-MM-DD):")); formPanel.add(dateField);
        formPanel.add(new JLabel("Total Amount (€):")); formPanel.add(totalAmountField);
    
        add(formPanel, BorderLayout.CENTER);
    
        // Button panel
        JPanel buttonPanel = new JPanel();
        submitButton = new JButton("Submit Order");
        submitButton.setBackground(new Color(194, 165, 108));
        submitButton.setForeground(Color.BLACK);
        submitButton.setFont(new Font("Arial", Font.PLAIN, 16));
        submitButton.setPreferredSize(new Dimension(250, 45));
        submitButton.addActionListener(e -> createOrder());
        buttonPanel.add(submitButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Αρχικός υπολογισμός του συνολικού ποσού
        updateTotalAmount();
    }
    
    // Μέθοδος για την ενημέρωση του συνολικού ποσού με βάση το επιλεγμένο προϊόν και την ποσότητα
    private void updateTotalAmount() {
        Product selectedProduct = (Product) productCombo.getSelectedItem();
        if (selectedProduct != null) {
            int quantity = (Integer) quantitySpinner.getValue();
            double pricePerUnit = selectedProduct.getPrice(); // Υποθέτουμε ότι η κλάση Product έχει μέθοδο getPrice()
            double total = quantity * pricePerUnit;
            // Χρησιμοποιούμε τελεία αντί για κόμμα στο δεκαδικό μέρος
            totalAmountField.setText(String.format("%.2f", total).replace(",", "."));
        }
    }
    
    private void createOrder() {
        try {
            Customer selectedCustomer = (Customer) customerCombo.getSelectedItem();
            int customerId = selectedCustomer.getCustomerID();
            
            Product selectedProduct = (Product) productCombo.getSelectedItem();
            int productId = selectedProduct.getProductID();
        
            int quantity = (Integer) quantitySpinner.getValue();
            String dateStr = dateField.getText();
            String totalAmountStr = totalAmountField.getText();
    
            if (!validateCustomer(selectedCustomer)) {
                showError("Invalid customer.");
                return;
            }
            
            if (!validateProduct(selectedProduct)) {
                showError("Invalid product.");
                return;
            }
            
            if (!validateDate(dateStr)) {
                showError("Invalid date format.");
                return;
            }
            
            if (!validateQuantity(quantity)) {
                showError("Quantity must be greater than 0.");
                return;
            }
            
            // Αντικατάσταση κόμματος με τελεία για σωστή μετατροπή σε double
            String parsableTotalAmount = totalAmountStr.replace(",", ".");
            double totalAmount = Double.parseDouble(parsableTotalAmount);
            double pricePerUnit = selectedProduct.getPrice();
    
            Date deliveryDate = Date.valueOf(dateStr);
            Date orderDate = new Date(System.currentTimeMillis());
    
            // Δημιουργία αντικειμένου Order
            Order tmp = new Order(0, customerId, "Pending", orderDate, deliveryDate, totalAmount);
    
            // Αποθήκευση στη βάση - περνάμε productId, quantity και pricePerUnit
            int orderID = tmp.saveOrder(conn, productId, quantity);
            
            Order order = new Order(orderID, customerId, "Pending", orderDate, deliveryDate, totalAmount);
            tmp = null;

    
            if (orderID != 0) {
                showSuccess("Order submitted successfully!");
                dispose();
            } else {
                showError("Order submission failed.");
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("An error occurred while submitting the order: " + ex.getMessage());
        }
    }

    private boolean validateCustomer(Customer customer) {
        if (customer == null) return false;
    
        try (PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM Customer WHERE customerID = ?")) {
            stmt.setInt(1, customer.getCustomerID());
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }
    
    private boolean validateQuantity(int quantity) {
        return quantity > 0;
    }
    
    private boolean validateProduct(Product product) {
        if (product == null) return false;
        
        try (PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM Product WHERE productID = ?")) {
            stmt.setInt(1, product.getProductID());
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean validateDate(String dateStr) {
        try {
            Date.valueOf(dateStr);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}