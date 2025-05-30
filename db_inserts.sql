-- 1. User table (UserID AUTO_INCREMENT)
INSERT INTO User (username, password, email, role) VALUES 
  ('john_papadopoulos','12345','john@example.com','Manager'),
  ('anna_miller','12345','anna_miller@example.com','Accountant'),
  ('mike_davis','12345','mike@example.com','DeliveryDriver'),
  ('mary_brown','12345','mary_brown@example.com','ProductionStaff'),
  ('kevin_smith','12345','kevin_smith@example.com','Manager'),
  ('elaine_clark','12345','elaine_clark@example.com','Accountant'),
  ('george_adams','12345','george_adams@example.com','DeliveryDriver'),
  ('sofia_turner','12345','sofia_turner@example.com','ProductionStaff'),
  ('peter_jones','12345','peter_jones@example.com','Manager'),
  ('elena_martin','12345','elena_martin@example.com','DeliveryDriver'),
  ('david_thomas','12345','david_thomas@example.com','Accountant'),
  ('kate_wilson','12345','kate_wilson@example.com','Manager'),
  ('julia_evans','12345','julia_evans@example.com','ProductionStaff'),
  ('andrew_lewis','12345','andrew_lewis@example.com','DeliveryDriver'),
  ('thanos_cooper','12345','thanos_cooper@example.com','DeliveryDriver'),
  ('ellen_harris','12345','ellen_harris@example.com','DeliveryDriver'),
  ('chris_perez','12345','chris_perez@example.com','Accountant'),
  ('victoria_white','12345','victoria_white@example.com','Accountant'),
  ('aris_brown','12345','aris_brown@example.com','Accountant'),
  ('sophia_miller','12345','sophia_miller@example.com','Accountant');

-- 2. Customer table (CustomerID AUTO_INCREMENT)
INSERT INTO Customer (name, phone, email, address, wantsInvoice) VALUES
  ('Alice Green','555-1234','alice@example.com','10 Maple Street, Springfield','Yes'),
  ('Bob White','555-2345','bob@example.com','5 Oak Avenue, Springfield','No'),
  ('Charlie Black','555-3456','charlie@example.com','12 Pine Road, Springfield','Yes'),
  ('Deborah Blue','555-4567','deborah@example.com','20 Birch Street, Springfield','No'),
  ('Ella Gray','555-5678','ella@example.com','1 Constitution Plaza, Springfield','Yes'),
  ('Frank Green','555-6789','frank@example.com','3 Liberty Lane, Springfield','Yes'),
  ('George Brown','555-7890','george@example.com','100 K Street, Springfield','No'),
  ('Hannah White','555-8901','hannah@example.com','15 Willow Road, Springfield','Yes'),
  ('Isla Black','555-9012','isla@example.com','213 Redwood Street, Springfield','No'),
  ('Jack Green','555-4321','jackgreen@example.com','654 Oak Street, Springfield','Yes'),
  ('Kate Johnson','555-5432','kate@example.com','543 Pine Lane, Springfield','No');

-- 3. Product table (ProductID AUTO_INCREMENT)
INSERT INTO Product (name, description, price, category) VALUES
  ('Sourdough Bread','Artisan sourdough loaf',5.00,'Bread'),
  ('Whole Wheat Bread','Healthy whole wheat loaf',4.50,'Bread'),
  ('Croissant','Buttery French croissant',2.50,'Pastry'),
  ('Chocolate Muffin','Rich chocolate muffin',3.00,'Pastry'),
  ('Bagel','Sesame seed bagel',1.50,'Bread'),
  ('Blueberry Muffin','Fresh blueberry muffin',3.00,'Pastry'),
  ('Baguette','Classic French baguette',2.00,'Bread'),
  ('Donut','Glazed ring donut',1.75,'Pastry'),
  ('Brownie','Chocolate brownie square',2.75,'Pastry'),
  ('Cupcake','Vanilla cupcake with icing',2.25,'Pastry'),
  ('Cake Slice','Slice of birthday cake',4.00,'Dessert'),
  ('Muffin Box','Box of 6 assorted muffins',15.00,'Pastry');

-- 4. Material table (MaterialID AUTO_INCREMENT)
INSERT INTO Material (name, description, unitCost, supplier, availableQuantity) VALUES
  ('Flour','All-purpose flour',0.50,'FlourCo',1000),
  ('Sugar','Granulated sugar',0.30,'SweetCo',800),
  ('Yeast','Dry yeast packets',0.20,'RiseCo',500),
  ('Butter','Unsalted butter',1.00,'DairyCo',600);

-- 5. DeliveryDriver (DriverID AUTO_INCREMENT)
INSERT INTO DeliveryDriver (UserID, name, phone, LicenseNumber, assignedOrders) VALUES
  (3, 'Nick Davis','555-9876','LIC12345',2),
  (7, 'George Adams','555-1122','LIC98765',3),
  (10,'Elena Martin','555-2233','LIC12378',4),
  (14,'Andrew Lewis','555-3344','LIC45612',5),
  (15,'Thanos Cooper','555-4455','LIC78945',2),
  (16,'Ellen Harris','555-5566','LIC01234',3);

-- 6. ProductionEmployee (EmployeeID AUTO_INCREMENT)
INSERT INTO ProductionEmployee (UserID, name, phone) VALUES
  (4, 'Mary Brown','555-7778'),
  (8, 'Sofia Turner','555-2345'),
  (13,'Julia Evans','555-3456');

-- 7. Accountant (AccountantID AUTO_INCREMENT)
INSERT INTO Accountant (UserID, name, hours, email, Certificate) VALUES
  (2, 'Anna Miller',40,'anna@bakery.com','CERT987'),
  (6, 'Elaine Clark',30,'elaine@bakery.com','CERT654'),
  (11,'David Thomas',35,'david@bakery.com','CERT789'),
  (17,'Chris Perez',28,'chris@bakery.com','CERT321'),
  (18,'Victoria White',40,'victoria@bakery.com','CERT654'),
  (19,'Aris Brown',32,'aris@bakery.com','CERT987'),
  (20,'Sophia Miller',45,'sophia@bakery.com','CERT321');

-- 8. Manager (ManagerID AUTO_INCREMENT)
INSERT INTO Manager (UserID, name, Score, email) VALUES
  (1, 'John Papadopoulos',90,'john@bakery.com'),
  (5, 'Kevin Smith',      88,'kevin@bakery.com'),
  (9, 'Peter Jones',      92,'peter@bakery.com'),
  (12,'Kate Wilson',      85,'kate@bakery.com');

-- 9. OrderTable (OrderID AUTO_INCREMENT)
INSERT INTO OrderTable (CustomerID, status, orderDate, DeliveryDate, totalAmount) VALUES
  (1,'Pending','2025-05-01',NULL,15.00),
  (2,'Completed','2025-04-28','2025-05-02',7.50),
  (3,'Shipped','2025-05-01',NULL,5.00),
  (4,'Pending','2025-05-02',NULL,12.00),
  (5,'Cancelled','2025-05-03',NULL,20.00),
  (6,'Completed','2025-05-04','2025-05-07',10.00),
  (7,'Pending','2025-05-05',NULL,3.50),
  (8,'Shipped','2025-05-06','2025-05-09',8.00),
  (9,'Completed','2025-05-07','2025-05-10',6.00),
  (10,'Shipped','2025-05-08',NULL,9.00),
  (11,'Pending','2025-05-09',NULL,5.00);

-- 10. ProductMaterials (composite PK)
INSERT INTO ProductMaterials (ProductID, MaterialID, quantityNeeded) VALUES
  (1,1,2),
  (1,3,1);

-- 11. Inventory (InventoryID AUTO_INCREMENT)
INSERT INTO Inventory (productID, stockQuantity, location, lastUpdated) VALUES
  (1,20,'Main Warehouse',NOW()),
  (2,30,'Main Warehouse',NOW()),
  (3,50,'Main Warehouse',NOW());

-- 12. OrderItem (OrderItemID AUTO_INCREMENT)
INSERT INTO OrderItem (orderID, productID, quantity, totalPrice) VALUES
  (1,1,2,5.00),
  (1,3,1,2.50),
  (2,5,5,1.50),
  (3,4,2,3.00),
  (4,2,3,4.50),
  (5,6,4,3.00),
  (6,7,2,2.00),
  (7,8,3,1.75),
  (8,9,2,2.75),
  (9,10,4,2.25),
  (10,11,1,4.00),
  (11,12,1,15.00);

-- 13. Invoice (InvoiceID AUTO_INCREMENT)
INSERT INTO Invoice (OrderID, issueDate, dueDate, total, status) VALUES
  (1,'2025-05-02','2025-05-15',15.00,'Unpaid');

-- 14. Payment (PaymentID AUTO_INCREMENT)
INSERT INTO Payment (OrderID, paymentMethod, amountPaid, paymentDate, Status) VALUES
  (1,'Credit Card',15.00,'2025-05-02','Completed');

-- 15. Expenses (OrderID PK, όχι AUTO_INCREMENT)
INSERT INTO Expenses (OrderID, amount, Date) VALUES
  (1,10.00,'2025-05-02');

-- 16. Income (IncomeID AUTO_INCREMENT)
INSERT INTO Income (amount, Date) VALUES
  (15.00,'2025-05-02');

-- 17. FinancialReport (reportId AUTO_INCREMENT)
INSERT INTO FinancialReport (accountantID, periodStart, periodEnd, totalIncome, totalExpenses) VALUES
  (1,'2025-05-01','2025-05-31',15.00,10.00);

-- 18. Many-to-many relations (composite PK)
INSERT INTO ManagerEmployees (ManagerID, EmployeeID) VALUES (1,1);
INSERT INTO ManagerInventory (ManagerID, InventoryID) VALUES (1,1);
INSERT INTO ManagerDrivers   (ManagerID, DriverID)    VALUES (1,3);
INSERT INTO DriverOrders     (DriverID, OrderID)      VALUES (1,1);

-- 19. Notifications (NotificationID AUTO_INCREMENT)
INSERT INTO Notifications (recipientID, Message, SendDate, UserType, UserTypeID) VALUES
  (1,'Your order has been shipped.',NOW(),'Customer',1);
