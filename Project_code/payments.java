import javax.swing.*;
import java.awt.*;

public class payments extends JFrame {

    public payments() {
        setTitle("Payment View");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Header Panel with three sections
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(122, 156, 95));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));

        // Left empty panel for symmetry
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(120, 40));
        headerPanel.add(leftPanel, BorderLayout.WEST);

        // Centered title
        JLabel headerLabel = new JLabel("Payment View", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        // Logout button on the right
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(178, 34, 34));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutButton.setPreferredSize(new Dimension(120, 40));

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setOpaque(false);
        logoutPanel.add(logoutButton);
        headerPanel.add(logoutPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel);

        // Paid Orders Panel
        JPanel paidOrdersPanel = new JPanel();
        paidOrdersPanel.setLayout(new BoxLayout(paidOrdersPanel, BoxLayout.Y_AXIS));
        paidOrdersPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        paidOrdersPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JLabel paidOrdersLabel = new JLabel("Paid Orders");
        paidOrdersLabel.setFont(new Font("Arial", Font.BOLD, 16));
        paidOrdersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        paidOrdersPanel.add(paidOrdersLabel);

        paidOrdersPanel.add(createPaymentItem("George Papadopoulos", "george@example.com", "6981234567", "Sofokli Street 12, Athens", 
            "Order 1: Amount 50, Card, Payment Date 02/01/2025"));
        paidOrdersPanel.add(createPaymentItem("Maria Konstantinou", "maria@example.com", "6998765432", "Konstantinou Street 7, Thessaloniki", 
            "Order 2: Amount 35, Cash, Payment Date 01/01/2025"));

        mainPanel.add(paidOrdersPanel);

        // Unpaid Orders Panel
        JPanel unpaidOrdersPanel = new JPanel();
        unpaidOrdersPanel.setLayout(new BoxLayout(unpaidOrdersPanel, BoxLayout.Y_AXIS));
        unpaidOrdersPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        unpaidOrdersPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JLabel unpaidOrdersLabel = new JLabel("Unpaid Orders");
        unpaidOrdersLabel.setFont(new Font("Arial", Font.BOLD, 16));
        unpaidOrdersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        unpaidOrdersPanel.add(unpaidOrdersLabel);

        unpaidOrdersPanel.add(createPaymentItem("Stelios Papakostas", "stelios@example.com", "6987654321", "Agion Street 4, Patras", 
            "Order 3: Amount 45, Pending Payment."));
        unpaidOrdersPanel.add(createPaymentItem("Eleni Mylonas", "eleni@example.com", "6971234567", "Iroon Street 3, Heraklion", 
            "Order 4: Amount 60, Pending Payment."));

        mainPanel.add(unpaidOrdersPanel);

        // Late Payments Notification Button
        JButton latePaymentsButton = new JButton("Late Payment Notifications");
        latePaymentsButton.setBackground(new Color(194, 165, 108));
        latePaymentsButton.setForeground(Color.BLACK);
        latePaymentsButton.setFont(new Font("Arial", Font.PLAIN, 16));
        latePaymentsButton.setPreferredSize(new Dimension(250, 40));
        latePaymentsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(latePaymentsButton);

        add(mainPanel);
    }

    private JPanel createPaymentItem(String name, String email, String phone, String address, String paymentDetails) {
        JPanel paymentItemPanel = new JPanel();
        paymentItemPanel.setLayout(new BoxLayout(paymentItemPanel, BoxLayout.Y_AXIS));
        paymentItemPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        paymentItemPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea detailsArea = new JTextArea();
        detailsArea.setText("Full Name: " + name + "\nEmail: " + email + "\nPhone: " + phone + "\nAddress: " + address);
        detailsArea.setEditable(false);
        detailsArea.setPreferredSize(new Dimension(300, 60));
        detailsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        detailsArea.setMargin(new Insets(10, 10, 10, 10));
        paymentItemPanel.add(detailsArea);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);

        JButton moreButton = new JButton("More");
        moreButton.setBackground(new Color(194, 165, 108));
        moreButton.setForeground(Color.BLACK);
        moreButton.setFont(new Font("Arial", Font.PLAIN, 16));
        moreButton.setPreferredSize(new Dimension(120, 40));
        moreButton.addActionListener(e -> showPaymentDetails(paymentDetails)); // Action for More button
        buttonPanel.add(moreButton);

        paymentItemPanel.add(buttonPanel);

        return paymentItemPanel;
    }

    // Method to show the payment details in a dialog
    private void showPaymentDetails(String paymentDetails) {
        JOptionPane.showMessageDialog(this, paymentDetails, "Payment Details", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            payments app = new payments();
            app.setVisible(true);
        });
    }
}