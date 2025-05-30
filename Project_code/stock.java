import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class stock {
    private JFrame mainFrame;
    private JPanel headerPanel;
    private JPanel containerPanel;
    private JPanel stockListPanel;
    private JDialog popupDialog;
    private ArrayList<StockItem> stockItems;
    
    private JComboBox<String> filterQuantity;
    private JTextField filterName;
    private JTextField filterDate;
    private JLabel noStockLabel;
    
    // Class to store stock item data
    private class StockItem {
        String name;
        int quantity;
        int minThreshold;
        String supplier;
        String lastUpdateDate;
        JPanel panel;
        
        public StockItem(String name, int quantity, int minThreshold, String supplier, String lastUpdateDate) {
            this.name = name;
            this.quantity = quantity;
            this.minThreshold = minThreshold;
            this.supplier = supplier;
            this.lastUpdateDate = lastUpdateDate;
            this.panel = createStockItemPanel();
        }
        
        private JPanel createStockItemPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(quantity < minThreshold ? new Color(255, 204, 204) : new Color(249, 249, 249));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JLabel nameLabel = new JLabel(name);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel quantityLabel = new JLabel("Current Quantity: " + quantity + " kg");
            quantityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel thresholdLabel = new JLabel("Minimum Threshold: " + minThreshold + " kg");
            thresholdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel supplierLabel = new JLabel("Supplier: " + supplier);
            supplierLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel dateLabel = new JLabel("Last Update Date: " + lastUpdateDate);
            dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JButton updateButton = new JButton("Update Stock");
            updateButton.setBackground(new Color(122, 156, 95));
            updateButton.setForeground(Color.WHITE);
            updateButton.setBorderPainted(false);
            updateButton.setFocusPainted(false);
            updateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            updateButton.addActionListener(e -> showUpdatePopup(this));
            
            // Hover effect
            updateButton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    updateButton.setBackground(new Color(95, 122, 66));
                }
                
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    updateButton.setBackground(new Color(122, 156, 95));
                }
            });
            
            panel.add(nameLabel);
            panel.add(Box.createVerticalStrut(5));
            panel.add(quantityLabel);
            panel.add(Box.createVerticalStrut(5));
            panel.add(thresholdLabel);
            panel.add(Box.createVerticalStrut(5));
            panel.add(supplierLabel);
            panel.add(Box.createVerticalStrut(5));
            panel.add(dateLabel);
            panel.add(Box.createVerticalStrut(10));
            panel.add(updateButton);
            
            return panel;
        }
        
        public void updateQuantity(int newQuantity) {
            this.quantity = newQuantity;
            JLabel quantityLabel = (JLabel) ((Container) panel.getComponent(2));
            quantityLabel.setText("Current Quantity: " + quantity + " kg");
            
            // Update background color based on quantity
            panel.setBackground(quantity < minThreshold ? new Color(255, 204, 204) : new Color(249, 249, 249));
            panel.revalidate();
            panel.repaint();
        }
    }
    
    public stock() {
        // Initialize stock items
        stockItems = new ArrayList<>();
        stockItems.add(new StockItem("Raw Material #1", 10, 20, "Supplier A", "28/03/2025"));
        stockItems.add(new StockItem("Raw Material #2", 50, 30, "Supplier B", "28/03/2025"));
        
        // Create main frame
        mainFrame = new JFrame("Stock Monitoring");
        mainFrame.setSize(800, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        
        // Create header
        createHeader();
        
        // Create container with filters and stock list
        createContainer();
        
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
    
    private void createHeader() {
        headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(122, 156, 95)); // #7a9c5f
        
        JLabel titleLabel = new JLabel("Stock Monitoring");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(178, 34, 34)); // #b22222
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> System.exit(0));
        
        JPanel logoutPanel = new JPanel();
        logoutPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(new Color(122, 156, 95));
        logoutPanel.add(logoutBtn);
        headerPanel.add(logoutPanel, BorderLayout.EAST);
        
        mainFrame.add(headerPanel, BorderLayout.NORTH);
    }
    
    private void createContainer() {
        containerPanel = new JPanel();
        containerPanel.setLayout(new BorderLayout());
        containerPanel.setBackground(new Color(244, 244, 244)); // #f4f4f4
        
        // Create filters panel
        JPanel filtersPanel = createFiltersPanel();
        containerPanel.add(filtersPanel, BorderLayout.NORTH);
        
        // Create stock list panel
        createStockListPanel();
        JScrollPane scrollPane = new JScrollPane(stockListPanel);
        scrollPane.setBorder(null);
        containerPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainFrame.add(containerPanel, BorderLayout.CENTER);
    }
    
    private JPanel createFiltersPanel() {
        JPanel filtersPanel = new JPanel();
        filtersPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        filtersPanel.setBackground(new Color(244, 244, 244));
        filtersPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        
        // Quantity filter
        String[] quantityOptions = {"Filter by Quantity", "Low to High", "High to Low"};
        filterQuantity = new JComboBox<>(quantityOptions);
        filterQuantity.setPreferredSize(new Dimension(200, 30));
        
        // Name filter
        filterName = new JTextField("Filter by Material Name");
        filterName.setPreferredSize(new Dimension(200, 30));
        
        // Date filter
        filterDate = new JTextField("dd/mm/yyyy");
        filterDate.setPreferredSize(new Dimension(100, 30));
        
        // Filter button
        JButton filterButton = new JButton("Filter");
        filterButton.setBackground(new Color(122, 156, 95));
        filterButton.setForeground(Color.WHITE);
        filterButton.setBorderPainted(false);
        filterButton.setFocusPainted(false);
        filterButton.addActionListener(e -> applyFilter());
        
        // Hover effect
        filterButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                filterButton.setBackground(new Color(95, 122, 66));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                filterButton.setBackground(new Color(122, 156, 95));
            }
        });
        
        filtersPanel.add(filterQuantity);
        filtersPanel.add(filterName);
        filtersPanel.add(filterDate);
        filtersPanel.add(filterButton);
        
        return filtersPanel;
    }
    
    private void createStockListPanel() {
        stockListPanel = new JPanel();
        stockListPanel.setLayout(new BoxLayout(stockListPanel, BoxLayout.Y_AXIS));
        stockListPanel.setBackground(Color.WHITE);
        stockListPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add stock items to panel
        for (StockItem item : stockItems) {
            JPanel itemPanel = item.panel;
            itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, itemPanel.getPreferredSize().height));
            stockListPanel.add(itemPanel);
            stockListPanel.add(Box.createVerticalStrut(10));
        }
        
        // No stock message
        noStockLabel = new JLabel("No raw materials are registered. Please add new materials.");
        noStockLabel.setForeground(new Color(178, 34, 34));
        noStockLabel.setFont(new Font("Arial", Font.BOLD, 16));
        noStockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        noStockLabel.setVisible(false);
        stockListPanel.add(noStockLabel);
    }
    
    private void showUpdatePopup(StockItem item) {
        popupDialog = new JDialog(mainFrame, "Update Stock", true);
        popupDialog.setSize(400, 200);
        popupDialog.setLayout(new BorderLayout());
        popupDialog.setLocationRelativeTo(mainFrame);
        
        JPanel popupPanel = new JPanel();
        popupPanel.setLayout(new BoxLayout(popupPanel, BoxLayout.Y_AXIS));
        popupPanel.setBackground(Color.WHITE);
        popupPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Update Stock");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel instructionLabel = new JLabel("Enter new quantity for raw material: " + item.name);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextField quantityField = new JTextField();
        quantityField.setMaximumSize(new Dimension(200, 30));
        quantityField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(178, 34, 34));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setBorderPainted(false);
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> {
            try {
                int newQuantity = Integer.parseInt(quantityField.getText());
                item.updateQuantity(newQuantity);
                popupDialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(popupDialog, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Hover effect
        submitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                submitButton.setBackground(new Color(139, 26, 26));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                submitButton.setBackground(new Color(178, 34, 34));
            }
        });
        
        popupPanel.add(titleLabel);
        popupPanel.add(Box.createVerticalStrut(10));
        popupPanel.add(instructionLabel);
        popupPanel.add(Box.createVerticalStrut(20));
        popupPanel.add(quantityField);
        popupPanel.add(Box.createVerticalStrut(20));
        popupPanel.add(submitButton);
        
        popupDialog.add(popupPanel);
        popupDialog.setVisible(true);
    }
    
    private void applyFilter() {
        String quantityFilterValue = (String) filterQuantity.getSelectedItem();
        String nameFilterValue = filterName.getText().toLowerCase();
        String dateFilterValue = filterDate.getText();
        
        boolean anyVisible = false;
        
        // Apply filters
        for (StockItem item : stockItems) {
            boolean shouldShow = true;
            
            // Name filter
            if (!nameFilterValue.isEmpty() && !nameFilterValue.equals("Filter by Material Name") && 
                !item.name.toLowerCase().contains(nameFilterValue)) {
                shouldShow = false;
            }
            
            // Quantity filter
            if (quantityFilterValue.equals("Low to High") && item.quantity > 20) {
                shouldShow = false;
            } else if (quantityFilterValue.equals("High to Low") && item.quantity <= 20) {
                shouldShow = false;
            }
            
            // Show/hide item
            item.panel.setVisible(shouldShow);
            if (shouldShow) {
                anyVisible = true;
            }
        }
        
        // Show/hide no stock message
        noStockLabel.setVisible(!anyVisible);
        
        // Refresh the panel
        stockListPanel.revalidate();
        stockListPanel.repaint();
    }
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Start application
        SwingUtilities.invokeLater(() -> new stock());
    }
}