import java.sql.*;

public class InventoryEmployee extends User {
    private String name;
    private String phone;

    public InventoryEmployee(int userID, String username, String password, String email, String name, String phone) {
        super(userID, username, password, email, "InventoryEmployee");
        this.name = name;
        this.phone = phone;
    }

    public static InventoryEmployee loadByUserID(Connection conn, int userID) throws SQLException {
        String sql = "SELECT u.UserID, u.username, u.password, u.email " +
                     "FROM User u WHERE u.UserID = ? AND u.role = 'InventoryEmployee'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new InventoryEmployee(
                        rs.getInt("UserID"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("username"), // Use username as name (or extend your schema)
                        "" // No phone unless you add to User
                    );
                }
            }
        }
        return null;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
}
