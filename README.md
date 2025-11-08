🛍️ E-Commerce Backend System (Spring Boot + MySQL)
📖 Overview

This project is a production-grade backend system for an E-Commerce Platform built with Java 17 and Spring Boot 3.
It manages users, products, shopping carts, orders, simulated payments, and inventory updates — providing a clean, RESTful API suitable for web or mobile clients.

🎯 Objectives

The backend supports:

👥 User registration, login, and role-based access (ADMIN, CUSTOMER)

🛒 Shopping cart operations

📦 Order creation and management

💳 Simulated payment processing

📊 Inventory tracking and automatic stock updates

🔐 JWT authentication for secure access

⚙️ Tech Stack
Component	Technology
Language	Java 17
Framework	Spring Boot 3
ORM	Spring Data JPA (Hibernate)
Database	MySQL 8
Security	Spring Security (JWT)
Build Tool	Maven
Testing	JUnit 5, Mockito
Logging	SLF4J / Logback
IDE	IntelliJ IDEA / VS Code
🧩 Project Structure
com.ecommerce
 ┣ 📂 controller        → REST API endpoints
 ┣ 📂 service           → Business logic
 ┣ 📂 repository        → Spring Data JPA interfaces
 ┣ 📂 entity            → JPA entities (User, Product, Cart, etc.)
 ┣ 📂 dto               → Request/Response Data Transfer Objects
 ┣ 📂 config            → Security, JWT, and application configs
 ┣ 📂 exception         → Global exception handling
 ┣ 📂 utils             → Helper utilities
 ┗ 📜 EcommerceApplication.java


✅ Architecture: Controller → Service → Repository → Entity
✅ Follows: Clean Code & Layered Architecture principles.

🗃️ Database Design
Entities
Entity	Description
User	id, name, email, password (BCrypt), role (ADMIN/CUSTOMER)
Product	id, name, description, price, stock, category, image_url, rating
Cart	id, user_id, total_price
CartItem	id, cart_id, product_id, quantity
Order	id, user_id, total_amount, order_date, payment_mode, order_status
OrderItem	id, order_id, product_id, quantity, price
Entity Relationships

User 1️⃣-1️⃣ Cart

Cart 1️⃣-🔢 CartItem

Order 1️⃣-🔢 OrderItem

User 1️⃣-🔢 Order

🏗️ Setup & Installation (Windows / Mac / Linux)
🧱 1. Prerequisites

JDK 17+

Maven 3.9+

MySQL 8+

IntelliJ IDEA (recommended)

⚙️ 2. Clone Repository
git clone https://github.com/<your-username>/ecommerce-backend.git
cd ecommerce-backend

🛢️ 3. Configure Database

Open MySQL and create a database:

CREATE DATABASE ecommerce_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

🧩 4. Update Credentials

Edit src/main/resources/application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=Surya@4878

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=ReplaceThisWithAStrongSecretKeyChangeMeReplaceThisWithAStrongSecret!
jwt.expirationMs=3600000


⚠️ Change the JWT secret to a 32-character or longer random string before deployment.

🚀 5. Build & Run Application

Run using IntelliJ Run or terminal:

mvn clean spring-boot:run


or build a JAR:

mvn clean package
java -jar target/ecommerce-backend-0.0.1-SNAPSHOT.jar


Server starts on http://localhost:8080

🧠 Core Features (Requirements A–F)
Feature	Description
A. User Management	Register, Login, Profile Update, Password Change, Role-based Access (ADMIN, CUSTOMER)
B. Product Management	Admin CRUD for products with pagination & filtering
C. Shopping Cart	Customer-specific cart: add, remove, update items
D. Order Management	Checkout, convert cart to order, view history
E. Payment Simulation	Simulate payment success/failure; auto update order status
F. Inventory Management	Auto-reduce stock after order; prevent out-of-stock purchase
🔐 JWT Authentication Flow

POST /api/auth/register → Creates user (default role: CUSTOMER)

POST /api/auth/login → Returns JWT token

Send the token in each protected request:

Authorization: Bearer <JWT_TOKEN>


Admin-only endpoints require ROLE_ADMIN

🧾 API Documentation (Summary)
🧍‍♂️ User APIs
Method	Endpoint	Role	Description
POST	/api/auth/register	Public	Register new user
POST	/api/auth/login	Public	Login and get JWT
GET	/api/users/{id}	Customer/Admin	Get user profile
PUT	/api/users/{id}	Customer/Admin	Update profile
PUT	/api/users/{id}/change-password	Customer	Change password
DELETE	/api/users/{id}	Admin	Delete user
📦 Product APIs
Method	Endpoint	Role	Description
POST	/api/products	Admin	Add new product
PUT	/api/products/{id}	Admin	Update product
DELETE	/api/products/{id}	Admin	Delete product
GET	/api/products	Public	List products (supports pagination, filtering)
GET	/api/products/{id}	Public	Get product details
🛒 Cart APIs
Method	Endpoint	Role	Description
POST	/api/cart/add/{productId}?quantity=2	Customer	Add item to cart
PUT	/api/cart/update/{productId}?quantity=3	Customer	Update quantity
DELETE	/api/cart/remove/{productId}	Customer	Remove product
GET	/api/cart	Customer	View cart contents
🧾 Order APIs
Method	Endpoint	Role	Description
POST	/api/orders/checkout	Customer	Convert cart to order
GET	/api/orders	Customer	View order history
GET	/api/orders/{id}	Customer/Admin	Get order details
PUT	/api/orders/{id}/status?status=SHIPPED	Admin	Update order status
GET	/api/orders/all	Admin	List all orders
💳 Payment Simulation

Checkout body example:

{
  "paymentMode": "CREDIT_CARD",
  "simulateSuccess": true
}


Responses:

"orderStatus": "PLACED" → Payment success

"orderStatus": "FAILED" → Payment failed

Inventory reduces only if success.

📦 Inventory Management

Product stock decremented on successful checkout.

If stock < requested quantity → 400 Bad Request.

Prevents overselling.

📂 Postman Collection

A ready-to-import Postman Collection is included:

postman/ecommerce-collection.json

⚙️ How to Use:

Import Ecommerce Backend.postman_collection.json into Postman.

Import environment file Ecommerce Local.postman_environment.json.

Run requests in sequence:

Register user → Login → Register admin → Promote admin (via DB) → Admin Login → Create product → Customer add to cart → Checkout → View order → Admin update status.

You can also run it automatically:

newman run postman/ecommerce-collection.json -e postman/ecommerce-env.json

🧪 Testing
Run Unit & Integration Tests
mvn test

Includes:

✅ UserServiceTest — user registration and validation

✅ ProductControllerTest — controller-layer mock MVC test

✅ CheckoutIntegrationTest — end-to-end flow test with MySQL (no Docker)

Tests use a separate DB (ecommerce_test) defined in
src/test/resources/application-test.properties.

📄 SQL Schema Reference (schema.sql)
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255),
  email VARCHAR(200) UNIQUE,
  password VARCHAR(255),
  role VARCHAR(50)
);
CREATE TABLE products (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255),
  description TEXT,
  price DOUBLE,
  stock INT,
  category VARCHAR(100),
  image_url VARCHAR(500),
  rating DOUBLE
);
-- Carts, CartItems, Orders, OrderItems as per entity mappings


(Full script included in repository as schema.sql)

🧩 Example Workflow (Manual Testing)

Register Admin

POST /api/auth/register
{ "name":"Admin","email":"admin@example.com","password":"Admin123" }


Then promote via:

UPDATE users SET role='ROLE_ADMIN' WHERE email='admin@example.com';


Login

POST /api/auth/login
{ "email":"admin@example.com","password":"Admin123" }


Copy token.

Add Product

POST /api/products
Header: Authorization: Bearer <token>
{
  "name":"Wireless Mouse",
  "description":"Ergonomic mouse",
  "price":699,
  "stock":50,
  "category":"Electronics"
}


Customer → Add to Cart

POST /api/cart/add/{productId}?quantity=2


Checkout

POST /api/orders/checkout
{
  "paymentMode":"CREDIT_CARD",
  "simulateSuccess":true
}

🧰 Development Tips

spring.jpa.hibernate.ddl-auto=update during dev; switch to validate or Flyway in production.

Use spring.sql.init.mode=always if you want data.sql seeding.

To test admin endpoints quickly, update role in MySQL directly.

JWT tokens expire after 1 hour by default (jwt.expirationMs).

👨‍💻 Author

Surya Prakash C
