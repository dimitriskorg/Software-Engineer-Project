public class Manager extends User {
    private int managerID;
    private String name;
    private int score;

    // Constructor
    public Manager(int managerID, int userID, String username, String password, String email, String role,
                   String name, int score) {
        super(userID, username, password, email, role);  // Κλήση του constructor της User
        this.managerID = managerID;
        this.name = name;
        this.score = score;
    }

    // Getters and Setters
    public int getManagerID() {
        return managerID;
    }

    public void setManagerID(int managerID) {
        this.managerID = managerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    // Method to display manager information
    public void displayManagerInfo() {
        System.out.println("Manager ID: " + managerID);
        System.out.println("User ID: " + getUserID());
        System.out.println("Username: " + getUsername());
        System.out.println("Name: " + name);
        System.out.println("Score: " + score);
        System.out.println("Email: " + getEmail());
        System.out.println("Role: " + getRole());
    }
}