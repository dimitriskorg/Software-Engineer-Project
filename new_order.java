import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class new_order extends JFrame {
    private JButton submitButton;
    private JButton logoutButton;
    private JTextField fullnameField, emailField, phoneField, addressField, dateField;
    private JComboBox<String> productCombo;
    private JSpinner quantitySpinner;

    public new_order() {
        // Set up the main frame
        setTitle("Add New Order");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
        setLayout(new BorderLayout());

        // Create header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(122, 156, 95));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Add New Order", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(178, 34, 34));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> System.exit(0));
        
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setOpaque(false);
        logoutPanel.add(logoutButton);
        headerPanel.add(logoutPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Create the form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(0, 1, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Full Name
        JLabel fullnameLabel = new JLabel("Full Name:");
        fullnameField = new JTextField();
        
        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField();
        
        // Phone
        JLabel phoneLabel = new JLabel("Phone:");
        phoneField = new JTextField();
        
        // Address
        JLabel addressLabel = new JLabel("Address:");
        addressField = new JTextField();
        
        // Product
        JLabel productLabel = new JLabel("Product Selection:");
        productCombo = new JComboBox<>();
        productCombo.addItem("Product 1");
        productCombo.addItem("Product 2");
        productCombo.addItem("Product 3");
        
        // Quantity
        JLabel quantityLabel = new JLabel("Quantity:");
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        
        // Delivery Date (just a text field)
        JLabel dateLabel = new JLabel("Delivery Date:");
        dateField = new JTextField();
        
        // Add form components
        formPanel.add(fullnameLabel);
        formPanel.add(fullnameField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(phoneLabel);
        formPanel.add(phoneField);
        formPanel.add(addressLabel);
        formPanel.add(addressField);
        formPanel.add(productLabel);
        formPanel.add(productCombo);
        formPanel.add(quantityLabel);
        formPanel.add(quantitySpinner);
        formPanel.add(dateLabel);
        formPanel.add(dateField);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel();
        submitButton = new JButton("Submit Order");
        submitButton.setBackground(new Color(194, 165, 108));
        submitButton.setForeground(Color.BLACK);
        submitButton.setFont(new Font("Arial", Font.PLAIN, 16));
        submitButton.setPreferredSize(new Dimension(250, 45));
        submitButton.addActionListener(e -> submitOrder());
        
        buttonPanel.add(submitButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void submitOrder() {
        String fullname = fullnameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String product = (String) productCombo.getSelectedItem();
        int quantity = (Integer) quantitySpinner.getValue();
        String deliveryDate = dateField.getText();
        
        // For simplicity, just print the order details
        System.out.println("Order Submitted!");
        System.out.println("Name: " + fullname);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phone);
        System.out.println("Address: " + address);
        System.out.println("Product: " + product);
        System.out.println("Quantity: " + quantity);
        System.out.println("Delivery Date: " + deliveryDate);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new_order app = new new_order();
            app.setVisible(true);
        });
    }
}