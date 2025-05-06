import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class invoice extends JFrame {
    private JButton createAccountsButton;
    private JButton viewAccountsButton;
    private JButton sendNotificationsButton;
    private JButton logoutButton;
    private JPanel messagePanel;
    private JLabel messageLabel;

    public invoice() {
        // Set up the main frame
        setTitle("Create Monthly Accounts");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(122, 156, 95));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Create Monthly Accounts", SwingConstants.CENTER);
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
        
        // Create main buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        
        createAccountsButton = new JButton("Create Accounts");
        styleButton(createAccountsButton, new Color(194, 165, 108));
        createAccountsButton.addActionListener(e -> messageLabel.setText("The account creation process has started."));
        
        viewAccountsButton = new JButton("View Accounts");
        styleButton(viewAccountsButton, new Color(194, 165, 108));
        viewAccountsButton.addActionListener(e -> messageLabel.setText("Viewing all monthly accounts."));
        
        sendNotificationsButton = new JButton("Send Notifications");
        styleButton(sendNotificationsButton, new Color(194, 165, 108));
        sendNotificationsButton.addActionListener(e -> messageLabel.setText("Notifications have been sent to the clients."));
        
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(createAccountsButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(viewAccountsButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(sendNotificationsButton);
        buttonPanel.add(Box.createVerticalGlue());
        
        add(buttonPanel, BorderLayout.CENTER);
        
        // Create message panel
        messagePanel = new JPanel();
        messageLabel = new JLabel("", JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        messageLabel.setForeground(Color.black);
        messagePanel.add(messageLabel);
        
        add(messagePanel, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(250, 45));
        button.setMaximumSize(new Dimension(250, 45));
        button.setFocusPainted(false);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(168, 138, 85));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            invoice app = new invoice();
            app.setVisible(true);
        });
    }
}