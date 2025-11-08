-- E-Commerce Database Initialization Script
-- Database: ecommerce_db
-- Run this script in MySQL Workbench after creating the database

USE ecommerce_db;

-- Note: Tables will be auto-created by Hibernate with ddl-auto=update
-- This script is provided for reference and manual creation if needed

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS carts;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS users;

-- Users Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    INDEX idx_username (username),
    INDEX idx_email (email)
);

-- Products Table
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    category VARCHAR(100),
    image_url VARCHAR(500),
    rating DECIMAL(3, 2) DEFAULT 0.0,
    INDEX idx_category (category),
    INDEX idx_price (price)
);

-- Carts Table
CREATE TABLE carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_price DECIMAL(10, 2) DEFAULT 0.0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_cart (user_id)
);

-- Cart Items Table
CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_cart_id (cart_id)
);

-- Orders Table
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    order_date DATETIME NOT NULL,
    payment_status VARCHAR(20) NOT NULL,
    order_status VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_order_date (order_date)
);

-- Order Items Table
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id)
);

-- Insert Sample Admin User (password: admin123 - hashed with BCrypt)
-- Default BCrypt hash for "admin123": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
INSERT INTO users (username, name, email, password, role) VALUES
('admin', 'Admin User', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN');

-- Insert Sample Customer User (password: password123 - hashed with BCrypt)
-- Default BCrypt hash for "password123": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- Note: In production, passwords should be hashed by the application
INSERT INTO users (username, name, email, password, role) VALUES
('johndoe', 'John Doe', 'john@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER');

-- Insert Sample Products
INSERT INTO products (name, description, price, stock, category, image_url, rating) VALUES
('Laptop', 'High-performance laptop with 16GB RAM and 512GB SSD', 999.99, 50, 'Electronics', 'https://example.com/laptop.jpg', 4.5),
('Smartphone', 'Latest smartphone with 128GB storage', 699.99, 100, 'Electronics', 'https://example.com/phone.jpg', 4.8),
('Headphones', 'Wireless noise-cancelling headphones', 199.99, 75, 'Electronics', 'https://example.com/headphones.jpg', 4.3),
('T-Shirt', 'Cotton t-shirt, available in multiple colors', 29.99, 200, 'Clothing', 'https://example.com/tshirt.jpg', 4.0),
('Running Shoes', 'Comfortable running shoes for daily use', 89.99, 150, 'Footwear', 'https://example.com/shoes.jpg', 4.6),
('Backpack', 'Durable backpack with laptop compartment', 59.99, 80, 'Accessories', 'https://example.com/backpack.jpg', 4.4),
('Watch', 'Smartwatch with fitness tracking', 249.99, 60, 'Electronics', 'https://example.com/watch.jpg', 4.7),
('Coffee Maker', 'Programmable coffee maker', 79.99, 40, 'Home & Kitchen', 'https://example.com/coffee.jpg', 4.2);

-- Note: The actual password hashes above are placeholders. 
-- In the application, passwords are hashed using BCryptPasswordEncoder.
-- To create proper hashes, register users through the API.

SELECT 'Database initialized successfully!' AS Status;

