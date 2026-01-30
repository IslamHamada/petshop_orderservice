create table orders(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    time DATETIME,
    price DECIMAL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    phone_number VARCHAR(255),
    country VARCHAR(255),
    city VARCHAR(255),
    postal_code VARCHAR(255),
    house_number VARCHAR(255),
    street VARCHAR(255)
);