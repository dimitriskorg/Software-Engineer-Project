import javax.swing.*;
import java.awt.*;

public class production extends JFrame {

    public production() {
        setTitle("Order Preparation List");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);

        // Main Panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(122, 156, 95)); // #7a9c5f color

        JLabel headerLabel = new JLabel("Order Preparation List", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutButton.setPreferredSize(new Dimension(120, 40));

        headerPanel.add(headerLabel, BorderLayout.CENTER);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Filters Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JComboBox<String> statusFilter = new JComboBox<>();
        statusFilter.addItem("Select Status");
        statusFilter.addItem("In Progress");
        statusFilter.addItem("Ready");

        JTextField dateStartField = new JTextField("Start Date", 10);
        JTextField dateEndField = new JTextField("End Date", 10);

        JButton filterButton = new JButton("Filter");
        filterButton.setBackground(new Color(34, 139, 34)); // Green
        filterButton.setForeground(Color.WHITE);
        filterButton.setFont(new Font("Arial", Font.PLAIN, 16));

        filterPanel.add(statusFilter);
        filterPanel.add(dateStartField);
        filterPanel.add(dateEndField);
        filterPanel.add(filterButton);

        mainPanel.add(filterPanel, BorderLayout.SOUTH);

        // Orders List Panel
        JPanel orderListPanel = new JPanel();
        orderListPanel.setLayout(new BoxLayout(orderListPanel, BoxLayout.Y_AXIS));
        orderListPanel.setOpaque(false);
        orderListPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        orderListPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        orderListPanel.add(createOrderItem("Order #1", "Sample product (2 items)", "12:30 - 28/03/2025", "13:30 - 28/03/2025", "In Progress"));
        orderListPanel.add(Box.createVerticalStrut(10));
        orderListPanel.add(createOrderItem("Order #2", "Sample product (5 items)", "14:00 - 28/03/2025", "15:00 - 28/03/2025", "Ready"));

        JPanel centerWrapper = new JPanel(new GridBagLayout()); 
        centerWrapper.setOpaque(false);
        centerWrapper.add(orderListPanel);

        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        // Popup for order details
        JDialog popupDialog = new JDialog(this, "Order Details", true);
        popupDialog.setSize(300, 200);
        popupDialog.setLocationRelativeTo(this);
        popupDialog.setLayout(new BorderLayout());

        JLabel popupLabel = new JLabel("More information about the order", SwingConstants.CENTER);
        popupDialog.add(popupLabel, BorderLayout.CENTER);

        JButton closePopupButton = new JButton("Close");
        closePopupButton.setBackground(new Color(34, 139, 34));
        closePopupButton.setForeground(Color.WHITE);
        closePopupButton.addActionListener(e -> popupDialog.setVisible(false));
        popupDialog.add(closePopupButton, BorderLayout.SOUTH);

        // Show popup when button clicked
        filterButton.addActionListener(e -> popupDialog.setVisible(true));

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createOrderItem(String orderId, String products, String timeOfEntry, String deliveryTime, String status) {
        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
        orderPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        orderPanel.setBackground(Color.WHITE);
        orderPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JLabel orderLabel = new JLabel(orderId);
        orderLabel.setFont(new Font("Arial", Font.BOLD, 16));
        orderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea orderDetails = new JTextArea();
        orderDetails.setText("Products: " + products + "\nEntry Time: " + timeOfEntry + "\nEstimated Delivery Time: " + deliveryTime + "\nStatus: " + status);
        orderDetails.setEditable(false);
        orderDetails.setFont(new Font("Arial", Font.PLAIN, 14));
        orderDetails.setMargin(new Insets(10, 10, 10, 10));
        orderDetails.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton startButton = new JButton("Start Preparation");
        startButton.setBackground(new Color(34, 139, 34));
        startButton.setForeground(Color.WHITE);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton completeButton = new JButton("Complete Preparation");
        completeButton.setBackground(new Color(34, 139, 34));
        completeButton.setForeground(Color.WHITE);
        completeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        orderPanel.add(orderLabel);
        orderPanel.add(orderDetails);
        orderPanel.add(startButton);
        orderPanel.add(completeButton);

        return orderPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(production::new);
    }
}