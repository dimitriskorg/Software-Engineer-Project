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
                JPanel buttonPanel = new JPanel();
                //buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                buttonPanel.setBackground(new Color(244, 244, 244));
                JButton ViewOrders = new JButton("View Orders");
                ViewOrders.setPreferredSize(new Dimension(150, 40)); // Set a larger siz
                ViewOrders.setBackground(new Color(194, 165, 108));
                ViewOrders.setForeground(Color.WHITE);
                ViewOrders.setBorderPainted(false);
                ViewOrders.setFocusPainted(false);
                ViewOrders.addActionListener(e -> SwingUtilities.invokeLater(() -> {
                    this.dispose();
                    SwingUtilities.invokeLater(() -> {
                        ViewOrders frame = new ViewOrders(deliveryDriver, conn);
                        frame.setVisible(true);
                    });
                }));
                buttonPanel.add(ViewOrders);
                contentPanel.add(buttonPanel);
                                
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
                contentPanel.add(new JLabel("Here is the company overview:"));
                // Add components for manager
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
}
