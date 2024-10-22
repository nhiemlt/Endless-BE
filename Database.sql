-- Tạo cơ sở dữ liệu EndlessEcommerce
CREATE DATABASE IF NOT EXISTS EndlessEcommerce;
USE EndlessEcommerce;

-- Tạo bảng Brands
CREATE TABLE Brands (
    BrandID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Name VARCHAR(255) NOT NULL,
    Logo TEXT
);

-- Tạo bảng Categories
CREATE TABLE Categories (
    CategoryID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Name VARCHAR(255) NOT NULL,
    EN_name VARCHAR(255)
);

-- Tạo bảng Attributes
CREATE TABLE Attributes (
    AttributeID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    AttributeName VARCHAR(255) NOT NULL,
    EN_atributeName VARCHAR(255)
);

-- Tạo bảng AttributeValues
CREATE TABLE AttributeValues (
    AttributeValueID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    AttributeID CHAR(36) NOT NULL,
    Value VARCHAR(255) NOT NULL,
    EN_value VARCHAR(255),
    FOREIGN KEY (AttributeID) REFERENCES Attributes(AttributeID)
);

-- Tạo bảng Products
CREATE TABLE Products (
    ProductID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    CategoryID CHAR(36) NOT NULL,
    BrandID CHAR(36) NOT NULL,
    Name VARCHAR(255) NOT NULL,
    Name_EN VARCHAR(255),
    Description TEXT,
    EN_description TEXT,
    FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID),
    FOREIGN KEY (BrandID) REFERENCES Brands(BrandID)
);

-- Tạo bảng ProductVersions
CREATE TABLE ProductVersions (
    ProductVersionID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    ProductID CHAR(36) NOT NULL,
    VersionName VARCHAR(255) NOT NULL,
    CostPrice DECIMAL(18, 2) NOT NULL,
    Price DECIMAL(18, 2) NOT NULL,
    Weight DECIMAL(18, 2) NOT NULL,
    Height DECIMAL(18, 2) NOT NULL, -- Chiều cao
    Length DECIMAL(18, 2) NOT NULL, -- Chiều dài
    Width DECIMAL(18, 2) NOT NULL,  -- Chiều rộng
    Status VARCHAR(50) NOT NULL,
    Image TEXT,
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

-- Tạo bảng VersionAttributes
CREATE TABLE VersionAttributes (
    VersionAttributeID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    ProductVersionID CHAR(36) NOT NULL,
    AttributeValueID CHAR(36) NOT NULL,
    FOREIGN KEY (ProductVersionID) REFERENCES ProductVersions(ProductVersionID),
    FOREIGN KEY (AttributeValueID) REFERENCES AttributeValues(AttributeValueID)
);

-- Tạo bảng Promotions
CREATE TABLE Promotions (
    PromotionID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Name VARCHAR(255) NOT NULL,
    EN_name VARCHAR(255),
    StartDate DATE NOT NULL,
    EndDate DATE NOT NULL,
    Poster VARCHAR(255),
    EN_description TEXT
);

-- Tạo bảng PromotionDetails
CREATE TABLE PromotionDetails (
    PromotionDetailID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    PromotionID CHAR(36) NOT NULL,
    PercentDiscount INT NOT NULL,
    FOREIGN KEY (PromotionID) REFERENCES Promotions(PromotionID)
);

-- Tạo bảng PromotionProducts
CREATE TABLE PromotionProducts (
    PromotionProductID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    PromotionDetailID CHAR(36) NOT NULL,
    ProductVersionID CHAR(36) NOT NULL,
    FOREIGN KEY (PromotionDetailID) REFERENCES PromotionDetails(PromotionDetailID),
    FOREIGN KEY (ProductVersionID) REFERENCES ProductVersions(ProductVersionID)
);

CREATE TABLE Users (
    UserID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Username VARCHAR(255) NOT NULL,
    Fullname VARCHAR(255),
    Password VARCHAR(255),
    Phone VARCHAR(11),
    Email VARCHAR(255),
    Avatar TEXT,
    Language VARCHAR(50),
    active BOOLEAN DEFAULT TRUE,
    forgetPassword BOOLEAN DEFAULT FALSE
);

-- Tạo bảng UserAddresses
CREATE TABLE UserAddresses (
    AddressID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    UserID CHAR(36) NOT NULL,
    ProvinceID INT NOT NULL,
    ProvinceName VARCHAR(50) NOT NULL,
    DistrictID INT NOT NULL, 
    DistrictName VARCHAR(50) NOT NULL,
    WardCode INT NOT NULL, 
    WardName VARCHAR(50) NOT NULL,
    DetailAddress TEXT NOT NULL,
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- Tạo bảng Vouchers
CREATE TABLE Vouchers (
    VoucherID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    VoucherCode VARCHAR(50) NOT NULL UNIQUE,
    LeastBill DECIMAL(18, 2) NOT NULL,
    LeastDiscount DECIMAL(18, 2) NOT NULL,
    BiggestDiscount DECIMAL(18, 2) NOT NULL,
    DiscountLevel INT NOT NULL,
    DiscountForm VARCHAR(50) NOT NULL,
    StartDate DATE NOT NULL,
    EndDate DATE NOT NULL
);

-- Tạo bảng UserVouchers
CREATE TABLE UserVouchers (
    UserVoucherID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    UserID CHAR(36) NOT NULL,
    VoucherID CHAR(36) NOT NULL,
    Status VARCHAR(50),
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (VoucherID) REFERENCES Vouchers(VoucherID)
);

-- Tạo bảng Orders
CREATE TABLE Orders (
    OrderID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    UserID CHAR(36) NOT NULL,
    VoucherID CHAR(36),
    OrderDate DATE NOT NULL,
    ShipFee DECIMAL(18, 2) NOT NULL,
    TotalMoney DECIMAL(18, 2) NOT NULL,
    CodValue DECIMAL(18, 2) DEFAULT 0, -- Giá trị thu hộ
    InsuranceValue DECIMAL(18, 2) DEFAULT 0, -- Giá trị bảo hiểm
    ServiceTypeID INT NOT NULL, -- Mã loại dịch vụ
    OrderAddress TEXT,
    OrderPhone VARCHAR(15),
    OrderName VARCHAR(255),
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (VoucherID) REFERENCES Vouchers(VoucherID)
);

-- Tạo bảng OrderDetails
CREATE TABLE OrderDetails (
    OrderDetailID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    OrderID CHAR(36) NOT NULL,
    ProductVersionID CHAR(36) NOT NULL,
    Quantity INT NOT NULL,
    Price DECIMAL(18, 2) NOT NULL,
    DiscountPrice DECIMAL(18, 2) NOT NULL,
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
    FOREIGN KEY (ProductVersionID) REFERENCES ProductVersions(ProductVersionID)
);

-- Tạo bảng Ratings
CREATE TABLE Ratings (
    RatingID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    UserID CHAR(36) NOT NULL,
    OrderDetailID CHAR(36) NOT NULL,
    RatingValue INT CHECK (RatingValue >= 1 AND RatingValue <= 5),
    Comment TEXT,
    RatingDate DATETIME NOT NULL,
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (OrderDetailID) REFERENCES OrderDetails(OrderDetailID)
);

-- Tạo bảng RatingPictures
CREATE TABLE RatingPictures (
    PictureID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    RatingID CHAR(36) NOT NULL,
    Picture TEXT,
    FOREIGN KEY (RatingID) REFERENCES Ratings(RatingID)
);

-- Tạo bảng Entries
CREATE TABLE Entries (
    EntryID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    OrderDate DATE NOT NULL,
    TotalMoney DECIMAL(18, 2) NOT NULL
);

-- Tạo bảng EntryDetails
CREATE TABLE EntryDetails (
    EntryDetailID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    EntryID CHAR(36) NOT NULL,
    ProductVersionID CHAR(36) NOT NULL,
    Quantity INT NOT NULL,
    Price DECIMAL(18, 2) NOT NULL,
    FOREIGN KEY (EntryID) REFERENCES Entries(EntryID),  
    FOREIGN KEY (ProductVersionID) REFERENCES ProductVersions(ProductVersionID)
);

-- Tạo bảng Carts
CREATE TABLE Carts (
    CartID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    UserID CHAR(36) NOT NULL,
    ProductVersionID CHAR(36) NOT NULL,
    Quantity INT NOT NULL,
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (ProductVersionID) REFERENCES ProductVersions(ProductVersionID)
);

-- Tạo bảng Favorite
CREATE TABLE Favorite (
    FavoriteID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    UserID CHAR(36) NOT NULL,
    ProductID CHAR(36) NOT NULL,
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

-- Tạo bảng Notifications
CREATE TABLE Notifications (
    NotificationID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Title VARCHAR(255) NOT NULL,
    Content TEXT NOT NULL,
    Type VARCHAR(50) NOT NULL,
    NotificationDate DATETIME NOT NULL,
    Status VARCHAR(50) NOT NULL
);

-- Tạo bảng NotificationRecipients
CREATE TABLE NotificationRecipients (
    NotificationRecipientID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    NotificationID CHAR(36) NOT NULL,
    UserID CHAR(36) NOT NULL,
    Status VARCHAR(50) NOT NULL,
    FOREIGN KEY (NotificationID) REFERENCES Notifications(NotificationID),
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- Tạo bảng Roles
CREATE TABLE Roles (
    Role_ID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    RoleName VARCHAR(255) NOT NULL,
    EN_nameRole VARCHAR(255)
);

-- Tạo bảng UserRoles
CREATE TABLE UserRoles (
    Userrole_ID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id CHAR(36) NOT NULL,
    role_Id CHAR(36) NOT NULL,
    FOREIGN KEY (role_Id) REFERENCES Roles(Role_ID),
    FOREIGN KEY (user_id) REFERENCES Users(UserID)
);

-- Tạo bảng Modules
CREATE TABLE Modules (
    ModuleID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    ModuleName VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    EN_ModuleName VARCHAR(255),
    EN_description VARCHAR(255)
);

-- Tạo bảng Permissions
CREATE TABLE Permissions (
    PermissionID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    ModuleID CHAR(36) NOT NULL,
    Code VARCHAR(255) NOT NULL,
    PermissionName VARCHAR(255) NOT NULL,
    EN_PermissionName VARCHAR(255),
    FOREIGN KEY (ModuleID) REFERENCES Modules(ModuleID)
);

-- Tạo bảng PermissionRole
CREATE TABLE PermissionRole (
    PermissionID CHAR(36) NOT NULL,
    RoleID CHAR(36) NOT NULL,
    PRIMARY KEY (PermissionID, RoleID),
    FOREIGN KEY (PermissionID) REFERENCES Permissions(PermissionID),
    FOREIGN KEY (RoleID) REFERENCES Roles(Role_ID)
);

-- Tạo bảng OrderStatusType 
CREATE TABLE OrderStatusType  (
    StatusID INT PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    EN_Name VARCHAR(255)
);

-- Tạo bảng OrderStatus
CREATE TABLE OrderStatus (
    OrderID CHAR(36) NOT NULL,
    StatusID INT NOT NULL,
    Time DATETIME NOT NULL,
    PRIMARY KEY (OrderID, StatusID),
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
    FOREIGN KEY (StatusID) REFERENCES OrderStatusType (StatusID)
);

-- Thêm dữ liệu mẫu cho bảng Brands
INSERT INTO Brands (Name, Logo) VALUES
('Apple',  'https://example.com/logos/apple.png'),
('Samsung',  'https://example.com/logos/samsung.png'),
('Dell',  'https://example.com/logos/dell.png'),
('HP',  'https://example.com/logos/hp.png'),
('Asus',  'https://example.com/logos/asus.png'),
('Lenovo',  'https://example.com/logos/lenovo.png'),
('Acer',  'https://example.com/logos/acer.png'),
('Microsoft',  'https://example.com/logos/microsoft.png'),
('Xiaomi',  'https://example.com/logos/xiaomi.png'),
('Huawei',  'https://example.com/logos/huawei.png');

-- Thêm dữ liệu mẫu cho bảng Categories
INSERT INTO Categories (Name, EN_name) VALUES
('Điện thoại', 'Smartphones'),
('Laptop', 'Laptops'),
('Máy tính bảng', 'Tablets'),
('Phụ kiện điện thoại', 'Phone Accessories'),
('Phụ kiện laptop', 'Laptop Accessories'),
('Máy tính để bàn', 'Desktops'),
('Thiết bị đeo thông minh', 'Wearable Devices'),
('Tivi', 'Televisions'),
('Máy in', 'Printers'),
('Thiết bị mạng', 'Networking Devices');

-- Thêm dữ liệu mẫu cho bảng Attributes
INSERT INTO Attributes (AttributeName, EN_atributeName) VALUES
('Màu sắc', 'Color'),
('Kích thước màn hình', 'Screen Size'),
('Bộ nhớ trong', 'Internal Storage'),
('RAM', 'RAM'),
('CPU', 'CPU'),
('Pin', 'Battery Capacity'),
('Camera', 'Camera'),
('Trọng lượng', 'Weight'),
('Hệ điều hành', 'Operating System'),
('Độ phân giải màn hình', 'Screen Resolution');

-- Thêm dữ liệu mẫu cho bảng AttributeValues
INSERT INTO AttributeValues (AttributeID, Value, EN_value) VALUES
((SELECT AttributeID FROM Attributes WHERE AttributeName = 'Màu sắc'), 'Đen', 'Black'),
((SELECT AttributeID FROM Attributes WHERE AttributeName = 'Màu sắc'), 'Trắng', 'White'),
((SELECT AttributeID FROM Attributes WHERE AttributeName = 'Kích thước màn hình'), '6.1 inch', '6.1 inch'),
((SELECT AttributeID FROM Attributes WHERE AttributeName = 'Kích thước màn hình'), '15.6 inch', '15.6 inch'),
((SELECT AttributeID FROM Attributes WHERE AttributeName = 'Bộ nhớ trong'), '128GB', '128GB'),
((SELECT AttributeID FROM Attributes WHERE AttributeName = 'Bộ nhớ trong'), '512GB', '512GB'),
((SELECT AttributeID FROM Attributes WHERE AttributeName = 'RAM'), '8GB', '8GB'),
((SELECT AttributeID FROM Attributes WHERE AttributeName = 'RAM'), '16GB', '16GB'),
((SELECT AttributeID FROM Attributes WHERE AttributeName = 'CPU'), 'Intel Core i7', 'Intel Core i7'),
((SELECT AttributeID FROM Attributes WHERE AttributeName = 'CPU'), 'Apple M1', 'Apple M1');

-- Thêm dữ liệu mẫu cho bảng Products
INSERT INTO Products (CategoryID, BrandID, Name, Description, EN_description) VALUES
((SELECT CategoryID FROM Categories WHERE Name = 'Điện thoại'), (SELECT BrandID FROM Brands WHERE Name = 'Apple'), 'iPhone 13', 'Điện thoại thông minh với chip A15 Bionic.', 'Smartphone with A15 Bionic chip.'),
((SELECT CategoryID FROM Categories WHERE Name = 'Điện thoại'), (SELECT BrandID FROM Brands WHERE Name = 'Samsung'), 'Samsung Galaxy S21', 'Điện thoại với màn hình 6.2 inch và camera 64MP.', 'Phone with 6.2-inch display and 64MP camera.'),
((SELECT CategoryID FROM Categories WHERE Name = 'Laptop'), (SELECT BrandID FROM Brands WHERE Name = 'Dell'), 'Dell XPS 13', 'Laptop cao cấp với màn hình 13.3 inch và CPU Intel Core i7.', 'Premium laptop with 13.3-inch display and Intel Core i7 CPU.'),
((SELECT CategoryID FROM Categories WHERE Name = 'Laptop'), (SELECT BrandID FROM Brands WHERE Name = 'Apple'), 'MacBook Pro 14', 'Laptop với chip Apple M1 và màn hình Retina.', 'Laptop with Apple M1 chip and Retina display.'),
((SELECT CategoryID FROM Categories WHERE Name = 'Máy tính bảng'), (SELECT BrandID FROM Brands WHERE Name = 'Apple'), 'iPad Pro 11', 'Máy tính bảng với màn hình 11 inch và chip M1.', 'Tablet with 11-inch display and M1 chip.'),
((SELECT CategoryID FROM Categories WHERE Name = 'Phụ kiện điện thoại'), (SELECT BrandID FROM Brands WHERE Name = 'Apple'), 'AirPods Pro', 'Tai nghe không dây với công nghệ chống ồn chủ động.', 'Wireless earbuds with active noise cancellation.'),
((SELECT CategoryID FROM Categories WHERE Name = 'Phụ kiện laptop'), (SELECT BrandID FROM Brands WHERE Name = 'Microsoft'), 'Surface Pen', 'Bút cảm ứng dành cho Surface.', 'Stylus for Surface devices.'),
((SELECT CategoryID FROM Categories WHERE Name = 'Thiết bị đeo thông minh'), (SELECT BrandID FROM Brands WHERE Name = 'Apple'), 'Apple Watch Series 7', 'Đồng hồ thông minh với nhiều tính năng sức khỏe.', 'Smartwatch with extensive health features.'),
((SELECT CategoryID FROM Categories WHERE Name = 'Tivi'), (SELECT BrandID FROM Brands WHERE Name = 'Samsung'), 'Samsung QLED 55', 'Tivi 55 inch với công nghệ QLED và độ phân giải 4K.', '55-inch TV with QLED technology and 4K resolution.'),
((SELECT CategoryID FROM Categories WHERE Name = 'Thiết bị mạng'), (SELECT BrandID FROM Brands WHERE Name = 'Asus'), 'Asus RT-AX88U', 'Router Wi-Fi 6 hiệu năng cao.', 'High-performance Wi-Fi 6 router.');

-- Thêm dữ liệu mẫu cho bảng ProductVersions
INSERT INTO ProductVersions (ProductID, VersionName, CostPrice, Price, Weight, Height, Length, Width, Status, Image) VALUES
((SELECT ProductID FROM Products WHERE Name = 'iPhone 13'), '128GB - Đen', 19000000, 22000000, 173, 7.65, 14.67, 0.73, 'Active', 'https://example.com/images/iphone_13_black.png'),
((SELECT ProductID FROM Products WHERE Name = 'Samsung Galaxy S21'), '256GB - Trắng', 15000000, 18000000, 200, 7.9, 15.5, 0.7, 'Active', 'https://example.com/images/galaxy_s21_white.png'),
((SELECT ProductID FROM Products WHERE Name = 'Dell XPS 13'), '16GB RAM - 512GB SSD', 30000000, 35000000, 1400, 1.48, 30.1, 19.9, 'Active', 'https://example.com/images/dell_xps_13.png'),
((SELECT ProductID FROM Products WHERE Name = 'MacBook Pro 14'), '16GB RAM - 1TB SSD', 50000000, 55000000, 1600, 1.6, 31.3, 22.2, 'Active', 'https://example.com/images/macbook_pro_14.png'),
((SELECT ProductID FROM Products WHERE Name = 'iPad Pro 11'), '128GB - Xám', 20000000, 23000000, 468, 0.61, 24.81, 17.95, 'Active', 'https://example.com/images/ipad_pro_11_gray.png'),
((SELECT ProductID FROM Products WHERE Name = 'AirPods Pro'), 'AirPods Pro', 5000000, 6000000, 56, 5.4, 4.5, 2.5, 'Active', 'https://example.com/images/airpods_pro.png'),
((SELECT ProductID FROM Products WHERE Name = 'Surface Pen'), 'Bút cảm ứng - Đen', 2000000, 2500000, 20, 0.6, 14, 1.5, 'Active', 'https://example.com/images/surface_pen_black.png'),
((SELECT ProductID FROM Products WHERE Name = 'Apple Watch Series 7'), '44mm - Xanh', 12000000, 14000000, 100, 1.1, 4.5, 3.3, 'Active', 'https://example.com/images/apple_watch_7_blue.png'),
((SELECT ProductID FROM Products WHERE Name = 'Samsung QLED 55'), 'QLED 55 inch', 15000000, 18000000, 21000, 7.9, 123.2, 72.6, 'Active', 'https://example.com/images/samsung_qled_55.png'),
((SELECT ProductID FROM Products WHERE Name = 'Asus RT-AX88U'), 'Router Wi-Fi 6', 4000000, 4500000, 960, 3.1, 25, 15, 'Active', 'https://example.com/images/asus_rt_ax88u.png');


-- Thêm dữ liệu mẫu cho bảng VersionAttributes
INSERT INTO VersionAttributes (ProductVersionID, AttributeValueID)
SELECT pv.ProductVersionID, av.AttributeValueID
FROM ProductVersions pv
JOIN AttributeValues av ON (av.Value = '128GB' AND pv.VersionName = '128GB - Đen') OR
                           (av.Value = 'Đen' AND pv.VersionName = '128GB - Đen') OR
                           (av.Value = '256GB' AND pv.VersionName = '256GB - Trắng') OR
                           (av.Value = 'Trắng' AND pv.VersionName = '256GB - Trắng') OR
                           (av.Value = '16GB' AND pv.VersionName = '16GB RAM - 512GB SSD') OR
                           (av.Value = '512GB' AND pv.VersionName = '16GB RAM - 512GB SSD') OR
                           (av.Value = '1TB' AND pv.VersionName = '16GB RAM - 1TB SSD') OR
                           (av.Value = '16GB' AND pv.VersionName = '16GB RAM - 1TB SSD') OR
                           (av.Value = '128GB' AND pv.VersionName = '128GB - Xám') OR
                           (av.Value = 'Xám' AND pv.VersionName = '128GB - Xám');


-- Thêm dữ liệu mẫu cho bảng Promotions
INSERT INTO Promotions (Name, EN_name, StartDate, EndDate, Poster, EN_description) VALUES
('Giảm giá mùa hè', 'Summer Sale', '2024-06-01', '2024-06-30', 'https://example.com/posters/summer_sale.png', 'Discounts on all electronics during summer!'),
('Black Friday', 'Black Friday', '2024-11-25', '2024-11-28', 'https://example.com/posters/black_friday.png', 'Huge discounts on Black Friday!'),
('Tết Nguyên Đán', 'Lunar New Year', '2024-01-15', '2024-02-15', 'https://example.com/posters/lunar_new_year.png', 'Celebrate the Lunar New Year with amazing deals!'),
('Giảm giá Noel', 'Christmas Sale', '2024-12-20', '2024-12-25', 'https://example.com/posters/christmas_sale.png', 'Merry Christmas with special discounts!'),
('Ngày của Mẹ', 'Mother\'s Day', '2024-05-10', '2024-05-14', 'https://example.com/posters/mothers_day.png', 'Special offers for Mother\'s Day.'),
('Ngày Quốc Khánh', 'National Day', '2024-09-01', '2024-09-03', 'https://example.com/posters/national_day.png', 'Celebrate National Day with big discounts.'),
('Mùa tựu trường', 'Back to School', '2024-08-15', '2024-09-15', 'https://example.com/posters/back_to_school.png', 'Back to School sale for students.'),
('Cyber Monday', 'Cyber Monday', '2024-11-29', '2024-11-29', 'https://example.com/posters/cyber_monday.png', 'One-day online shopping spree!'),
('Valentine\'s Day', 'Valentine\'s Day', '2024-02-10', '2024-02-14', 'https://example.com/posters/valentines_day.png', 'Special discounts for your loved one.'),
('Ngày Quốc tế Phụ nữ', 'International Women\'s Day', '2024-03-07', '2024-03-08', 'https://example.com/posters/womens_day.png', 'Celebrate Women\'s Day with exclusive offers.');

-- Thêm dữ liệu mẫu cho bảng PromotionDetails
INSERT INTO PromotionDetails (PromotionID, PercentDiscount) VALUES
((SELECT PromotionID FROM Promotions WHERE Name = 'Giảm giá mùa hè'), 10),
((SELECT PromotionID FROM Promotions WHERE Name = 'Black Friday'), 20),
((SELECT PromotionID FROM Promotions WHERE Name = 'Tết Nguyên Đán'), 15),
((SELECT PromotionID FROM Promotions WHERE Name = 'Giảm giá Noel'), 25),
((SELECT PromotionID FROM Promotions WHERE Name = 'Ngày của Mẹ'), 5),
((SELECT PromotionID FROM Promotions WHERE Name = 'Ngày Quốc Khánh'), 10),
((SELECT PromotionID FROM Promotions WHERE Name = 'Mùa tựu trường'), 15),
((SELECT PromotionID FROM Promotions WHERE Name = 'Cyber Monday'), 30),
((SELECT PromotionID FROM Promotions WHERE Name = 'Valentine\'s Day'), 20),
((SELECT PromotionID FROM Promotions WHERE Name = 'Ngày Quốc tế Phụ nữ'), 15);

-- Thêm dữ liệu mẫu cho bảng PromotionProducts
INSERT INTO PromotionProducts (PromotionDetailID, ProductVersionID)
SELECT pd.PromotionDetailID, pv.ProductVersionID
FROM PromotionDetails pd
JOIN ProductVersions pv ON (pv.VersionName = '128GB - Đen' AND pd.PercentDiscount = 10) OR
                           (pv.VersionName = '256GB - Trắng' AND pd.PercentDiscount = 20) OR
                           (pv.VersionName = '16GB RAM - 512GB SSD' AND pd.PercentDiscount = 15) OR
                           (pv.VersionName = '16GB RAM - 1TB SSD' AND pd.PercentDiscount = 25) OR
                           (pv.VersionName = '128GB - Xám' AND pd.PercentDiscount = 5) OR
                           (pv.VersionName = 'AirPods Pro' AND pd.PercentDiscount = 10) OR
                           (pv.VersionName = 'Surface Pen' AND pd.PercentDiscount = 15) OR
                           (pv.VersionName = 'Apple Watch Series 7' AND pd.PercentDiscount = 30) OR
                           (pv.VersionName = 'Samsung QLED 55' AND pd.PercentDiscount = 20) OR
                           (pv.VersionName = 'Asus RT-AX88U' AND pd.PercentDiscount = 15);

-- Thêm dữ liệu mẫu cho bảng Users
INSERT INTO Users (Username, Fullname, Password, Phone, Email, Avatar, Language, active, forgetPassword) VALUES
('user01', 'Nguyen Van A', 'AItAAtqZ+MHVTXQtCBOxSTN1Pe/DDIqz', '0987654321', 'user01@example.com', 'https://example.com/avatars/user01.png', 'vi', TRUE, FALSE),
('user02', 'Le Thi B', 'AItAAtqZ+MHVTXQtCBOxSTN1Pe/DDIqz', '0987654322', 'user02@example.com', 'https://example.com/avatars/user02.png', 'vi', TRUE, FALSE),
('user03', 'Tran Van C', 'AItAAtqZ+MHVTXQtCBOxSTN1Pe/DDIqz', '0987654323', 'user03@example.com', 'https://example.com/avatars/user03.png', 'vi', TRUE, FALSE),
('user04', 'Pham Thi D', 'AItAAtqZ+MHVTXQtCBOxSTN1Pe/DDIqz', '0987654324', 'user04@example.com', 'https://example.com/avatars/user04.png', 'vi', TRUE, FALSE),
('user05', 'Hoang Van E', 'AItAAtqZ+MHVTXQtCBOxSTN1Pe/DDIqz', '0987654325', 'user05@example.com', 'https://example.com/avatars/user05.png', 'vi', TRUE, FALSE),
('user06', 'Vu Thi F', 'AItAAtqZ+MHVTXQtCBOxSTN1Pe/DDIqz', '0987654326', 'user06@example.com', 'https://example.com/avatars/user06.png', 'vi', TRUE, FALSE),
('user07', 'Nguyen Van G', 'AItAAtqZ+MHVTXQtCBOxSTN1Pe/DDIqz', '0987654327', 'user07@example.com', 'https://example.com/avatars/user07.png', 'vi', TRUE, FALSE),
('user08', 'Le Thi H', 'AItAAtqZ+MHVTXQtCBOxSTN1Pe/DDIqz', '0987654328', 'user08@example.com', 'https://example.com/avatars/user08.png', 'vi', TRUE, FALSE),
('user09', 'Tran Van I', 'AItAAtqZ+MHVTXQtCBOxSTN1Pe/DDIqz', '0987654329', 'user09@example.com', 'https://example.com/avatars/user09.png', 'vi', TRUE, FALSE),
('user10', 'Pham Thi J', 'AItAAtqZ+MHVTXQtCBOxSTN1Pe/DDIqz', '0987654330', 'user10@example.com', 'https://example.com/avatars/user10.png', 'vi', TRUE, FALSE);

-- Thêm dữ liệu mẫu cho bảng Vouchers
INSERT INTO Vouchers (VoucherCode, LeastBill, LeastDiscount, BiggestDiscount, DiscountLevel, DiscountForm, StartDate, EndDate) VALUES
('SUMMER2024', 500000, 50000, 100000, 10, 'Percent', '2024-06-01', '2024-06-30'),
('BLACKFRIDAY', 1000000, 100000, 200000, 20, 'Percent', '2024-11-25', '2024-11-28'),
('TET2024', 800000, 80000, 150000, 15, 'Percent', '2024-01-15', '2024-02-15'),
('XMAS2024', 700000, 70000, 140000, 25, 'Percent', '2024-12-20', '2024-12-25'),
('MOTHERDAY', 600000, 60000, 120000, 5, 'Percent', '2024-05-10', '2024-05-14');

-- Thêm dữ liệu mẫu cho bảng UserVouchers
INSERT INTO UserVouchers (UserID, VoucherID) VALUES
((SELECT UserID FROM Users WHERE Username = 'user01'), 
 (SELECT VoucherID FROM Vouchers WHERE VoucherCode = 'SUMMER2024')),
((SELECT UserID FROM Users WHERE Username = 'user02'), 
 (SELECT VoucherID FROM Vouchers WHERE VoucherCode = 'BLACKFRIDAY')),
((SELECT UserID FROM Users WHERE Username = 'user03'), 
 (SELECT VoucherID FROM Vouchers WHERE VoucherCode = 'TET2024')),
((SELECT UserID FROM Users WHERE Username = 'user04'), 
 (SELECT VoucherID FROM Vouchers WHERE VoucherCode = 'XMAS2024')),
((SELECT UserID FROM Users WHERE Username = 'user05'), 
 (SELECT VoucherID FROM Vouchers WHERE VoucherCode = 'MOTHERDAY'));

-- Thêm dữ liệu mẫu cho bảng Orders
INSERT INTO Orders (UserID, VoucherID, OrderDate, ShipFee, TotalMoney, CodValue, InsuranceValue, ServiceTypeID, OrderAddress, OrderPhone, OrderName) VALUES
((SELECT UserID FROM Users WHERE Username = 'user01'), (SELECT VoucherID FROM Vouchers WHERE VoucherCode = 'SUMMER2024'), '2024-06-05', 60000, 600000, 0, 0, 1, '123 Phúc Xá', '0987654321', 'Nguyen Van A'),
((SELECT UserID FROM Users WHERE Username = 'user02'), (SELECT VoucherID FROM Vouchers WHERE VoucherCode = 'BLACKFRIDAY'), '2024-11-26', 70000, 1500000, 100000, 0, 1, '456 Cầu Ông Lãnh', '0987654322', 'Le Thi B'),
((SELECT UserID FROM Users WHERE Username = 'user03'), (SELECT VoucherID FROM Vouchers WHERE VoucherCode = 'TET2024'), '2024-02-01', 50000, 900000, 0, 50000, 2, '789 Phạm Đình Hổ', '0987654323', 'Tran Van C'),
((SELECT UserID FROM Users WHERE Username = 'user04'), (SELECT VoucherID FROM Vouchers WHERE VoucherCode = 'XMAS2024'), '2024-12-21', 80000, 800000, 200000, 0, 1, '321 Bình Thạnh', '0987654324', 'Pham Thi D'),
((SELECT UserID FROM Users WHERE Username = 'user05'), (SELECT VoucherID FROM Vouchers WHERE VoucherCode = 'MOTHERSDAY'), '2024-05-11', 60000, 700000, 0, 0, 1, '654 Vĩnh Phúc', '0987654325', 'Hoang Van E');

-- Thêm dữ liệu mẫu cho bảng OrderDetails
INSERT INTO OrderDetails (OrderID, ProductVersionID, Quantity, Price, DiscountPrice) VALUES
((SELECT OrderID FROM Orders WHERE OrderName = 'Nguyen Van A'), 
 (SELECT ProductVersionID FROM ProductVersions WHERE VersionName = '128GB - Đen'), 1, 600000, 540000),
((SELECT OrderID FROM Orders WHERE OrderName = 'Le Thi B'), 
 (SELECT ProductVersionID FROM ProductVersions WHERE VersionName = '256GB - Trắng'), 1, 1500000, 1200000),
((SELECT OrderID FROM Orders WHERE OrderName = 'Tran Van C'), 
 (SELECT ProductVersionID FROM ProductVersions WHERE VersionName = '16GB RAM - 512GB SSD'), 1, 900000, 810000),
((SELECT OrderID FROM Orders WHERE OrderName = 'Pham Thi D'), 
 (SELECT ProductVersionID FROM ProductVersions WHERE VersionName = '16GB RAM - 1TB SSD'), 1, 800000, 600000),
((SELECT OrderID FROM Orders WHERE OrderName = 'Hoang Van E'), 
 (SELECT ProductVersionID FROM ProductVersions WHERE VersionName = '128GB - Xám'), 1, 700000, 665000);

-- Thêm dữ liệu mẫu cho bảng Ratings
INSERT INTO Ratings (UserID, OrderDetailID, RatingValue, Comment, RatingDate) VALUES
((SELECT UserID FROM Users WHERE Username = 'user01'), 
 (SELECT OrderDetailID FROM OrderDetails WHERE Price = 600000), 5, 'Sản phẩm rất tốt!', '2024-06-06 14:00:00'),
((SELECT UserID FROM Users WHERE Username = 'user02'), 
 (SELECT OrderDetailID FROM OrderDetails WHERE Price = 1500000), 4, 'Hài lòng với sản phẩm.', '2024-11-27 09:30:00'),
((SELECT UserID FROM Users WHERE Username = 'user03'), 
 (SELECT OrderDetailID FROM OrderDetails WHERE Price = 900000), 3, 'Sản phẩm tạm ổn.', '2024-02-02 16:45:00'),
((SELECT UserID FROM Users WHERE Username = 'user04'), 
 (SELECT OrderDetailID FROM OrderDetails WHERE Price = 800000), 2, 'Không hài lòng với chất lượng.', '2024-12-22 10:15:00'),
((SELECT UserID FROM Users WHERE Username = 'user05'), 
 (SELECT OrderDetailID FROM OrderDetails WHERE Price = 700000), 1, 'Rất thất vọng về sản phẩm.', '2024-05-12 11:20:00');

-- Thêm dữ liệu mẫu cho bảng RatingPictures
INSERT INTO RatingPictures (RatingID, Picture) VALUES
((SELECT RatingID FROM Ratings WHERE Comment = 'Sản phẩm rất tốt!'), 'image_good1.png'),
((SELECT RatingID FROM Ratings WHERE Comment = 'Hài lòng với sản phẩm.'), 'image_good2.png'),
((SELECT RatingID FROM Ratings WHERE Comment = 'Sản phẩm tạm ổn.'), 'image_okay1.png'),
((SELECT RatingID FROM Ratings WHERE Comment = 'Không hài lòng với chất lượng.'), 'image_bad1.png'),
((SELECT RatingID FROM Ratings WHERE Comment = 'Rất thất vọng về sản phẩm.'), 'image_bad2.png');

-- Thêm dữ liệu mẫu cho bảng Entries
INSERT INTO Entries (OrderDate, TotalMoney)
VALUES
('2024-07-01', 10000000),
('2024-08-01', 5000000),
('2024-09-01', 2000000),
('2024-10-01', 3000000),
('2024-11-01', 15000000);

-- Thêm dữ liệu mẫu cho bảng EntryDetails
INSERT INTO EntryDetails (EntryID, ProductVersionID, Quantity, Price)
VALUES
((SELECT EntryID FROM Entries WHERE OrderDate = '2024-07-01'), 
 (SELECT ProductVersionID FROM ProductVersions WHERE VersionName = '128GB - Đen'), 10, 600000),
((SELECT EntryID FROM Entries WHERE OrderDate = '2024-08-01'), 
 (SELECT ProductVersionID FROM ProductVersions WHERE VersionName = '256GB - Trắng'), 5, 1500000),
((SELECT EntryID FROM Entries WHERE OrderDate = '2024-09-01'), 
 (SELECT ProductVersionID FROM ProductVersions WHERE VersionName = '16GB RAM - 512GB SSD'), 2, 900000),
((SELECT EntryID FROM Entries WHERE OrderDate = '2024-10-01'), 
 (SELECT ProductVersionID FROM ProductVersions WHERE VersionName = '16GB RAM - 1TB SSD'), 3, 800000),
((SELECT EntryID FROM Entries WHERE OrderDate = '2024-11-01'), 
 (SELECT ProductVersionID FROM ProductVersions WHERE VersionName = '128GB - Xám'), 15, 700000);

-- Thêm dữ liệu mẫu cho bảng Carts
INSERT INTO Carts (UserID, ProductVersionID, Quantity) VALUES
((SELECT UserID FROM Users WHERE Username = 'user01'), 
 (SELECT ProductVersionID FROM ProductVersions WHERE VersionName = '128GB - Đen'), 1),
((SELECT UserID FROM Users WHERE Username = 'user02'), 
 (SELECT ProductVersionID FROM ProductVersions WHERE VersionName = '256GB - Trắng'), 2),
((SELECT UserID FROM Users WHERE Username = 'user03'), 
 (SELECT ProductVersionID FROM ProductVersions WHERE VersionName = '16GB RAM - 512GB SSD'), 1),
((SELECT UserID FROM Users WHERE Username = 'user04'), 
 (SELECT ProductVersionID FROM ProductVersions WHERE VersionName = '16GB RAM - 1TB SSD'), 3),
((SELECT UserID FROM Users WHERE Username = 'user05'), 
 (SELECT ProductVersionID FROM ProductVersions WHERE VersionName = '128GB - Xám'), 1);

-- Thêm dữ liệu mẫu cho bảng Favorite
INSERT INTO Favorite (UserID, ProductID) VALUES
((SELECT UserID FROM Users WHERE Username = 'user01'), (SELECT ProductID FROM Products WHERE Name = 'iPhone 13')),
((SELECT UserID FROM Users WHERE Username = 'user02'), (SELECT ProductID FROM Products WHERE Name = 'Samsung Galaxy S21')),
((SELECT UserID FROM Users WHERE Username = 'user03'), (SELECT ProductID FROM Products WHERE Name = 'Dell XPS 13')),
((SELECT UserID FROM Users WHERE Username = 'user04'), (SELECT ProductID FROM Products WHERE Name = 'MacBook Pro 14')),
((SELECT UserID FROM Users WHERE Username = 'user05'), (SELECT ProductID FROM Products WHERE Name = 'iPad Pro 11'));

-- Insert data into Notifications
INSERT INTO Notifications (Title, Content, Type, NotificationDate, Status) VALUES
('Khuyến mãi mùa hè', 'Giảm giá đến 50% cho tất cả các sản phẩm!', 'Promotion', '2024-06-01 08:00:00', 'Sent'),
('Black Friday', 'Giảm giá sốc 70% trong ngày Black Friday!', 'Promotion', '2024-11-25 09:00:00', 'Scheduled'),
('Tết 2024', 'Mua sắm thả ga với khuyến mãi Tết 2024!', 'Promotion', '2024-01-15 07:00:00', 'Sent'),
('Giáng sinh 2024', 'Ưu đãi lớn cho mùa Giáng sinh năm nay!', 'Promotion', '2024-12-20 10:00:00', 'Sent'),
('Ngày của mẹ', 'Món quà tuyệt vời dành cho mẹ nhân ngày của mẹ!', 'Promotion', '2024-05-10 08:30:00', 'Scheduled');

-- Insert data into NotificationRecipients
INSERT INTO NotificationRecipients (NotificationID, UserID, Status) VALUES
((SELECT NotificationID FROM Notifications WHERE Title = 'Khuyến mãi mùa hè'), 
 (SELECT UserID FROM Users WHERE Username = 'user01'), 'Delivered'),
((SELECT NotificationID FROM Notifications WHERE Title = 'Black Friday'), 
 (SELECT UserID FROM Users WHERE Username = 'user02'), 'Pending'),
((SELECT NotificationID FROM Notifications WHERE Title = 'Tết 2024'), 
 (SELECT UserID FROM Users WHERE Username = 'user03'), 'Delivered'),
((SELECT NotificationID FROM Notifications WHERE Title = 'Giáng sinh 2024'), 
 (SELECT UserID FROM Users WHERE Username = 'user04'), 'Delivered'),
((SELECT NotificationID FROM Notifications WHERE Title = 'Ngày của mẹ'), 
 (SELECT UserID FROM Users WHERE Username = 'user05'), 'Pending');
 
 
INSERT INTO UserAddresses (UserID, ProvinceID, ProvinceName, DistrictID, DistrictName, WardCode, WardName, DetailAddress) VALUES
((SELECT UserID FROM Users WHERE Username = 'user01'), 1, 'ProvinceName1', 101, 'DistrictName1', '001', 'WardName1', '123 Main St'),
((SELECT UserID FROM Users WHERE Username = 'user02'), 1, 'ProvinceName1', 102, 'DistrictName2', '002', 'WardName2', '456 Elm St'),
((SELECT UserID FROM Users WHERE Username = 'user03'), 1, 'ProvinceName1', 103, 'DistrictName3', '003', 'WardName3', '789 Oak St'),
((SELECT UserID FROM Users WHERE Username = 'user04'), 1, 'ProvinceName1', 104, 'DistrictName4', '004', 'WardName4', '101 Pine St'),
((SELECT UserID FROM Users WHERE Username = 'user05'), 1, 'ProvinceName1', 105, 'DistrictName5', '005', 'WardName5', '202 Maple St');

-- Cập nhật bảng Modules (không cần trường Code)
INSERT INTO Modules (ModuleName, EN_ModuleName) VALUES 
    ('Quản lý xác thực', 'Auth Management'),
    ('Quản lý thông báo', 'Notification Management'),
    ('Quản lý đơn hàng', 'Order Management'),
    ('Quản lý nhập hàng', 'Entry Management'),
    ('Quản lý thuộc tính', 'Attribute Management'),
    ('Quản lý thương hiệu', 'Brand Management'),
    ('Quản lý danh mục', 'Category Management'),
    ('Quản lý sản phẩm', 'Product Management'),
    ('Quản lý phiên bản sản phẩm', 'Product Version Management'),
    ('Quản lý khuyến mãi', 'Promotion Management'),
    ('Quản lý chi tiết khuyến mãi', 'Promotion Details Management'),
    ('Quản lý sản phẩm trong khuyến mãi', 'Promotion Product Management'),
    ('Quản lý đánh giá', 'Rating Management'),
    ('Quản lý sản phẩm yêu thích', 'Favorite Management'),
    ('Quản lý giỏ hàng', 'Cart Management'),
    ('Quản lý voucher', 'Voucher Management'),
    ('Quản lý quyền', 'Role Management'),
    ('Thống kê báo cáo', 'Reporting Management');

-- Chèn dữ liệu vào bảng Permissions với trường Code
INSERT INTO Permissions (ModuleID, PermissionName, EN_PermissionName, Code) VALUES 
    -- Quản lý xác thực (AUTH)
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Auth Management'), 'Đăng nhập', 'Login', 'login'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Auth Management'), 'Đăng xuất', 'Logout', 'logout'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Auth Management'), 'Đăng ký', 'Register', 'register'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Auth Management'), 'Xác thực', 'Verify', 'verify'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Auth Management'), 'Đăng nhập bằng Google', 'Login with Google', 'login/google'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Auth Management'), 'Đổi mật khẩu', 'Change Password', 'change-password'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Auth Management'), 'Quên mật khẩu', 'Forgot Password', 'forgot-password'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Auth Management'), 'Đặt lại mật khẩu', 'Reset Password', 'reset-password'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Auth Management'), 'Xác thực token', 'Token Validate', 'token/validate'),

    -- Quản lý thông báo (NOTIFICATION)
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Notification Management'), 'Xem tất cả thông báo', 'View all notifications', 'view_all_notifications'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Notification Management'), 'Gửi thông báo', 'Send notifications', 'send_notifications'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Notification Management'), 'Đánh dấu thông báo đã đọc', 'Mark notification as read', 'notifications/markAsRead'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Notification Management'), 'Đánh dấu tất cả thông báo đã đọc', 'Mark all notifications as read', 'notifications/markAllAsRead'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Notification Management'), 'Xóa thông báo', 'Delete notification', 'notifications/delete'),

    -- Quản lý đơn hàng (ORDER)
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Order Management'), 'Xem tất cả đơn hàng', 'View all orders', 'view_all_orders'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Order Management'), 'Thêm đơn hàng mới', 'Add new order', 'orders/create'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Order Management'), 'Xem chi tiết đơn hàng', 'View order details', 'orders/{id}/details'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Order Management'), 'Hủy đơn hàng', 'Cancel order', 'orders/cancel'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Order Management'), 'Xác nhận thanh toán', 'Confirm payment', 'orders/mark-as-paid'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Order Management'), 'Đang giao hàng', 'Mark as shipping', 'orders/mark-as-shipping'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Order Management'), 'Đã giao hàng', 'Mark as delivered', 'orders/mark-as-delivered'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Order Management'), 'Xác nhận đơn hàng', 'Confirm order', 'orders/mark-as-confirmed'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Order Management'), 'Đang chờ xử lý', 'Mark as pending', 'orders/mark-as-pending'),

    -- Quản lý đơn hàng (ENTRIES)
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Entry Management'), 'Xem tất cả đơn nhập', 'View all entries', 'view_all_entries'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Entry Management'), 'Thêm đơn nhập mới', 'Add new entries', 'entries'),

    -- Quản lý thuộc tính (ATTRIBUTE)
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Attribute Management'), 'Xem tất cả thuộc tính', 'View all attributes', 'view_attributes'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Attribute Management'), 'Thêm thuộc tính mới', 'Add new attribute', 'add_new_attribute'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Attribute Management'), 'Cập nhật thuộc tính mới', 'Update attribute', 'update_attribute'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Attribute Management'), 'Thêm mới thuộc tính', 'Add attribute value', 'add_attribute_value'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Attribute Management'), 'Cập nhật thuộc tính', 'Update attribute value', 'update_attribute_value'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Attribute Management'), 'Xóa giá trị thuộc tính', 'Delete attribute value', 'delete_attribute_value'),    
    
    -- Quản lý thương hiệu (BRAND)
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Brand Management'), 'Xem tất cả thương hiệu', 'View all brands', 'view_all_brands'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Brand Management'), 'Thêm thương hiệu mới', 'Add new brand', 'add_new_brand'),
    
    -- Quản lý danh mục (CATEGORY)
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Category Management'), 'Xem tất cả danh mục', 'View all categories', 'view_all_categories'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Category Management'), 'Thêm danh mục mới', 'Add new category', 'add_new_category'),
	((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Category Management'), 'Xóa danh mục', 'Remove category', 'delete_category'),
    
    -- Quản lý sản phẩm (PRODUCT)
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Product Management'), 'Xem tất cả sản phẩm', 'View all products', 'view_all_products'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Product Management'), 'Thêm sản phẩm mới', 'Add new product', 'add_new_product'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Product Management'), 'Chỉnh sửa sản phẩm', 'Edit product', 'edit_product'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Product Management'), 'Xóa sản phẩm', 'Delete product', 'delete_product'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Product Management'), 'Quản lý phiên bản sản phẩm', 'Manage product versions', 'manage_product_versions'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Product Management'), 'Nhập hàng loạt sản phẩm', 'Bulk import products', 'bulk_import_products'),
    
    -- Quản lý phiên bản sản phẩm (PRODUCT VERSION)
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Product Version Management'), 'Xem tất cả phiên bản sản phẩm', 'View all product versions', 'view_all_product_versions'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Product Version Management'), 'Thêm phiên bản sản phẩm mới', 'Add new product version', 'add_new_product_version'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Product Version Management'), 'Xóa phiên bản sản phẩm', 'Delete product version', 'delete_product_version'),
    
    
    -- Quản lý khuyến mãi (PROMOTIONS)
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Promotion Management'), 'Xem danh sách khuyến mãi', 'View promotions list', 'view_promotions_list'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Promotion Management'), 'Kích hoạt khuyến mãi', 'Activate promotions', 'activate_promotions'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Promotion Management'), 'Cập nhật khuyến mãi', 'Update promotions', 'update_promotion'),
	((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Promotion Management'), 'Thêm mới khuyến mãi', 'Add new promotions', 'add_new_promotion'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Promotion Management'), 'Cập nhật khuyến mãi', 'Update promotions', 'search_promotions'),
    
    -- Quản lý chi tiết khuyến mãi (PROMOTION DETAILS)
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Promotion Details Management'), 'Thêm chi tiết khuyến mãi', 'TAdd new promotion details', 'add_new_promotion_details'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Promotion Details Management'), 'Xem chi tiết khuyến mãi', 'View promotion details', 'view_promotion_details'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Promotion Details Management'), 'Cập nhật chi tiết khuyến mãi', 'Update promotion details', 'update_promotion_details'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Promotion Details Management'), 'Xóa chi tiết khuyến mãi', 'Delete promotion details', 'delete_promotion_details'),
    
    -- Quản lý sản phẩm trong khuyến mãi (PROMOTION PRODUCT)
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Promotion Product Management'), 'Xem tất cả sản phẩm khuyến mãi', 'View all promotion products', 'view_all_promotion_products'),
    
    -- Quản lý đánh giá (RATING)
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Rating Management'), 'Xem đánh giá', 'View reviews', 'view_reviews'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Rating Management'), 'Xóa đánh giá', 'Delete reviews', 'delete_reviews'),
    
    -- Quản lý sản phẩm yêu thích (FAVORITE)
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Favorite Management'), 'Xem sản phẩm yêu thích', 'View favorite products', 'view_favorite_products'),
    
    -- Quản lý giỏ hàng (CART)
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Cart Management'), 'Xem giỏ hàng', 'View cart', 'view_cart'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Cart Management'), 'Thêm sản phẩm vào giỏ hàng', 'Add product to cart', 'add_product_to_cart'),
    
    -- Quản lý voucher (VOUCHER)
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Voucher Management'), 'Xem tất cả voucher', 'View all vouchers', 'view_all_vouchers'),
    
    -- Quản lý quyền (ROLE)
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Role Management'), 'Xem tất cả quyền', 'View all roles', 'view_all_roles'),
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Role Management'), 'Thêm quyền mới', 'Add new role', 'add_new_role'),
    
    -- Thống kê báo cáo (REPORTING)
    ((SELECT ModuleID FROM Modules WHERE EN_ModuleName = 'Reporting Management'), 'Xem báo cáo', 'View reports', 'view_reports');


INSERT INTO Roles (RoleName, EN_nameRole) VALUES
    ('SuperAdmin', 'SuperAdministrator'),
    ('Admin', 'Administrator'),
    ('Nhân viên bán hàng', 'Saler'),
    ('Quản lý', 'Manager'),
    ('Nhân viên hỗ trợ', 'Support');

-- Thêm dữ liệu mẫu cho bảng UserRoles
INSERT INTO UserRoles (user_id, role_id) VALUES
    ((SELECT UserID FROM Users WHERE Username like 'user01'), 
     (SELECT Role_ID FROM Roles WHERE RoleName like 'SuperAdmin')),
    ((SELECT UserID FROM Users WHERE Username like 'user02'), 
     (SELECT Role_ID FROM Roles WHERE RoleName like 'Nhân viên bán hàng')),
    ((SELECT UserID FROM Users WHERE Username like 'user03'), 
     (SELECT Role_ID FROM Roles WHERE RoleName like 'Quản lý')),
    ((SELECT UserID FROM Users WHERE Username like 'user04'), 
     (SELECT Role_ID FROM Roles WHERE RoleName like 'Admin')),
    ((SELECT UserID FROM Users WHERE Username like 'user05'), 
     (SELECT Role_ID FROM Roles WHERE RoleName like 'Nhân viên hỗ trợ'));

-- Thêm tất cả quyền cho SuperAdmin
INSERT INTO PermissionRole (PermissionID, RoleID)
SELECT PermissionID, (SELECT Role_ID FROM Roles WHERE RoleName LIKE 'SuperAdmin')
FROM Permissions;

INSERT INTO OrderStatusType (StatusID, Name, EN_Name) VALUES
(-1, 'Đã hủy', 'Cancelled'),
(1, 'Chờ xác nhận', 'Pending'),
(2, 'Chờ thanh toán', 'Pending Payment'),
(3, 'Đã thanh toán', 'Paid'),
(4, 'Đã xác nhận', 'Confirmed'),
(5, 'Đang giao hàng', 'Shipping'),
(6, 'Đã giao hàng', 'Delivered');

INSERT INTO OrderStatus (OrderID, StatusID, Time)
SELECT OrderID, 1, NOW() FROM Orders;

