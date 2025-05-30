import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;

public class ViewInvoice extends JFrame {
    private Connection connection;
    private User user;
    private JTable customersTable;
    private DefaultTableModel tableModel;
    private List<Customer> customers;
    private List<Invoice> invoices;
    private List<List<OrderItem>> customerOrderItems; // List for each customer's products
    private JButton refreshButton;
    private JButton logoutButton;

    public ViewInvoice(User user, Connection connection) {
        this.connection = connection;
        this.user = user;
        this.customers = new ArrayList<>();
        this.invoices = new ArrayList<>();
        this.customerOrderItems = new ArrayList<>();
        
        initializeUI();
        loadCustomersAndCreateInvoices();
    }

    private void initializeUI() {
        setTitle("Customer Invoice Viewer");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(122, 156, 95));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = new JLabel("Customer Invoices - " + getCurrentMonth(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(178, 34, 34)); // #b22222
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setOpaque(true);
        logoutButton.setContentAreaFilled(true);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> System.exit(0));

        headerPanel.add(logoutButton, BorderLayout.EAST);

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setOpaque(false);
        logoutPanel.add(logoutButton);
        headerPanel.add(logoutPanel, BorderLayout.EAST);
        
        // Back button
        JButton backButton = new JButton("Back");
        backButton.setBackground(new Color(194, 165, 108));
        backButton.setForeground(Color.WHITE);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            this.dispose();
            new HomePage(new User(user.getUserID(), user.getUsername(), 
                       user.getPassword(), user.getEmail(), user.getRole()), connection);
        });
        headerPanel.add(backButton, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create table
        String[] columnNames = {"Customer", "Email", "Phone", "Orders", "Total Amount", "View Invoice"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only the button column is editable
            }
        };

        customersTable = new JTable(tableModel);
        customersTable.setRowHeight(40);
        customersTable.setFont(new Font("Arial", Font.PLAIN, 12));
        customersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        customersTable.getTableHeader().setBackground(new Color(194, 165, 108));

        // Add button renderer and editor for the last column
        //customersTable.getColumn("View Invoice").setCellRenderer(new ButtonRenderer());
        customersTable.getColumn("View Invoice").setCellEditor(new ButtonEditor());
        customersTable.getColumn("View Invoice").setPreferredWidth(120);
        customersTable.getColumn("View Invoice").setMaxWidth(120);
        customersTable.getColumn("View Invoice").setMinWidth(120);

        JScrollPane scrollPane = new JScrollPane(customersTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Customers with Enabled Invoicing"));

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with refresh button
        JPanel bottomPanel = new JPanel(new FlowLayout());
        refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(0,0,255));
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setFocusPainted(false);
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.addActionListener(e -> {
            loadCustomersAndCreateInvoices();
            JOptionPane.showMessageDialog(this, "Data refreshed successfully!");
        });

        bottomPanel.add(refreshButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadCustomersAndCreateInvoices() {
        // Clear existing data
        tableModel.setRowCount(0);
        customers.clear();
        invoices.clear();
        customerOrderItems.clear();

        List<Customer> eligibleCustomers = getCustomers();
        
        for (Customer customer : eligibleCustomers) {
            int orderCount = getOrders(customer.getCustomerID());
            double totalAmount = getTotalAmountForCurrentMonth(customer.getCustomerID());
            List<OrderItem> orderItems = getOrderItemsForCustomer(customer.getCustomerID());
            
            if (orderCount > 0) {
                Invoice invoice = createInvoice(customer.getCustomerID(), totalAmount);
                if (invoice != null) {
                    customers.add(customer);
                    invoices.add(invoice);
                    customerOrderItems.add(orderItems);
                    
                    Object[] rowData = {
                        customer.getName(),
                        customer.getEmail(),
                        customer.getPhone(),
                        orderCount + " orders",
                        String.format("$%.2f", totalAmount),
                        "View"
                    };
                    tableModel.addRow(rowData);
                }
            } else {
                // Add customer even with 0 orders
                customers.add(customer);
                invoices.add(null); // No invoice for customers with no orders
                customerOrderItems.add(new ArrayList<>()); // Empty list for customers with no orders
                
                Object[] rowData = {
                    customer.getName(),
                    customer.getEmail(),
                    customer.getPhone(),
                    "No orders",
                    "$0.00",
                    "Not Available"
                };
                tableModel.addRow(rowData);
            }
        }
    }

    private List<Customer> getCustomers() {
        List<Customer> customerList = new ArrayList<>();
        String sql = "SELECT * FROM Customer WHERE wantsInvoice = 'Yes'";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Customer customer = new Customer(
                    rs.getInt("CustomerID"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address")
                );
                customerList.add(customer);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading customers: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        return customerList;
    }

    private int getOrders(int customerID) {
        String sql = "SELECT COUNT(*) as orderCount FROM OrderTable WHERE CustomerID = ? " +
                     "AND MONTH(orderDate) = MONTH(CURDATE()) AND YEAR(orderDate) = YEAR(CURDATE())";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("orderCount");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error retrieving orders: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
        
        return 0;
    }

    private double getTotalAmountForCurrentMonth(int customerID) {
        String sql = "SELECT SUM(totalAmount) as total FROM OrderTable WHERE CustomerID = ? " +
                     "AND MONTH(orderDate) = MONTH(CURDATE()) AND YEAR(orderDate) = YEAR(CURDATE())";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error calculating total amount: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
        
        return 0.0;
    }

    // New method to get products for each customer
    private List<OrderItem> getOrderItemsForCustomer(int customerID) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT oi.OrderItemID, oi.orderID, oi.productID, oi.quantity, oi.totalPrice, " +
                     "p.name as productName, p.description, p.price as unitPrice " +
                     "FROM OrderItem oi " +
                     "JOIN OrderTable o ON oi.orderID = o.OrderID " +
                     "JOIN Product p ON oi.productID = p.ProductID " +
                     "WHERE o.CustomerID = ? " +
                     "AND MONTH(o.orderDate) = MONTH(CURDATE()) AND YEAR(o.orderDate) = YEAR(CURDATE()) " +
                     "ORDER BY oi.orderID, oi.OrderItemID";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerID);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                OrderItem item = new OrderItem(
                    rs.getInt("OrderItemID"),
                    rs.getInt("orderID"),
                    rs.getInt("productID"),
                    rs.getInt("quantity"),
                    rs.getDouble("totalPrice"),
                    rs.getString("productName"),
                    rs.getString("description"),
                    rs.getDouble("unitPrice")
                );
                orderItems.add(item);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error retrieving products: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
        
        return orderItems;
    }

    private Invoice createInvoice(int customerID, double totalAmount) {
        // First, check if invoice already exists for this customer this month
        String checkSql = "SELECT * FROM Invoice i JOIN OrderTable o ON i.OrderID = o.OrderID " +
                         "WHERE o.CustomerID = ? " +
                         "AND MONTH(i.issueDate) = MONTH(CURDATE()) AND YEAR(i.issueDate) = YEAR(CURDATE())";
        
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, customerID);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Invoice already exists, return existing invoice
                return new Invoice(
                    rs.getInt("InvoiceID"),
                    rs.getInt("OrderID"),
                    rs.getDate("issueDate"),
                    rs.getDate("dueDate"),
                    rs.getDouble("total"),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error checking existing invoice: " + e.getMessage());
        }

        // Create new invoice
        // First get a representative order from this customer for this month
        String getOrderSql = "SELECT OrderID FROM OrderTable WHERE CustomerID = ? " +
                            "AND MONTH(orderDate) = MONTH(CURDATE()) AND YEAR(orderDate) = YEAR(CURDATE()) " +
                            "LIMIT 1";
        
        int orderID = -1;
        try (PreparedStatement stmt = connection.prepareStatement(getOrderSql)) {
            stmt.setInt(1, customerID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                orderID = rs.getInt("OrderID");
            } else {
                return null; // No orders found
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error retrieving order: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            return null;
        }

        // Create invoice in database
        String insertSql = "INSERT INTO Invoice (OrderID, issueDate, dueDate, total, status) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            LocalDate issueDate = LocalDate.now();
            LocalDate dueDate = issueDate.plusDays(30);
            
            stmt.setInt(1, orderID);
            stmt.setDate(2, Date.valueOf(issueDate));
            stmt.setDate(3, Date.valueOf(dueDate));
            stmt.setDouble(4, totalAmount);
            stmt.setString(5, "Pending");
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int invoiceID = rs.getInt(1);
                return new Invoice(invoiceID, orderID, Date.valueOf(issueDate), Date.valueOf(dueDate), totalAmount, "Pending");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error creating invoice: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
        
        return null;
    }

    private void showInvoiceDetails(int customerIndex) {
        if (customerIndex >= 0 && customerIndex < invoices.size()) {
            Invoice invoice = invoices.get(customerIndex);
            Customer customer = customers.get(customerIndex);
            List<OrderItem> orderItems = customerOrderItems.get(customerIndex);
            
            if (invoice == null) {
                JOptionPane.showMessageDialog(this,
                    "No invoice available for this customer.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            showInvoiceWindow(invoice, customer, orderItems);
        }
    }

    private void showInvoiceWindow(Invoice invoice, Customer customer, List<OrderItem> orderItems) {
        JDialog invoiceDialog = new JDialog(this, "Invoice Details", true);
        invoiceDialog.setSize(700, 600);
        invoiceDialog.setLocationRelativeTo(this);
        invoiceDialog.setLayout(new BorderLayout());

        // Create main panel with scroll
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create invoice details panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Invoice Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Add invoice details
        addDetailRow(detailsPanel, gbc, 0, "Invoice Number:", String.valueOf(invoice.getInvoiceID()));
        addDetailRow(detailsPanel, gbc, 1, "Customer:", customer.getName());
        addDetailRow(detailsPanel, gbc, 2, "Email:", customer.getEmail());
        addDetailRow(detailsPanel, gbc, 3, "Phone:", customer.getPhone());
        addDetailRow(detailsPanel, gbc, 4, "Address:", customer.getAddress());
        addDetailRow(detailsPanel, gbc, 5, "Issue Date:", invoice.getIssueDate().toString());
        addDetailRow(detailsPanel, gbc, 6, "Due Date:", invoice.getDueDate().toString());
        addDetailRow(detailsPanel, gbc, 7, "Status:", invoice.getStatus());

        mainPanel.add(detailsPanel, BorderLayout.NORTH);

        // Create products table
        if (!orderItems.isEmpty()) {
            JPanel productsPanel = new JPanel(new BorderLayout());
            productsPanel.setBorder(BorderFactory.createTitledBorder("Products"));
            
            String[] productColumns = {"Product", "Description", "Unit Price", "Quantity", "Total"};
            DefaultTableModel productTableModel = new DefaultTableModel(productColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            JTable productsTable = new JTable(productTableModel);
            productsTable.setFont(new Font("Arial", Font.PLAIN, 11));
            productsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
            productsTable.getTableHeader().setBackground(new Color(194, 165, 108));
            productsTable.setRowHeight(30);
            
            // Add products to table
            for (OrderItem item : orderItems) {
                Object[] rowData = {
                    item.getProductName(),
                    item.getDescription(),
                    String.format("$%.2f", item.getUnitPrice()),
                    item.getQuantity(),
                    String.format("$%.2f", item.getTotalPrice())
                };
                productTableModel.addRow(rowData);
            }
            
            JScrollPane productsScrollPane = new JScrollPane(productsTable);
            productsScrollPane.setPreferredSize(new Dimension(650, 200));
            productsPanel.add(productsScrollPane, BorderLayout.CENTER);
            
            // Add total row
            JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JLabel totalLabel = new JLabel("Total Amount: " + String.format("$%.2f", invoice.getTotal()));
            totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
            totalLabel.setForeground(new Color(122, 156, 95));
            totalPanel.add(totalLabel);
            productsPanel.add(totalPanel, BorderLayout.SOUTH);
            
            mainPanel.add(productsPanel, BorderLayout.CENTER);
        } else {
            JLabel noProductsLabel = new JLabel("No products to display", SwingConstants.CENTER);
            noProductsLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            mainPanel.add(noProductsLabel, BorderLayout.CENTER);
        }

        // Add scroll pane for the entire content
        JScrollPane mainScrollPane = new JScrollPane(mainPanel);
        invoiceDialog.add(mainScrollPane, BorderLayout.CENTER);

        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton saveButton = new JButton("Save to Computer");
        saveButton.setBackground(new Color(0, 255, 0));
        saveButton.setForeground(Color.BLACK);
        saveButton.setFocusPainted(false);
        saveButton.setPreferredSize(new Dimension(150, 30));
        saveButton.addActionListener(e -> saveInvoiceToFile(invoice, customer, orderItems));

        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(255, 0, 0));
        closeButton.setForeground(Color.BLACK);
        closeButton.setFocusPainted(false);
        closeButton.setPreferredSize(new Dimension(80, 30));
        closeButton.addActionListener(e -> invoiceDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);
        invoiceDialog.add(buttonPanel, BorderLayout.SOUTH);

        invoiceDialog.setVisible(true);
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(labelComponent, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(valueComponent, gbc);
    }

    private void saveInvoiceToFile(Invoice invoice, Customer customer, List<OrderItem> orderItems) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Invoice");
        fileChooser.setSelectedFile(new java.io.File("Invoice_" + customer.getName().replaceAll("\\s+", "_") + 
                                                    "_" + getCurrentMonth().replaceAll("\\s+", "_") + ".txt"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            
            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write("=== INVOICE ===\n\n");
                writer.write("Invoice Number: " + invoice.getInvoiceID() + "\n");
                writer.write("Issue Date: " + invoice.getIssueDate() + "\n");
                writer.write("Due Date: " + invoice.getDueDate() + "\n\n");
                
                writer.write("=== CUSTOMER DETAILS ===\n");
                writer.write("Name: " + customer.getName() + "\n");
                writer.write("Email: " + customer.getEmail() + "\n");
                writer.write("Phone: " + customer.getPhone() + "\n");
                writer.write("Address: " + customer.getAddress() + "\n\n");
                
                // Add product details
                if (!orderItems.isEmpty()) {
                    writer.write("=== PRODUCTS ===\n");
                    writer.write(String.format("%-30s %-15s %-10s %-15s\n", "Product", "Unit Price", "Quantity", "Total"));
                    writer.write("--------------------------------------------------------------------------------\n");
                    
                    for (OrderItem item : orderItems) {
                        writer.write(String.format("%-30s %-15s %-10d %-15s\n",
                            truncateString(item.getProductName(), 30),
                            String.format("$%.2f", item.getUnitPrice()),
                            item.getQuantity(),
                            String.format("$%.2f", item.getTotalPrice())
                        ));
                        
                        // Add description if exists
                        if (item.getDescription() != null && !item.getDescription().trim().isEmpty()) {
                            writer.write("   Description: " + item.getDescription() + "\n");
                        }
                        writer.write("\n");
                    }
                    writer.write("--------------------------------------------------------------------------------\n");
                }
                
                writer.write("=== FINANCIAL DETAILS ===\n");
                writer.write("Total Amount: " + String.format("$%.2f", invoice.getTotal()) + "\n");
                writer.write("Status: " + invoice.getStatus() + "\n\n");
                
                writer.write("Invoice Month: " + getCurrentMonth() + "\n");
                writer.write("File Creation Date: " + LocalDate.now() + "\n");

                JOptionPane.showMessageDialog(this,
                    "Invoice saved successfully to:\n" + fileToSave.getAbsolutePath(),
                    "Save Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Error saving file: " + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Helper method to limit text length
    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    private String getCurrentMonth() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
    }

    // Fixed Button renderer for table
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFocusPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            if (row < invoices.size() && invoices.get(row) != null) {
                setText("View");
                setBackground(new Color(122, 156, 255));
                setForeground(Color.BLACK);
                setEnabled(true);
            } else {
                setText("Not Available");
                setBackground(Color.GRAY);
                setForeground(Color.WHITE);
                setEnabled(false);
            }
            
            return this;
        }
    }

    // Fixed Button editor for table
    class ButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        protected JButton button;
        private String label;
        boolean isPushed;
        private int selectedRow;

        public ButtonEditor() {
            button = new JButton();
            button.setOpaque(true);
            button.setFocusPainted(false);
            button.addActionListener(this);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            
            selectedRow = row;
            if (row < invoices.size() && invoices.get(row) != null) {
                label = "View";
                button.setText(label);
                button.setBackground(new Color(122, 156, 95));
                button.setForeground(Color.WHITE);
                button.setEnabled(true);
            } else {
                label = "Not Available";
                button.setText(label);
                button.setBackground(Color.GRAY);
                button.setForeground(Color.WHITE);
                button.setEnabled(false);
            }
            
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedRow < invoices.size() && invoices.get(selectedRow) != null) {
                showInvoiceDetails(selectedRow);
            }
            fireEditingStopped();
        }
    }
}