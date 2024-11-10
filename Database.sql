-- Tạo cơ sở dữ liệu EndlessEcommerce
CREATE DATABASE IF NOT EXISTS EndlessEcommerce;
USE EndlessEcommerce;

-- Tạo bảng Brands
CREATE TABLE Brands (
    BrandID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Name NVARCHAR(255) NOT NULL,
    Logo LONGTEXT,
    CreateDate DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng Categories
CREATE TABLE Categories (
    CategoryID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Name NVARCHAR(255) NOT NULL,
    CreateDate DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng Attributes
CREATE TABLE Attributes (
    AttributeID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    AttributeName NVARCHAR(255) NOT NULL,
    CreateDate DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng AttributeValues
CREATE TABLE AttributeValues (
    AttributeValueID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    AttributeID CHAR(36) NOT NULL,
    Value NVARCHAR(255) NOT NULL,
    FOREIGN KEY (AttributeID) REFERENCES Attributes(AttributeID)
);

-- Tạo bảng Products
CREATE TABLE Products (
    ProductID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    CategoryID CHAR(36) NOT NULL,
    BrandID CHAR(36) NOT NULL,
    Name NVARCHAR(255) NOT NULL,
    Description TEXT CHARACTER SET utf8mb4,
    CreateDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID),
    FOREIGN KEY (BrandID) REFERENCES Brands(BrandID)
);

-- Tạo bảng ProductVersions
CREATE TABLE ProductVersions (
    ProductVersionID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    ProductID CHAR(36) NOT NULL,
    VersionName NVARCHAR(255) NOT NULL,
    CostPrice DECIMAL(18, 2) NOT NULL,
    Price DECIMAL(18, 2) NOT NULL,
    Weight DECIMAL(18, 2) NOT NULL,
    Height DECIMAL(18, 2) NOT NULL, -- Chiều cao
    Length DECIMAL(18, 2) NOT NULL, -- Chiều dài
    Width DECIMAL(18, 2) NOT NULL,  -- Chiều rộng
    Status NVARCHAR(50) NOT NULL,
    Image TEXT,
    CreateDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

-- Tạo bảng VersionAttributes
CREATE TABLE VersionAttributes (
    VersionAttributeID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    ProductVersionID CHAR(36) NOT NULL,
    AttributeValueID CHAR(36) NOT NULL,
    CreateDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ProductVersionID) REFERENCES ProductVersions(ProductVersionID),
    FOREIGN KEY (AttributeValueID) REFERENCES AttributeValues(AttributeValueID)
);

-- Tạo bảng Promotions
CREATE TABLE Promotions (
    PromotionID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Name NVARCHAR(255) NOT NULL,
    StartDate DATETIME NOT NULL,
    EndDate DATETIME NOT NULL,
    Poster LONGTEXT,
    CreateDate DATETIME DEFAULT CURRENT_TIMESTAMP
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
    Username NVARCHAR(255) NOT NULL,
    Fullname NVARCHAR(255),
    Password NVARCHAR(255),
    Phone NVARCHAR(11),
    Email NVARCHAR(255),
    Avatar TEXT,
    Active BOOLEAN DEFAULT TRUE,
    ForgetPassword BOOLEAN DEFAULT FALSE,
    Token TEXT,
    CreateDate DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng UserAddresses
CREATE TABLE UserAddresses (
    AddressID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    UserID CHAR(36) NOT NULL,
    ProvinceID INT NOT NULL,
    ProvinceName NVARCHAR(50) NOT NULL,
    DistrictID INT NOT NULL, 
    DistrictName NVARCHAR(50) NOT NULL,
    WardCode INT NOT NULL, 
    WardName NVARCHAR(50) NOT NULL,
    DetailAddress TEXT CHARACTER SET utf8mb4 NOT NULL,
    CreateDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- Tạo bảng Vouchers
CREATE TABLE Vouchers (
    VoucherID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    VoucherCode NVARCHAR(50) NOT NULL UNIQUE,
    LeastBill DECIMAL(18, 2) NOT NULL,
    LeastDiscount DECIMAL(18, 2) NOT NULL,
    BiggestDiscount DECIMAL(18, 2) NOT NULL,
    DiscountLevel INT NOT NULL,
    StartDate DATETIME NOT NULL,
    EndDate DATETIME NOT NULL,
    CreateDate DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng UserVouchers
CREATE TABLE UserVouchers (
    UserVoucherID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    UserID CHAR(36) NOT NULL,
    VoucherID CHAR(36) NOT NULL,
    Status BIT NOT NULL DEFAULT b'1',
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (VoucherID) REFERENCES Vouchers(VoucherID)
);

-- Tạo bảng Orders
CREATE TABLE Orders (
    OrderID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    UserID CHAR(36) NOT NULL,
    VoucherID CHAR(36),
    OrderDate DATETIME NOT NULL,
    ShipFee DECIMAL(18, 2) NOT NULL,
    VoucherDiscount DECIMAL(18, 2) DEFAULT 0,
    TotalMoney DECIMAL(18, 2) NOT NULL,
    CodValue DECIMAL(18, 2) DEFAULT 0, -- Giá trị thu hộ
    InsuranceValue DECIMAL(18, 2) DEFAULT 0, -- Giá trị bảo hiểm
    ServiceTypeID INT NOT NULL, -- Mã loại dịch vụ
    OrderAddress TEXT CHARACTER SET utf8mb4,
    OrderPhone NVARCHAR(15),
    OrderName NVARCHAR(255),
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
    Comment TEXT CHARACTER SET utf8mb4,
    RatingDate DATETIME NOT NULL,
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (OrderDetailID) REFERENCES OrderDetails(OrderDetailID)
);

-- Tạo bảng RatingPictures
CREATE TABLE RatingPictures (
    PictureID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    RatingID CHAR(36) NOT NULL,
    Picture LONGTEXT,
    FOREIGN KEY (RatingID) REFERENCES Ratings(RatingID)
);

-- Tạo bảng Entries
CREATE TABLE Entries (
    EntryID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    EntryDate DATETIME NOT NULL,
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
    CreateDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (ProductVersionID) REFERENCES ProductVersions(ProductVersionID)
);

-- Tạo bảng Notifications
CREATE TABLE Notifications (
    NotificationID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Title NVARCHAR(255) NOT NULL,
    Content TEXT CHARACTER SET utf8mb4 NOT NULL,
    Type NVARCHAR(50) NOT NULL,
    NotificationDate DATETIME NOT NULL,
    Status NVARCHAR(50) NOT NULL
);

-- Tạo bảng NotificationRecipients
CREATE TABLE NotificationRecipients (
    NotificationRecipientID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    NotificationID CHAR(36) NOT NULL,
    UserID CHAR(36) NOT NULL,
    Status NVARCHAR(50) NOT NULL,
    FOREIGN KEY (NotificationID) REFERENCES Notifications(NotificationID),
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- Tạo bảng Roles
CREATE TABLE Roles (
    Role_ID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    RoleName NVARCHAR(255) NOT NULL
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
    ModuleName NVARCHAR(255) NOT NULL,
    description NVARCHAR(255)
);

-- Tạo bảng Permissions
CREATE TABLE Permissions (
    PermissionID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    ModuleID CHAR(36) NOT NULL,
    Code NVARCHAR(255) NOT NULL,
    PermissionName NVARCHAR(255) NOT NULL,
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
    Name NVARCHAR(255) NOT NULL
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
INSERT INTO Categories (Name) VALUES
('Điện thoại'),
('Laptop'),
('Máy tính bảng'),
('Phụ kiện điện thoại'),
('Phụ kiện laptop'),
('Máy tính để bàn'),
('Thiết bị đeo thông minh'),
('Tivi'),
('Máy in'),
('Thiết bị mạng');

-- Thêm dữ liệu mẫu cho bảng Attributes
INSERT INTO Attributes (AttributeName) VALUES
('Màu sắc'),
('Kích thước màn hình'),
('Bộ nhớ trong'),
('RAM'),
('CPU'),
('Pin'),
('Camera'),
('Trọng lượng'),
('Hệ điều hành'),
('Độ phân giải màn hình');

-- Thêm dữ liệu mẫu cho bảng AttributeValues
INSERT INTO AttributeValues (AttributeID, Value) VALUES
((SELECT AttributeID FROM Attributes WHERE AttributeName = 'Màu sắc'), 'Đen'),
((SELECT AttributeID FROM Attributes WHERE AttributeName = 'Màu sắc'), 'Trắng'),
((SELECT AttributeID FROM Attributes WHERE AttributeName = 'Kích thước màn hình'), '6.1 inch'),
((SELECT AttributeID FROM Attributes WHERE AttributeName = 'Kích thước màn hình'), '15.6 inch'),
((SELECT AttributeID FROM Attributes WHERE AttributeName = 'Bộ nhớ trong'), '128GB'),
((SELECT AttributeID FROM Attributes WHERE AttributeName = 'Bộ nhớ trong'), '512GB'),
((SELECT AttributeID FROM Attributes WHERE AttributeName = 'RAM'), '8GB'),
((SELECT AttributeID FROM Attributes WHERE AttributeName = 'RAM'), '16GB'),
((SELECT AttributeID FROM Attributes WHERE AttributeName = 'CPU'), 'Intel Core i7'),
((SELECT AttributeID FROM Attributes WHERE AttributeName = 'CPU'), 'Apple M1');

-- Thêm dữ liệu mẫu cho bảng Products
INSERT INTO Products (CategoryID, BrandID, Name, Description) VALUES
((SELECT CategoryID FROM Categories WHERE Name = 'Điện thoại'), (SELECT BrandID FROM Brands WHERE Name = 'Apple'), 'iPhone 13', 'Điện thoại thông minh với chip A15 Bionic.'),
((SELECT CategoryID FROM Categories WHERE Name = 'Điện thoại'), (SELECT BrandID FROM Brands WHERE Name = 'Samsung'), 'Samsung Galaxy S21', 'Điện thoại với màn hình 6.2 inch và camera 64MP.'),
((SELECT CategoryID FROM Categories WHERE Name = 'Laptop'), (SELECT BrandID FROM Brands WHERE Name = 'Dell'), 'Dell XPS 13', 'Laptop cao cấp với màn hình 13.3 inch và CPU Intel Core i7.'),
((SELECT CategoryID FROM Categories WHERE Name = 'Laptop'), (SELECT BrandID FROM Brands WHERE Name = 'Apple'), 'MacBook Pro 14', 'Laptop với chip Apple M1 và màn hình Retina.'),
((SELECT CategoryID FROM Categories WHERE Name = 'Máy tính bảng'), (SELECT BrandID FROM Brands WHERE Name = 'Apple'), 'iPad Pro 11', 'Máy tính bảng với màn hình 11 inch và chip M1.'),
((SELECT CategoryID FROM Categories WHERE Name = 'Phụ kiện điện thoại'), (SELECT BrandID FROM Brands WHERE Name = 'Apple'), 'AirPods Pro', 'Tai nghe không dây với công nghệ chống ồn chủ động.'),
((SELECT CategoryID FROM Categories WHERE Name = 'Phụ kiện laptop'), (SELECT BrandID FROM Brands WHERE Name = 'Microsoft'), 'Surface Pen', 'Bút cảm ứng dành cho Surface.'),
((SELECT CategoryID FROM Categories WHERE Name = 'Thiết bị đeo thông minh'), (SELECT BrandID FROM Brands WHERE Name = 'Apple'), 'Apple Watch Series 7', 'Đồng hồ thông minh với nhiều tính năng sức khỏe.'),
((SELECT CategoryID FROM Categories WHERE Name = 'Tivi'), (SELECT BrandID FROM Brands WHERE Name = 'Samsung'), 'Samsung QLED 55', 'Tivi 55 inch với công nghệ QLED và độ phân giải 4K.'),
((SELECT CategoryID FROM Categories WHERE Name = 'Thiết bị mạng'), (SELECT BrandID FROM Brands WHERE Name = 'Asus'), 'Asus RT-AX88U', 'Router Wi-Fi 6 hiệu năng cao.');

-- Thêm dữ liệu mẫu cho bảng ProductVersions
INSERT INTO ProductVersions (ProductID, VersionName, CostPrice, Price, Weight, Height, Length, Width, Status, Image) VALUES
((SELECT ProductID FROM Products WHERE Name = 'iPhone 13'), '128GB - Đen', 19000000, 22000000, 173, 7.65, 14.67, 0.73, 'Active', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/Product%2Fcanon_eosr8.jpeg?alt=media&token=388f3aae-04e1-436f-a900-a32dd50ff633'),
((SELECT ProductID FROM Products WHERE Name = 'Samsung Galaxy S21'), '256GB - Trắng', 15000000, 18000000, 200, 7.9, 15.5, 0.7, 'Active', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/Product%2Fcanon_eosr8.jpeg?alt=media&token=388f3aae-04e1-436f-a900-a32dd50ff633'),
((SELECT ProductID FROM Products WHERE Name = 'Dell XPS 13'), '16GB RAM - 512GB SSD', 30000000, 35000000, 1400, 1.48, 30.1, 19.9, 'Active', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/Product%2Fcanon_eosr8.jpeg?alt=media&token=388f3aae-04e1-436f-a900-a32dd50ff633'),
((SELECT ProductID FROM Products WHERE Name = 'MacBook Pro 14'), '16GB RAM - 1TB SSD', 50000000, 55000000, 1600, 1.6, 31.3, 22.2, 'Active', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/Product%2Fcanon_eosr8.jpeg?alt=media&token=388f3aae-04e1-436f-a900-a32dd50ff633'),
((SELECT ProductID FROM Products WHERE Name = 'iPad Pro 11'), '128GB - Xám', 20000000, 23000000, 468, 0.61, 24.81, 17.95, 'Active', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/Product%2Fcanon_eosr8.jpeg?alt=media&token=388f3aae-04e1-436f-a900-a32dd50ff633'),
((SELECT ProductID FROM Products WHERE Name = 'AirPods Pro'), 'AirPods Pro', 5000000, 6000000, 56, 5.4, 4.5, 2.5, 'Active', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/Product%2Fcanon_eosr8.jpeg?alt=media&token=388f3aae-04e1-436f-a900-a32dd50ff633'),
((SELECT ProductID FROM Products WHERE Name = 'Surface Pen'), 'Bút cảm ứng - Đen', 2000000, 2500000, 20, 0.6, 14, 1.5, 'Active', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/Product%2Fcanon_eosr8.jpeg?alt=media&token=388f3aae-04e1-436f-a900-a32dd50ff633'),
((SELECT ProductID FROM Products WHERE Name = 'Apple Watch Series 7'), '44mm - Xanh', 12000000, 14000000, 100, 1.1, 4.5, 3.3, 'Active', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/Product%2Fcanon_eosr8.jpeg?alt=media&token=388f3aae-04e1-436f-a900-a32dd50ff633'),
((SELECT ProductID FROM Products WHERE Name = 'Samsung QLED 55'), 'QLED 55 inch', 15000000, 18000000, 21000, 7.9, 123.2, 72.6, 'Active', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/Product%2Fcanon_eosr8.jpeg?alt=media&token=388f3aae-04e1-436f-a900-a32dd50ff633'),
((SELECT ProductID FROM Products WHERE Name = 'Asus RT-AX88U'), 'Router Wi-Fi 6', 4000000, 4500000, 960, 3.1, 25, 15, 'Active', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/Product%2Fcanon_eosr8.jpeg?alt=media&token=388f3aae-04e1-436f-a900-a32dd50ff633');


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
INSERT INTO Promotions (Name,StartDate, EndDate, Poster) VALUES
('Giảm giá mùa hè', '2024-06-01', '2024-06-30', 'https://example.com/posters/summer_sale.png'),
('Black Friday', '2024-11-25', '2024-11-28', 'https://example.com/posters/black_friday.png'),
('Tết Nguyên Đán', '2024-01-15', '2024-02-15', 'https://example.com/posters/lunar_new_year.png'),
('Giảm giá Noel', '2024-12-20', '2024-12-25', 'https://example.com/posters/christmas_sale.png'),
('Ngày của Mẹ', '2024-05-10', '2024-05-14', 'https://example.com/posters/mothers_day.png'),
('Ngày Quốc Khánh', '2024-09-01', '2024-09-03', 'https://example.com/posters/national_day.png'),
('Mùa tựu trường', '2024-08-15', '2024-09-15', 'https://example.com/posters/back_to_school.png'),
('Cyber Monday', '2024-11-29', '2024-11-29', 'https://example.com/posters/cyber_monday.png'),
('Valentine\'s Day', '2024-02-10', '2024-02-14', 'https://example.com/posters/valentines_day.png'),
('Ngày Quốc tế Phụ nữ', '2024-03-07', '2024-03-08', 'https://example.com/posters/womens_day.png');

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
INSERT INTO Users (Username, Fullname, Password, Phone, Email, Avatar, active, forgetPassword) VALUES
('user01', 'Nguyen Van A', 'AItAAtqZ+MHVTXQtCBOxSTN1Pe/DDIqz', '0987654321', 'user01@example.com', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/User%2Ftong-hop-25-hinh-anh-meme-ech-xanh-hai-huoc_3.jpg?alt=media&token=1b5cccab-9eeb-44cd-85a2-f2e6c262d06a', TRUE, FALSE),
('user02', 'Le Thi B', 'AItAAtqZ+MHVTXQtCBOxSTN1Pe/DDIqz', '0987654322', 'user02@example.com', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/User%2Ftong-hop-25-hinh-anh-meme-ech-xanh-hai-huoc_3.jpg?alt=media&token=1b5cccab-9eeb-44cd-85a2-f2e6c262d06a', TRUE, FALSE),
('user03', 'Tran Van C', 'AItAAtqZ+MHVTXQtCBOxSTN1Pe/DDIqz', '0987654323', 'user03@example.com', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/User%2Ftong-hop-25-hinh-anh-meme-ech-xanh-hai-huoc_3.jpg?alt=media&token=1b5cccab-9eeb-44cd-85a2-f2e6c262d06a', TRUE, FALSE),
('user04', 'Pham Thi D', 'AItAAtqZ+MHVTXQtCBOxSTN1Pe/DDIqz', '0987654324', 'user04@example.com', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/User%2Ftong-hop-25-hinh-anh-meme-ech-xanh-hai-huoc_3.jpg?alt=media&token=1b5cccab-9eeb-44cd-85a2-f2e6c262d06a', TRUE, FALSE),
('user05', 'Hoang Van E', 'AItAAtqZ+MHVTXQtCBOxSTN1Pe/DDIqz', '0987654325', 'user05@example.com', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/User%2Ftong-hop-25-hinh-anh-meme-ech-xanh-hai-huoc_3.jpg?alt=media&token=1b5cccab-9eeb-44cd-85a2-f2e6c262d06a', TRUE, FALSE),
('user06', 'Vu Thi F', 'AItAAtqZ+MHVTXQtCBOxSTN1Pe/DDIqz', '0987654326', 'user06@example.com', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/User%2Ftong-hop-25-hinh-anh-meme-ech-xanh-hai-huoc_3.jpg?alt=media&token=1b5cccab-9eeb-44cd-85a2-f2e6c262d06a', TRUE, FALSE),
('user07', 'Nguyen Van G', 'AItAAtqZ+MHVTXQtCBOxSTN1Pe/DDIqz', '0987654327', 'user07@example.com', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/User%2Ftong-hop-25-hinh-anh-meme-ech-xanh-hai-huoc_3.jpg?alt=media&token=1b5cccab-9eeb-44cd-85a2-f2e6c262d06a', TRUE, FALSE),
('user08', 'Le Thi H', 'AItAAtqZ+MHVTXQtCBOxSTN1Pe/DDIqz', '0987654328', 'user08@example.com', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/User%2Ftong-hop-25-hinh-anh-meme-ech-xanh-hai-huoc_3.jpg?alt=media&token=1b5cccab-9eeb-44cd-85a2-f2e6c262d06a', TRUE, FALSE),
('user09', 'Tran Van I', 'AItAAtqZ+MHVTXQtCBOxSTN1Pe/DDIqz', '0987654329', 'user09@example.com', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/User%2Ftong-hop-25-hinh-anh-meme-ech-xanh-hai-huoc_3.jpg?alt=media&token=1b5cccab-9eeb-44cd-85a2-f2e6c262d06a', TRUE, FALSE),
('user10', 'Pham Thi J', 'AItAAtqZ+MHVTXQtCBOxSTN1Pe/DDIqz', '0987654330', 'user10@example.com', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/User%2Ftong-hop-25-hinh-anh-meme-ech-xanh-hai-huoc_3.jpg?alt=media&token=1b5cccab-9eeb-44cd-85a2-f2e6c262d06a', TRUE, FALSE);

-- Thêm dữ liệu mẫu cho bảng Vouchers
INSERT INTO Vouchers (VoucherCode, LeastBill, LeastDiscount, BiggestDiscount, DiscountLevel, StartDate, EndDate) VALUES
('SUMMER2024', 500000, 50000, 100000, 10, '2024-06-01', '2024-06-30'),
('BLACKFRIDAY', 1000000, 100000, 200000, 20, '2024-11-25', '2024-11-28'),
('TET2024', 800000, 80000, 150000, 15, '2024-01-15', '2024-02-15'),
('XMAS2024', 700000, 70000, 140000, 25, '2024-12-20', '2024-12-25'),
('MOTHERDAY', 600000, 60000, 120000, 5, '2024-05-10', '2024-05-14');

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
INSERT INTO Orders (UserID, VoucherID, VoucherDiscount, OrderDate, ShipFee, TotalMoney, CodValue, InsuranceValue, ServiceTypeID, OrderAddress, OrderPhone, OrderName) VALUES
((SELECT UserID FROM Users WHERE Username = 'user01'), (SELECT VoucherID FROM Vouchers WHERE VoucherCode = 'SUMMER2024'),10000, '2024-06-05', 60000, 590000, 0, 0, 1, '123 Phúc Xá', '0987654321', 'Nguyen Van A'),
((SELECT UserID FROM Users WHERE Username = 'user02'), (SELECT VoucherID FROM Vouchers WHERE VoucherCode = 'BLACKFRIDAY'),50000, '2024-10-26', 70000, 1220000, 100000, 0, 1, '456 Cầu Ông Lãnh', '0987654322', 'Le Thi B'),
((SELECT UserID FROM Users WHERE Username = 'user03'), (SELECT VoucherID FROM Vouchers WHERE VoucherCode = 'TET2024'),20000, '2024-02-01', 50000, 840000, 0, 50000, 2, '789 Phạm Đình Hổ', '0987654323', 'Tran Van C'),
((SELECT UserID FROM Users WHERE Username = 'user04'), (SELECT VoucherID FROM Vouchers WHERE VoucherCode = 'XMAS2024'),10000, '2024-10-21', 40000, 630000, 200000, 0, 1, '321 Bình Thạnh', '0987654324', 'Pham Thi D'),
((SELECT UserID FROM Users WHERE Username = 'user05'), (SELECT VoucherID FROM Vouchers WHERE VoucherCode = 'MOTHERSDAY'),10000, '2024-05-11', 60000, 750000, 0, 0, 1, '654 Vĩnh Phúc', '0987654325', 'Hoang Van E');

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
 (SELECT ProductVersionID FROM ProductVersions WHERE VersionName = '128GB - Xám'), 1, 700000, 700000);

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
INSERT INTO Entries (EntryDate, TotalMoney)
VALUES
('2024-07-01', 10000000),
('2024-08-01', 5000000),
('2024-09-01', 2000000),
('2024-10-01', 3000000),
('2024-10-02', 15000000);

-- Thêm dữ liệu mẫu cho bảng EntryDetails
INSERT INTO EntryDetails (EntryID, ProductVersionID, Quantity, Price)
VALUES
((SELECT EntryID FROM Entries WHERE EntryDate = '2024-07-01'), 
 (SELECT ProductVersionID FROM ProductVersions WHERE VersionName = '128GB - Đen'), 10, 600000),
((SELECT EntryID FROM Entries WHERE EntryDate = '2024-08-01'), 
 (SELECT ProductVersionID FROM ProductVersions WHERE VersionName = '256GB - Trắng'), 5, 1500000),
((SELECT EntryID FROM Entries WHERE EntryDate = '2024-09-01'), 
 (SELECT ProductVersionID FROM ProductVersions WHERE VersionName = '16GB RAM - 512GB SSD'), 2, 900000),
((SELECT EntryID FROM Entries WHERE EntryDate = '2024-10-01'), 
 (SELECT ProductVersionID FROM ProductVersions WHERE VersionName = '16GB RAM - 1TB SSD'), 3, 800000),
((SELECT EntryID FROM Entries WHERE EntryDate = '2024-10-02'), 
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

-- Insert data into Notifications
INSERT INTO Notifications (Title, Content, Type, NotificationDate, Status) VALUES
('Khuyến mãi mùa hè', 'Giảm giá đến 50% cho tất cả các sản phẩm!', 'Gửi tự động', '2024-06-01 08:00:00', 'Sent'),
('Black Friday', 'Giảm giá sốc 70% trong ngày Black Friday!', 'Gửi tự động', '2024-11-25 09:00:00', 'Scheduled'),
('Tết 2024', 'Mua sắm thả ga với khuyến mãi Tết 2024!', 'Gửi tự động', '2024-01-15 07:00:00', 'Sent'),
('Giáng sinh 2024', 'Ưu đãi lớn cho mùa Giáng sinh năm nay!', 'Gửi tự động', '2024-12-20 10:00:00', 'Sent'),
('Ngày của mẹ', 'Món quà tuyệt vời dành cho mẹ nhân ngày của mẹ!', 'Gửi tự động', '2024-05-10 08:30:00', 'Scheduled');

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
INSERT INTO Modules (ModuleName) VALUES 
    ('Quản lý xác thực'),
    ('Quản lý thông báo'),
    ('Quản lý đơn hàng'),
    ('Quản lý nhập hàng'),
    ('Quản lý người dùng'),
    ('Quản lý thuộc tính'),
    ('Quản lý thương hiệu'),
    ('Quản lý danh mục'),
    ('Quản lý sản phẩm'),
    ('Quản lý phiên bản sản phẩm'),
    ('Quản lý khuyến mãi'),
    ('Quản lý chi tiết khuyến mãi'),
    ('Quản lý sản phẩm trong khuyến mãi'),
    ('Quản lý đánh giá'),
    ('Quản lý giỏ hàng'),
    ('Quản lý voucher'),
    ('Quản lý quyền'),
    ('Thống kê báo cáo');

-- Chèn dữ liệu vào bảng Permissions với trường Code
INSERT INTO Permissions (ModuleID, PermissionName, Code) VALUES 
    -- Quản lý xác thực (AUTH)
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý xác thực'), 'Đăng nhập', 'login'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý xác thực'), 'Đăng xuất', 'logout'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý xác thực'), 'Đăng ký', 'register'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý xác thực'), 'Xác thực', 'verify'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý xác thực'), 'Đăng nhập bằng Google', 'login/google'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý xác thực'), 'Đổi mật khẩu', 'change-password'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý xác thực'), 'Quên mật khẩu', 'forgot-password'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý xác thực'), 'Đặt lại mật khẩu', 'reset-password'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý xác thực'), 'Xác thực token', 'token/validate'),

    -- Quản lý thông báo (NOTIFICATION)
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý thông báo'), 'Xem tất cả thông báo', 'view_all_notifications'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý thông báo'), 'Gửi thông báo', 'send_notifications'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý thông báo'), 'Đánh dấu thông báo đã đọc', 'notifications/markAsRead'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý thông báo'), 'Đánh dấu tất cả thông báo đã đọc', 'notifications/markAllAsRead'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý thông báo'), 'Xóa thông báo', 'notifications/delete'),

    -- Quản lý đơn hàng (ORDER)
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Xem tất cả đơn hàng', 'view_all_orders'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Thêm đơn hàng mới', 'orders/create'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Xem chi tiết đơn hàng', 'orders/{id}/details'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Hủy đơn hàng', 'orders/cancel'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Xác nhận thanh toán', 'orders/mark-as-paid'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Đang giao hàng', 'orders/mark-as-shipping'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Đã giao hàng', 'orders/mark-as-delivered'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Xác nhận đơn hàng', 'orders/mark-as-confirmed'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Đang chờ xử lý', 'orders/mark-as-pending'),

    -- Quản lý đơn hàng (ENTRIES)
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý nhập hàng'), 'Xem tất cả đơn nhập', 'view_all_entries'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý nhập hàng'), 'Thêm đơn nhập mới', 'add_new_entries'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý nhập hàng'), 'Cập nhật đơn nhập', 'update_entries'),

    -- Quản lý thuộc tính (ATTRIBUTE)
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý thuộc tính'), 'Xem tất cả thuộc tính', 'view_attributes'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý thuộc tính'), 'Thêm thuộc tính mới', 'add_new_attribute'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý thuộc tính'), 'Cập nhật thuộc tính mới', 'update_attribute'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý thuộc tính'), 'Thêm mới thuộc tính', 'add_attribute_value'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý thuộc tính'), 'Cập nhật thuộc tính', 'update_attribute_value'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý thuộc tính'), 'Xóa giá trị thuộc tính', 'delete_attribute_value'),    
    
    -- Quản lý thương hiệu (BRAND)
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý thương hiệu'), 'Xem tất cả thương hiệu', 'view_all_brands'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý thương hiệu'), 'Thêm thương hiệu mới', 'add_new_brand'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý thương hiệu'), 'Cập nhật thương hiệu', 'update_brand'),
	((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý thương hiệu'), 'Xóa thương hiệu', 'delete_brand'),
    
    -- Quản lý danh mục (CATEGORY)
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý danh mục'), 'Xem tất cả danh mục', 'view_all_categories'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý danh mục'), 'Thêm danh mục mới', 'add_new_category'),
	((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý danh mục'), 'Xóa danh mục', 'delete_category'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý danh mục'), 'Cập nhật danh mục', 'update_category'),
    
    -- Quản lý người dùng (USER)
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý người dùng'), 'Xem tất cả người dùng', 'view_all_users'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý người dùng'), 'Thêm người dùng mới', 'add_new_user'),
	((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý người dùng'), 'Xóa người dùng', 'delete_user'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý người dùng'), 'Cập nhật người dùngc', 'update_user'),
    
    -- Quản lý sản phẩm (PRODUCT)
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý sản phẩm'), 'Xem tất cả sản phẩm', 'view_all_products'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý sản phẩm'), 'Thêm sản phẩm mới', 'add_new_product'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý sản phẩm'), 'Chỉnh sửa sản phẩm', 'edit_product'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý sản phẩm'), 'Xóa sản phẩm', 'delete_product'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý sản phẩm'), 'Quản lý phiên bản sản phẩm', 'manage_product_versions'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý sản phẩm'), 'Nhập hàng loạt sản phẩm', 'bulk_import_products'),
    
    -- Quản lý phiên bản sản phẩm (PRODUCT VERSION)
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý phiên bản sản phẩm'), 'Xem tất cả phiên bản sản phẩm', 'view_all_product_versions'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý phiên bản sản phẩm'), 'Thêm phiên bản sản phẩm mới', 'add_new_product_version'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý phiên bản sản phẩm'), 'Xóa phiên bản sản phẩm', 'delete_product_version'),
    
    
    -- Quản lý khuyến mãi (PROMOTIONS)
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý khuyến mãi'), 'Xem danh sách khuyến mãi', 'view_promotions_list'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý khuyến mãi'), 'Kích hoạt khuyến mãi', 'activate_promotions'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý khuyến mãi'), 'Cập nhật khuyến mãi', 'update_promotion'),
	((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý khuyến mãi'), 'Thêm mới khuyến mãi', 'add_new_promotion'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý khuyến mãi'), 'Cập nhật khuyến mãi', 'search_promotions'),
    
    -- Quản lý chi tiết khuyến mãi (PROMOTION DETAILS)
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý chi tiết khuyến mãi'), 'Thêm chi tiết khuyến mãi', 'add_new_promotion_details'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý chi tiết khuyến mãi'), 'Xem chi tiết khuyến mãi', 'view_promotion_details'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý chi tiết khuyến mãi'), 'Cập nhật chi tiết khuyến mãi', 'update_promotion_details'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý chi tiết khuyến mãi'), 'Xóa chi tiết khuyến mãi', 'delete_promotion_details'),
    
    -- Quản lý sản phẩm trong khuyến mãi (PROMOTION PRODUCT)
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý sản phẩm trong khuyến mãi'), 'Xem tất cả sản phẩm khuyến mãi', 'view_all_promotion_products'),
    
    -- Quản lý đánh giá (RATING)
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý đánh giá'), 'Xem đánh giá', 'view_reviews'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý đánh giá'), 'Xóa đánh giá', 'delete_reviews'),
    
    -- Quản lý giỏ hàng (CART)
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý giỏ hàng'), 'Xem giỏ hàng', 'view_cart'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý giỏ hàng'), 'Thêm sản phẩm vào giỏ hàng', 'add_product_to_cart'),
    
    -- Quản lý voucher (VOUCHER)
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý voucher'), 'Quản lý voucher', 'manage_voucher'),
    
    -- Quản lý quyền (ROLE)
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý quyền'), 'Xem tất cả quyền', 'view_all_roles'),
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Quản lý quyền'), 'Thêm quyền mới', 'add_new_role'),
    
    -- Thống kê báo cáo (REPORTING)
    ((SELECT ModuleID FROM Modules WHERE ModuleName = 'Thống kê báo cáo'), 'Xem báo cáo', 'view_reports');


INSERT INTO Roles (RoleName) VALUES
    ('SuperAdmin'),
    ('Admin'),
    ('Nhân viên'),
    ('Quản lý'),
    ('Nhân viên hỗ trợ');

-- Thêm dữ liệu mẫu cho bảng UserRoles
INSERT INTO UserRoles (user_id, role_id) VALUES
    ((SELECT UserID FROM Users WHERE Username like 'user01'), 
     (SELECT Role_ID FROM Roles WHERE RoleName like 'SuperAdmin')),
    ((SELECT UserID FROM Users WHERE Username like 'user02'), 
     (SELECT Role_ID FROM Roles WHERE RoleName like 'Nhân viên')),
    ((SELECT UserID FROM Users WHERE Username like 'user03'), 
     (SELECT Role_ID FROM Roles WHERE RoleName like 'Quản lý')),
    ((SELECT UserID FROM Users WHERE Username like 'user04'), 
     (SELECT Role_ID FROM Roles WHERE RoleName like 'Admin')),
    ((SELECT UserID FROM Users WHERE Username like 'user05'), 
     (SELECT Role_ID FROM Roles WHERE RoleName like 'Nhân viên'));

-- Thêm tất cả quyền cho SuperAdmin
INSERT INTO PermissionRole (PermissionID, RoleID)
SELECT PermissionID, (SELECT Role_ID FROM Roles WHERE RoleName LIKE 'SuperAdmin')
FROM Permissions;

INSERT INTO OrderStatusType (StatusID, Name) VALUES
(-1, 'Đã hủy'),
(1, 'Chờ xác nhận'),
(2, 'Chờ thanh toán'),
(3, 'Đã thanh toán'),
(4, 'Đã xác nhận'),
(5, 'Đang giao hàng'),
(6, 'Đã giao hàng');

INSERT INTO OrderStatus (OrderID, StatusID, Time)
SELECT OrderID, 1, NOW() FROM Orders;
