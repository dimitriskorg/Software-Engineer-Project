/* Με βάση τον χρήστη που συνδέθηκε, ελγέχουμε αρχικά τον ρόλο του κα ανάλογα με το τι
 * είναι φορτώνεται η αντίστοιχη σελίδα (φορτώνει μόνο του διανομέα γιατί αυτόν αφορούσαν
 * τα use cases μου). Μετά, δημιουργεί ένα αντικείμενο του DeliveryDriver για τον User που έχει
 * κάνει login μέσω της συνάρτησης getDeliveryDriverByUserID και τέλος περνάει τον διανομέα 
 * και την σύνδεση στην οθόνη ViewOrders.
 */
import javax.swing.*;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class HomePage extends JFrame {
    User user;
    Connection conn;

    public HomePage(User user, Connection conn) {
        this.user = user;
        this.conn = conn;

        // Set up the main frame
        setTitle("Home");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(122, 156, 95)); // Color similar to stock page
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Home - " + user.getRole());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(244, 244, 244));
        
        // Create padding on all sides
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Role-specific content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(244, 244, 244));
        
        // Display different content based on role
        switch (user.getRole()) {
            case "DeliveryDriver":

                DeliveryDriver deliveryDriver = getDeliveryDriverByUserID(user.getUserID(), conn);

                // Welcome label with center alignment
                JLabel welcomeLabelD = new JLabel("Welcome, Delivery Driver!");
                welcomeLabelD.setFont(new Font("Arial", Font.BOLD, 20));
                welcomeLabelD.setAlignmentX(Component.CENTER_ALIGNMENT);
                contentPanel.add(welcomeLabelD);
                
                contentPanel.add(Box.createRigidArea(new Dimension(0, 30))); // More space
                
                // Button panel with grid layout for better organization
                JPanel buttonPanelD = new JPanel(new GridLayout(2, 1, 10, 15));
                buttonPanelD.setBackground(new Color(244, 244, 244));
                buttonPanelD.setMaximumSize(new Dimension(200, 120));
                
                JButton ViewOrdersD = new JButton("View Orders");
                ViewOrdersD.setPreferredSize(new Dimension(180, 50));
                ViewOrdersD.setBackground(new Color(194, 165, 108));
                ViewOrdersD.setForeground(Color.WHITE);
                ViewOrdersD.setFont(new Font("Arial", Font.BOLD, 14));
                ViewOrdersD.setBorderPainted(false);
                ViewOrdersD.setFocusPainted(false);
                ViewOrdersD.addActionListener(e -> SwingUtilities.invokeLater(() -> {
                    this.dispose();
                    SwingUtilities.invokeLater(() -> {
                        ViewOrders frame = new ViewOrders(deliveryDriver, conn);
                        frame.setVisible(true);
                    });
                }));
               
                JButton NewOrder = new JButton("New Order");
                NewOrder.setPreferredSize(new Dimension(180, 50));
                NewOrder.setBackground(new Color(194, 165, 108));
                NewOrder.setForeground(Color.WHITE);
                NewOrder.setFont(new Font("Arial", Font.BOLD, 14));
                NewOrder.setBorderPainted(false);
                NewOrder.setFocusPainted(false);
                NewOrder.addActionListener(e -> SwingUtilities.invokeLater(() -> {
                    this.dispose();
                    openNewOrderScreen(deliveryDriver, conn);
                }));

                buttonPanelD.add(NewOrder);
                buttonPanelD.add(ViewOrdersD);
                
                // Center the button panel
                JPanel buttonWrapperD = new JPanel(new FlowLayout(FlowLayout.CENTER));
                buttonWrapperD.setBackground(new Color(244, 244, 244));
                buttonWrapperD.add(buttonPanelD);
                contentPanel.add(buttonWrapperD);
                                
                break;
                
            case "ProductionEmployee":
                JLabel welcomeLabelP = new JLabel("Welcome, Production Employee!");
                welcomeLabelP.setFont(new Font("Arial", Font.BOLD, 30));
                welcomeLabelP.setAlignmentX(Component.CENTER_ALIGNMENT);
                contentPanel.add(welcomeLabelP);
                
                JLabel tasksLabel = new JLabel("Here are your tasks:");
                tasksLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                contentPanel.add(tasksLabel);
                // Add components for production employee
                break;
                
            case "Accountant":
                JLabel welcomeLabelA = new JLabel("Welcome, Accountant!");
                welcomeLabelA.setFont(new Font("Arial", Font.BOLD, 30));
                welcomeLabelA.setAlignmentX(Component.CENTER_ALIGNMENT);
                contentPanel.add(welcomeLabelA);
                
                JLabel reportsLabel = new JLabel("Here are your financial reports:");
                reportsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                contentPanel.add(reportsLabel);
                // Add components for accountant
                break;
                
            case "Manager":
                JLabel welcomeLabelM = new JLabel("Welcome, Manager!");
                welcomeLabelM.setFont(new Font("Arial", Font.BOLD, 30));
                welcomeLabelM.setAlignmentX(Component.CENTER_ALIGNMENT);
                contentPanel.add(welcomeLabelM);
    
                Manager manager = getManagerByUserID(user.getUserID(), conn);
                
                contentPanel.add(Box.createRigidArea(new Dimension(0, 30))); // More space
                
                // Button panel with grid layout for better organization
                JPanel buttonPanelM = new JPanel(new GridLayout(3, 2, 10, 15));
                buttonPanelM.setBackground(new Color(244, 244, 244));
                buttonPanelM.setMaximumSize(new Dimension(400, 200));
                
                JButton ViewOrdersM = new JButton("View Orders");
                ViewOrdersM.setPreferredSize(new Dimension(180, 45));
                ViewOrdersM.setBackground(new Color(194, 165, 108));
                ViewOrdersM.setForeground(Color.WHITE);
                ViewOrdersM.setFont(new Font("Arial", Font.BOLD, 12));
                ViewOrdersM.setBorderPainted(false);
                ViewOrdersM.setFocusPainted(false);
                ViewOrdersM.addActionListener(e -> SwingUtilities.invokeLater(() -> {
                    this.dispose();
                    SwingUtilities.invokeLater(() -> {
                        ViewOrders frame = new ViewOrders(manager, conn);
                        frame.setVisible(true);
                    });
                }));
                
                JButton manageProductsBtn = new JButton("Manage Products");
                manageProductsBtn.setPreferredSize(new Dimension(180, 45));
                manageProductsBtn.setBackground(new Color(194, 165, 108));
                manageProductsBtn.setForeground(Color.WHITE);
                manageProductsBtn.setFont(new Font("Arial", Font.BOLD, 12));
                manageProductsBtn.setBorderPainted(false);
                manageProductsBtn.setFocusPainted(false);
                manageProductsBtn.addActionListener(e -> SwingUtilities.invokeLater(() -> {
                    this.dispose();
                    SwingUtilities.invokeLater(() -> {
                        ProductManagement frame = new ProductManagement(manager, conn);
                        frame.setVisible(true);
                    });
                }));

                JButton manageCustomersBtn = new JButton("Manage Customers");
                manageCustomersBtn.setPreferredSize(new Dimension(180, 45));
                manageCustomersBtn.setBackground(new Color(194, 165, 108));
                manageCustomersBtn.setForeground(Color.WHITE);
                manageCustomersBtn.setFont(new Font("Arial", Font.BOLD, 12));
                manageCustomersBtn.setBorderPainted(false);
                manageCustomersBtn.setFocusPainted(false);
                manageCustomersBtn.addActionListener(e -> SwingUtilities.invokeLater(() -> {
                    this.dispose();
                    SwingUtilities.invokeLater(() -> {
                        CustomerManagement frame = new CustomerManagement(manager, conn);
                        frame.setVisible(true);
                    });
                }));

                JButton ViewExpenses = new JButton("View Expenses");
                ViewExpenses.setPreferredSize(new Dimension(180, 45));
                ViewExpenses.setBackground(new Color(194, 165, 108));
                ViewExpenses.setForeground(Color.WHITE);
                ViewExpenses.setFont(new Font("Arial", Font.BOLD, 12));
                ViewExpenses.setBorderPainted(false);
                ViewExpenses.setFocusPainted(false);
                ViewExpenses.addActionListener(e -> SwingUtilities.invokeLater(() -> {
                    this.dispose();
                    SwingUtilities.invokeLater(() -> {
                        ViewExpenses frame = new ViewExpenses(manager, conn);
                        frame.setVisible(true);
                    });
                }));
                
                JButton ViewPayments = new JButton("View Payments");
                ViewPayments.setPreferredSize(new Dimension(180, 45));
                ViewPayments.setBackground(new Color(194, 165, 108));
                ViewPayments.setForeground(Color.WHITE);
                ViewPayments.setFont(new Font("Arial", Font.BOLD, 12));
                ViewPayments.setBorderPainted(false);
                ViewPayments.setFocusPainted(false);
                ViewPayments.addActionListener(e -> SwingUtilities.invokeLater(() -> {
                    this.dispose();
                    SwingUtilities.invokeLater(() -> {
                        ViewPayments frame = new ViewPayments(manager, conn);
                        frame.setVisible(true);
                    });
                }));

                JButton ViewInvoice = new JButton("View Invoice");
                ViewInvoice.setPreferredSize(new Dimension(180, 45));
                ViewInvoice.setBackground(new Color(194, 165, 108));
                ViewInvoice.setForeground(Color.WHITE);
                ViewInvoice.setFont(new Font("Arial", Font.BOLD, 12));
                ViewInvoice.setBorderPainted(false);
                ViewInvoice.setFocusPainted(false);
                ViewInvoice.addActionListener(e -> SwingUtilities.invokeLater(() -> {
                    this.dispose();
                    SwingUtilities.invokeLater(() -> {
                        ViewInvoice frame = new ViewInvoice(user,conn);
                        frame.setVisible(true);
                    });
                }));

                // Add buttons to grid in organized manner
                buttonPanelM.add(ViewOrdersM);
                buttonPanelM.add(manageProductsBtn);
                buttonPanelM.add(manageCustomersBtn);
                buttonPanelM.add(ViewExpenses);
                buttonPanelM.add(ViewPayments);
                buttonPanelM.add(ViewInvoice);
                
                // Center the button panel
                JPanel buttonWrapperM = new JPanel(new FlowLayout(FlowLayout.CENTER));
                buttonWrapperM.setBackground(new Color(244, 244, 244));
                buttonWrapperM.add(buttonPanelM);
                contentPanel.add(buttonWrapperM);
                break;
                
            default:
                JLabel unknownLabel = new JLabel("Unknown role: " + user.getRole());
                unknownLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                contentPanel.add(unknownLabel);
        }

        // Add content panel to main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add header and main panel to the frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);  // Center the window on screen
        setVisible(true);
    }
    
    public static DeliveryDriver getDeliveryDriverByUserID(int userID, Connection conn) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Ερώτημα για αναζήτηση του DeliveryDriver με το συγκεκριμένο userID
            String sql = "SELECT * FROM DeliveryDriver d INNER JOIN User u ON d.userID = u.userID WHERE d.userID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);  // Ρύθμιση του userID στο ερώτημα

            rs = stmt.executeQuery();

            // Αν βρεθεί ο αντίστοιχος DeliveryDriver
            if (rs.next()) {
                int driverID = rs.getInt("driverID");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String role = rs.getString("role");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String licenseNumber = rs.getString("licenseNumber");
                int assignedOrders = rs.getInt("assignedOrders");

                return new DeliveryDriver(driverID, userID, username, password, email, role, name, phone, licenseNumber, assignedOrders);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // Επιστρέφει null αν δεν βρεθεί ο οδηγός
    }

    public static Manager getManagerByUserID(int userID, Connection conn) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
    
        try {
            // Ερώτημα για αναζήτηση του Manager με το συγκεκριμένο userID
            String sql = "SELECT * FROM Manager m INNER JOIN User u ON m.userID = u.userID WHERE m.userID = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);  // Ρύθμιση του userID στο ερώτημα
    
            rs = stmt.executeQuery();
    
            // Αν βρεθεί ο αντίστοιχος Manager
            if (rs.next()) {
                int managerID = rs.getInt("managerID");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String role = rs.getString("role");
                String name = rs.getString("name");
                int score = rs.getInt("score");
    
                return new Manager(managerID, userID, username, password, email, role, name, score);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    
        return null;  // Επιστρέφει null αν δεν βρεθεί ο manager
    }    

    private void openNewOrderScreen(DeliveryDriver deliveryDriver, Connection conn){
        ArrayList<Customer> customers = new ArrayList<>();
        ArrayList<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM Customer";
    
        try (PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
    
            while (rs.next()) {
                int id = rs.getInt("CustomerID");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                String address = rs.getString("address");
    
                customers.add(new Customer(id, name, phone, email, address));
            }
    
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching customers from the database.");
            return; // μην συνεχίσεις αν αποτύχει το query
        }
    
        // Query για προϊόντα
        String productSQL = "SELECT * FROM Product";
        try (PreparedStatement stmt = conn.prepareStatement(productSQL);
            ResultSet rs = stmt.executeQuery()) {
    
            while (rs.next()) {
                int id = rs.getInt("ProductID");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                String category = rs.getString("category");
    
                products.add(new Product(id, name, description, price, category));
            }
    
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching products from the database.");
            return;
        }
    
        // Άνοιγμα παραθύρου παραγγελίας
        SwingUtilities.invokeLater(() -> {
            NewOrder frame = new NewOrder(deliveryDriver, customers, products, conn);
            frame.setVisible(true);
        });
    }
}