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
    private User user;
    private Connection conn;

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
        contentPanel.setFont(new Font("Arial", Font.BOLD, 30));
        contentPanel.setBackground(new Color(244, 244, 244));
        
        // Display different content based on role
        switch (user.getRole()) {
            case "DeliveryDriver":

                DeliveryDriver deliveryDriver = getDeliveryDriverByUserID(user.getUserID(), conn);

                contentPanel.add(new JLabel("Welcome, Delivery Driver!"));
                //contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                contentPanel.add(Box.createRigidArea(new Dimension(0, 20))); // space above
                JPanel buttonPanelD = new JPanel();
                //buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                buttonPanelD.setBackground(new Color(244, 244, 244));
                
                JButton ViewOrdersD = new JButton("View Orders");
                ViewOrdersD.setPreferredSize(new Dimension(150, 40)); // Set a larger siz
                ViewOrdersD.setBackground(new Color(194, 165, 108));
                ViewOrdersD.setForeground(Color.WHITE);
                ViewOrdersD.setBorderPainted(false);
                ViewOrdersD.setFocusPainted(false);
                ViewOrdersD.addActionListener(e -> SwingUtilities.invokeLater(() -> {
                    this.dispose();
                    SwingUtilities.invokeLater(() -> {
                        ViewOrders frame = new ViewOrders(deliveryDriver, conn);
                        frame.setVisible(true);
                    });
                }));
                
                contentPanel.add(Box.createRigidArea(new Dimension(0, 20))); // space above
               
                JButton NewOrder = new JButton("New Order");
                NewOrder.setPreferredSize(new Dimension(150, 40)); // Set a larger siz
                NewOrder.setBackground(new Color(194, 165, 108));
                NewOrder.setForeground(Color.WHITE);
                NewOrder.setBorderPainted(false);
                NewOrder.setFocusPainted(false);
                NewOrder.addActionListener(e -> SwingUtilities.invokeLater(() -> {
                    this.dispose();
                    openNewOrderScreen(deliveryDriver, conn); // Οπως ακριβώς περιγράφεται στο Sequence
                }));

                buttonPanelD.add(NewOrder);
                buttonPanelD.add(ViewOrdersD);
                contentPanel.add(buttonPanelD);
                                
                break;
            case "ProductionEmployee":
                contentPanel.add(new JLabel("Welcome, Production Employee!"));
                contentPanel.add(new JLabel("Here are your tasks:"));
                // Add components for production employee
                break;
            case "Accountant":
                contentPanel.add(new JLabel("Welcome, Accountant!"));
                contentPanel.add(new JLabel("Here are your financial reports:"));
                // Add components for accountant
                break;
            case "Manager":
            contentPanel.add(new JLabel("Welcome, Manager!"));                
    
            Manager manager = getManagerByUserID(user.getUserID(), conn);
            
            contentPanel.add(Box.createRigidArea(new Dimension(0, 20))); // space above
            JPanel buttonPanelM = new JPanel();
            buttonPanelM.setBackground(new Color(244, 244, 244));
            
            JButton ViewOrdersM = new JButton("View Orders");
            ViewOrdersM.setPreferredSize(new Dimension(150, 40));
            ViewOrdersM.setBackground(new Color(194, 165, 108));
            ViewOrdersM.setForeground(Color.WHITE);
            ViewOrdersM.setBorderPainted(false);
            ViewOrdersM.setFocusPainted(false);
            ViewOrdersM.addActionListener(e -> SwingUtilities.invokeLater(() -> {
                this.dispose();
                SwingUtilities.invokeLater(() -> {
                    ViewOrders frame = new ViewOrders(manager, conn);
                    frame.setVisible(true);
                });
            }));
            
            // Add the Product Management button
            JButton manageProductsBtn = new JButton("Διαχείριση Προϊόντων");
            manageProductsBtn.setPreferredSize(new Dimension(180, 40));
            manageProductsBtn.setBackground(new Color(194, 165, 108));
            manageProductsBtn.setForeground(Color.WHITE);
            manageProductsBtn.setBorderPainted(false);
            manageProductsBtn.setFocusPainted(false);
            manageProductsBtn.addActionListener(e -> SwingUtilities.invokeLater(() -> {
                this.dispose();
                SwingUtilities.invokeLater(() -> {
                    ProductManagement frame = new ProductManagement(manager, conn);
                    frame.setVisible(true);
                });
            }));
            
            buttonPanelM.add(ViewOrdersM);
            buttonPanelM.add(manageProductsBtn);
            contentPanel.add(buttonPanelM);
            break;
            default:
                contentPanel.add(new JLabel("Unknown role: " + user.getRole()));
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
