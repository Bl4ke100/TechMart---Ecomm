CREATE DATABASE IF NOT EXISTS techmartdb;
USE techmartdb;

DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER' NOT NULL
);

CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    inventoryCount INT NOT NULL
);

CREATE TABLE orders (
    orderId INT AUTO_INCREMENT PRIMARY KEY,
    productId INT NOT NULL,
    username VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    orderDate DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (productId) REFERENCES products(id)
);

INSERT INTO users (username, password, email, role) VALUES
('user', 'user', 'user@techmart.com', 'USER'),
('admin', 'admin', 'admin@techmart.com', 'ADMIN');

INSERT INTO products (name, description, price, inventoryCount) VALUES 
('Quantum Laptop', 'High performance laptop', 1299.99, 10),
('Nexus Phone', 'Latest smartphone', 899.50, 25),
('Ultra Headphones', 'Noise cancelling', 199.99, 50),
('Smart Watch Series X', 'Fitness and health tracker', 299.99, 30),
('4K Monitor', '32-inch ultra HD display', 399.00, 15);
