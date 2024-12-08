DROP DATABASE IF EXISTS EndlessEcommerce;

CREATE DATABASE EndlessEcommerce;
USE EndlessEcommerce;
CREATE TABLE brands (
    BrandID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Name VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL,
    Logo LONGTEXT CHARACTER SET utf8mb4,
    CreateDate DATETIME DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE categories (
    CategoryID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Name VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL,
    CreateDate DATETIME DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE attributes (
    AttributeID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    AttributeName VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL,
    CreateDate DATETIME DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE attributevalues (
    AttributeValueID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    AttributeID CHAR(36) NOT NULL,
    Value VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL,
    FOREIGN KEY (AttributeID) REFERENCES attributes(AttributeID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE products (
    ProductID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    CategoryID CHAR(36) NOT NULL,
    BrandID CHAR(36) NOT NULL,
    Name VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL,
    Description TEXT CHARACTER SET utf8mb4,
    CreateDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (CategoryID) REFERENCES categories(CategoryID),
    FOREIGN KEY (BrandID) REFERENCES brands(BrandID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE productversions (
    ProductVersionID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    ProductID CHAR(36) NOT NULL,
    VersionName VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL,
    CostPrice DECIMAL(18, 2) NOT NULL,
    Price DECIMAL(18, 2) NOT NULL,
    Weight DECIMAL(18, 2) NOT NULL,
    Height DECIMAL(18, 2) NOT NULL,
    Length DECIMAL(18, 2) NOT NULL, 
    Width DECIMAL(18, 2) NOT NULL,  
    Status VARCHAR(50) CHARACTER SET utf8mb4 NOT NULL,
    Image TEXT CHARACTER SET utf8mb4,
    CreateDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ProductID) REFERENCES products(ProductID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE versionattributes (
    VersionAttributeID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    ProductVersionID CHAR(36) NOT NULL,
    AttributeValueID CHAR(36) NOT NULL,
    CreateDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ProductVersionID) REFERENCES productversions(ProductVersionID),
    FOREIGN KEY (AttributeValueID) REFERENCES attributevalues(AttributeValueID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE promotions (
    PromotionID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    Name VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL,
    StartDate DATETIME NOT NULL,
    EndDate DATETIME NOT NULL,
    PercentDiscount INT NOT NULL,
    Poster LONGTEXT CHARACTER SET utf8mb4,
    Active BOOLEAN DEFAULT TRUE,
    CreateDate DATETIME DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE promotionproducts (
    PromotionProductID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    PromotionID CHAR(36) NOT NULL,
    ProductVersionID CHAR(36) NOT NULL,
    FOREIGN KEY (ProductVersionID) REFERENCES productversions(ProductVersionID),
    FOREIGN KEY (PromotionID) REFERENCES promotions(PromotionID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE TABLE users (
    userID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    username VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL,
    fullname VARCHAR(255) CHARACTER SET utf8mb4,
    password VARCHAR(255) CHARACTER SET utf8mb4,
    phone VARCHAR(11) CHARACTER SET utf8mb4,
    email VARCHAR(255) CHARACTER SET utf8mb4,
    avatar TEXT CHARACTER SET utf8mb4,
    active BOOLEAN DEFAULT TRUE,
    forgetPassword BOOLEAN DEFAULT FALSE,
    token TEXT CHARACTER SET utf8mb4,
    createDate DATETIME DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE useraddresses (
    addressID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    userID CHAR(36) NOT NULL,
    provinceID INT NOT NULL,
    provinceName VARCHAR(50) CHARACTER SET utf8mb4 NOT NULL,
    districtID INT NOT NULL,
    districtName VARCHAR(50) CHARACTER SET utf8mb4 NOT NULL,
    wardCode INT NOT NULL,
    wardName VARCHAR(50) CHARACTER SET utf8mb4 NOT NULL,
    detailAddress TEXT CHARACTER SET utf8mb4 NOT NULL,
    createDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userID) REFERENCES users(userID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE vouchers (
    voucherID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    voucherCode VARCHAR(50) CHARACTER SET utf8mb4 NOT NULL UNIQUE,
    leastBill DECIMAL(18, 2) NOT NULL,
    leastDiscount DECIMAL(18, 2) NOT NULL,
    biggestDiscount DECIMAL(18, 2) NOT NULL,
    discountLevel INT NOT NULL,
    startDate DATETIME NOT NULL,
    endDate DATETIME NOT NULL,
    createDate DATETIME DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE uservouchers (
    userVoucherID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    userID CHAR(36) NOT NULL,
    voucherID CHAR(36) NOT NULL,
    status BIT NOT NULL DEFAULT b'1',
    FOREIGN KEY (userID) REFERENCES users(userID),
    FOREIGN KEY (voucherID) REFERENCES vouchers(voucherID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE notifications (
    notificationID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    notificationDate DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE notificationrecipients (
    notificationRecipientID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    notificationID CHAR(36) NOT NULL,
    userID CHAR(36) NOT NULL,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (notificationID) REFERENCES notifications(notificationID),
    FOREIGN KEY (userID) REFERENCES users(userID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE orders (
    orderID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    userID CHAR(36) NOT NULL,
    voucherID CHAR(36),
    orderDate DATETIME NOT NULL,
    shipFee DECIMAL(18, 2) NOT NULL,
    voucherDiscount DECIMAL(18, 2) DEFAULT 0,
    totalMoney DECIMAL(18, 2) NOT NULL,
    codValue DECIMAL(18, 2) DEFAULT 0,
    insuranceValue DECIMAL(18, 2) DEFAULT 0,
    serviceTypeID INT NOT NULL,
    orderAddress TEXT CHARACTER SET utf8mb4,
    orderPhone VARCHAR(15) CHARACTER SET utf8mb4,
    orderName VARCHAR(255) CHARACTER SET utf8mb4,
    FOREIGN KEY (userID) REFERENCES users(userID),
    FOREIGN KEY (voucherID) REFERENCES vouchers(voucherID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE orderdetails (
    orderDetailID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    orderID CHAR(36) NOT NULL,
    productVersionID CHAR(36) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(18, 2) NOT NULL,
    discountPrice DECIMAL(18, 2) NOT NULL,
    FOREIGN KEY (orderID) REFERENCES orders(orderID),
    FOREIGN KEY (productVersionID) REFERENCES productversions(productVersionID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE ratings (
    ratingID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    userID CHAR(36) NOT NULL,
    orderDetailID CHAR(36) NOT NULL,
    ratingValue INT CHECK (ratingValue >= 1 AND ratingValue <= 5),
    comment TEXT CHARACTER SET utf8mb4,
    ratingDate DATETIME NOT NULL,
    FOREIGN KEY (userID) REFERENCES users(userID),
    FOREIGN KEY (orderDetailID) REFERENCES orderdetails(orderDetailID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE ratingpictures (
    pictureID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    ratingID CHAR(36) NOT NULL,
    picture LONGTEXT CHARACTER SET utf8mb4,
    FOREIGN KEY (ratingID) REFERENCES ratings(ratingID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE entries (
    entryID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    entryDate DATETIME NOT NULL,
    totalMoney DECIMAL(18, 2) NOT NULL
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE entrydetails (
    entryDetailID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    entryID CHAR(36) NOT NULL,
    productVersionID CHAR(36) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(18, 2) NOT NULL,
    FOREIGN KEY (entryID) REFERENCES entries(entryID),
    FOREIGN KEY (productVersionID) REFERENCES productversions(productVersionID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE carts (
    cartID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    userID CHAR(36) NOT NULL,
    productVersionID CHAR(36) NOT NULL,
    quantity INT NOT NULL,
    createDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userID) REFERENCES users(userID),
    FOREIGN KEY (productVersionID) REFERENCES productversions(productVersionID),
    UNIQUE (userID, productVersionID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE roles(
    Role_ID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    RoleName VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE userroles(
    Userrole_ID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id CHAR(36) NOT NULL,
    role_Id CHAR(36) NOT NULL,
    FOREIGN KEY (role_Id) REFERENCES roles(Role_ID),
    FOREIGN KEY (user_id) REFERENCES users(UserID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE modules (
    moduleID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    moduleName VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL,
    description VARCHAR(255) CHARACTER SET utf8mb4
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE permissions (
    permissionID CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    moduleID CHAR(36) NOT NULL,
    code VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL,
    permissionName VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL,
    FOREIGN KEY (moduleID) REFERENCES modules(moduleID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE permissionrole (
    PermissionID CHAR(36) NOT NULL,
    RoleID CHAR(36) NOT NULL,
    PRIMARY KEY (PermissionID, RoleID),
    FOREIGN KEY (PermissionID) REFERENCES permissions(PermissionID),
    FOREIGN KEY (RoleID) REFERENCES roles(Role_ID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE orderstatustype (
    statusID INT PRIMARY KEY,
    name VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE orderstatus (
    orderID CHAR(36) NOT NULL,
    statusID INT NOT NULL,
    time DATETIME NOT NULL,
    PRIMARY KEY (orderID, statusID),
    FOREIGN KEY (orderID) REFERENCES orders(orderID),
    FOREIGN KEY (statusID) REFERENCES orderstatustype(statusID)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


INSERT INTO brands (Name, Logo) VALUES
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

INSERT INTO categories (Name) VALUES
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

INSERT INTO attributes (AttributeName) VALUES
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

INSERT INTO attributevalues (AttributeID, Value) VALUES
((SELECT AttributeID FROM attributes WHERE AttributeName = 'Màu sắc'), 'Đen'),
((SELECT AttributeID FROM attributes WHERE AttributeName = 'Màu sắc'), 'Trắng'),
((SELECT AttributeID FROM attributes WHERE AttributeName = 'Kích thước màn hình'), '6.1 inch'),
((SELECT AttributeID FROM attributes WHERE AttributeName = 'Kích thước màn hình'), '15.6 inch'),
((SELECT AttributeID FROM attributes WHERE AttributeName = 'Bộ nhớ trong'), '128GB'),
((SELECT AttributeID FROM attributes WHERE AttributeName = 'Bộ nhớ trong'), '512GB'),
((SELECT AttributeID FROM attributes WHERE AttributeName = 'RAM'), '8GB'),
((SELECT AttributeID FROM attributes WHERE AttributeName = 'RAM'), '16GB'),
((SELECT AttributeID FROM attributes WHERE AttributeName = 'CPU'), 'Intel Core i7'),
((SELECT AttributeID FROM attributes WHERE AttributeName = 'CPU'), 'Apple M1');

INSERT INTO products (CategoryID, BrandID, Name, Description) VALUES
((SELECT CategoryID FROM categories WHERE Name = 'Điện thoại'), (SELECT BrandID FROM brands WHERE Name = 'Apple'), 'iPhone 13', 'Điện thoại thông minh với chip A15 Bionic.'),
((SELECT CategoryID FROM categories WHERE Name = 'Điện thoại'), (SELECT BrandID FROM brands WHERE Name = 'Samsung'), 'Samsung Galaxy S21', 'Điện thoại với màn hình 6.2 inch và camera 64MP.'),
((SELECT CategoryID FROM categories WHERE Name = 'Laptop'), (SELECT BrandID FROM brands WHERE Name = 'Dell'), 'Dell XPS 13', 'Laptop cao cấp với màn hình 13.3 inch và CPU Intel Core i7.'),
((SELECT CategoryID FROM categories WHERE Name = 'Laptop'), (SELECT BrandID FROM brands WHERE Name = 'Apple'), 'MacBook Pro 14', 'Laptop với chip Apple M1 và màn hình Retina.'),
((SELECT CategoryID FROM categories WHERE Name = 'Máy tính bảng'), (SELECT BrandID FROM brands WHERE Name = 'Apple'), 'iPad Pro 11', 'Máy tính bảng với màn hình 11 inch và chip M1.'),
((SELECT CategoryID FROM categories WHERE Name = 'Phụ kiện điện thoại'), (SELECT BrandID FROM brands WHERE Name = 'Apple'), 'AirPods Pro', 'Tai nghe không dây với công nghệ chống ồn chủ động.'),
((SELECT CategoryID FROM categories WHERE Name = 'Phụ kiện laptop'), (SELECT BrandID FROM brands WHERE Name = 'Microsoft'), 'Surface Pen', 'Bút cảm ứng dành cho Surface.'),
((SELECT CategoryID FROM categories WHERE Name = 'Thiết bị đeo thông minh'), (SELECT BrandID FROM brands WHERE Name = 'Apple'), 'Apple Watch Series 7', 'Đồng hồ thông minh với nhiều tính năng sức khỏe.'),
((SELECT CategoryID FROM categories WHERE Name = 'Tivi'), (SELECT BrandID FROM brands WHERE Name = 'Samsung'), 'Samsung QLED 55', 'Tivi 55 inch với công nghệ QLED và độ phân giải 4K.'),
((SELECT CategoryID FROM categories WHERE Name = 'Thiết bị mạng'), (SELECT BrandID FROM brands WHERE Name = 'Asus'), 'Asus RT-AX88U', 'Router Wi-Fi 6 hiệu năng cao.');

INSERT INTO productversions (ProductID, VersionName, CostPrice, Price, Weight, Height, Length, Width, Status, Image) VALUES
((SELECT ProductID FROM products WHERE Name = 'iPhone 13'), '128GB - Đen', 19000000, 22000000, 173, 7.65, 14.67, 0.73, 'Active', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/Product%2Fcanon_eosr8.jpeg?alt=media&token=388f3aae-04e1-436f-a900-a32dd50ff633'),
((SELECT ProductID FROM products WHERE Name = 'Samsung Galaxy S21'), '256GB - Trắng', 15000000, 18000000, 200, 7.9, 15.5, 0.7, 'Active', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/Product%2Fcanon_eosr8.jpeg?alt=media&token=388f3aae-04e1-436f-a900-a32dd50ff633'),
((SELECT ProductID FROM products WHERE Name = 'Dell XPS 13'), '16GB RAM - 512GB SSD', 30000000, 35000000, 1400, 1.48, 30.1, 19.9, 'Active', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/Product%2Fcanon_eosr8.jpeg?alt=media&token=388f3aae-04e1-436f-a900-a32dd50ff633'),
((SELECT ProductID FROM products WHERE Name = 'MacBook Pro 14'), '16GB RAM - 1TB SSD', 50000000, 55000000, 1600, 1.6, 31.3, 22.2, 'Active', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/Product%2Fcanon_eosr8.jpeg?alt=media&token=388f3aae-04e1-436f-a900-a32dd50ff633'),
((SELECT ProductID FROM products WHERE Name = 'iPad Pro 11'), '128GB - Xám', 20000000, 23000000, 468, 0.61, 24.81, 17.95, 'Active', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/Product%2Fcanon_eosr8.jpeg?alt=media&token=388f3aae-04e1-436f-a900-a32dd50ff633'),
((SELECT ProductID FROM products WHERE Name = 'AirPods Pro'), 'AirPods Pro', 5000000, 6000000, 56, 5.4, 4.5, 2.5, 'Active', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/Product%2Fcanon_eosr8.jpeg?alt=media&token=388f3aae-04e1-436f-a900-a32dd50ff633'),
((SELECT ProductID FROM products WHERE Name = 'Surface Pen'), 'Bút cảm ứng - Đen', 2000000, 2500000, 20, 0.6, 14, 1.5, 'Active', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/Product%2Fcanon_eosr8.jpeg?alt=media&token=388f3aae-04e1-436f-a900-a32dd50ff633'),
((SELECT ProductID FROM products WHERE Name = 'Apple Watch Series 7'), '44mm - Xanh', 12000000, 14000000, 100, 1.1, 4.5, 3.3, 'Active', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/Product%2Fcanon_eosr8.jpeg?alt=media&token=388f3aae-04e1-436f-a900-a32dd50ff633'),
((SELECT ProductID FROM products WHERE Name = 'Samsung QLED 55'), 'QLED 55 inch', 15000000, 18000000, 21000, 7.9, 123.2, 72.6, 'Active', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/Product%2Fcanon_eosr8.jpeg?alt=media&token=388f3aae-04e1-436f-a900-a32dd50ff633'),
((SELECT ProductID FROM products WHERE Name = 'Asus RT-AX88U'), 'Router Wi-Fi 6', 4000000, 4500000, 960, 3.1, 25, 15, 'Active', 'https://firebasestorage.googleapis.com/v0/b/endlesstechstoreecommerce.appspot.com/o/Product%2Fcanon_eosr8.jpeg?alt=media&token=388f3aae-04e1-436f-a900-a32dd50ff633');


INSERT INTO versionattributes (ProductVersionID, AttributeValueID)
SELECT pv.ProductVersionID, av.AttributeValueID
FROM productversions pv
JOIN attributevalues av ON (av.Value = '128GB' AND pv.VersionName = '128GB - Đen') OR
                           (av.Value = 'Đen' AND pv.VersionName = '128GB - Đen') OR
                           (av.Value = '256GB' AND pv.VersionName = '256GB - Trắng') OR
                           (av.Value = 'Trắng' AND pv.VersionName = '256GB - Trắng') OR
                           (av.Value = '16GB' AND pv.VersionName = '16GB RAM - 512GB SSD') OR
                           (av.Value = '512GB' AND pv.VersionName = '16GB RAM - 512GB SSD') OR
                           (av.Value = '1TB' AND pv.VersionName = '16GB RAM - 1TB SSD') OR
                           (av.Value = '16GB' AND pv.VersionName = '16GB RAM - 1TB SSD') OR
                           (av.Value = '128GB' AND pv.VersionName = '128GB - Xám') OR
                           (av.Value = 'Xám' AND pv.VersionName = '128GB - Xám');


INSERT INTO promotions (Name, StartDate, EndDate, PercentDiscount) VALUES
('Giảm giá mùa hè', '2024-06-01', '2024-06-30', 10),
('Black Friday', '2024-11-29', '2024-11-29', 20),
('Tết Nguyên Đán', '2025-01-01', '2025-01-31', 15),
('Giảm giá Noel', '2024-12-24', '2024-12-25', 25),
('Ngày của Mẹ', '2024-05-12', '2024-05-12', 5),
('Ngày Quốc Khánh', '2024-09-02', '2024-09-02', 10),
('Mùa tựu trường', '2024-08-01', '2024-08-31', 15),
('Cyber Monday', '2024-12-02', '2024-12-02', 30),
('Valentine\'s Day', '2024-02-14', '2024-02-14', 20),
('Ngày Quốc tế Phụ nữ', '2024-03-08', '2024-03-08', 15);

INSERT INTO promotionproducts (PromotionID, ProductVersionID)
SELECT 
    (SELECT PromotionID FROM promotions WHERE Name = 'Giảm giá mùa hè') AS PromotionID,
    (SELECT ProductVersionID FROM productversions WHERE VersionName = '128GB - Đen') AS ProductVersionID
UNION ALL
SELECT 
    (SELECT PromotionID FROM promotions WHERE Name = 'Black Friday') AS PromotionID,
    (SELECT ProductVersionID FROM productversions WHERE VersionName = '256GB - Trắng') AS ProductVersionID
UNION ALL
SELECT 
    (SELECT PromotionID FROM promotions WHERE Name = 'Tết Nguyên Đán') AS PromotionID,
    (SELECT ProductVersionID FROM productversions WHERE VersionName = '16GB RAM - 512GB SSD') AS ProductVersionID
UNION ALL
SELECT 
    (SELECT PromotionID FROM promotions WHERE Name = 'Giảm giá Noel') AS PromotionID,
    (SELECT ProductVersionID FROM productversions WHERE VersionName = '16GB RAM - 1TB SSD') AS ProductVersionID
UNION ALL
SELECT 
    (SELECT PromotionID FROM promotions WHERE Name = 'Ngày của Mẹ') AS PromotionID,
    (SELECT ProductVersionID FROM productversions WHERE VersionName = '128GB - Xám') AS ProductVersionID
UNION ALL
SELECT 
    (SELECT PromotionID FROM promotions WHERE Name = 'Ngày Quốc Khánh') AS PromotionID,
    (SELECT ProductVersionID FROM productversions WHERE VersionName = 'AirPods Pro') AS ProductVersionID
UNION ALL
SELECT 
    (SELECT PromotionID FROM promotions WHERE Name = 'Mùa tựu trường') AS PromotionID,
    (SELECT ProductVersionID FROM productversions WHERE VersionName = 'Bút cảm ứng - Đen') AS ProductVersionID
UNION ALL
SELECT 
    (SELECT PromotionID FROM promotions WHERE Name = 'Cyber Monday') AS PromotionID,
    (SELECT ProductVersionID FROM productversions WHERE VersionName = '44mm - Xanh') AS ProductVersionID
UNION ALL
SELECT 
    (SELECT PromotionID FROM promotions WHERE Name = 'Valentine''s Day') AS PromotionID,
    (SELECT ProductVersionID FROM productversions WHERE VersionName = 'QLED 55 inch') AS ProductVersionID
UNION ALL
SELECT 
    (SELECT PromotionID FROM promotions WHERE Name = 'Ngày Quốc tế Phụ nữ') AS PromotionID,
    (SELECT ProductVersionID FROM productversions WHERE VersionName = 'Router Wi-Fi 6') AS ProductVersionID;



INSERT INTO users (Username, Fullname, Password, Phone, Email, Avatar, active, forgetPassword) VALUES
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

INSERT INTO vouchers (VoucherCode, LeastBill, LeastDiscount, BiggestDiscount, DiscountLevel, StartDate, EndDate) VALUES
('SUMMER2024', 500000, 50000, 100000, 10, '2024-06-01', '2024-06-30'),
('BLACKFRIDAY', 1000000, 100000, 200000, 20, '2024-11-25', '2024-11-28'),
('TET2024', 800000, 80000, 150000, 15, '2024-01-15', '2024-02-15'),
('XMAS2024', 700000, 70000, 140000, 25, '2024-12-20', '2024-12-25'),
('MOTHERDAY', 600000, 60000, 120000, 5, '2024-05-10', '2024-05-14');

INSERT INTO uservouchers (UserID, VoucherID) VALUES
((SELECT UserID FROM users WHERE Username = 'user01'), 
 (SELECT VoucherID FROM vouchers WHERE VoucherCode = 'SUMMER2024')),
((SELECT UserID FROM users WHERE Username = 'user02'), 
 (SELECT VoucherID FROM vouchers WHERE VoucherCode = 'BLACKFRIDAY')),
((SELECT UserID FROM users WHERE Username = 'user03'), 
 (SELECT VoucherID FROM vouchers WHERE VoucherCode = 'TET2024')),
((SELECT UserID FROM users WHERE Username = 'user04'), 
 (SELECT VoucherID FROM vouchers WHERE VoucherCode = 'XMAS2024')),
((SELECT UserID FROM users WHERE Username = 'user05'), 
 (SELECT VoucherID FROM vouchers WHERE VoucherCode = 'MOTHERDAY'));

INSERT INTO orders (UserID, VoucherID, VoucherDiscount, OrderDate, ShipFee, TotalMoney, CodValue, InsuranceValue, ServiceTypeID, OrderAddress, OrderPhone, OrderName) VALUES
((SELECT UserID FROM users WHERE Username = 'user01'), (SELECT VoucherID FROM vouchers WHERE VoucherCode = 'SUMMER2024'),10000, '2024-06-05', 60000, 590000, 0, 0, 1, '123 Phúc Xá', '0987654321', 'Nguyen Van A'),
((SELECT UserID FROM users WHERE Username = 'user02'), (SELECT VoucherID FROM vouchers WHERE VoucherCode = 'BLACKFRIDAY'),50000, '2024-10-26', 70000, 1220000, 100000, 0, 1, '456 Cầu Ông Lãnh', '0987654322', 'Le Thi B'),
((SELECT UserID FROM users WHERE Username = 'user03'), (SELECT VoucherID FROM vouchers WHERE VoucherCode = 'TET2024'),20000, '2024-02-01', 50000, 840000, 0, 50000, 2, '789 Phạm Đình Hổ', '0987654323', 'Tran Van C'),
((SELECT UserID FROM users WHERE Username = 'user04'), (SELECT VoucherID FROM vouchers WHERE VoucherCode = 'XMAS2024'),10000, '2024-10-21', 40000, 630000, 200000, 0, 1, '321 Bình Thạnh', '0987654324', 'Pham Thi D'),
((SELECT UserID FROM users WHERE Username = 'user05'), (SELECT VoucherID FROM vouchers WHERE VoucherCode = 'MOTHERSDAY'),10000, '2024-05-11', 60000, 750000, 0, 0, 1, '654 Vĩnh Phúc', '0987654325', 'Hoang Van E');

INSERT INTO orderdetails (OrderID, ProductVersionID, Quantity, Price, DiscountPrice) VALUES
((SELECT OrderID FROM orders WHERE OrderName = 'Nguyen Van A'), 
 (SELECT ProductVersionID FROM productversions WHERE VersionName = '128GB - Đen'), 1, 600000, 540000),
((SELECT OrderID FROM orders WHERE OrderName = 'Le Thi B'), 
 (SELECT ProductVersionID FROM productversions WHERE VersionName = '256GB - Trắng'), 1, 1500000, 1200000),
((SELECT OrderID FROM orders WHERE OrderName = 'Tran Van C'), 
 (SELECT ProductVersionID FROM productversions WHERE VersionName = '16GB RAM - 512GB SSD'), 1, 900000, 810000),
((SELECT OrderID FROM orders WHERE OrderName = 'Pham Thi D'), 
 (SELECT ProductVersionID FROM productversions WHERE VersionName = '16GB RAM - 1TB SSD'), 1, 800000, 600000),
((SELECT OrderID FROM orders WHERE OrderName = 'Hoang Van E'), 
 (SELECT ProductVersionID FROM productversions WHERE VersionName = '128GB - Xám'), 1, 700000, 700000);

INSERT INTO ratings (UserID, OrderDetailID, RatingValue, Comment, RatingDate) VALUES
((SELECT UserID FROM users WHERE Username = 'user01'), 
 (SELECT OrderDetailID FROM orderdetails WHERE Price = 600000), 5, 'Sản phẩm rất tốt!', '2024-06-06 14:00:00'),
((SELECT UserID FROM users WHERE Username = 'user02'), 
 (SELECT OrderDetailID FROM orderdetails WHERE Price = 1500000), 4, 'Hài lòng với sản phẩm.', '2024-11-27 09:30:00'),
((SELECT UserID FROM users WHERE Username = 'user03'), 
 (SELECT OrderDetailID FROM orderdetails WHERE Price = 900000), 3, 'Sản phẩm tạm ổn.', '2024-02-02 16:45:00'),
((SELECT UserID FROM users WHERE Username = 'user04'), 
 (SELECT OrderDetailID FROM orderdetails WHERE Price = 800000), 2, 'Không hài lòng với chất lượng.', '2024-12-22 10:15:00'),
((SELECT UserID FROM users WHERE Username = 'user05'), 
 (SELECT OrderDetailID FROM orderdetails WHERE Price = 700000), 1, 'Rất thất vọng về sản phẩm.', '2024-05-12 11:20:00');

INSERT INTO ratingpictures (RatingID, Picture) VALUES
((SELECT RatingID FROM ratings WHERE Comment = 'Sản phẩm rất tốt!'), 'image_good1.png'),
((SELECT RatingID FROM ratings WHERE Comment = 'Hài lòng với sản phẩm.'), 'image_good2.png'),
((SELECT RatingID FROM ratings WHERE Comment = 'Sản phẩm tạm ổn.'), 'image_okay1.png'),
((SELECT RatingID FROM ratings WHERE Comment = 'Không hài lòng với chất lượng.'), 'image_bad1.png'),
((SELECT RatingID FROM ratings WHERE Comment = 'Rất thất vọng về sản phẩm.'), 'image_bad2.png');

INSERT INTO entries (EntryDate, TotalMoney)
VALUES
('2024-07-01', 10000000),
('2024-08-01', 5000000),
('2024-09-01', 2000000),
('2024-10-01', 3000000),
('2024-10-02', 15000000);

INSERT INTO entrydetails (EntryID, ProductVersionID, Quantity, Price)
VALUES
((SELECT EntryID FROM entries WHERE EntryDate = '2024-07-01'), 
 (SELECT ProductVersionID FROM productversions WHERE VersionName = '128GB - Đen'), 10, 600000),
((SELECT EntryID FROM entries WHERE EntryDate = '2024-08-01'), 
 (SELECT ProductVersionID FROM productversions WHERE VersionName = '256GB - Trắng'), 5, 1500000),
((SELECT EntryID FROM entries WHERE EntryDate = '2024-09-01'), 
 (SELECT ProductVersionID FROM productversions WHERE VersionName = '16GB RAM - 512GB SSD'), 2, 900000),
((SELECT EntryID FROM entries WHERE EntryDate = '2024-10-01'), 
 (SELECT ProductVersionID FROM productversions WHERE VersionName = '16GB RAM - 1TB SSD'), 3, 800000),
((SELECT EntryID FROM entries WHERE EntryDate = '2024-10-02'), 
 (SELECT ProductVersionID FROM productversions WHERE VersionName = '128GB - Xám'), 15, 700000);

INSERT INTO carts (UserID, ProductVersionID, Quantity) VALUES
((SELECT UserID FROM users WHERE Username = 'user01'), 
 (SELECT ProductVersionID FROM productversions WHERE VersionName = '128GB - Đen'), 1),
((SELECT UserID FROM users WHERE Username = 'user02'), 
 (SELECT ProductVersionID FROM productversions WHERE VersionName = '256GB - Trắng'), 2),
((SELECT UserID FROM users WHERE Username = 'user03'), 
 (SELECT ProductVersionID FROM productversions WHERE VersionName = '16GB RAM - 512GB SSD'), 1),
((SELECT UserID FROM users WHERE Username = 'user04'), 
 (SELECT ProductVersionID FROM productversions WHERE VersionName = '16GB RAM - 1TB SSD'), 3),
((SELECT UserID FROM users WHERE Username = 'user05'), 
 (SELECT ProductVersionID FROM productversions WHERE VersionName = '128GB - Xám'), 1);

INSERT INTO notifications (Title, Content, Type, NotificationDate, Status) VALUES
('Khuyến mãi mùa hè', 'Giảm giá đến 50% cho tất cả các sản phẩm!', 'Gửi tự động', '2024-06-01 08:00:00', 'Sent'),
('Black Friday', 'Giảm giá sốc 70% trong ngày Black Friday!', 'Gửi tự động', '2024-11-25 09:00:00', 'Scheduled'),
('Tết 2024', 'Mua sắm thả ga với khuyến mãi Tết 2024!', 'Gửi tự động', '2024-01-15 07:00:00', 'Sent'),
('Giáng sinh 2024', 'Ưu đãi lớn cho mùa Giáng sinh năm nay!', 'Gửi tự động', '2024-12-20 10:00:00', 'Sent'),
('Ngày của mẹ', 'Món quà tuyệt vời dành cho mẹ nhân ngày của mẹ!', 'Gửi tự động', '2024-05-10 08:30:00', 'Scheduled');

INSERT INTO notificationrecipients (NotificationID, UserID, Status) VALUES
((SELECT NotificationID FROM notifications WHERE Title = 'Khuyến mãi mùa hè'), 
 (SELECT UserID FROM users WHERE Username = 'user01'), 'Delivered'),
((SELECT NotificationID FROM notifications WHERE Title = 'Black Friday'), 
 (SELECT UserID FROM users WHERE Username = 'user02'), 'Pending'),
((SELECT NotificationID FROM notifications WHERE Title = 'Tết 2024'), 
 (SELECT UserID FROM users WHERE Username = 'user03'), 'Delivered'),
((SELECT NotificationID FROM notifications WHERE Title = 'Giáng sinh 2024'), 
 (SELECT UserID FROM users WHERE Username = 'user04'), 'Delivered'),
((SELECT NotificationID FROM notifications WHERE Title = 'Ngày của mẹ'), 
 (SELECT UserID FROM users WHERE Username = 'user05'), 'Pending');
 
INSERT INTO useraddresses (UserID, ProvinceID, ProvinceName, DistrictID, DistrictName, WardCode, WardName, DetailAddress) VALUES
((SELECT UserID FROM users WHERE Username = 'user01'), 1, 'ProvinceName1', 101, 'DistrictName1', '001', 'WardName1', '123 Main St'),
((SELECT UserID FROM users WHERE Username = 'user02'), 1, 'ProvinceName1', 102, 'DistrictName2', '002', 'WardName2', '456 Elm St'),
((SELECT UserID FROM users WHERE Username = 'user03'), 1, 'ProvinceName1', 103, 'DistrictName3', '003', 'WardName3', '789 Oak St'),
((SELECT UserID FROM users WHERE Username = 'user04'), 1, 'ProvinceName1', 104, 'DistrictName4', '004', 'WardName4', '101 Pine St'),
((SELECT UserID FROM users WHERE Username = 'user05'), 1, 'ProvinceName1', 105, 'DistrictName5', '005', 'WardName5', '202 Maple St');

INSERT INTO modules (ModuleName) VALUES 
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


INSERT INTO permissions (ModuleID, PermissionName, Code) VALUES 
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thông báo'), 'Xem tất cả thông báo', 'view_all_notifications'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thông báo'), 'Gửi thông báo', 'send_notifications'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thông báo'), 'Đánh dấu thông báo đã đọc', 'notifications/markAsRead'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thông báo'), 'Đánh dấu tất cả thông báo đã đọc', 'notifications/markAllAsRead'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thông báo'), 'Xóa thông báo', 'notifications/delete'),

    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Xem tất cả đơn hàng', 'view_all_orders'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Thêm đơn hàng mới', 'orders/create'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Xem chi tiết đơn hàng', 'orders/{id}/details'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Hủy đơn hàng', 'orders/cancel'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Xác nhận thanh toán', 'orders/mark-as-paid'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Đang giao hàng', 'orders/mark-as-shipping'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Đã giao hàng', 'orders/mark-as-delivered'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Xác nhận đơn hàng', 'orders/mark-as-confirmed'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Đang chờ xử lý', 'orders/mark-as-pending'),

    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý nhập hàng'), 'Xem tất cả đơn nhập', 'view_all_entries'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý nhập hàng'), 'Thêm đơn nhập mới', 'add_new_entries'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý nhập hàng'), 'Cập nhật đơn nhập', 'update_entries'),

    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thuộc tính'), 'Xem tất cả thuộc tính', 'view_attributes'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thuộc tính'), 'Thêm thuộc tính mới', 'add_new_attribute'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thuộc tính'), 'Cập nhật thuộc tính mới', 'update_attribute'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thuộc tính'), 'Thêm mới thuộc tính', 'add_attribute_value'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thuộc tính'), 'Cập nhật thuộc tính', 'update_attribute_value'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thuộc tính'), 'Xóa giá trị thuộc tính', 'delete_attribute_value'),    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thuộc tính'), 'Xóa thuộc tính mới', 'delete_attribute'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thương hiệu'), 'Xem tất cả thương hiệu', 'view_all_brands'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thương hiệu'), 'Thêm thương hiệu mới', 'add_new_brand'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thương hiệu'), 'Cập nhật thương hiệu', 'update_brand'),
	((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thương hiệu'), 'Xóa thương hiệu', 'delete_brand'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý danh mục'), 'Xem tất cả danh mục', 'view_all_categories'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý danh mục'), 'Thêm danh mục mới', 'add_new_category'),
	((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý danh mục'), 'Xóa danh mục', 'delete_category'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý danh mục'), 'Cập nhật danh mục', 'update_category'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý người dùng'), 'Xem tất cả người dùng', 'view_all_users'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý người dùng'), 'Thêm người dùng mới', 'add_new_user'),
	((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý người dùng'), 'Xóa người dùng', 'delete_user'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý người dùng'), 'Cập nhật người dùngc', 'update_user'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý sản phẩm'), 'Xem tất cả sản phẩm', 'view_all_products'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý sản phẩm'), 'Thêm sản phẩm mới', 'add_new_product'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý sản phẩm'), 'Chỉnh sửa sản phẩm', 'edit_product'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý sản phẩm'), 'Xóa sản phẩm', 'delete_product'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý sản phẩm'), 'Quản lý phiên bản sản phẩm', 'manage_product_versions'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý sản phẩm'), 'Nhập hàng loạt sản phẩm', 'bulk_import_products'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý phiên bản sản phẩm'), 'Xem tất cả phiên bản sản phẩm', 'view_all_product_versions'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý phiên bản sản phẩm'), 'Thêm phiên bản sản phẩm mới', 'add_new_product_version'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý phiên bản sản phẩm'), 'Xóa phiên bản sản phẩm', 'delete_product_version'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý phiên bản sản phẩm'), 'Cập nhật phiên bản sản phẩm mới', 'update_product_version'),
    
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý khuyến mãi'), 'Xem danh sách khuyến mãi', 'view_promotions_list'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý khuyến mãi'), 'Kích hoạt khuyến mãi', 'activate_promotions'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý khuyến mãi'), 'Cập nhật khuyến mãi', 'update_promotion'),
	((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý khuyến mãi'), 'Thêm mới khuyến mãi', 'add_new_promotion'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý khuyến mãi'), 'Cập nhật khuyến mãi', 'search_promotions'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý khuyến mãi'), 'Xóa khuyến mãi', 'delete_promotion'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý chi tiết khuyến mãi'), 'Thêm chi tiết khuyến mãi', 'add_new_promotion_details'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý chi tiết khuyến mãi'), 'Xem chi tiết khuyến mãi', 'view_promotion_details'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý chi tiết khuyến mãi'), 'Cập nhật chi tiết khuyến mãi', 'update_promotion_details'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý chi tiết khuyến mãi'), 'Xóa chi tiết khuyến mãi', 'delete_promotion_details'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý sản phẩm trong khuyến mãi'), 'Xem tất cả sản phẩm khuyến mãi', 'view_all_promotion_products'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý đánh giá'), 'Xem đánh giá', 'view_reviews'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý đánh giá'), 'Xóa đánh giá', 'delete_reviews'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý giỏ hàng'), 'Xem giỏ hàng', 'view_cart'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý giỏ hàng'), 'Thêm sản phẩm vào giỏ hàng', 'add_product_to_cart'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý voucher'), 'Quản lý voucher', 'manage_voucher'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý quyền'), 'Xem tất cả quyền', 'view_all_roles'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý quyền'), 'Thêm quyền mới', 'add_new_role'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Thống kê báo cáo'), 'Xem báo cáo', 'view_reports');


INSERT INTO roles(RoleName) VALUES
    ('SuperAdmin'),
    ('Admin'),
    ('Nhân viên'),
    ('Quản lý'),
    ('Nhân viên hỗ trợ');

INSERT INTO userroles(user_id, role_id) VALUES
    ((SELECT UserID FROM users WHERE Username like 'user01'), 
     (SELECT Role_ID FROM roles WHERE RoleName like 'SuperAdmin')),
    ((SELECT UserID FROM users WHERE Username like 'user02'), 
     (SELECT Role_ID FROM roles WHERE RoleName like 'Nhân viên')),
    ((SELECT UserID FROM users WHERE Username like 'user03'), 
     (SELECT Role_ID FROM roles WHERE RoleName like 'Quản lý')),
    ((SELECT UserID FROM users WHERE Username like 'user04'), 
     (SELECT Role_ID FROM roles WHERE RoleName like 'Admin')),
    ((SELECT UserID FROM users WHERE Username like 'user05'), 
     (SELECT Role_ID FROM roles WHERE RoleName like 'Nhân viên'));

INSERT INTO permissionrole (PermissionID, RoleID)
SELECT PermissionID, (SELECT Role_ID FROM roles WHERE RoleName LIKE 'SuperAdmin')
FROM permissions;

INSERT INTO orderstatustype (StatusID, Name) VALUES
(1, 'Chờ xác nhận'),
(2, 'Chờ thanh toán'),
(3, 'Đã thanh toán'),
(4, 'Đã xác nhận'),
(5, 'Đang giao hàng'),
(6, 'Đã giao hàng'),
(7, 'Đã hủy');

INSERT INTO orderstatus (OrderID, StatusID, Time)
SELECT OrderID, 1, '2024-6-6 05:55:13' FROM orders;