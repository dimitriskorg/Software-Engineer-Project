import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class InventoryManagementUI extends JFrame {
    private Connection conn;
    private InventoryEmployee employee;
    private JList<Inventory> inventoryList;
    private DefaultListModel<Inventory> inventoryListModel;
    private JButton updateButton;

    public InventoryManagementUI(Connection conn, InventoryEmployee employee) {
        super("Διαχείριση Αποθεμάτων");
        this.conn = conn;
        this.employee = employee;
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        inventoryListModel = new DefaultListModel<>();
        inventoryList = new JList<>(inventoryListModel);
        add(new JScrollPane(inventoryList), BorderLayout.CENTER);

        updateButton = new JButton("Ενημέρωση Αποθέματος");
        add(updateButton, BorderLayout.SOUTH);

        updateButton.addActionListener(e -> showUpdateScreen());

        loadStockData();
    }

    public void loadStockData() {
        inventoryListModel.clear();
        List<Inventory> stocks = Inventory.getStockLevels(conn);
        boolean hasLow = false;
        for (Inventory inv : stocks) {
            inventoryListModel.addElement(inv);
            if (inv.stockQuantity < Inventory.LOW_STOCK_THRESHOLD) {
                hasLow = true;
            }
        }
        if (hasLow) {
            JOptionPane.showMessageDialog(this, "Χαμηλό Απόθεμα!");
        }
    }

    private void showUpdateScreen() {
        Inventory selected = inventoryList.getSelectedValue();
        if (selected == null) return;
        String input = JOptionPane.showInputDialog(this, "Νέα Ποσότητα:", selected.stockQuantity);
        if (input != null) {
            try {
                int newQty = Integer.parseInt(input);
                if (Inventory.validateQuantity(newQty)) {
                    Inventory.updateStock(selected.inventoryID, newQty, conn);
                    JOptionPane.showMessageDialog(this, "Επιτυχής ενημέρωση αποθέματος πρώτων υλών.");
                    loadStockData();
                } else {
                    JOptionPane.showMessageDialog(this, "Μη έγκυρη ποσότητα.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Μη έγκυρη εισαγωγή.");
            }
        }
    }
}

class Inventory {
    public static final int LOW_STOCK_THRESHOLD = 5;
    public int inventoryID;
    public int productID;
    public String location;
    public int stockQuantity;

    public Inventory(int inventoryID, int productID, String location, int stockQuantity) {
        this.inventoryID = inventoryID;
        this.productID = productID;
        this.location = location;
        this.stockQuantity = stockQuantity;
    }

    public String toString() {
        return "Product #" + productID + " (" + stockQuantity + ") at " + location;
    }

    public static List<Inventory> getStockLevels(Connection conn) {
        List<Inventory> list = new ArrayList<>();
        try {
            String sql = "SELECT InventoryID, productID, stockQuantity, location FROM Inventory";
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    list.add(new Inventory(
                        rs.getInt("InventoryID"),
                        rs.getInt("productID"),
                        rs.getString("location"),
                        rs.getInt("stockQuantity")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static boolean validateQuantity(int quantity) {
        return quantity >= 0;
    }

    public static void updateStock(int inventoryID, int quantity, Connection conn) {
        try {
            String sql = "UPDATE Inventory SET stockQuantity = ?, lastUpdated = NOW() WHERE InventoryID = ?";
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                st.setInt(1, quantity);
                st.setInt(2, inventoryID);
                st.executeUpdate();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
