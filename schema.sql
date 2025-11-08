-- ============================================================
-- E-Commerce Backend System - Database Schema (MySQL)
-- Author: Surya Prakash C
-- Date: November 2025
-- ============================================================

-- 1️⃣ Create Database
CREATE DATABASE IF NOT EXISTS ecommerce_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE ecommerce_db;

-- ============================================================
-- 2️⃣ USERS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ROLE_ADMIN','ROLE_CUSTOMER') NOT NULL DEFAULT 'ROLE_CUSTOMER'
    );

-- ============================================================
-- 3️⃣ PRODUCTS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS products (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE NOT NULL,
    stock INT NOT NULL CHECK (stock >= 0),
    category VARCHAR(100),
    image_url VARCHAR(500),
    rating DOUBLE DEFAULT 0.0
    );

-- ============================================================
-- 4️⃣ CART TABLE
-- Each user has exactly one cart
-- ============================================================
CREATE TABLE IF NOT EXISTS carts (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     user_id BIGINT NOT NULL UNIQUE,
                                     total_price DOUBLE DEFAULT 0.0,
                                     CONSTRAINT fk_cart_user FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    );

-- ============================================================
-- 5️⃣ CART ITEMS TABLE
-- Each cart item belongs to one cart and one product
-- ============================================================
CREATE TABLE IF NOT EXISTS cart_items (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          cart_id BIGINT NOT NULL,
                                          product_id BIGINT NOT NULL,
                                          quantity INT NOT NULL CHECK (quantity > 0),
    CONSTRAINT fk_cartitem_cart FOREIGN KEY (cart_id)
    REFERENCES carts(id)
    ON DELETE CASCADE,
    CONSTRAINT fk_cartitem_product FOREIGN KEY (product_id)
    REFERENCES products(id)
    ON DELETE RESTRICT,
    UNIQUE KEY ux_cart_product (cart_id, product_id)
    );

-- ============================================================
-- 6️⃣ ORDERS TABLE
-- Stores completed checkouts and payment details
-- ============================================================
CREATE TABLE IF NOT EXISTS orders (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      user_id BIGINT NOT NULL,
                                      total_amount DOUBLE NOT NULL,
                                      order_date DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    payment_mode ENUM('CREDIT_CARD','DEBIT_CARD','UPI','NET_BANKING','CASH_ON_DELIVERY') NOT NULL,
    order_status ENUM('PENDING','PLACED','FAILED','SHIPPED','DELIVERED','CANCELLED') DEFAULT 'PENDING',
    CONSTRAINT fk_order_user FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    );

-- ============================================================
-- 7️⃣ ORDER ITEMS TABLE
-- Links products to orders (line items)
-- ============================================================
CREATE TABLE IF NOT EXISTS order_items (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           order_id BIGINT NOT NULL,
                                           product_id BIGINT NOT NULL,
                                           quantity INT NOT NULL CHECK (quantity > 0),
    price DOUBLE NOT NULL,
    CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id)
    REFERENCES orders(id)
    ON DELETE CASCADE,
    CONSTRAINT fk_orderitem_product FOREIGN KEY (product_id)
    REFERENCES products(id)
    ON DELETE RESTRICT
    );

-- ============================================================
-- 8️⃣ SAMPLE INDEXES
-- ============================================================
CREATE INDEX idx_product_category ON products(category);
CREATE INDEX idx_order_user ON orders(user_id);
CREATE INDEX idx_order_date ON orders(order_date);

-- ============================================================

-- ============================================================
INSERT INTO products (name, description, price, stock, category, image_url, rating) VALUES
                                                                                        ('Wireless Mouse', 'Ergonomic wireless mouse with adjustable DPI', 699.0, 50, 'Electronics', 'https://example.com/mouse.jpg', 4.5),
                                                                                        ('Mechanical Keyboard', 'RGB mechanical keyboard with blue switches', 2499.0, 30, 'Electronics', 'https://example.com/keyboard.jpg', 4.7),
                                                                                        ('USB-C Charger', 'Fast 65W USB-C charger with GaN tech', 1299.0, 80, 'Accessories', 'https://example.com/charger.jpg', 4.3),
                                                                                        ('Noise Cancelling Headphones', 'Over-ear ANC headphones', 7999.0, 20, 'Audio', 'https://example.com/headphones.jpg', 4.6);


