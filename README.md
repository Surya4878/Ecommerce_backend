🛍️ E-Commerce Backend System
📚 Project Overview

The E-Commerce Backend System is a production-grade RESTful backend built with Spring Boot 3 and Java 17.
It manages all core functionalities of an online shopping platform including:

User Management – Registration, Login, Profile Management, Role-based Access (Admin/Customer)

Product Management – Admin CRUD operations with pagination and filters

Shopping Cart – Customer-specific cart with quantity updates and total price calculation

Order Management – Checkout, payment simulation, order tracking

Inventory Management – Auto stock reduction and out-of-stock prevention

Payment Simulation – Mimics real payment flows with success/failure outcomes

This backend provides APIs ready for integration with web or mobile frontends.

⚙️ Tech Stack
Component	Technology
Language	Java 17
Framework	Spring Boot 3
ORM	Spring Data JPA (Hibernate)
Database	MySQL 8
Build Tool	Maven
Testing	JUnit 5, Mockito
Security	Spring Security (JWT Authentication)
Logging	SLF4J / Logback
🧩 How to Run Locally
🧱 1. Prerequisites

Make sure the following are installed:

Java 17+

Maven 3.9+

MySQL 8+

IntelliJ IDEA (recommended)

🗄️ 2. Create Database

Open MySQL Workbench or terminal and run:

CREATE DATABASE ecommerce_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

⚙️ 3. Configure Database Connection

In your project, open src/main/resources/application.properties and verify:

spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=Surya@4878

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT configuration
jwt.secret=ReplaceThisWithAStrongSecretKeyChangeMeReplaceThisWithAStrongSecret!
jwt.expirationMs=3600000


⚠️ Replace the jwt.secret value with a random 32+ character key before deployment.

🚀 4. Build & Run Application

From the terminal in the project root:

mvn clean spring-boot:run


or

mvn clean package
java -jar target/ecommerce-backend-0.0.1-SNAPSHOT.jar


The application runs at:
👉 http://localhost:8080

🧪 5. Test APIs

Use Postman to test the APIs:

Import the collection file → postman/ecommerce-collection.json

Run APIs in this order:

Register User

Login (get JWT token)

Register Admin

Promote Admin (via SQL)

Admin Login → Create Product

Customer → Add to Cart → Checkout → View Orders

Admin → Update Order Status

📘 API Documentation
👤 User APIs
Method	Endpoint	Role	Description
POST	/api/auth/register	Public	Register new user
POST	/api/auth/login	Public	Login and receive JWT
GET	/api/users/{id}	Customer/Admin	View profile
PUT	/api/users/{id}	Customer/Admin	Update user profile
PUT	/api/users/{id}/change-password	Customer	Change password
DELETE	/api/users/{id}	Admin	Delete user
📦 Product APIs
Method	Endpoint	Role	Description
POST	/api/products	Admin	Add new product
PUT	/api/products/{id}	Admin	Update product
DELETE	/api/products/{id}	Admin	Delete product
GET	/api/products	Public	View all products (with pagination & filters)
GET	/api/products/{id}	Public	View product details
🛒 Cart APIs
Method	Endpoint	Role	Description
POST	/api/cart/add/{productId}?quantity=2	Customer	Add product to cart
PUT	/api/cart/update/{productId}?quantity=3	Customer	Update quantity
DELETE	/api/cart/remove/{productId}	Customer	Remove from cart
GET	/api/cart	Customer	View cart contents & total price
🧾 Order APIs
Method	Endpoint	Role	Description
POST	/api/orders/checkout	Customer	Checkout cart & simulate payment
GET	/api/orders	Customer	View order history
GET	/api/orders/{id}	Customer/Admin	View order details
PUT	/api/orders/{id}/status?status=SHIPPED	Admin	Update order status
GET	/api/orders/all	Admin	View all orders
💳 Payment Simulation

Example request:

{
  "paymentMode": "CREDIT_CARD",
  "simulateSuccess": true
}


If simulateSuccess=true → order is PLACED
If simulateSuccess=false → order is FAILED

⚖️ Inventory Management

Stock is automatically reduced after successful checkout.

Attempting to order more than available stock returns 400 Bad Request.

Out-of-stock products cannot be added to the cart.

🗃️ Database Schema
Entity Relationship Diagram (Text Form)
User (1) ──── (1) Cart
User (1) ──── (∞) Order
Cart (1) ──── (∞) CartItem
Order (1) ──── (∞) OrderItem
CartItem (∞) ──── (1) Product
OrderItem (∞) ──── (1) Product

SQL Schema (MySQL)
CREATE DATABASE IF NOT EXISTS ecommerce_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ecommerce_db;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    role ENUM('ROLE_ADMIN','ROLE_CUSTOMER') NOT NULL
);

CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE NOT NULL,
    stock INT NOT NULL CHECK (stock >= 0),
    category VARCHAR(100),
    image_url VARCHAR(500),
    rating DOUBLE DEFAULT 0.0
);

CREATE TABLE carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    total_price DOUBLE DEFAULT 0.0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
    UNIQUE KEY (cart_id, product_id)
);

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_amount DOUBLE NOT NULL,
    order_date DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    payment_mode ENUM('CREDIT_CARD','DEBIT_CARD','UPI','NET_BANKING','CASH_ON_DELIVERY') NOT NULL,
    order_status ENUM('PENDING','PLACED','FAILED','SHIPPED','DELIVERED','CANCELLED') DEFAULT 'PENDING',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DOUBLE NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT
);

✅ Sample Data (Optional)
INSERT INTO products (name, description, price, stock, category, image_url, rating) VALUES
('Wireless Mouse', 'Ergonomic wireless mouse', 699.0, 50, 'Electronics', 'https://example.com/mouse.jpg', 4.5),
('Mechanical Keyboard', 'RGB mechanical keyboard', 2499.0, 30, 'Electronics', 'https://example.com/keyboard.jpg', 4.7),
('USB-C Charger', 'Fast 65W USB-C charger', 1299.0, 80, 'Accessories', 'https://example.com/charger.jpg', 4.3);


👨‍💻 Author

Surya Prakash C
