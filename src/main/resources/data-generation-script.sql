-- =============================================
-- E-COMMERCE DATABASE SAMPLE DATA GENERATION SCRIPT
-- =============================================

-- Disable foreign key checks and clear existing data
SET FOREIGN_KEY_CHECKS = 0;
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

TRUNCATE TABLE sales_statistics;
TRUNCATE TABLE messages;
TRUNCATE TABLE product_suggestions;
TRUNCATE TABLE product_images;
TRUNCATE TABLE reviews;
TRUNCATE TABLE order_items;
TRUNCATE TABLE orders;
TRUNCATE TABLE cart_items;
TRUNCATE TABLE carts;
TRUNCATE TABLE ship_info;
TRUNCATE TABLE products;
TRUNCATE TABLE categories;
TRUNCATE TABLE reasons;
TRUNCATE TABLE users;

-- Giữ tắt khóa ngoại cho đến khi chèn xong toàn bộ dữ liệu; sẽ bật lại ở cuối file

-- =============================================
-- 1. USERS TABLE - User accounts
-- =============================================
INSERT INTO users (username, password, email, full_name, phone, address, role, is_active, email_verified, created_at, updated_at) VALUES
-- Admin users
('admin', '1', 'admin@ecommerce.com', 'System Administrator', '0901234567', '123 ABC Street, District 1, HCMC', 'admin', true, true, NOW(), NOW()),
('admin2', '1', 'admin2@ecommerce.com', 'Administrator 2', '0901234568', '456 DEF Street, District 2, HCMC', 'admin', true, true, NOW(), NOW()),

-- Regular users
('user001', '1', 'john.doe@gmail.com', 'John Doe', '0912345678', '789 GHI Street, District 3, HCMC', 'user', true, true, NOW(), NOW()),
('user002', '1', 'jane.smith@gmail.com', 'Jane Smith', '0923456789', '321 JKL Street, District 4, HCMC', 'user', true, true, NOW(), NOW()),
('user003', '1', 'michael.johnson@gmail.com', 'Michael Johnson', '0934567890', '654 MNO Street, District 5, HCMC', 'user', true, true, NOW(), NOW()),
('user004', '1', 'sarah.wilson@gmail.com', 'Sarah Wilson', '0945678901', '987 PQR Street, District 6, HCMC', 'user', true, true, NOW(), NOW()),
('user005', '1', 'david.brown@gmail.com', 'David Brown', '0956789012', '147 STU Street, District 7, HCMC', 'user', true, true, NOW(), NOW()),
('user006', '1', 'emma.davis@gmail.com', 'Emma Davis', '0967890123', '258 VWX Street, District 8, HCMC', 'user', true, true, NOW(), NOW()),
('user007', '1', 'robert.miller@gmail.com', 'Robert Miller', '0978901234', '369 YZ Street, District 9, HCMC', 'user', true, true, NOW(), NOW()),
('user008', '1', 'lisa.garcia@gmail.com', 'Lisa Garcia', '0989012345', '741 ABC1 Street, District 10, HCMC', 'user', true, true, NOW(), NOW()),
('user009', '1', 'james.martinez@gmail.com', 'James Martinez', '0990123456', '852 DEF1 Street, District 11, HCMC', 'user', true, true, NOW(), NOW()),
('user010', '1', 'maria.rodriguez@gmail.com', 'Maria Rodriguez', '0901234560', '963 GHI1 Street, District 12, HCMC', 'user', true, true, NOW(), NOW());

-- =============================================
-- 2. CATEGORIES TABLE - Product categories
-- =============================================
INSERT INTO categories (name, description, parent_id, created_at, updated_at) VALUES
-- Parent categories
('Mobile & Accessories', 'Mobile phones and mobile accessories', NULL, NOW(), NOW()),
('Laptops & Computers', 'Laptops and computer devices', NULL, NOW(), NOW()),
('Fashion', 'Clothing, shoes and fashion accessories', NULL, NOW(), NOW()),
('Home & Living', 'Home appliances and lifestyle products', NULL, NOW(), NOW()),
('Books & Office', 'Books and office supplies', NULL, NOW(), NOW()),
('Sports & Travel', 'Sports equipment and travel accessories', NULL, NOW(), NOW()),

-- Sub categories
('Smartphones', 'Smartphones from various brands', 1, NOW(), NOW()),
('Headphones', 'Wired and wireless headphones', 1, NOW(), NOW()),
('Cases & Accessories', 'Phone cases, screen protectors and other accessories', 1, NOW(), NOW()),
('Gaming Laptops', 'Laptops designed for gaming', 2, NOW(), NOW()),
('Office Laptops', 'Laptops for office work', 2, NOW(), NOW()),
('Mouse & Keyboard', 'Computer input devices', 2, NOW(), NOW()),
('Men Clothing', 'Shirts, t-shirts for men', 3, NOW(), NOW()),
('Women Clothing', 'Shirts, t-shirts for women', 3, NOW(), NOW()),
('Men Shoes', 'Sports shoes, dress shoes for men', 3, NOW(), NOW()),
('Women Shoes', 'High heels, sports shoes for women', 3, NOW(), NOW());

-- =============================================
-- 3. PRODUCTS TABLE - Products
-- =============================================
INSERT INTO products (name, description, import_price, selling_price, stock_quantity, category_id, is_featured, is_deleted, created_at, updated_at) VALUES
-- Smartphones
('iPhone 15 Pro Max 256GB', 'iPhone 15 Pro Max with A17 Pro chip, 48MP camera, 6.7 inch display', 25000000.00, 31990000.00, 50, 7, true, false, NOW(), NOW()),
('Samsung Galaxy S24 Ultra', 'Galaxy S24 Ultra with S Pen, 200MP camera, Dynamic AMOLED 6.8 inch display', 22000000.00, 29990000.00, 45, 7, true, false, NOW(), NOW()),
('Xiaomi 14 Ultra', 'Xiaomi 14 Ultra with Leica camera, Snapdragon 8 Gen 3 chip', 18000000.00, 24990000.00, 60, 7, false, false, NOW(), NOW()),
('OPPO Find X7 Pro', 'OPPO Find X7 Pro with Hasselblad camera, Dimensity 9300 chip', 16000000.00, 21990000.00, 40, 7, false, false, NOW(), NOW()),

-- Headphones
('Apple AirPods Pro Gen 2', 'Apple wireless earbuds with active noise cancellation', 4500000.00, 6290000.00, 80, 8, true, false, NOW(), NOW()),
('Sony WH-1000XM5', 'Sony over-ear headphones with industry-leading noise cancellation', 6000000.00, 8990000.00, 30, 8, true, false, NOW(), NOW()),
('JBL Tune 760NC', 'JBL over-ear headphones with noise cancellation', 1500000.00, 2490000.00, 70, 8, false, false, NOW(), NOW()),

-- Gaming Laptops
('ASUS ROG Strix G15', 'ASUS gaming laptop with RTX 4060, AMD Ryzen 7', 18000000.00, 24990000.00, 25, 10, true, false, NOW(), NOW()),
('MSI Gaming GF63', 'MSI gaming laptop with GTX 1650, Intel Core i5', 12000000.00, 16990000.00, 30, 10, false, false, NOW(), NOW()),
('Acer Nitro 5', 'Acer gaming laptop with RTX 3050, AMD Ryzen 5', 14000000.00, 18990000.00, 35, 10, false, false, NOW(), NOW()),

-- Office Laptops
('MacBook Air M3 13 inch', 'MacBook Air with M3 chip, 8GB RAM, 256GB SSD', 24000000.00, 28990000.00, 40, 11, true, false, NOW(), NOW()),
('Dell XPS 13', 'Dell XPS 13 with Intel Core i7, 16GB RAM, 512GB SSD', 28000000.00, 35990000.00, 20, 11, true, false, NOW(), NOW()),
('HP Pavilion 15', 'HP Pavilion 15 with Intel Core i5, 8GB RAM, 256GB SSD', 13000000.00, 17990000.00, 50, 11, false, false, NOW(), NOW()),

-- Men Clothing
('Men Oxford Shirt', 'High quality men Oxford shirt, white color', 150000.00, 299000.00, 100, 13, false, false, NOW(), NOW()),
('Men Polo T-Shirt', 'Men polo t-shirt 100% cotton, multiple colors', 120000.00, 249000.00, 150, 13, false, false, NOW(), NOW()),
('Men Bomber Jacket', 'Men bomber jacket with street style design', 200000.00, 399000.00, 80, 13, false, false, NOW(), NOW()),

-- Men Shoes
('Nike Air Max Sneakers', 'Nike Air Max sports shoes for men', 1800000.00, 2990000.00, 60, 15, true, false, NOW(), NOW()),
('Men Oxford Leather Shoes', 'Black Oxford leather shoes for men, suitable for office', 800000.00, 1490000.00, 40, 15, false, false, NOW(), NOW()),
('Adidas Ultraboost Sneakers', 'Adidas Ultraboost 22 sneakers', 2200000.00, 3490000.00, 35, 15, true, false, NOW(), NOW()),

-- Home appliances
('Panasonic Rice Cooker 1.8L', 'Panasonic electric rice cooker 1.8L for 4-6 people', 800000.00, 1290000.00, 100, 4, false, false, NOW(), NOW()),
('Philips Blender', 'Philips blender 2L, 600W power', 1200000.00, 1890000.00, 70, 4, false, false, NOW(), NOW()),
('Non-stick Pan Set 3pcs', 'Set of 3 non-stick pans in different sizes', 600000.00, 999000.00, 90, 4, false, false, NOW(), NOW());

-- =============================================
-- 4. BẢNG PRODUCT_IMAGES - Hình ảnh sản phẩm
-- =============================================
INSERT INTO product_images (product_id, image_url, is_primary, created_at) VALUES
-- iPhone 15 Pro Max
(1, '/images/iphone-15-pro-max-1.jpg', true, NOW()),
(1, '/images/iphone-15-pro-max-2.jpg', false, NOW()),
(1, '/images/iphone-15-pro-max-3.jpg', false, NOW()),

-- Samsung Galaxy S24 Ultra
(2, '/images/samsung-s24-ultra-1.jpg', true, NOW()),
(2, '/images/samsung-s24-ultra-2.jpg', false, NOW()),
(2, '/images/samsung-s24-ultra- 3.jpg', false, NOW()),

-- AirPods Pro
(5, '/images/airpods-pro-1.jpg', true, NOW()),
(5, '/images/airpods-pro-2.jpg', false, NOW()),

-- MacBook Air M3
(11, '/images/macbook-air-m3-1.jpg', true, NOW()),
(11, '/images/macbook-air-m3-2.jpg', false, NOW()),
(11, '/images/macbook-air-m3-3.jpg', false, NOW()),

-- Nike Air Max
(17, '/images/nike-air-max-1.jpg', true, NOW()),
(17, '/images/nike-air-max-2.jpg', false, NOW());

-- =============================================
-- 5. REASONS TABLE - Cancel/Reject reasons
-- =============================================
INSERT INTO reasons (reason_type, description, required_detail, is_active) VALUES
('cancel', 'Customer changed mind', false, true),
('cancel', 'Product out of stock', false, true),
('cancel', 'Incorrect delivery address', true, true),
('cancel', 'Cannot contact customer', false, true),
('cancel', 'Other reason', true, true),
('reject', 'Product quality not acceptable', false, true),
('reject', 'Incorrect product information', true, true),
('reject', 'Violates sales policy', false, true),
('reject', 'Product already exists in system', false, true),
('reject', 'Category not suitable', true, true);

-- =============================================
-- 6. SHIP_INFO TABLE - Shipping information
-- =============================================
INSERT INTO ship_info (user_id, reciever, phone, address) VALUES
(3, 'John Doe', '0912345678', '789 GHI Street, District 3, HCMC'),
(3, 'John Doe', '0912345678', '100 Company Street, District 1, HCMC'),
(4, 'Jane Smith', '0923456789', '321 JKL Street, District 4, HCMC'),
(4, 'Jane Smith Husband', '0923456780', '321 JKL Street, District 4, HCMC'),
(5, 'Michael Johnson', '0934567890', '654 MNO Street, District 5, HCMC'),
(6, 'Sarah Wilson', '0945678901', '987 PQR Street, District 6, HCMC'),
(7, 'David Brown', '0956789012', '147 STU Street, District 7, HCMC'),
(8, 'Emma Davis', '0967890123', '258 VWX Street, District 8, HCMC'),
(9, 'Robert Miller', '0978901234', '369 YZ Street, District 9, HCMC'),
(10, 'Lisa Garcia', '0989012345', '741 ABC1 Street, District 10, HCMC');

-- =============================================
-- 7. BẢNG CARTS - Giỏ hàng
-- =============================================
INSERT INTO carts (user_id, created_at, updated_at) VALUES
(3, NOW(), NOW()),
(4, NOW(), NOW()),
(5, NOW(), NOW()),
(6, NOW(), NOW()),
(7, NOW(), NOW()),
(8, NOW(), NOW()),
(9, NOW(), NOW()),
(10, NOW(), NOW()),
(11, NOW(), NOW()),
(12, NOW(), NOW());

-- =============================================
-- 8. BẢNG CART_ITEMS - Sản phẩm trong giỏ hàng
-- =============================================
INSERT INTO cart_items (cart_id, product_id, quantity, created_at, updated_at) VALUES
-- Giỏ hàng của user 3
(1, 1, 1, NOW(), NOW()),
(1, 5, 2, NOW(), NOW()),
(1, 11, 1, NOW(), NOW()),

-- Giỏ hàng của user 4
(2, 2, 1, NOW(), NOW()),
(2, 6, 1, NOW(), NOW()),

-- Giỏ hàng của user 5
(3, 8, 1, NOW(), NOW()),
(3, 14, 2, NOW(), NOW()),

-- Giỏ hàng của user 6
(4, 17, 1, NOW(), NOW()),
(4, 19, 3, NOW(), NOW()),

-- Giỏ hàng của user 7
(5, 3, 1, NOW(), NOW()),
(5, 7, 1, NOW(), NOW()),

-- Giỏ hàng của user 8
(6, 12, 1, NOW(), NOW()),
(6, 18, 2, NOW(), NOW()),

-- Giỏ hàng của user 9
(7, 4, 1, NOW(), NOW()),
(7, 20, 1, NOW(), NOW()),

-- Giỏ hàng của user 10
(8, 9, 1, NOW(), NOW()),
(8, 15, 2, NOW(), NOW());

-- =============================================
-- 9. BẢNG ORDERS - Đơn hàng
-- =============================================
INSERT INTO orders (user_id, total_amount, info_id, payment_method, status, reason_id, reason_detailed, created_at, updated_at) VALUES
-- Đơn hàng đã giao thành công
(3, 31990000.00, 1, 'Chuyển khoản', 'DELIVERED', NULL, NULL, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
(4, 8990000.00, 3, 'Tiền mặt', 'DELIVERED', NULL, NULL, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(5, 24990000.00, 5, 'Thẻ tín dụng', 'DELIVERED', NULL, NULL, DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),

-- Đơn hàng đang giao
(6, 17990000.00, 6, 'Chuyển khoản', 'SHIPPED', NULL, NULL, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(7, 2990000.00, 7, 'Tiền mặt', 'SHIPPED', NULL, NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- Đơn hàng đã xác nhận
(8, 28990000.00, 8, 'Thẻ tín dụng', 'CONFIRMED', NULL, NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
(9, 18990000.00, 9, 'Chuyển khoản', 'CONFIRMED', NULL, NULL, NOW(), NOW()),

-- Đơn hàng chờ xử lý
(10, 1290000.00, 10, 'Tiền mặt', 'PENDING', NULL, NULL, NOW(), NOW()),
(11, 3490000.00, 1, 'Chuyển khoản', 'PENDING', NULL, NULL, NOW(), NOW()),

-- Đơn hàng bị hủy
(12, 16990000.00, 2, 'Thẻ tín dụng', 'CANCELLED', 1, NULL, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),

-- Đơn hàng bị từ chối
(3, 35990000.00, 1, 'Chuyển khoản', 'REJECTED', 6, 'Sản phẩm Dell XPS 13 tạm hết hàng do nhà cung cấp gặp vấn đề', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY));

-- =============================================
-- 10. BẢNG ORDER_ITEMS - Sản phẩm trong đơn hàng
-- =============================================
INSERT INTO order_items (order_id, product_id, quantity, price, created_at) VALUES
-- Đơn hàng 1 (user 3 - iPhone 15 Pro Max)
(1, 1, 1, 31990000.00, DATE_SUB(NOW(), INTERVAL 10 DAY)),

-- Đơn hàng 2 (user 4 - Sony WH-1000XM5)
(2, 6, 1, 8990000.00, DATE_SUB(NOW(), INTERVAL 8 DAY)),

-- Đơn hàng 3 (user 5 - ASUS ROG Strix G15)
(3, 8, 1, 24990000.00, DATE_SUB(NOW(), INTERVAL 15 DAY)),

-- Đơn hàng 4 (user 6 - HP Pavilion 15)
(4, 13, 1, 17990000.00, DATE_SUB(NOW(), INTERVAL 3 DAY)),

-- Đơn hàng 5 (user 7 - Nike Air Max)
(5, 17, 1, 2990000.00, DATE_SUB(NOW(), INTERVAL 2 DAY)),

-- Đơn hàng 6 (user 8 - MacBook Air M3)
(6, 11, 1, 28990000.00, DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- Đơn hàng 7 (user 9 - Acer Nitro 5)
(7, 10, 1, 18990000.00, NOW()),

-- Đơn hàng 8 (user 10 - Nồi cơm điện)
(8, 20, 1, 1290000.00, NOW()),

-- Đơn hàng 9 (user 11 - Adidas Ultraboost)
(9, 19, 1, 3490000.00, NOW()),

-- Đơn hàng 10 (user 12 - MSI Gaming bị hủy)
(10, 9, 1, 16990000.00, DATE_SUB(NOW(), INTERVAL 5 DAY)),

-- Đơn hàng 11 (user 3 - Dell XPS 13 bị từ chối)
(11, 12, 1, 35990000.00, DATE_SUB(NOW(), INTERVAL 6 DAY));

-- =============================================
-- 11. BẢNG REVIEWS - Đánh giá sản phẩm
-- =============================================
INSERT INTO reviews (product_id, user_id, rating, comment, created_at, updated_at) VALUES
-- Reviews cho iPhone 15 Pro Max
(1, 3, 5, 'Điện thoại tuyệt vời! Camera chụp ảnh rất đẹp, hiệu năng mượt mà. Đáng đồng tiền bát gạo.', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, 4, 4, 'Sản phẩm tốt nhưng giá hơi cao. Thiết kế đẹp, chất lượng build tốt.', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),

-- Reviews cho Samsung Galaxy S24 Ultra
(2, 5, 5, 'Galaxy S24 Ultra với S Pen rất tiện lợi cho việc ghi chú. Camera zoom 100x thật ấn tượng!', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 6, 4, 'Điện thoại tốt, pin trâu, camera chất lượng. Hệ điều hành OneUI mượt mà.', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),

-- Reviews cho Sony WH-1000XM5
(6, 4, 5, 'Tai nghe chống ồn tuyệt vời! Âm thanh trong trẻo, đeo thoải mái cả ngày.', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(6, 7, 4, 'Chất lượng âm thanh rất tốt, chống ồn hiệu quả. Giá hơi cao nhưng xứng đáng.', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- Reviews cho ASUS ROG Strix G15
(8, 5, 4, 'Laptop gaming mạnh mẽ, chơi game mượt. Thiết kế đẹp, tản nhiệt tốt.', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
(8, 8, 5, 'Hiệu năng tuyệt vời cho gaming! RTX 4060 chạy mọi game ở setting cao.', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),

-- Reviews cho MacBook Air M3
(11, 9, 5, 'MacBook Air M3 nhanh và tiết kiệm pin tuyệt vời. Thiết kế mỏng nhẹ, màn hình đẹp.', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
(11, 10, 4, 'Máy đẹp, hiệu năng tốt cho công việc văn phòng. Hệ điều hành macOS ổn định.', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),

-- Reviews cho Nike Air Max
(17, 7, 4, 'Giày đẹp, chất lượng tốt. Đi thoải mái, phù hợp cho thể thao và hàng ngày.', NOW(), NOW()),
(17, 11, 5, 'Nike Air Max luôn là sự lựa chọn tốt! Thiết kế đẹp, êm chân.', NOW(), NOW()),

-- Reviews cho Xiaomi 14 Ultra
(3, 12, 4, 'Camera Leica chụp ảnh rất đẹp, màu sắc tự nhiên. Hiệu năng mạnh mẽ.', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),

-- Reviews cho AirPods Pro Gen 2
(5, 3, 5, 'AirPods Pro Gen 2 chống ồn rất tốt. Kết nối nhanh với iPhone, âm thanh chất lượng cao.', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
(5, 8, 4, 'Tai nghe tốt, tính năng spatial audio thú vị. Hộp sạc nhỏ gọn tiện lợi.', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY));

-- =============================================
-- 12. BẢNG PRODUCT_SUGGESTIONS - Đề xuất sản phẩm
-- =============================================
INSERT INTO product_suggestions (user_id, product_name, description, suggested_category, status, created_at, updated_at) VALUES
(3, 'Apple Watch Series 9', 'Smartwatch Apple mới nhất với chip S9, tính năng theo dõi sức khỏe tiên tiến', 'Smartwatch & Wearable', 'PENDING', NOW(), NOW()),
(4, 'Samsung Galaxy Buds3 Pro', 'Tai nghe không dây Samsung với ANC nâng cao, âm thanh 360 độ', 'Tai nghe', 'APPROVED', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(5, 'iPad Pro M3 12.9 inch', 'Máy tính bảng Apple với chip M3, màn hình Liquid Retina XDR', 'Tablet', 'APPROVED', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(6, 'Google Pixel 8 Pro', 'Smartphone Google với AI photography, chip Tensor G3', 'Điện thoại thông minh', 'REJECTED', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(7, 'PlayStation 5 Slim', 'Console gaming Sony PS5 phiên bản Slim mới', 'Gaming Console', 'PENDING', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(8, 'Microsoft Surface Pro 9', '2-in-1 laptop tablet Microsoft với Windows 11', 'Laptop & Máy tính', 'APPROVED', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(9, 'Canon EOS R8', 'Máy ảnh mirrorless full-frame Canon cho nhiếp ảnh gia', 'Camera & Photography', 'PENDING', NOW(), NOW()),
(10, 'DJI Mini 4 Pro', 'Drone camera 4K với tính năng obstacle avoidance', 'Drone & UAV', 'PENDING', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(11, 'Nintendo Switch OLED', 'Console gaming di động Nintendo với màn hình OLED', 'Gaming Console', 'REJECTED', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(12, 'Tesla Model Y Accessories', 'Phụ kiện nội thất và ngoại thất cho Tesla Model Y', 'Ô tô & Xe máy', 'PENDING', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));

-- =============================================
-- 13. BẢNG MESSAGES - Tin nhắn
-- =============================================
INSERT INTO messages (sender_id, receiver_id, content, is_read, created_at) VALUES
-- Tin nhắn giữa admin và users
(1, 3, 'Xin chào! Cảm ơn bạn đã mua iPhone 15 Pro Max. Hy vọng bạn hài lòng với sản phẩm!', true, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(3, 1, 'Dạ, sản phẩm rất tốt ạ! Cảm ơn shop. Tôi muốn hỏi về bảo hành như thế nào?', true, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(1, 3, 'Sản phẩm Apple được bảo hành chính hãng 12 tháng tại các trung tâm bảo hành Apple toàn quốc.', false, DATE_SUB(NOW(), INTERVAL 3 DAY)),

(2, 4, 'Chào bạn! Đơn hàng Sony WH-1000XM5 của bạn đã được giao thành công. Bạn có hài lòng không?', true, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(4, 2, 'Dạ có ạ, tai nghe rất tuyệt! Tôi sẽ giới thiệu bạn bè mua ở shop.', true, DATE_SUB(NOW(), INTERVAL 2 DAY)),

(1, 5, 'Cảm ơn bạn đã đánh giá 5 sao cho ASUS ROG Strix G15! Nếu cần hỗ trợ gì thêm, hãy liên hệ nhé.', false, DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- Tin nhắn support
(6, 1, 'Xin chào admin, tôi muốn hỏi về tình trạng đơn hàng HP Pavilion 15 của tôi?', false, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(7, 2, 'Shop ơi, giày Nike Air Max của tôi dự kiến khi nào giao được ạ?', false, NOW()),

-- Tin nhắn giữa users
(3, 4, 'Chào bạn! Mình thấy bạn cũng mua đồ tech ở shop này. Chất lượng thế nào?', false, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(4, 3, 'Chào bạn! Shop này tốt lắm, mình đã mua nhiều lần rồi. Sản phẩm chính hãng, ship nhanh.', false, DATE_SUB(NOW(), INTERVAL 1 DAY)),

(8, 9, 'Bạn ơi, MacBook Air M3 dùng có tốt không? Mình đang cân nhắc mua.', false, NOW()),
(5, 8, 'ASUS ROG Strix G15 mình mua chơi game rất mượt! Bạn có cần tư vấn gì không?', false, DATE_SUB(NOW(), INTERVAL 3 HOUR));

-- =============================================
-- 14. BẢNG SALES_STATISTICS - Thống kê bán hàng
-- =============================================
INSERT INTO sales_statistics (year, month, total_sales, total_profit, total_orders, best_selling_product_id, created_at) VALUES
-- Thống kê năm 2024 (tham chiếu theo tên sản phẩm để tránh lệ thuộc ID)
(2024, 1, 125000000.00, 18500000.00, 45, (SELECT id FROM products WHERE name = 'iPhone 15 Pro Max 256GB' LIMIT 1), DATE('2024-02-01')),
(2024, 2, 98000000.00, 15200000.00, 38, (SELECT id FROM products WHERE name = 'Samsung Galaxy S24 Ultra' LIMIT 1), DATE('2024-03-01')),
(2024, 3, 156000000.00, 22800000.00, 52, (SELECT id FROM products WHERE name = 'iPhone 15 Pro Max 256GB' LIMIT 1), DATE('2024-04-01')),
(2024, 4, 187000000.00, 28900000.00, 61, (SELECT id FROM products WHERE name = 'MacBook Air M3 13 inch' LIMIT 1), DATE('2024-05-01')),
(2024, 5, 145000000.00, 21200000.00, 48, (SELECT id FROM products WHERE name = 'ASUS ROG Strix G15' LIMIT 1), DATE('2024-06-01')),
(2024, 6, 203000000.00, 32500000.00, 67, (SELECT id FROM products WHERE name = 'iPhone 15 Pro Max 256GB' LIMIT 1), DATE('2024-07-01')),
(2024, 7, 178000000.00, 26700000.00, 56, (SELECT id FROM products WHERE name = 'Samsung Galaxy S24 Ultra' LIMIT 1), DATE('2024-08-01')),
(2024, 8, 165000000.00, 24100000.00, 53, (SELECT id FROM products WHERE name = 'MacBook Air M3 13 inch' LIMIT 1), DATE('2024-09-01')),
(2024, 9, 192000000.00, 29800000.00, 63, (SELECT id FROM products WHERE name = 'iPhone 15 Pro Max 256GB' LIMIT 1), DATE('2024-10-01')),
(2024, 10, 234000000.00, 38200000.00, 78, (SELECT id FROM products WHERE name = 'iPhone 15 Pro Max 256GB' LIMIT 1), DATE('2024-11-01')),
(2024, 11, 267000000.00, 42800000.00, 89, (SELECT id FROM products WHERE name = 'MacBook Air M3 13 inch' LIMIT 1), DATE('2024-12-01')),
(2024, 12, 298000000.00, 48500000.00, 95, (SELECT id FROM products WHERE name = 'iPhone 15 Pro Max 256GB' LIMIT 1), DATE('2025-01-01'));

-- =============================================
-- Kết thúc script
-- =============================================

-- Kiểm tra dữ liệu đã insert
SELECT 'Users' as Table_Name, COUNT(*) as Record_Count FROM users
UNION ALL
SELECT 'Categories', COUNT(*) FROM categories
UNION ALL
SELECT 'Products', COUNT(*) FROM products
UNION ALL
SELECT 'Product Images', COUNT(*) FROM product_images
UNION ALL
SELECT 'Reasons', COUNT(*) FROM reasons
UNION ALL
SELECT 'Ship Info', COUNT(*) FROM ship_info
UNION ALL
SELECT 'Carts', COUNT(*) FROM carts
UNION ALL
SELECT 'Cart Items', COUNT(*) FROM cart_items
UNION ALL
SELECT 'Orders', COUNT(*) FROM orders
UNION ALL
SELECT 'Order Items', COUNT(*) FROM order_items
UNION ALL
SELECT 'Reviews', COUNT(*) FROM reviews
UNION ALL
SELECT 'Product Suggestions', COUNT(*) FROM product_suggestions
UNION ALL
SELECT 'Messages', COUNT(*) FROM messages
UNION ALL
SELECT 'Sales Statistics', COUNT(*) FROM sales_statistics;

-- Print completion message
SELECT 'Data generation script executed successfully!' as Message;

-- Bật lại khóa ngoại sau tất cả các thao tác chèn dữ liệu
SET FOREIGN_KEY_CHECKS = 1;
