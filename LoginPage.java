/* Αυτή η κλάση αφορά το Login Page. To σκεπτικό είναι πως αρχικά συνδέεται με τη βάση δεδομέων
 * και αποθηκεύει την σύνδεση για την υπόλοιπη εφαρμογή. Ο λόγος που γίνεται αυτό, είναι για να 
 * μην συνδεόμαστε κάθε φορά που θέλουμε να εκτελέσουμε ένα query. Στην συνέχεια, εισάγει ο χρήστης 
 * τα στοιχεία του, ψάχνει στον πίνακα User αν υπάρχει φορτώνει την αρχική σελίδα και περνάει σε
 * αυτήν τον χρήστη που συνδέθηκε και την σύνδεση.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    
    // Στατική μεταβλητή για τον χρήστη
    private static User loggedInUser;
    private static Connection conn;
    
    public LoginPage() {
        // Set up the main frame
        setTitle("Login");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(122, 156, 95)); // Color similar to stock page
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Main content panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(244, 244, 244));
        
        // Create padding on all sides
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Center container panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(244, 244, 244));
        
        // Email panel
        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        emailPanel.setBackground(new Color(244, 244, 244));
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20); // Increased size from 15 to 20
        emailField.setPreferredSize(new Dimension(200, 30)); // Set explicit size
        emailPanel.add(emailLabel);
        emailPanel.add(emailField);
        
        // Password panel
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        passwordPanel.setBackground(new Color(244, 244, 244));
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20); // Increased size from 15 to 20
        passwordField.setPreferredSize(new Dimension(200, 30)); // Set explicit size
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(244, 244, 244));
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(150, 40)); // Set a larger siz
        loginButton.setBackground(new Color(194, 165, 108));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> authenticateUser(conn));
        buttonPanel.add(loginButton);
        
        // Add components to the center panel
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(emailPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(passwordPanel);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(buttonPanel);
        centerPanel.add(Box.createVerticalGlue());
        
        // Add center panel to main panel
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Add header and main panel to the frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        
        setLocationRelativeTo(null);  // Center the window on screen
        setVisible(true);
    }

    private void authenticateUser(Connection conn) {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try {
            conn = database.getConnection();

            String query = "SELECT * FROM User WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                loggedInUser = new User(
                        rs.getInt("UserID"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("role")
                );
                String role = rs.getString("role");
                loadHomePage(loggedInUser, conn);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error");
        }
    }

    private void loadHomePage(User user, Connection conn) {
        this.dispose(); // Close the login window
        // Open the home page for all users
        SwingUtilities.invokeLater(() -> new HomePage(user, conn));
    }

    // Μέθοδος για να πάρεις τον συνδεδεμένο χρήστη
    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start application
        SwingUtilities.invokeLater(() -> new LoginPage());
    }
}