// Εισαγωγή των απαραίτητων βιβλιοθηκών για GUI και σύνδεση με τη βάση δεδομένων
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginPage extends JFrame {
    // Πεδία για την εισαγωγή email και κωδικού χρήστη
    private JTextField emailField;
    private JPasswordField passwordField;

    // Στατική μεταβλητή για τον συνδεδεμένο χρήστη
    private static User loggedInUser;

    // Στατική σύνδεση με τη βάση δεδομένων
    private static Connection conn;

    public LoginPage() {
        // Ρυθμίσεις παραθύρου
        setTitle("Login");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Πάνελ επικεφαλίδας με τίτλο
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(122, 156, 95));
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Κεντρικό πάνελ με περιθώρια
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(244, 244, 244));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Πάνελ για την τοποθέτηση στοιχείων κάθετα
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(244, 244, 244));

        // Πάνελ για το email
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        emailPanel.setBackground(new Color(244, 244, 244));
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);
        emailField.setPreferredSize(new Dimension(200, 30));
        emailPanel.add(emailLabel);
        emailPanel.add(emailField);

        // Πάνελ για τον κωδικό πρόσβασης
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        passwordPanel.setBackground(new Color(244, 244, 244));
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(200, 30));
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        // Πάνελ για το κουμπί εισόδου
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(244, 244, 244));
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(150, 40));
        loginButton.setBackground(new Color(194, 165, 108));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        
        // Καθορισμός της ενέργειας του κουμπιού login
        loginButton.addActionListener(e -> authenticateUser(conn));
        buttonPanel.add(loginButton);

        // Προσθήκη των πάνελ στο κύριο πάνελ
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(emailPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(passwordPanel);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(buttonPanel);
        centerPanel.add(Box.createVerticalGlue());

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Προσθήκη του header και main panel στο frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Τοποθέτηση του παραθύρου στο κέντρο της οθόνης
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Μέθοδος για έλεγχο των στοιχείων σύνδεσης του χρήστη
    private void authenticateUser(Connection conn) {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try {
            conn = database.getConnection(); // Δημιουργία σύνδεσης με βάση

            // Ερώτημα για αναζήτηση του χρήστη με τα δοσμένα στοιχεία
            String query = "SELECT * FROM User WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Αν βρεθεί ο χρήστης, δημιουργία αντικειμένου και φόρτωση αρχικής σελίδας
                loggedInUser = new User(
                        rs.getInt("UserID"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("role")
                );
                loadHomePage(loggedInUser, conn);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error");
        }
    }

    // Μέθοδος για φόρτωση της αρχικής σελίδας μετά από επιτυχή login
    private void loadHomePage(User user, Connection conn) {
        this.dispose(); // Κλείσιμο του παραθύρου σύνδεσης
        SwingUtilities.invokeLater(() -> new HomePage(user, conn)); // Εκκίνηση της αρχικής σελίδας
    }

    // Στατική μέθοδος για απόκτηση του συνδεδεμένου χρήστη από άλλα μέρη της εφαρμογής
    public static User getLoggedInUser() {
        return loggedInUser;
    }

    // Εκκίνηση της εφαρμογής
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Δημιουργία και εμφάνιση της σελίδας σύνδεσης
        SwingUtilities.invokeLater(() -> new LoginPage());
    }
}