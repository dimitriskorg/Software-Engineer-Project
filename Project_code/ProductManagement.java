import javax.swing.*;
import java.awt.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductManagement extends JFrame {
    Manager manager;
    private Connection conn;
    private ArrayList<Product> products;
    private JPanel productsPanel;
    private JScrollPane scrollPane;

    public ProductManagement(Manager manager, Connection conn) {
        this.manager = manager;
        this.conn = conn;
        this.products = new ArrayList<>();

        // Set up the main frame
        setTitle("Manage Products");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(122, 156, 95));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Manage Products");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

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
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            this.dispose();
            new HomePage(new User(manager.getUserID(), manager.getUsername(), 
                       manager.getPassword(), manager.getEmail(), manager.getRole()), conn);
        });
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(244, 244, 244));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create products panel with GridBagLayout for flexibility
        productsPanel = new JPanel();
        productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.Y_AXIS));
        productsPanel.setBackground(new Color(244, 244, 244));
        
        // Create a scroll pane for the products
        scrollPane = new JScrollPane(productsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add Product button
        JButton addProductButton = new JButton("Add Product");
        addProductButton.setPreferredSize(new Dimension(180, 40));
        addProductButton.setBackground(new Color(194, 165, 108));
        addProductButton.setForeground(Color.WHITE);
        addProductButton.setBorderPainted(false);
        addProductButton.setFocusPainted(false);
        addProductButton.addActionListener(e -> showAddProductDialog());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(244, 244, 244));
        buttonPanel.add(addProductButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add header and main panel to the frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Load products
        loadProducts();

        setLocationRelativeTo(null);  // Center the window on screen
    }

    private void loadProducts() {
        products.clear();
        productsPanel.removeAll();
        
        // Add title row
        JPanel titleRow = new JPanel(new GridLayout(1, 5, 10, 0));
        titleRow.setBackground(new Color(230, 230, 230));
        titleRow.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel idTitle = new JLabel("ID");
        idTitle.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel nameTitle = new JLabel("Name");
        nameTitle.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel catTitle = new JLabel("Category");
        catTitle.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel descTitle = new JLabel("Description");
        descTitle.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel priceTitle = new JLabel("Price");
        priceTitle.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel actionTitle = new JLabel("Actions");
        actionTitle.setFont(new Font("Arial", Font.BOLD, 14));
        
        titleRow.add(idTitle);
        titleRow.add(nameTitle);
        titleRow.add(catTitle);
        titleRow.add(descTitle);
        titleRow.add(priceTitle);
        titleRow.add(actionTitle);
        
        titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        productsPanel.add(titleRow);
        productsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        try {
            String sql = "SELECT * FROM Product";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("ProductID");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                String category = rs.getString("category");
                
                Product product = new Product(id, name, description, price, category);
                products.add(product);
                
                // Create a panel for this product
                JPanel productPanel = createProductPanel(product);
                productsPanel.add(productPanel);
                productsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading products: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        // Refresh the panel
        productsPanel.revalidate();
        productsPanel.repaint();
    }
    
    private JPanel createProductPanel(Product product) {
        JPanel panel = new JPanel(new GridLayout(1, 6, 10, 0)); // τώρα 6 στήλες
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // 1. ID
        JLabel idLabel = new JLabel("#" + product.getProductID());
        
        // 2. Name
        JLabel nameLabel = new JLabel(product.getName());
        
        // 3. Category
        JLabel categoryLabel = new JLabel(product.getCategory());
        
        // 4. Description (με αποκοπή αν είναι πολύ μεγάλο)
        String descText = product.getDescription();
        if (descText != null && descText.length() > 30) {
            descText = descText.substring(0, 27) + "...";
        }
        JLabel descLabel = new JLabel(descText != null ? descText : "");
        
        // 5. Price
        JLabel priceLabel = new JLabel(String.format("%.2f €", product.getPrice()));
        
        // 6. Delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(220, 53, 69));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteProduct(product.getProductID()));
        
        // Προσθήκη όλων των components στο panel
        panel.add(idLabel);
        panel.add(nameLabel);
        panel.add(categoryLabel);
        panel.add(descLabel);
        panel.add(priceLabel);
        panel.add(deleteButton);
        
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        return panel;
    }
    
    private void showAddProductDialog() {
        // Create a dialog for adding a new product
        JDialog dialog = new JDialog(this, "Add Product", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Name field
        JLabel nameLabel = new JLabel("Product name:");
        JTextField nameField = new JTextField(20);
        
        // Price field
        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField(10);
        
        // Description field
        JLabel descLabel = new JLabel("Description (optional):");
        JTextArea descArea = new JTextArea(3, 20);
        JScrollPane descScrollPane = new JScrollPane(descArea);
        
        // Category field
        JLabel categoryLabel = new JLabel("Category:");
        JTextField categoryField = new JTextField(20);
        
        // Add components to form
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryField);
        formPanel.add(descLabel);
        formPanel.add(descScrollPane);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(194, 165, 108));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorderPainted(false);
        saveButton.addActionListener(e -> {
            try {
                if (nameField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Product name is mandatory", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
        
                double price;
                try {
                    price = Double.parseDouble(priceField.getText());
                    if (price < 0) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Price cannot be negative.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Enter a valid price", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
        
                // Βήμα 1: Δημιουργία dummy Product αντικειμένου
                Product dummyProduct = new Product(-1, nameField.getText(), descArea.getText(), price, categoryField.getText());
        
                // Βήμα 2: Εισαγωγή στη βάση και λήψη του νέου productID
                int newProductID = Product.addNewProduct(dummyProduct, conn);
        
                // Βήμα 3: Δημιουργία πλήρους αντικειμένου με σωστό ID
                Product actualProduct = new Product(newProductID, dummyProduct.getName(), dummyProduct.getDescription(), dummyProduct.getPrice(), dummyProduct.getCategory());
        
                // Προσθήκη στο τοπικό ArrayList και στο GUI
                products.add(actualProduct);
                JPanel productPanel = createProductPanel(actualProduct);
                productsPanel.add(productPanel);
                productsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
                productsPanel.revalidate();
                productsPanel.repaint();
        
                dialog.dispose();
        
                JOptionPane.showMessageDialog(this, 
                    "Product added successfully.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error while saving: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
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
    
    private void deleteProduct(int productId) {
    // Show confirmation dialog
    int option = JOptionPane.showConfirmDialog(this, 
        "Are you sure you want to delete the product?", 
        "Confirm deletion", JOptionPane.YES_NO_OPTION);
    
    if (option == JOptionPane.YES_OPTION) {
        try {
            // Ξεκινάμε συναλλαγή για να διασφαλίσουμε την ακεραιότητα
            conn.setAutoCommit(false);
            
            // 1. Διαγραφή από πίνακα OrderItem
            String deleteOrderItems = "DELETE FROM OrderItem WHERE productID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteOrderItems)) {
                stmt.setInt(1, productId);
                stmt.executeUpdate();
            }
            
            // 2. Διαγραφή από πίνακα ManagerInventory που σχετίζεται με το Inventory αυτού του προϊόντος
            String deleteManagerInventory = "DELETE FROM ManagerInventory WHERE InventoryID IN (SELECT InventoryID FROM Inventory WHERE productID = ?)";
            try (PreparedStatement stmt = conn.prepareStatement(deleteManagerInventory)) {
                stmt.setInt(1, productId);
                stmt.executeUpdate();
            }
            
            // 3. Διαγραφή από πίνακα Inventory
            String deleteInventory = "DELETE FROM Inventory WHERE productID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteInventory)) {
                stmt.setInt(1, productId);
                stmt.executeUpdate();
            }
            
            // 4. Διαγραφή από πίνακα ProductMaterials
            String deleteProductMaterials = "DELETE FROM ProductMaterials WHERE ProductID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteProductMaterials)) {
                stmt.setInt(1, productId);
                stmt.executeUpdate();
            }
            
            // 5. Τέλος, διαγραφή του προϊόντος
            String deleteProduct = "DELETE FROM Product WHERE ProductID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteProduct)) {
                stmt.setInt(1, productId);
                stmt.executeUpdate();
            }
            
            // Ολοκλήρωση της συναλλαγής
            conn.commit();
            conn.setAutoCommit(true);
            
            // Show success message
            JOptionPane.showMessageDialog(this, 
                "Product deleted successfully", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Reload products
            loadProducts();
            
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
                "Error while deleting product: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
}