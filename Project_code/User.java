public class User {
    private int userID;
    private String username;
    private String password;
    private String email;
    private String role;

    public User() {
        // Μπορείς να αφήσεις αυτόν τον constructor κενό ή να δώσεις default τιμές
    }
    
    public User(int userID, String username, String password, String email, String role) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public int getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }
}