drop database softengproject;
create database softengproject;
use softengproject;

CREATE TABLE Customer (
    CustomerID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    wantsInvoice VARCHAR(3)
);

CREATE TABLE User (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(50) NOT NULL
);

CREATE TABLE DeliveryDriver (
    DriverID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    LicenseNumber VARCHAR(50) NOT NULL,
    assignedOrders INT,
    FOREIGN KEY (UserID) REFERENCES User(UserID)
);

CREATE TABLE ProductionEmployee (
    EmployeeID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    FOREIGN KEY (UserID) REFERENCES User(UserID)
);

CREATE TABLE Accountant (
    AccountantID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    hours INT,
    email VARCHAR(100),
    Certificate VARCHAR(100),
    FOREIGN KEY (UserID) REFERENCES User(UserID)
);

CREATE TABLE Manager (
    ManagerID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    Score INT,
    email VARCHAR(100),
    FOREIGN KEY (UserID) REFERENCES User(UserID)
);

CREATE TABLE OrderTable (
    OrderID INT AUTO_INCREMENT PRIMARY KEY,
    CustomerID INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    orderDate DATE NOT NULL,
    DeliveryDate DATE,
    totalAmount DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID)
);

CREATE TABLE Notifications (
    NotificationID INT AUTO_INCREMENT PRIMARY KEY,
    recipientID INT NOT NULL,
    Message TEXT NOT NULL,
    SendDate DATETIME NOT NULL,
    UserType VARCHAR(50),
    UserTypeID INT
);

CREATE TABLE Product (
    ProductID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50)
);

CREATE TABLE OrderItem (
    OrderItemID INT AUTO_INCREMENT PRIMARY KEY,
    orderID INT NOT NULL,
    productID INT NOT NULL,
    quantity INT NOT NULL,
    totalPrice DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (orderID) REFERENCES OrderTable(OrderID),
    FOREIGN KEY (productID) REFERENCES Product(ProductID)
);

CREATE TABLE Invoice (
    InvoiceID INT AUTO_INCREMENT PRIMARY KEY,
    OrderID INT NOT NULL,
    issueDate DATE NOT NULL,
    dueDate DATE NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (OrderID) REFERENCES OrderTable(OrderID)
);

CREATE TABLE Inventory (
    InventoryID INT AUTO_INCREMENT PRIMARY KEY,
    productID INT NOT NULL,
    stockQuantity INT NOT NULL,
    location VARCHAR(100),
    lastUpdated DATETIME NOT NULL,
    FOREIGN KEY (productID) REFERENCES Product(ProductID)
);

CREATE TABLE Material (
    MaterialID INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    unitCost DECIMAL(10, 2) NOT NULL,
    supplier VARCHAR(100),
    availableQuantity INT NOT NULL
);

CREATE TABLE Payment (
    PaymentID INT AUTO_INCREMENT PRIMARY KEY,
    OrderID INT NOT NULL,
    paymentMethod VARCHAR(50) NOT NULL,
    amountPaid DECIMAL(10, 2) NOT NULL,
    paymentDate DATE NOT NULL,
    Status VARCHAR(50) NOT NULL,
    FOREIGN KEY (OrderID) REFERENCES OrderTable(OrderID)
);

CREATE TABLE Income (
    IncomeID INT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(10, 2) NOT NULL,
    Date DATE NOT NULL
);

CREATE TABLE Expenses (
    OrderID INT PRIMARY KEY,
    amount DECIMAL(10, 2) NOT NULL,
    Date DATE NOT NULL,
    FOREIGN KEY (OrderID) REFERENCES OrderTable(OrderID)
);

CREATE TABLE FinancialReport (
    reportId INT AUTO_INCREMENT PRIMARY KEY,
    accountantID INT NOT NULL,
    periodStart DATE NOT NULL,
    periodEnd DATE NOT NULL,
    totalIncome DECIMAL(10, 2) NOT NULL,
    totalExpenses DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (accountantID) REFERENCES Accountant(AccountantID)
);

CREATE TABLE ProductMaterials (
    ProductID INT,
    MaterialID INT,
    quantityNeeded INT NOT NULL,
    PRIMARY KEY (ProductID, MaterialID),
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID),
    FOREIGN KEY (MaterialID) REFERENCES Material(MaterialID)
);

CREATE TABLE ManagerEmployees (
    ManagerID INT,
    EmployeeID INT,
    PRIMARY KEY (ManagerID, EmployeeID),
    FOREIGN KEY (ManagerID) REFERENCES Manager(ManagerID),
    FOREIGN KEY (EmployeeID) REFERENCES ProductionEmployee(EmployeeID)
);

CREATE TABLE ManagerInventory (
    ManagerID INT,
    InventoryID INT,
    PRIMARY KEY (ManagerID, InventoryID),
    FOREIGN KEY (ManagerID) REFERENCES Manager(ManagerID),
    FOREIGN KEY (InventoryID) REFERENCES Inventory(InventoryID)
);

CREATE TABLE ManagerDrivers (
    ManagerID INT,
    DriverID INT,
    PRIMARY KEY (ManagerID, DriverID),
    FOREIGN KEY (ManagerID) REFERENCES Manager(ManagerID),
    FOREIGN KEY (DriverID) REFERENCES DeliveryDriver(DriverID)
);

CREATE TABLE DriverOrders (
    DriverID INT,
    OrderID INT,
    PRIMARY KEY (DriverID, OrderID),
    FOREIGN KEY (DriverID) REFERENCES DeliveryDriver(DriverID),
    FOREIGN KEY (OrderID) REFERENCES OrderTable(OrderID)
);
