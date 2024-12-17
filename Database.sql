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
('Thiết bị đeo thông minh');

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
    ('Quản lý nhân viên'),
    ('Quản lý khách hàng'),
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
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thông báo'), 'Xem tất cả thông báo', 'view_notification'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thông báo'), 'Gửi thông báo', 'send_notifications'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thông báo'), 'Đánh dấu thông báo đã đọc', 'notifications/markAsRead'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thông báo'), 'Đánh dấu tất cả thông báo đã đọc', 'notifications/markAllAsRead'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thông báo'), 'Xóa thông báo', 'notifications/delete'),

    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Xem tất cả đơn hàng', 'view_order'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Xem chi tiết đơn hàng', 'view_order'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Hủy đơn hàng', 'cancel_order'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Xác nhận thanh toán', 'mark-as-paid_order'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Đang giao hàng', 'mark-as-shipping_order'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý đơn hàng'), 'Xác nhận đơn hàng', 'mark-as-confirmed_order'),

    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý nhập hàng'), 'Xem đơn nhập', 'view_entry'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý nhập hàng'), 'Thêm đơn nhập mới', 'add_new_entry'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý nhập hàng'), 'Cập nhật đơn nhập', 'update_entry'),

    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thuộc tính'), 'Xem tất cả thuộc tính', 'view_attribute'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thuộc tính'), 'Thêm thuộc tính mới', 'add_attribute'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thuộc tính'), 'Cập nhật thuộc tính mới', 'update_attribute'),
	((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thương hiệu'), 'Xóa thương hiệu', 'delete_attribute'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thương hiệu'), 'Xem tất cả thương hiệu', 'view_brand'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thương hiệu'), 'Thêm thương hiệu mới', 'add_brand'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thương hiệu'), 'Cập nhật thương hiệu', 'update_brand'),
	((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý thương hiệu'), 'Xóa thương hiệu', 'delete_brand'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý danh mục'), 'Xem tất cả danh mục', 'view_category'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý danh mục'), 'Thêm danh mục mới', 'add_category'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý danh mục'), 'Cập nhật danh mục', 'update_category'),
	((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý danh mục'), 'Xóa danh mục', 'delete_category'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý nhân viên'), 'Xem tất cả nhân viên', 'view_employee'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý nhân viên'), 'Thêm người nhân viên', 'add_employee'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý nhân viên'), 'Cập nhật nhân viên', 'update_employee'),
    
	((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý khách hàng'), 'Xem tất cả khách hàng', 'view_customer'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý khách hàng'), 'Thêm người khách hàng', 'add_customer'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý khách hàng'), 'Cập nhật khách hàng', 'update_customer'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý sản phẩm'), 'Xem sản phẩm', 'view_product'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý sản phẩm'), 'Thêm sản phẩm mới', 'add_product'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý sản phẩm'), 'Chỉnh sửa sản phẩm', 'edit_product'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý sản phẩm'), 'Xóa sản phẩm', 'delete_product'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý phiên bản sản phẩm'), 'Xem phiên bản sản phẩm', 'view_product_version'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý phiên bản sản phẩm'), 'Thêm phiên bản sản phẩm mới', 'add_product_version'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý phiên bản sản phẩm'), 'Xóa phiên bản sản phẩm', 'delete_product_version'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý phiên bản sản phẩm'), 'Cập nhật phiên bản sản phẩm mới', 'update_product_version'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý khuyến mãi'), 'Xem danh sách khuyến mãi', 'view_promotion'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý khuyến mãi'), 'Kích hoạt khuyến mãi', 'activate_promotion'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý khuyến mãi'), 'Cập nhật khuyến mãi', 'update_promotion'),
	((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý khuyến mãi'), 'Thêm mới khuyến mãi', 'add_new_promotion'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý khuyến mãi'), 'Cập nhật khuyến mãi', 'search_promotions'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý khuyến mãi'), 'Xóa khuyến mãi', 'delete_promotion'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý chi tiết khuyến mãi'), 'Thêm chi tiết khuyến mãi', 'add__promotion_detail'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý chi tiết khuyến mãi'), 'Xem chi tiết khuyến mãi', 'view_promotion_detail'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý chi tiết khuyến mãi'), 'Cập nhật chi tiết khuyến mãi', 'update_promotion_detail'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý chi tiết khuyến mãi'), 'Xóa chi tiết khuyến mãi', 'delete_promotion_detail'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý sản phẩm trong khuyến mãi'), 'Xem tất cả sản phẩm khuyến mãi', 'view_promotion_product'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý đánh giá'), 'Xem đánh giá', 'view_review'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý đánh giá'), 'Xóa đánh giá', 'delete_review'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý voucher'), 'Quản lý voucher', 'manage_voucher'),    
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý quyền'), 'Xem quyền', 'view_role'),
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Quản lý quyền'), 'Thêm quyền mới', 'add_new_role'),
    
    ((SELECT ModuleID FROM modules WHERE ModuleName = 'Thống kê báo cáo'), 'Xem báo cáo', 'view_report');


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

DELIMITER $$

CREATE PROCEDURE getstatistics(
    IN startdate DATETIME,
    IN enddate DATETIME
)
BEGIN
    -- Thống kê tổng nhập kho (entry) và tổng lượt bán (orderdetails)
    SELECT
        p.name AS productname,
        pv.versionname AS productversion,
        IFNULL(SUM(ed.quantity), 0) AS totalimport,
        IFNULL(SUM(od.quantity), 0) AS totalsales,
        IFNULL(SUM(od.quantity * od.price), 0) AS totalrevenue
    FROM products p
    JOIN productversions pv ON pv.productID = p.productID
    LEFT JOIN entrydetails ed ON ed.productVersionID = pv.productVersionID
    LEFT JOIN orders o ON o.orderdate BETWEEN startdate AND enddate
    LEFT JOIN orderdetails od ON o.orderID = od.orderID AND od.productVersionID = pv.productVersionID
    LEFT JOIN orderstatus os ON os.orderID = o.orderID
    WHERE os.statusID = 6  -- Trạng thái "Đã giao hàng"
    GROUP BY p.name, pv.versionname;

    -- Thống kê doanh thu theo danh mục sản phẩm (productcategories)
    SELECT
        c.name AS categoryname,
        IFNULL(SUM(od.quantity * od.price), 0) AS totalrevenue,
        IFNULL(SUM(od.quantity * od.price) / (SELECT SUM(od2.quantity * od2.price) 
                                              FROM orderdetails od2
                                              JOIN orders o2 ON o2.orderID = od2.orderID
                                              LEFT JOIN orderstatus os2 ON os2.orderID = o2.orderID
                                              WHERE o2.orderdate BETWEEN startdate AND enddate
                                                AND os2.statusID = 6) * 100, 0) AS percentage
    FROM orderdetails od
    JOIN productversions pv ON pv.productVersionID = od.productVersionID
    JOIN products p ON p.productID = pv.productID
    JOIN categories c ON c.categoryID = p.categoryID
    JOIN orders o ON o.orderID = od.orderID
    LEFT JOIN orderstatus os ON os.orderID = o.orderID
    WHERE o.orderdate BETWEEN startdate AND enddate
      AND os.statusID = 6  -- Trạng thái "Đã giao hàng"
    GROUP BY c.name
    HAVING totalrevenue > 0;  -- Chỉ hiển thị những danh mục có doanh thu

	-- Thống kê sản phẩm không bán được (không có đơn hàng liên quan)
	SELECT DISTINCT
		p.name AS productname,
		pv.versionname AS productversion
	FROM products p
	JOIN productversions pv ON pv.productID = p.productID
	LEFT JOIN orderdetails od ON od.productVersionID = pv.productVersionID
	LEFT JOIN orders o ON o.orderID = od.orderID
	WHERE od.orderDetailID IS NULL;  -- Chỉ những sản phẩm không có trong bất kỳ đơn hàng nào

END$$

DELIMITER ;

CALL GetStatistics('2024-10-01 00:00:00', '2024-12-31 23:59:59');




