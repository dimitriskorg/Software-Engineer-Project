import java.sql.*;

public class ProductionEmployee extends User {
    private int employeeID;
    private String name;
    private String phone;

    public ProductionEmployee(int employeeID, int userID, String name, String phone, String username, String password, String email) {
        super(userID, username, password, email, "ProductionEmployee");
        this.employeeID = employeeID;
        this.name = name;
        this.phone = phone;
    }

    public static ProductionEmployee loadByUserID(Connection conn, int userID) throws SQLException {
        String sql = "SELECT pe.EmployeeID, pe.UserID, pe.name, pe.phone, u.username, u.password, u.email " +
                     "FROM ProductionEmployee pe JOIN User u ON pe.UserID = u.UserID WHERE u.UserID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ProductionEmployee(
                        rs.getInt("EmployeeID"),
                        rs.getInt("UserID"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                    );
                }
            }
        }
        return null;
    }

    public int getEmployeeID() { return employeeID; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
}
