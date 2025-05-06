public class DeliveryDriver {
    private int driverID;
    private int userID;
    private String name;
    private String phone;
    private String licenseNumber;
    private int assignedOrders;

    // Constructors
    public DeliveryDriver() {
        // Default constructor
    }

    public DeliveryDriver(int driverID, int userID, String name, String phone, String licenseNumber, int assignedOrders) {
        this.driverID = driverID;
        this.userID = userID;
        this.name = name;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.assignedOrders = assignedOrders;
    }

    // Getters and Setters
    public int getDriverID() {
        return driverID;
    }

    public void setDriverID(int driverID) {
        this.driverID = driverID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public int getAssignedOrders() {
        return assignedOrders;
    }

    public void setAssignedOrders(int assignedOrders) {
        this.assignedOrders = assignedOrders;
    }

    @Override
    public String toString() {
        return name + " DriverID: " + driverID + " UserID: " + userID + " Phone: " + phone;
    }
}