
-- Πίνακας User
INSERT INTO User VALUES (1, 'john_doe', 'password123', 'john@example.com', 'Manager');
INSERT INTO User VALUES (2, 'anna_smith', 'pass456', 'anna@example.com', 'Accountant');
INSERT INTO User VALUES (3, 'mike_driver', 'driverpass', 'mike@example.com', 'DeliveryDriver');
INSERT INTO User VALUES (4, 'julia_prod', 'prodpass', 'julia@example.com', 'ProductionEmployee');

-- Πίνακας Customer
INSERT INTO Customer VALUES (1, 'Alice Green', '1234567890', 'alice@example.com', '123 Maple Street');

-- Πίνακας DeliveryDriver
INSERT INTO DeliveryDriver VALUES (1, 3, 'Mike Johnson', '9876543210', 'LIC123456', 2);

-- Πίνακας ProductionEmployee
INSERT INTO ProductionEmployee VALUES (1, 4, 'Julia Brown', '5557778888');

-- Πίνακας Accountant
INSERT INTO Accountant VALUES (1, 2, 'Anna Smith', 40, 'anna@example.com', 'CERT987');

-- Πίνακας Manager
INSERT INTO Manager VALUES (1, 1, 'John Doe', 90, 'john@example.com');

-- Πίνακας Product
INSERT INTO Product VALUES (1, 'Treadmill', 'High quality treadmill', 1200.00, 'Fitness');
INSERT INTO Product VALUES (2, 'Dumbbell Set', '20kg dumbbell set', 150.00, 'Weights');

-- Πίνακας OrderTable
INSERT INTO OrderTable VALUES (1, 1, 'Pending', '2025-05-01', NULL, 1350.00);

-- Πίνακας OrderItem
INSERT INTO OrderItem VALUES (1, 1, 1, 1, 1200.00);
INSERT INTO OrderItem VALUES (2, 1, 2, 1, 150.00);

-- Πίνακας Invoice
INSERT INTO Invoice VALUES (1, 1, '2025-05-02', '2025-05-15', 1350.00, 'Unpaid');

-- Πίνακας Inventory
INSERT INTO Inventory VALUES (1, 1, 5, 'Warehouse A', NOW());
INSERT INTO Inventory VALUES (2, 2, 10, 'Warehouse A', NOW());

-- Πίνακας Material
INSERT INTO Material VALUES (1, 'Steel', 'High grade steel', 10.00, 'Steel Inc.', 500);
INSERT INTO Material VALUES (2, 'Rubber', 'Durable rubber', 5.00, 'Rubber Co.', 300);

-- Πίνακας ProductMaterials
INSERT INTO ProductMaterials VALUES (1, 1, 20);
INSERT INTO ProductMaterials VALUES (1, 2, 5);

-- Πίνακας Payment
INSERT INTO Payment VALUES (1, 1, 'Credit Card', 1350.00, '2025-05-02', 'Completed');

-- Πίνακας Income
INSERT INTO Income VALUES (1, 1350.00, '2025-05-02');

-- Πίνακας Expenses
INSERT INTO Expenses VALUES (1, 700.00, '2025-05-02');

-- Πίνακας FinancialReport
INSERT INTO FinancialReport VALUES (1, 1, '2025-05-01', '2025-05-31', 1350.00, 700.00);

-- Πίνακας Notifications
INSERT INTO Notifications VALUES (1, 1, 'Your order has been shipped.', NOW(), 'Customer', 1);

-- Πίνακας ManagerEmployees
INSERT INTO ManagerEmployees VALUES (1, 1);

-- Πίνακας ManagerInventory
INSERT INTO ManagerInventory VALUES (1, 1);

-- Πίνακας ManagerDrivers
INSERT INTO ManagerDrivers VALUES (1, 1);

-- Πίνακας DriverOrders
INSERT INTO DriverOrders VALUES (1, 1);

-- Πίνακας User
INSERT INTO User VALUES (5, 'david_jones', 'david123', 'david@example.com', 'Manager');
INSERT INTO User VALUES (6, 'susan_white', 'susan456', 'susan@example.com', 'Accountant');
INSERT INTO User VALUES (7, 'lucas_driver', 'lucaspass', 'lucas@example.com', 'DeliveryDriver');
INSERT INTO User VALUES (8, 'maria_prod', 'mariapass', 'maria@example.com', 'ProductionEmployee');
INSERT INTO User VALUES (9, 'sophia_brown', 'sophiapass', 'sophia@example.com', 'Manager');
INSERT INTO User VALUES (10, 'emily_lee', 'emilypass', 'emily@example.com', 'DeliveryDriver');
INSERT INTO User VALUES (11, 'william_king', 'williampass', 'william@example.com', 'Accountant');
INSERT INTO User VALUES (12, 'jack_fox', 'jackpass', 'jack@example.com', 'Manager');
INSERT INTO User VALUES (13, 'olivia_wilson', 'oliviapass', 'olivia@example.com', 'ProductionEmployee');
INSERT INTO User VALUES (14, 'noah_martin', 'noahpass', 'noah@example.com', 'DeliveryDriver');

-- Πίνακας Customer
INSERT INTO Customer VALUES (2, 'Bob White', '2345678901', 'bob@example.com', '456 Oak Avenue');
INSERT INTO Customer VALUES (3, 'Charlie Black', '3456789012', 'charlie@example.com', '789 Pine Road');
INSERT INTO Customer VALUES (4, 'Deborah Blue', '4567890123', 'deborah@example.com', '123 Birch Street');
INSERT INTO Customer VALUES (5, 'Ella Gray', '5678901234', 'ella@example.com', '987 Cedar Drive');
INSERT INTO Customer VALUES (6, 'Frankie Green', '6789012345', 'frankie@example.com', '654 Maple Lane');
INSERT INTO Customer VALUES (7, 'George Brown', '7890123456', 'george@example.com', '321 Elm Avenue');
INSERT INTO Customer VALUES (8, 'Hannah White', '8901234567', 'hannah@example.com', '432 Willow Road');
INSERT INTO Customer VALUES (9, 'Isla Black', '9012345678', 'isla@example.com', '213 Redwood Street');
INSERT INTO Customer VALUES (10, 'Jack Green', '1234325678', 'jackgreen@example.com', '654 Oak Street');
INSERT INTO Customer VALUES (11, 'Kate Johnson', '2345436789', 'kate@example.com', '543 Pine Lane');

-- Πίνακας DeliveryDriver
INSERT INTO DeliveryDriver VALUES (2, 7, 'Lucas Parker', '1122334455', 'LIC987654', 3);
INSERT INTO DeliveryDriver VALUES (3, 8, 'Emily Harper', '2233445566', 'LIC123789', 4);
INSERT INTO DeliveryDriver VALUES (4, 9, 'Daniel Carter', '3344556677', 'LIC456123', 5);
INSERT INTO DeliveryDriver VALUES (5, 10, 'Oliver Smith', '4455667788', 'LIC789456', 2);
INSERT INTO DeliveryDriver VALUES (6, 11, 'Sophia Garcia', '5566778899', 'LIC012345', 3);
INSERT INTO DeliveryDriver VALUES (7, 12, 'Mason Taylor', '6677889900', 'LIC345678', 4);
INSERT INTO DeliveryDriver VALUES (8, 13, 'Ava Adams', '7788990011', 'LIC567890', 1);
INSERT INTO DeliveryDriver VALUES (9, 14, 'Jacob Scott', '8899001122', 'LIC789012', 2);
INSERT INTO DeliveryDriver VALUES (10, 15, 'Liam Carter', '9900112233', 'LIC890123', 6);
INSERT INTO DeliveryDriver VALUES (11, 16, 'Sophia Harris', '1011122334', 'LIC123456', 7);

-- Πίνακας ProductionEmployee
INSERT INTO ProductionEmployee VALUES (2, 5, 'Maria Lopez', '2345678901');
INSERT INTO ProductionEmployee VALUES (3, 6, 'John Williams', '3456789012');
INSERT INTO ProductionEmployee VALUES (4, 7, 'Sarah Hall', '4567890123');
INSERT INTO ProductionEmployee VALUES (5, 8, 'James White', '5678901234');
INSERT INTO ProductionEmployee VALUES (6, 9, 'Emma Jackson', '6789012345');
INSERT INTO ProductionEmployee VALUES (7, 10, 'Liam Harris', '7890123456');
INSERT INTO ProductionEmployee VALUES (8, 11, 'Sophia Clark', '8901234567');
INSERT INTO ProductionEmployee VALUES (9, 12, 'Jackson Lewis', '9012345678');
INSERT INTO ProductionEmployee VALUES (10, 13, 'David King', '1122334455');
INSERT INTO ProductionEmployee VALUES (11, 14, 'Lily Robinson', '2233445566');

-- Πίνακας Accountant
INSERT INTO Accountant VALUES (2, 11, 'William King', 30, 'william@example.com', 'CERT654');
INSERT INTO Accountant VALUES (3, 12, 'Jack Fox', 35, 'jack@example.com', 'CERT789');
INSERT INTO Accountant VALUES (4, 13, 'Olivia Wilson', 28, 'olivia@example.com', 'CERT321');
INSERT INTO Accountant VALUES (5, 14, 'Noah Martin', 40, 'noah@example.com', 'CERT654');
INSERT INTO Accountant VALUES (6, 15, 'Emily Lee', 32, 'emily@example.com', 'CERT987');
INSERT INTO Accountant VALUES (7, 16, 'Sophia Harris', 45, 'sophia@example.com', 'CERT321');
INSERT INTO Accountant VALUES (8, 17, 'David Parker', 50, 'david@example.com', 'CERT123');
INSERT INTO Accountant VALUES (9, 18, 'Grace Walker', 29, 'grace@example.com', 'CERT876');
INSERT INTO Accountant VALUES (10, 19, 'Max Brown', 39, 'max@example.com', 'CERT654');
INSERT INTO Accountant VALUES (11, 20, 'Sophia White', 33, 'sophia@example.com', 'CERT111');

-- Πίνακας Manager
INSERT INTO Manager VALUES (2, 5, 'David Jones', 88, 'david@example.com');
INSERT INTO Manager VALUES (3, 6, 'Susan White', 92, 'susan@example.com');
INSERT INTO Manager VALUES (4, 7, 'Lucas Driver', 85, 'lucas@example.com');
INSERT INTO Manager VALUES (5, 8, 'Maria Production', 78, 'maria@example.com');
INSERT INTO Manager VALUES (6, 9, 'Sophia Brown', 90, 'sophia@example.com');
INSERT INTO Manager VALUES (7, 10, 'Emily Lee', 95, 'emily@example.com');
INSERT INTO Manager VALUES (8, 11, 'William King', 70, 'william@example.com');
INSERT INTO Manager VALUES (9, 12, 'Jack Fox', 82, 'jack@example.com');
INSERT INTO Manager VALUES (10, 13, 'Olivia Wilson', 75, 'olivia@example.com');
INSERT INTO Manager VALUES (11, 14, 'Noah Martin', 88, 'noah@example.com');

-- Πίνακας Product
INSERT INTO Product VALUES (3, 'Exercise Bike', 'High-end stationary bike', 800.00, 'Fitness');
INSERT INTO Product VALUES (4, 'Yoga Mat', 'Durable yoga mat', 30.00, 'Yoga');
INSERT INTO Product VALUES (5, 'Dumbbells', '15kg dumbbells set', 120.00, 'Weights');
INSERT INTO Product VALUES (6, 'Barbell Set', '20kg barbell set', 180.00, 'Weights');
INSERT INTO Product VALUES (7, 'Rowing Machine', 'Water rowing machine', 1000.00, 'Fitness');
INSERT INTO Product VALUES (8, 'Kettlebell', '8kg kettlebell', 40.00, 'Weights');
INSERT INTO Product VALUES (9, 'Resistance Bands', 'Set of resistance bands', 25.00, 'Yoga');
INSERT INTO Product VALUES (10, 'Jump Rope', 'Adjustable jump rope', 15.00, 'Cardio');
INSERT INTO Product VALUES (11, 'Treadmill Belt', 'Replacement treadmill belt', 250.00, 'Parts');
INSERT INTO Product VALUES (12, 'Elliptical Trainer', 'Smooth elliptical trainer', 950.00, 'Fitness');

-- Πίνακας OrderTable
INSERT INTO OrderTable VALUES (2, 2, 'Completed', '2025-04-28', '2025-05-02', 800.00);
INSERT INTO OrderTable VALUES (3, 3, 'Shipped', '2025-05-01', NULL, 600.00);
INSERT INTO OrderTable VALUES (4, 4, 'Pending', '2025-05-02', NULL, 400.00);
INSERT INTO OrderTable VALUES (5, 5, 'Cancelled', '2025-05-03', NULL, 1200.00);
INSERT INTO OrderTable VALUES (6, 6, 'Completed', '2025-05-04', '2025-05-07', 700.00);
INSERT INTO OrderTable VALUES (7, 7, 'Pending', '2025-05-05', NULL, 150.00);
INSERT INTO OrderTable VALUES (8, 8, 'Shipped', '2025-05-06', '2025-05-09', 600.00);
INSERT INTO OrderTable VALUES (9, 9, 'Completed', '2025-05-07', '2025-05-10', 350.00);
INSERT INTO OrderTable VALUES (10, 10, 'Shipped', '2025-05-08', NULL, 800.00);
INSERT INTO OrderTable VALUES (11, 11, 'Pending', '2025-05-09', NULL, 500.00);

-- Πίνακας OrderItem
INSERT INTO OrderItem VALUES (3, 2, 3, 1, 800.00);
INSERT INTO OrderItem VALUES (4, 3, 4, 1, 30.00);
INSERT INTO OrderItem VALUES (5, 4, 5, 1, 120.00);
INSERT INTO OrderItem VALUES (6, 5, 6, 1, 180.00);
INSERT INTO OrderItem VALUES (7, 6, 7, 1, 1000.00);
INSERT INTO OrderItem VALUES (8, 7, 8, 1, 40.00);
INSERT INTO OrderItem VALUES (9, 8, 9, 1, 25.00);
INSERT INTO OrderItem VALUES (10, 9, 10, 1, 15.00);
INSERT INTO OrderItem VALUES (11, 10, 11, 1, 250.00);
INSERT INTO OrderItem VALUES (12, 11, 12, 1, 950.00);
