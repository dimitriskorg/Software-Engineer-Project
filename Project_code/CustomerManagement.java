import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerManagement extends JFrame {
    private Manager manager;
    private Connection conn;
    private ArrayList<Customer> customers;
    private JPanel customersPanel;
    private JScrollPane scrollPane;

    public CustomerManagement(Manager manager, Connection conn) {
        this.manager = manager;
        this.conn = conn;
        this.customers = new ArrayList<>();

        // Set up the main frame
        setTitle("Διαχείριση Πελατών");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(122, 156, 95));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Διαχείριση Πελατών");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(178, 34, 34));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> System.exit(0));
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setOpaque(false);
        logoutPanel.add(logoutButton);
        headerPanel.add(logoutPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Back button
        JButton backButton = new JButton("Πίσω");
        backButton.setBackground(new Color(194, 165, 108));
        backButton.setForeground(Color.WHITE);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            this.dispose();
            new HomePage(new User(manager.getUserID(), manager.getUsername(), 
                       manager.getPassword(), manager.getEmail(), manager.getRole()), conn);
        });
        
        headerPanel.add(backButton, BorderLayout.WEST);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(244, 244, 244));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create customers panel with BoxLayout
        customersPanel = new JPanel();
        customersPanel.setLayout(new BoxLayout(customersPanel, BoxLayout.Y_AXIS));
        customersPanel.setBackground(new Color(244, 244, 244));
        
        // Create a scroll pane for the customers
        scrollPane = new JScrollPane(customersPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add Customer button
        JButton addCustomerButton = new JButton("Προσθήκη Πελάτη");
        addCustomerButton.setPreferredSize(new Dimension(180, 40));
        addCustomerButton.setBackground(new Color(194, 165, 108));
        addCustomerButton.setForeground(Color.WHITE);
        addCustomerButton.setBorderPainted(false);
        addCustomerButton.setFocusPainted(false);
        addCustomerButton.addActionListener(e -> showAddCustomerDialog());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(244, 244, 244));
        buttonPanel.add(addCustomerButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add header and main panel to the frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Load customers
        loadCustomers();

        setLocationRelativeTo(null);  // Center the window on screen
        setVisible(true);
    }

    private void loadCustomers() {
        customers.clear();
        customersPanel.removeAll();
        
        // Add title row
        JPanel titleRow = new JPanel(new GridLayout(1, 6, 10, 0));
        titleRow.setBackground(new Color(230, 230, 230));
        titleRow.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel idTitle = new JLabel("ID");
        idTitle.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel nameTitle = new JLabel("Όνομα");
        nameTitle.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel phoneTitle = new JLabel("Τηλέφωνο");
        phoneTitle.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel emailTitle = new JLabel("Email");
        emailTitle.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel addressTitle = new JLabel("Διεύθυνση");
        addressTitle.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel actionTitle = new JLabel("Ενέργειες");
        actionTitle.setFont(new Font("Arial", Font.BOLD, 14));
        
        titleRow.add(idTitle);
        titleRow.add(nameTitle);
        titleRow.add(phoneTitle);
        titleRow.add(emailTitle);
        titleRow.add(addressTitle);
        titleRow.add(actionTitle);
        
        titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        customersPanel.add(titleRow);
        customersPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        try {
            String sql = "SELECT * FROM Customer";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("CustomerID");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                String address = rs.getString("address");
                
                Customer customer = new Customer(id, name, phone, email, address);
                customers.add(customer);
                
                // Create a panel for this customer
                JPanel customerPanel = createCustomerPanel(customer);
                customersPanel.add(customerPanel);
                customersPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Σφάλμα κατά τη φόρτωση των πελατών: " + e.getMessage(), 
                "Σφάλμα", JOptionPane.ERROR_MESSAGE);
        }
        
        // Refresh the panel
        customersPanel.revalidate();
        customersPanel.repaint();
    }
    
    private JPanel createCustomerPanel(Customer customer) {
        JPanel panel = new JPanel(new GridLayout(1, 6, 10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // 1. ID
        JLabel idLabel = new JLabel("#" + customer.getCustomerID());
        
        // 2. Name
        JLabel nameLabel = new JLabel(customer.getName());
        
        // 3. Phone
        JLabel phoneLabel = new JLabel(customer.getPhone());
        
        // 4. Email (με αποκοπή αν είναι πολύ μεγάλο)
        String emailText = customer.getEmail();
        if (emailText != null && emailText.length() > 30) {
            emailText = emailText.substring(0, 27) + "...";
        }
        JLabel emailLabel = new JLabel(emailText != null ? emailText : "");
        
        // 5. Address (με αποκοπή αν είναι πολύ μεγάλο)
        String addressText = customer.getAddress();
        if (addressText != null && addressText.length() > 30) {
            addressText = addressText.substring(0, 27) + "...";
        }
        JLabel addressLabel = new JLabel(addressText != null ? addressText : "");
        
        // 6. Delete button
        JButton deleteButton = new JButton("Διαγραφή");
        deleteButton.setBackground(new Color(220, 53, 69));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteCustomer(customer.getCustomerID()));
        
        // Προσθήκη όλων των components στο panel
        panel.add(idLabel);
        panel.add(nameLabel);
        panel.add(phoneLabel);
        panel.add(emailLabel);
        panel.add(addressLabel);
        panel.add(deleteButton);
        
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        return panel;
    }
    
    private void showAddCustomerDialog() {
        // Create a dialog for adding a new customer
        JDialog dialog = new JDialog(this, "Προσθήκη Πελάτη", true);
        dialog.setSize(400, 350);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Name field
        JLabel nameLabel = new JLabel("Όνομα πελάτη:");
        JTextField nameField = new JTextField(20);
        
        // Phone field
        JLabel phoneLabel = new JLabel("Τηλέφωνο:");
        JTextField phoneField = new JTextField(10);
        
        // Email field
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);
        
        // Address field
        JLabel addressLabel = new JLabel("Διεύθυνση:");
        JTextField addressField = new JTextField(20);
        
        // Add components to form
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(phoneLabel);
        formPanel.add(phoneField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(addressLabel);
        formPanel.add(addressField);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Ακύρωση");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = new JButton("Καταχώρηση");
        saveButton.setBackground(new Color(194, 165, 108));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorderPainted(false);
        saveButton.addActionListener(e -> {
            try {
                if (nameField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Το όνομα πελάτη είναι υποχρεωτικό.", 
                        "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (phoneField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Το τηλέφωνο είναι υποχρεωτικό.", 
                        "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                    return;
                }
        
                // Βήμα 1: Δημιουργία dummy Customer αντικειμένου
                Customer dummyCustomer = new Customer(-1, nameField.getText().trim(), 
                                                    phoneField.getText().trim(), 
                                                    emailField.getText().trim(), 
                                                    addressField.getText().trim());
        
                // Βήμα 2: Εισαγωγή στη βάση και λήψη του νέου customerID
                int newCustomerID = Customer.addNewCustomer(dummyCustomer, conn);
        
                // Βήμα 3: Δημιουργία πλήρους αντικειμένου με σωστό ID
                Customer actualCustomer = new Customer(newCustomerID, dummyCustomer.getName(), 
                                                    dummyCustomer.getPhone(), dummyCustomer.getEmail(), 
                                                    dummyCustomer.getAddress());
        
                // Προσθήκη στο τοπικό ArrayList και στο GUI
                customers.add(actualCustomer);
                JPanel customerPanel = createCustomerPanel(actualCustomer);
                customersPanel.add(customerPanel);
                customersPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
                customersPanel.revalidate();
                customersPanel.repaint();
        
                dialog.dispose();
        
                JOptionPane.showMessageDialog(this, 
                    "Ο πελάτης προστέθηκε επιτυχώς.", 
                    "Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
        
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Σφάλμα κατά την αποθήκευση: " + ex.getMessage(), 
                    "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void deleteCustomer(int customerId) {
        // Show confirmation dialog
        int option = JOptionPane.showConfirmDialog(this, 
            "Είστε βέβαιοι ότι θέλετε να διαγράψετε αυτόν τον πελάτη;", 
            "Επιβεβαίωση διαγραφής", JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            try {
                // Ξεκινάμε συναλλαγή για να διασφαλίσουμε την ακεραιότητα
                conn.setAutoCommit(false);
                
                // 1. Έλεγχος για παραγγελίες του πελάτη
                String checkOrders = "SELECT COUNT(*) FROM CustomerOrder WHERE CustomerID = ?";
                try (PreparedStatement stmt = conn.prepareStatement(checkOrders)) {
                    stmt.setInt(1, customerId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        conn.rollback();
                        JOptionPane.showMessageDialog(this, 
                            "Δεν είναι δυνατή η διαγραφή! Ο πελάτης έχει παραγγελίες στο σύστημα.", 
                            "Προειδοποίηση", JOptionPane.WARNING_MESSAGE);
                        conn.setAutoCommit(true);
                        return;
                    }
                }
                
                // 2. Διαγραφή του πελάτη
                String deleteCustomer = "DELETE FROM Customer WHERE CustomerID = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteCustomer)) {
                    stmt.setInt(1, customerId);
                    int result = stmt.executeUpdate();
                    
                    if (result > 0) {
                        // Ολοκλήρωση της συναλλαγής
                        conn.commit();
                        conn.setAutoCommit(true);
                        
                        // Show success message
                        JOptionPane.showMessageDialog(this, 
                            "Ο πελάτης αφαιρέθηκε επιτυχώς.", 
                            "Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
                        
                        // Reload customers
                        loadCustomers();
                    } else {
                        // Δεν βρέθηκε ο πελάτης
                        conn.rollback();
                        conn.setAutoCommit(true);
                        JOptionPane.showMessageDialog(this, 
                            "Ο πελάτης δεν βρέθηκε.", 
                            "Προειδοποίηση", JOptionPane.WARNING_MESSAGE);
                    }
                }
                
            } catch (SQLException e) {
                try {
                    // Αναίρεση της συναλλαγής σε περίπτωση σφάλματος
                    conn.rollback();
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Σφάλμα κατά τη διαγραφή του πελάτη: " + e.getMessage(), 
                    "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}