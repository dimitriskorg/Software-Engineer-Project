import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class OrderPreparationUI extends JFrame {
    private Connection conn;
    private ProductionEmployee employee;
    private JList<Order> orderList;
    private DefaultListModel<Order> orderListModel;
    private JButton detailsButton, updateStatusButton;

    public OrderPreparationUI(Connection conn, ProductionEmployee employee) {
        super("Λίστα Παραγγελιών προς Παρασκευή");
        this.conn = conn;
        this.employee = employee;

        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        orderListModel = new DefaultListModel<>();
        orderList = new JList<>(orderListModel);
        add(new JScrollPane(orderList), BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        detailsButton = new JButton("Προβολή Πληροφοριών");
        updateStatusButton = new JButton("Ενημέρωση Κατάστασης");
        buttonsPanel.add(detailsButton);
        buttonsPanel.add(updateStatusButton);

        add(buttonsPanel, BorderLayout.SOUTH);

        detailsButton.addActionListener(e -> showOrderDetails());
        updateStatusButton.addActionListener(e -> updateOrderStatus());

        showOrdersScreen();
    }

    public void showOrdersScreen() {
        orderListModel.clear();
        List<Order> orders = Order.fetchPendingOrders(conn);
        boolean urgent = Order.checkUrgentOrders(conn);
        if (urgent) {
            JOptionPane.showMessageDialog(this, "Επείγουσα Παραγγελία!");
        }
        for (Order order : orders) {
            orderListModel.addElement(order);
        }
    }

    private void showOrderDetails() {
        Order selected = orderList.getSelectedValue();
        if (selected == null) return;
        OrderDetails details = Order.fetchOrderDetails(selected.orderID, conn);
        JOptionPane.showMessageDialog(this, details.toString());
    }

    private void updateOrderStatus() {
        Order selected = orderList.getSelectedValue();
        if (selected == null) return;
        String[] statuses = {"Pending", "In Progress", "Completed"};
        String newStatus = (String) JOptionPane.showInputDialog(this, "Επιλογή νέας κατάστασης:", "Ενημέρωση Κατάστασης", JOptionPane.PLAIN_MESSAGE, null, statuses, selected.status);

        if (newStatus != null && !newStatus.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, "Επιβεβαιώνετε την αλλαγή;", "Επιβεβαίωση", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Order.updateOrderStatus(selected.orderID, newStatus, conn);
                showOrdersScreen();
            }
        }
    }
}

class Order {
    public int orderID;
    public String status;

    public Order(int id, String status) {
        this.orderID = id;
        this.status = status;
    }

    public String toString() {
        return "Order #" + orderID + " [" + status + "]";
    }

    public static List<Order> fetchPendingOrders(Connection conn) {
        List<Order> orders = new ArrayList<>();
        try {
            String sql = "SELECT OrderID, status FROM OrderTable WHERE status = 'Pending'";
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    orders.add(new Order(rs.getInt("OrderID"), rs.getString("status")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return orders;
    }

    public static boolean checkUrgentOrders(Connection conn) {
        try {
            String sql = "SELECT COUNT(*) FROM OrderTable WHERE status = 'Pending' AND orderDate < DATE_SUB(CURDATE(), INTERVAL 3 DAY)";
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                ResultSet rs = st.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static OrderDetails fetchOrderDetails(int orderID, Connection conn) {
        StringBuilder details = new StringBuilder();
        try {
            String sql = "SELECT oi.productID, p.name, oi.quantity, oi.totalPrice FROM OrderItem oi JOIN Product p ON oi.productID = p.ProductID WHERE oi.orderID = ?";
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                st.setInt(1, orderID);
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    details.append("Product: ").append(rs.getString("name"))
                           .append(", Quantity: ").append(rs.getInt("quantity"))
                           .append(", Total: ").append(rs.getDouble("totalPrice")).append("\n");
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return new OrderDetails(orderID, details.toString());
    }

    public static void updateOrderStatus(int orderID, String status, Connection conn) {
        try {
            String sql = "UPDATE OrderTable SET status = ? WHERE OrderID = ?";
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                st.setString(1, status);
                st.setInt(2, orderID);
                st.executeUpdate();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}

class OrderDetails {
    public int orderID;
    public String productInfo;
    public OrderDetails(int id, String prodInfo) {
        this.orderID = id;
        this.productInfo = prodInfo;
    }
    public String toString() {
        return "Παραγγελία #" + orderID + "\n" + productInfo;
    }
}
