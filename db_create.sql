use softengproject;

-- Δημιουργία πίνακα Πελάτη (Customer)
CREATE TABLE Customer (
    CustomerID INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT
);

-- Δημιουργία πίνακα Χρήστη (User)
CREATE TABLE User (
    UserID INT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(50) NOT NULL
);

-- Δημιουργία πίνακα Οδηγού Παράδοσης (DeliveryDriver)
CREATE TABLE DeliveryDriver (
    DriverID INT PRIMARY KEY,
    UserID INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    LicenseNumber VARCHAR(50) NOT NULL,
    assignedOrders INT,
    FOREIGN KEY (UserID) REFERENCES User(UserID)
);

-- Δημιουργία πίνακα Υπαλλήλου Παραγωγής (ProductionEmployee)
CREATE TABLE ProductionEmployee (
    EmployeeID INT PRIMARY KEY,
    UserID INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    FOREIGN KEY (UserID) REFERENCES User(UserID)
);

-- Δημιουργία πίνακα Λογιστή (Accountant)
CREATE TABLE Accountant (
    AccountantID INT PRIMARY KEY,
    UserID INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    hours INT,
    email VARCHAR(100),
    Certificate VARCHAR(100),
    FOREIGN KEY (UserID) REFERENCES User(UserID)
);

-- Δημιουργία πίνακα Διαχειριστή (Manager)
CREATE TABLE Manager (
    ManagerID INT PRIMARY KEY,
    UserID INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    Score INT,
    email VARCHAR(100),
    FOREIGN KEY (UserID) REFERENCES User(UserID)
);

-- Δημιουργία πίνακα Παραγγελίας (Order)
CREATE TABLE OrderTable (
    OrderID INT PRIMARY KEY,
    CustomerID INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    orderDate DATE NOT NULL,
    DeliveryDate DATE,
    totalAmount DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID)
);

-- Δημιουργία πίνακα Ειδοποιήσεων (Notifications)
CREATE TABLE Notifications (
    NotificationID INT PRIMARY KEY,
    recipientID INT NOT NULL,
    Message TEXT NOT NULL,
    SendDate DATETIME NOT NULL
);

-- Δημιουργία πίνακα Προϊόντος (Product)
CREATE TABLE Product (
    ProductID INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50)
);

-- Δημιουργία πίνακα Στοιχείων Παραγγελίας (OrderItem)
CREATE TABLE OrderItem (
    OrderItemID INT PRIMARY KEY,
    orderID INT NOT NULL,
    productID INT NOT NULL,
    quantity INT NOT NULL,
    totalPrice DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (orderID) REFERENCES OrderTable(OrderID),
    FOREIGN KEY (productID) REFERENCES Product(ProductID)
);

-- Δημιουργία πίνακα Τιμολογίου (Invoice)
CREATE TABLE Invoice (
    InvoiceID INT PRIMARY KEY,
    OrderID INT NOT NULL,
    issueDate DATE NOT NULL,
    dueDate DATE NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (OrderID) REFERENCES OrderTable(OrderID)
);

-- Δημιουργία πίνακα Αποθέματος (Inventory)
CREATE TABLE Inventory (
    InventoryID INT PRIMARY KEY,
    productID INT NOT NULL,
    stockQuantity INT NOT NULL,
    location VARCHAR(100),
    lastUpdated DATETIME NOT NULL,
    FOREIGN KEY (productID) REFERENCES Product(ProductID)
);

-- Δημιουργία πίνακα Υλικού (Material)
CREATE TABLE Material (
    MaterialID INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    unitCost DECIMAL(10, 2) NOT NULL,
    supplier VARCHAR(100),
    availableQuantity INT NOT NULL
);

-- Δημιουργία πίνακα Πληρωμής (Payment)
CREATE TABLE Payment (
    PaymentID INT PRIMARY KEY,
    OrderID INT NOT NULL,
    paymentMethod VARCHAR(50) NOT NULL,
    amountPaid DECIMAL(10, 2) NOT NULL,
    paymentDate DATE NOT NULL,
    Status VARCHAR(50) NOT NULL,
    FOREIGN KEY (OrderID) REFERENCES OrderTable(OrderID)
);

-- Δημιουργία πίνακα Εσόδων (Income)
CREATE TABLE Income (
    IncomeID INT PRIMARY KEY,
    amount DECIMAL(10, 2) NOT NULL,
    Date DATE NOT NULL
);

-- Δημιουργία πίνακα Εξόδων (Expenses)
CREATE TABLE Expenses (
    OrderID INT,
    amount DECIMAL(10, 2) NOT NULL,
    Date DATE NOT NULL,
    PRIMARY KEY (OrderID),
    FOREIGN KEY (OrderID) REFERENCES OrderTable(OrderID)
);

-- Δημιουργία πίνακα Οικονομικής Αναφοράς (Financial Report)
CREATE TABLE FinancialReport (
    reportId INT PRIMARY KEY,
    accountantID INT NOT NULL,
    periodStart DATE NOT NULL,
    periodEnd DATE NOT NULL,
    totalIncome DECIMAL(10, 2) NOT NULL,
    totalExpenses DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (accountantID) REFERENCES Accountant(AccountantID)
);

-- Δημιουργία πίνακα συσχέτισης μεταξύ Προϊόντων και Υλικών
CREATE TABLE ProductMaterials (
    ProductID INT,
    MaterialID INT,
    quantityNeeded INT NOT NULL,
    PRIMARY KEY (ProductID, MaterialID),
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID),
    FOREIGN KEY (MaterialID) REFERENCES Material(MaterialID)
);

-- Σημείωση: Αφαιρείται ο πίνακας UserRoles καθώς η συσχέτιση των χρηστών με τους ρόλους τους
-- υλοποιείται πλέον μέσω των ξένων κλειδιών στους πίνακες DeliveryDriver, ProductionEmployee, Manager και Accountant

-- Πίνακες συσχέτισης για τις σχέσεις πολλά-προς-πολλά
-- Σχέση μεταξύ Διαχειριστή και Υπαλλήλου Παραγωγής
CREATE TABLE ManagerEmployees (
    ManagerID INT,
    EmployeeID INT,
    PRIMARY KEY (ManagerID, EmployeeID),
    FOREIGN KEY (ManagerID) REFERENCES Manager(ManagerID),
    FOREIGN KEY (EmployeeID) REFERENCES ProductionEmployee(EmployeeID)
);

-- Σχέση μεταξύ Διαχειριστή και Αποθέματος
CREATE TABLE ManagerInventory (
    ManagerID INT,
    InventoryID INT,
    PRIMARY KEY (ManagerID, InventoryID),
    FOREIGN KEY (ManagerID) REFERENCES Manager(ManagerID),
    FOREIGN KEY (InventoryID) REFERENCES Inventory(InventoryID)
);

-- Σχέση μεταξύ Διαχειριστή και Οδηγού Παράδοσης
CREATE TABLE ManagerDrivers (
    ManagerID INT,
    DriverID INT,
    PRIMARY KEY (ManagerID, DriverID),
    FOREIGN KEY (ManagerID) REFERENCES Manager(ManagerID),
    FOREIGN KEY (DriverID) REFERENCES DeliveryDriver(DriverID)
);

-- Σχέση μεταξύ Οδηγού Παράδοσης και Παραγγελίας
CREATE TABLE DriverOrders (
    DriverID INT,
    OrderID INT,
    PRIMARY KEY (DriverID, OrderID),
    FOREIGN KEY (DriverID) REFERENCES DeliveryDriver(DriverID),
    FOREIGN KEY (OrderID) REFERENCES OrderTable(OrderID)
);

-- Για τις ειδοποιήσεις, προσθέτουμε συσχετίσεις με τους διάφορους τύπους χρηστών
ALTER TABLE Notifications
ADD COLUMN UserType VARCHAR(50),
ADD COLUMN UserTypeID INT;